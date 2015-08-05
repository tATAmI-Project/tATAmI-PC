/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.amilab.util;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.finagle.ChannelClosedException;
import com.twitter.finagle.ServiceFactory;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.builder.ClientConfig.Yes;
import com.twitter.finagle.kestrel.java.Client;
import com.twitter.finagle.kestrel.protocol.Command;
import com.twitter.finagle.kestrel.protocol.Kestrel;
import com.twitter.finagle.kestrel.protocol.Response;
import com.twitter.util.Duration;
import com.twitter.util.Time;

public class SimpleKestrelClient implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleKestrelClient.class);

    private final Client client;

    public SimpleKestrelClient(String host, int port) {
        this(new InetSocketAddress(host, port));
    }

    public SimpleKestrelClient(InetSocketAddress addr) {
        ClientBuilder<Command, Response, Yes, Yes, Yes> builder = ClientBuilder.get()
                    .codec(Kestrel.get())
                    .hosts(addr)
                    .hostConnectionLimit(1);
        ServiceFactory<Command, Response> kestrelClientBuilder = ClientBuilder.safeBuildFactory(builder);
        client = Client.newInstance(kestrelClientBuilder);
    }

    public void delete(String queueName) {
        client.delete(queueName).apply();
    }

    @Override
    public void close() {
        client.close();
    }

    public void set(String queueName, String value) {
        set(queueName, 0, value);
    }

    public void set(String queueName, int exp, String value) {
        Time expTime = Time.fromMilliseconds(exp);
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(value.getBytes(CharsetUtil.UTF_8));
        client.set(queueName, buffer, expTime);
    }

    public String get(String queueName) {
        return get(queueName, 0);
    }

    public String get(String queueName, int waitFor) {
        Duration waitDuration = Duration.apply(waitFor, TimeUnit.MILLISECONDS);
        return get(queueName, waitDuration);
    }

    private static final List<Class<? extends Exception>> THROUGH_EXCEPTIONS = new ArrayList<Class<? extends Exception>>();
    static {
        THROUGH_EXCEPTIONS.add(ChannelClosedException.class);
    };
    public String get(String queueName, Duration waitDuration) {
        try {
            ChannelBuffer value = client.get(queueName, waitDuration).apply();
            return value == null ? null : value.toString(CharsetUtil.UTF_8);
        } catch (Exception e) {
            if (THROUGH_EXCEPTIONS.contains(e.getClass())) {
                 return null;
            }
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    public String peek(String queueName) {
        return peek(queueName, 0);
    }

    public String peek(String queueName, int waitFor) {
        Duration waitDuration = Duration.apply(waitFor, TimeUnit.MILLISECONDS);
        return peek(queueName, waitDuration);
    }

    public String peek(String queueName, Duration waitDuration) {
        return get(queueName + "/peek", waitDuration);
    }
}
