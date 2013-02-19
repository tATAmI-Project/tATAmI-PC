package testing.tudor.jade_interface;

import util.jadeutil.JadeInterface;

public class JadeInterfaceTest {

	public static void main(String[] args) {
		JadeInterface ji;
		
//		JadeInterface.runJadeWithArguments("-host localhost -port 9999 -agents ag1:testing.tudor.utils.GUITestAgent()");
//		JadeInterface.runJadeWithArguments("-host localhost -port 9999 -gui -agents ag1:testing.tudor.utils.GUITestAgent()");
//		JadeInterface.runJadeWithArguments("-host localhost -port 9999 -gui -platform-id Claim -agents ag1:testing.tudor.utils.GUITestAgent()");
		
		ji = new JadeInterface();
		
		ji.startPlatform("ClaimPlatform","myMain");
		
		ji.addAgentToContainer("myMain", "claimAgent1", "testing.tudor.utils.GUITestAgent", null);
		
		ji.startContainer(null);
		ji.startContainer("secondContainer");
		ji.startContainer(null);
		ji.startContainer("fourthContainer");
		ji.startContainerWithArguments("-local-host localhost");
		ji.startContainerWithArguments("-container -local-host localhost");
		ji.startContainerWithArguments("-container -local-host localhost -container-name seventhContainer");
		
		ji.addAgentToContainer("seventhContainer", "7thCAg1", "testing.tudor.utils.GUITestAgent", null);
		
		System.out.println(ji.getMainContainerName());
	}

}
