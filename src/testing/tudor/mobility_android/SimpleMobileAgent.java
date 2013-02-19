package testing.tudor.mobility_android;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class SimpleMobileAgent extends Agent{

	protected void setup() {
		getContentManager().registerLanguage(new SLCodec());
		getContentManager().registerOntology(MobilityOntology.getInstance());
		
		addBehaviour(new OneShotBehaviour() {
//			public boolean started = false;
			@Override
			public void action() {
				// TODO Auto-generated method stub

				// Wait a while:
				long startTime = System.currentTimeMillis();
				while(System.currentTimeMillis()-startTime<5000);
				
//				AgentMobilityService am = new AgentMobilityService();
				
				System.out.println("trying to move ...");
				Location dest = new ContainerID("android",null);
				doMove(dest);

//				try {
//					System.out.println("trying to move ...");
//					Hashtable<String, Location> locations = new Hashtable<String, Location>();
//
//					// Get available locations with AMS
//					sendRequest(new Action(getAMS(), new QueryPlatformLocationsAction()));
//
//					//Receive response from AMS
//					MessageTemplate mt = MessageTemplate.and(
//							MessageTemplate.MatchSender(getAMS()),
//							MessageTemplate.MatchPerformative(ACLMessage.INFORM));
//					ACLMessage resp = blockingReceive(mt);
//					ContentElement ce = getContentManager().extractContent(resp);
//					Result result = (Result) ce;
//					jade.util.leap.Iterator it = result.getItems().iterator();
//					while (it.hasNext()) {
//						Location loc = (Location)it.next();
//						locations.put(loc.getName(), loc);
//					}
//
//					Location dest = locations.get("android");
//					doMove(dest);
//				}
//				catch (Exception e) { e.printStackTrace(); }
			}
		});
	}
	
	public void sendRequest(Action action)
	{
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setLanguage(new SLCodec().getName());
		request.setOntology(MobilityOntology.getInstance().getName());
		try {
			getContentManager().fillContent(request, action);
			request.addReceiver(action.getActor());
			send(request);
		}
		catch (Exception ex) { ex.printStackTrace(); }
	}
}