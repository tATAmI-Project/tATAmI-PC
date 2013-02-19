package testing.tudor.loader_android;

import util.jadeutil.JadeInterface;


public class StartJadePlatform {

	public static void main(String[] args) {

		// starting the platform:
//		JadeInterface.runJadeWithArguments("-host localhost -platform-id ClaimPlatform -gui -icps jade.imtp.leap.http.HTTPPeer(1099)");
		JadeInterface.runJadeWithArguments("-host localhost -port 1099 -platform-id ClaimPlatform -gui");

	}
}
