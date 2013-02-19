package testing.tudor.loader_pc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import testing.tudor.utils.AgentView;
import testing.tudor.utils.GUITestAgent;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.wrapper.ControllerException;

import jade.lang.acl.ACLMessage;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.leap.LEAPCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.domain.mobility.BehaviourLoadingOntology;
import jade.domain.mobility.LoadBehaviour;

public class SenderBehavAg extends GUITestAgent {

	private static final long serialVersionUID = 3935321587434848446L;

	private Codec codec = new LEAPCodec();
	private Ontology onto = BehaviourLoadingOntology.getInstance();

	protected void setup() {
		gui = new AgentView(this);
		try {
			appendToScreen("Here I am! So this is my birth place: "
					+ getContainerController().getContainerName()
					+ ". Hmmm! Nice!");
		} catch (ControllerException e) {
			e.printStackTrace();
		}

		addBehaviour(new WakerBehaviour(this, 3000) {

			private static final long serialVersionUID = 3816512245327539925L;

			public void onWake() {
				// send a behavior loading request to agent2

				appendToScreen("Let\'s send that message!");

				try {
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

					AID dest = new AID("agent2", AID.ISLOCALNAME);

					request.addReceiver(dest);
					request.setLanguage(codec.getName());
					request.setOntology(onto.getName());

					LoadBehaviour lb = new LoadBehaviour();

					lb.setClassName("testing.tudor.loader_pc.BehaviorToLoad");

					FileInputStream str;
					str = new FileInputStream(
							"bin/testing/tudor/loader_pc/BehaviorToLoad.class");
					int length;
					length = str.available();
					byte[] fileContent = new byte[length];
					str.read(fileContent, 0, length);
					lb.setCode(fileContent);

					Action actionExpr = new Action(dest, lb);
					actionExpr.setActor(dest);
					actionExpr.setAction(lb);
					getContentManager().registerLanguage(codec);
					getContentManager().registerOntology(onto);
					getContentManager().fillContent(request, actionExpr);
					send(request);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CodecException e) {
					e.printStackTrace();
				} catch (OntologyException e) {
					e.printStackTrace();
				}

			}
		});
	}
}