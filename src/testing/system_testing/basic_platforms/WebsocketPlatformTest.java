package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

/**
 * Platform testing a ping between agents communicating through WebSockets. Remember to press Create Agents and Start.
 */
public class WebsocketPlatformTest
{
	
	/**
	 * The scenario.
	 */
	private static final String SCENARIO = "../../websocket/scenario-ping";
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		Boot.main(new String[] { BasicPlatformTest.DIR + SCENARIO + BasicPlatformTest.EXT });
	}
}
