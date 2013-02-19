package testing.tudor.mobility;

import util.jadeutil.JadeInterface;

public class StartMobilityTest {

	public static void main(String[] args) {

		// starting the platform:
		JadeInterface ji = new JadeInterface();
		
		ji.startPlatform(
				"ClaimPlatform", null);
		
		// Starting the containers:
		ji.startContainer("FirstContainer");
		ji
				.startContainerWithArguments("-container -container-name SecondContainer -local-host localhost -agents agent1:testing.tudor.MovingTestAgent");
		ji.startContainer("ThirdContainer");

		// Starting the agents:
		ji.addAgentToContainer("FirstContainer", "agent2",
				"testing.tudor.mobility.MovingTestAgent", null);
		ji.addAgentToContainer("ThirdContainer", "agent3",
				"testing.tudor.mobility.MovingTestAgent", null);
		ji.addAgentToContainer("FirstContainer", "agent4",
				"testing.tudor.mobility.MovingTestAgent", null);
	}

}
