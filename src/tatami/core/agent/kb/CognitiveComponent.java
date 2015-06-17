package tatami.core.agent.kb;

import net.xqhs.graphs.context.ContextPattern;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.kb.simple.SimpleKB;

/** Class describing the cognitive component of an agent.
 * @author Tudor
 *
 */
public class CognitiveComponent extends AgentComponent // TODO implement to be used as ContextComponent parent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The agent's knowledge base. It cannot be changed (i.e. create a different instance)
	 * throughout the agent's lifecycle. It can only be accessed by this level of the agent, which
	 * serves knowledge-related requests to the layers above through specialized functions.
	 */
	private final KnowledgeBase	knowledgeBase	= new SimpleKB();
	
	/**
	 * Constructor with no arguments.
	 */
	public CognitiveComponent()
	{
		super(AgentComponentName.COGNITIVE_COMPONENT);
	}
	
	@SuppressWarnings("javadoc")
	public void addPattern(@SuppressWarnings("unused") ContextPattern pattern)
	{
		// TODO
	}
	
	/**
	 * Provides access to the knowledge base.
	 * 
	 * @return the knowledge base.
	 */
	public KnowledgeBase getKB()
	{
		return knowledgeBase;
	}
}
