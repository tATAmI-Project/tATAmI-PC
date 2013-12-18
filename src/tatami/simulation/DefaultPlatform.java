package tatami.simulation;

import tatami.pc.util.XML.XMLTree.XMLNode;

/**
 * THe default platform for running agents. It is a minimal platform, offering no facilities.
 * <p>
 * Loading agents on the platform will practically have no effect on the agents.
 * 
 * @author Andrei Olaru
 */
public class DefaultPlatform implements PlatformLoader
{
	
	@Override
	public String getName()
	{
		return StandardPlatformType.DEFAULT.toString();
	}
	
	@Override
	public PlatformLoader setConfig(XMLNode configuration)
	{
		// do nothing.
		return this;
	}
	
	@Override
	public boolean start()
	{
		// does nothing.
		return true;
	}
	
	@Override
	public boolean addContainer(String containerName)
	{
		// does nothing.
		return true;
	}
	
	/**
	 * The method does nothing. The agents are ready to start anyway, as they need no platform support.
	 * <p>
	 * {@link AgentManager#setPlatformLink()} is not called, as no support will be offered by the platform.
	 */
	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager)
	{
		return true;
	}
	
}
