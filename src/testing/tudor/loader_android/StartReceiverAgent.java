package testing.tudor.loader_android;

import util.jadeutil.JadeInterface;


public class StartReceiverAgent {

	public static void main(String[] args) {

		// starting the agent... It should be started after the platform was launched
		// and also the jade-android application, so that the android agent has already joined
		// the platform.
		
		JadeInterface.runJadeWithArguments("-host localhost -platform-id ClaimPlatform -gui");
		JadeInterface.runJadeWithArguments("-container -container-name pc2 -local-host localhost "+
				"android:testing.tudor.loader_android.LoaderBehavAg()");

	}
}
