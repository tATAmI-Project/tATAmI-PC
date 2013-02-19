package testing.tudor.mobility_android;

import util.jadeutil.JadeInterface;


public class StartMobileAgentForAndroid {

	public static void main(String[] args) {

		// starting the agent... It should be started after the platform was launched
		// and also the jade-android application, so that the android agent has already joined
		// the platform.
//		JadeInterface.runJadeWithArguments("-host localhost -gui");
//		JadeInterface.runJadeWithArguments("-container -container-name android1 -local-host localhost ");

//		long startTime = System.currentTimeMillis();
//		while((System.currentTimeMillis()-startTime)-3000<0);
		
		JadeInterface.runJadeWithArguments("-container -container-name pc1 -host localhost -port 1099 -local-host localhost "+
		"agent1:testing.tudor.mobility_android.SimpleMobileAgent()");
	}
}
