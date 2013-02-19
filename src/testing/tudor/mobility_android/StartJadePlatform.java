package testing.tudor.mobility_android;

import util.jadeutil.JadeInterface;


public class StartJadePlatform {

	public static void main(String[] args) {

		// starting the platform:
//		JadeInterface.runJadeWithArguments("-host localhost -platform-id ClaimPlatform -gui -icps jade.imtp.leap.http.HTTPPeer(1099)");
		JadeInterface.runJadeWithArguments("-host 132.227.203.43 -port 1099 -local-host 132.227.203.43 -platform-id ClaimPlatform -gui -nomtp");

//		JadeInterface.runJadeWithArguments("-container -container-name pc -port 1099 -local-host 132.227.203.43 -local-port 1096");

	}
}
