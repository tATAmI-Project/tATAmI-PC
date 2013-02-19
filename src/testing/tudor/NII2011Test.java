package testing.tudor;

import simulation.Boot;
import testing.andrei.httpServer.TestServer;

public class NII2011Test
{
	public static void main(String[] args)
	{
		TestServer.main(null);
		Boot.main(new String[]{"scenario/nii2011/scenario.xml"});
	}
}
