package scenario.amilab;

import tatami.amilab.AmILabComponent;
import tatami.core.agent.AgentEvent;

/**
 * Tester for {@link AmILabComponent}.
 * 
 * @author Claudiu-Mihai Toma
 */
public class FeederConsumerComponent extends AmILabComponent
{

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 7753773976224362270L;
	
	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);
		
		set("message_1");
		set("message_2");
		set("message_3");
		
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		String message;
		do{
			message = get();
			System.out.println(message);
		}while(message!=null);
	}
}
