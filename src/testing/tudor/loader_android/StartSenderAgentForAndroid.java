package testing.tudor.loader_android;

import util.jadeutil.JadeInterface;


public class StartSenderAgentForAndroid {

	public static void main(String[] args) {

		// starting the agent... It should be started after the platform was launched
		// and also the jade-android application, so that the android agent has already joined
		// the platform.
		
		JadeInterface.runJadeWithArguments("-container -container-name pc -local-host localhost "+
				"agent1:testing.tudor.loader_android.SenderBehavAgForAndroid()");

	}
}
