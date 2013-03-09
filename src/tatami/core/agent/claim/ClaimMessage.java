/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent.claim;

import java.util.Iterator;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ClaimMessage extends ACLMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//FIXME build message with another parameters
	
	public ClaimMessage() {
		super(ACLMessage.INFORM);
		this.setLanguage(ClaimOntology.LANGUAGE);
		this.setOntology(ClaimOntology.NAME);
	}
	
	/**
	 * build message if already knew the receiver, the protocol, and the content (which is a String)
	 * @param receiver: local name of the receiver
	 * @param protocol: name of protocol
	 * @param content: String represented the content of message
	 */
	public ClaimMessage(String receiver, String protocol, String content){
		super(ACLMessage.INFORM);		
		this.addReceiver(new AID(receiver, AID.ISLOCALNAME));
		this.setLanguage(ClaimOntology.LANGUAGE);
		this.setOntology(ClaimOntology.NAME);
		this.setProtocol(protocol);
		
		this.setContent(content);
	}
	
	/**
	 * build message if already knew the receiver, the protocol
	 * @param receiver: local name of the receiver
	 * @param protocol: name of protocol
	 */
	public ClaimMessage(String receiver, String protocol){
		super(ACLMessage.INFORM);		
		this.addReceiver(new AID(receiver, AID.ISLOCALNAME));
		this.setLanguage(ClaimOntology.LANGUAGE);
		this.setOntology(ClaimOntology.NAME);
		this.setProtocol(protocol);
	}
	
	public static String printMessage(ACLMessage msg)
	{
		String ret = "";
		String receivers = "";
		for(@SuppressWarnings("unchecked") Iterator<AID> it = msg.getAllReceiver(); it.hasNext();)
			receivers += ((receivers.length() > 0) ? ";" : "") + it.next().toString();
		try
		{
			ret += "[from:" + msg.getSender().getLocalName() + "][to:" + receivers + "][" + msg.getOntology() + "][" + msg.getProtocol() + "][" + ((msg.getContentObject() != null) ? msg.getContentObject().toString() : "-") + "]";
		} catch(UnreadableException e)
		{
			ret += "[from:" + msg.getSender().getLocalName() + "][" + msg.getOntology() + "][" + msg.getProtocol() + "][" + "#unreadable#" + "]";
		}
		return ret;
	}
}
