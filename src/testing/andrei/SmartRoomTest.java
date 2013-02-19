package testing.andrei;

import tatami.simulation.Boot;
import testing.andrei.httpServer.TestServer;

public class SmartRoomTest
{
	public static void main(String[] args)
	{
		TestServer.main(null);
		Boot.main(new String[]{"scenario/SmartRoomStep2/Step2scenario.xml"});
	}
}
