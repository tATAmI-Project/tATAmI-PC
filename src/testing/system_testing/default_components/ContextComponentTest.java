package testing.system_testing.default_components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.xqhs.graphs.context.ContextPattern;
import net.xqhs.graphs.context.ContinuousMatchingProcess;
import net.xqhs.graphs.context.ContinuousMatchingProcess.MatchNotificationReceiver;
import net.xqhs.graphs.graph.Edge;
import net.xqhs.graphs.graph.Graph;
import net.xqhs.graphs.graph.Node;
import net.xqhs.graphs.graph.SimpleEdge;
import net.xqhs.graphs.graph.SimpleGraph;
import net.xqhs.graphs.matcher.Match;
import net.xqhs.graphs.pattern.GraphPattern;
import net.xqhs.graphs.representation.text.TextGraphRepresentation;
import net.xqhs.graphs.util.ContentHolder;

import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponent;
import net.xqhs.util.logging.Logger;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.kb.ContextComponent;

/**
 * Basic test for ContextComponent
 * for ContextGraph and ContinuousMatching Test see {@link ContextGraphTest}
 */

@SuppressWarnings("javadoc")
public class ContextComponentTest extends Unit
{
	List<ContextPattern> GPs;
	
	Node Emily, Living, Bathroom, Hall, Kitchen;
	Edge e1;
	
	public ContextComponentTest()
	{
		setUnitName("context component tester");
		li("starting...");
		
		CompositeAgent agent = new CompositeAgent();
		agent.addComponent(new ContextComponent());
		
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			private static final long	serialVersionUID	= 1L;
			UnitComponent	locallog;
			
			@Override
			protected AgentComponent getAgentComponent(AgentComponentName name)
			{
				return super.getAgentComponent(name);
			}
			
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
		
				locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring").setLogLevel(Logger.Level.ALL);
				AgentEventHandler allEventHandler = new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						locallog.li("event: [" + event.getType().toString() + "]");
						ContextComponent context = (ContextComponent) getAgentComponent(AgentComponentName.COGNITIVE_COMPONENT);
						
						if(context == null)
							locallog.li("\t parametric component is currently null");
						else if (event.getType() == AgentEventType.AGENT_START) {
							
							try {
									
								BufferedReader reader = new BufferedReader(new InputStreamReader(
															new FileInputStream(new File("playground/house"))));
								StringBuilder builder = new StringBuilder();
								String line;
								
								while((line = reader.readLine()) != null)
								{
									builder.append(line);
									builder.append('\n');
								}
								ContentHolder<String> input = new ContentHolder<String>(builder.toString());
								reader.close();
								
								Graph g = new TextGraphRepresentation(new SimpleGraph()).readRepresentation(input);
								locallog.li("graph: []", new TextGraphRepresentation(g).update());
								
								// load patterns
								GPs = new ArrayList<ContextPattern>();
								while(input.get().length() > 0)
								{
									ContextPattern p = new ContextPattern();
									TextGraphRepresentation repr = new TextGraphRepresentation(p);
									repr.readRepresentation(input);
									locallog.li("new pattern: []", repr.toString());
									GPs.add(p);
									input.set(input.get().trim());
								}
								
								context.registerMatchNotificationTarget(GPs.get(0), new MatchNotificationReceiver() {
									@Override
									public void receiveMatchNotification(ContinuousMatchingProcess platform, Match m)
									{
										locallog.li("new match for pattern 0: ", m);
									}
								});
							
								for(ContextPattern pattern : GPs)
									context.addPattern(pattern);
								
								Emily = g.getNodesNamed("Emily").iterator().next();
								Living = g.getNodesNamed("Living").iterator().next();
								Bathroom = g.getNodesNamed("Bathroom").iterator().next();
								Hall = g.getNodesNamed("Hall").iterator().next();
								Kitchen = g.getNodesNamed("Kitchen").iterator().next();
								
								context.add(g);
								
								} catch (IOException e) {
									e.printStackTrace();
								}
					
						} else if (event.getType() == AgentEventType.AGENT_STOP) {

							e1 = new SimpleEdge(Emily, Living, "is-in");
							Graph newGraph = new GraphPattern();
							newGraph.add(Emily);
							newGraph.add(Living);
							newGraph.add(e1);
						
							locallog.li("Current context graph: ", context.getKnowledge());
							locallog.li("Add to context graph: ", newGraph);
							context.add(newGraph);
							locallog.li("After adding: ", context.getKnowledge());

						}	
						
						if(event.getType() == AgentEventType.AGENT_STOP)
							locallog.doExit();
					}
				};
				for(AgentEventType eventType : AgentEventType.values())
					registerHandler(eventType, allEventHandler);
		
			}
		});
		
		agent.start();
		try
		{
			Thread.sleep(200);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		agent.exit();
		li("done.");
		doExit();
	}
		
	@SuppressWarnings("unused")
	public static void main(String args[]) throws IOException
	{
		new ContextComponentTest();
	}
}