package testing.tudor.mobility_android;

import util.jadeutil.JadeInterface;


public class StartMovingAgent {

	public static void main(String[] args) {

		// starting the agent... It should be started after the platform was launched
		// and also the jade-android application, so that the android agent has already joined
		// the platform.
//		JadeInterface.runJadeWithArguments("-host localhost -gui");
//		JadeInterface.runJadeWithArguments("-container -container-name android -port 1099 -local-host localhost");
		
//		long startTime = System.currentTimeMillis();
//		while((System.currentTimeMillis()-startTime)-3000<0);

//		JadeInterface.runJadeWithArguments("-container -container-name pc -host 192.168.1.2 -port 1099 -local-host 192.168.1.2 -local-port 1099 -icps jade.imtp.leap.http.HTTPPeer -proto http "+
//		"agent1:testing.tudor.mobility_android.ContinuousMovingAgent();guiAg:testing.tudor.utils.GUITestAgent()");

		JadeInterface.runJadeWithArguments("-container -container-name pc -host localhost -port 1099 -local-host 132.227.203.43 -local-port 1097 "+
		"agent1:testing.tudor.mobility_android.ContinuousMovingAgent();guiAg:testing.tudor.utils.GUITestAgent()");
}
}
