package scenario.amilab.sclaim_app;

import java.util.Vector;

import tatami.core.agent.claim.ClaimFunctionLibrary;
import tatami.sclaim.constructs.basic.ClaimValue;

/**
 * Functions used by the S-CLAIM components.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class SclaimFunctions implements ClaimFunctionLibrary
{

	/**
	 * Determines the closest client and puts the result over the second client.
	 * 
	 * @param arguments
	 *            - list of arguments given in this order: client1, proximity1, client2, proximity2
	 * @return usually {@code true}
	 */
	public static boolean bestClient(Vector<ClaimValue> arguments)
	{
		// Extract proximities.
		Long proximity = (Long) arguments.get(1).getValue();
		Long smallestProximity = (Long) arguments.get(3).getValue();

		// Check if the smallest proximity is still the smallest.
		if (proximity.longValue() < smallestProximity.longValue())
		{
			// Extract the name of the new best client.
			String client = (String) arguments.get(0).getValue();

			// Update the values for the best client.
			arguments.set(2, new ClaimValue(client));
			arguments.set(3, new ClaimValue(proximity));
		}

		return false;
	}
}
