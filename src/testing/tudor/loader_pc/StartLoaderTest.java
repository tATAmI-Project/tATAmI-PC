package testing.tudor.loader_pc;

import util.jadeutil.JadeInterface;


public class StartLoaderTest {

	public static void main(String[] args) {

		// starting the platform:
		JadeInterface ji = new JadeInterface();

		ji.startPlatform(
				"ClaimPlatform", null);
		
		// Starting the container:
		ji.startContainer("PCContainer");

		// Starting the agents:
		ji.addAgentToContainer("PCContainer", "agent1",
				"testing.tudor.loader_pc.SenderBehavAg", null);
		ji.addAgentToContainer("PCContainer", "agent2",
				"testing.tudor.loader_pc.LoaderBehavAg", null);
		
/*		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis()<startTime+10000);
		System.out.println("Shuting down...");
		ji.shutDownPlatform();
*/	}

}
