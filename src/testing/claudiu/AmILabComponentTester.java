package testing.claudiu;

import tatami.amilab.AmILabComponent;
import tatami.amilab.AmILabComponent.AmILabDataType;

/**
 * Tests the functionality of {@link AmILabComponent}
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabComponentTester
{
	/**
	 * Here goes nothing.
	 * 
	 * @param args
	 *            - main arguments
	 */
	public static void main(String[] args)
	{
		// Create component.
		AmILabComponent tester = new AmILabComponent(AmILabComponent.KESTREL_LOCAL_SERVER_IP,
				AmILabComponent.KESTREL_SERVER_PORT, AmILabComponent.KESTREL_AI_MAS_QUEUE);
		
		//Push some data.
		tester.set("a");
		tester.set("b");
		tester.set("image_depth {data}");
		
		//Test "get" method.
		System.out.println("data found: " + tester.get(AmILabDataType.IMAGE_DEPTH));
	}
}
