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
	
}
