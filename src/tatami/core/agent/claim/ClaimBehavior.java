/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent.claim;

import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import sun.java2d.pipe.SpanShapeRenderer.Simple;
import tatami.sclaim.constructs.basic.ClaimBehaviorDefinition;
import tatami.sclaim.constructs.basic.ClaimBehaviorType;
import tatami.sclaim.constructs.basic.ClaimCondition;
import tatami.sclaim.constructs.basic.ClaimConstruct;
import tatami.sclaim.constructs.basic.ClaimConstructType;
import tatami.sclaim.constructs.basic.ClaimForAllK;
import tatami.sclaim.constructs.basic.ClaimFunctionCall;
import tatami.sclaim.constructs.basic.ClaimFunctionType;
import tatami.sclaim.constructs.basic.ClaimIf;
import tatami.sclaim.constructs.basic.ClaimStructure;
import tatami.sclaim.constructs.basic.ClaimValue;
import tatami.sclaim.constructs.basic.ClaimVariable;
import tatami.sclaim.constructs.basic.ClaimWhile;
import tatami.core.agent.kb.KnowledgeBase;
import tatami.core.agent.kb.KnowledgeBase.KnowledgeDescription;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.visualization.AgentGui;
import net.xqhs.graphs.context.ContextGraph;
import net.xqhs.graphs.context.ContextPattern;
import net.xqhs.graphs.graph.Graph;
import net.xqhs.graphs.graph.Node;
import net.xqhs.graphs.graph.SimpleGraph;
import net.xqhs.graphs.graph.SimpleNode;
import net.xqhs.graphs.matcher.Match;
import net.xqhs.graphs.pattern.GraphPattern;
import net.xqhs.graphs.pattern.NodeP;
import net.xqhs.graphs.representation.text.TextGraphRepresentation;
import net.xqhs.graphs.util.ContentHolder;
import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.visualization.AgentGui.InputListener;

/**
 * 
 * There is one {@link ClaimBehavior} instance for each behavior in a {@link ClaimComponent}.
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 * 
 */
public class ClaimBehavior implements InputListener
{
	private boolean		finished;			// used to verify if the behavior is finished
	private long			wakeUpAtTime;		// used by the primitive wait
											
	private int			currentStatement;	// the current statement being processed
	private int		numberOfStatements; // the number of statements of the behavior
											
	private boolean		initialized;		// tells if this behavior was initialized
	private ClaimComponent	myAgent;			// instance of ClaimComponent
	
	private boolean 		isReceive;			// true when the currentStatement is a receive statement
	
	// used for if branches detection TODO emma modify
	private int			sizeIfStatement;			
	private int			pozIfStatement;
	
	/**
	 * TODO
	 * Behavior for agent moving.
	 * 
	 * <p>
	 * Wait for agent to find location of destination. Once agent has found the location, it'll move to this location.
	 * 
	 * see <code>handleIn()</code>
	 
	private class MoveToDest extends WakerBehaviour
	{
		private static final long	serialVersionUID	= -5996155613563560744L;
		private static final long	findLocTime			= 3000;
		
		public MoveToDest(Agent agent)
		{
			super(agent, findLocTime);
		}
		
		@Override
		public void onWake()
		{
			Location loc = (Location) getDataStore().get(ClaimOntology.LOC_RTN);
			
			((VisualizableComponent) this.myAgent).getLog().info("Move to " + loc);
			((ClaimComponent) this.myAgent).doMove(loc);
		}
	}
	*/
	
	private static final long					serialVersionUID	= 1212301036264628711L;
	
	protected 	ClaimBehaviorDefinition				cbd;
	private 	SymbolTable	    st;
	protected  transient Logger					log					= null;
	
	/**
	 * for WS reply support; the last message that was received and validated by the behavior;
	 */
	 protected ACLMessage							lastMessage			= null;
	/**
	 *  for WS support (needed to reply)
	 */
	 protected Action								lastReceivedAction	= null;
	
	/**
	 * Contains the queues of events for the input(s); the events are registered by <code>receiveInput</code>
	 */
	protected Map<String, Queue<Vector<Object>>>	inputQueues			= null;
	
	public ClaimBehavior(ClaimBehaviorDefinition behaviorDefinition, SymbolTable agentSt, ClaimComponent myAgent)
	{	
		this.cbd = behaviorDefinition;
		this.myAgent = myAgent;
		st = new SymbolTable(agentSt);
		inputQueues = new HashMap<String, Queue<Vector<Object>>>();
		finished = false;
		wakeUpAtTime = -1;
		numberOfStatements = cbd.getStatements().size();
		currentStatement = 0; //numberOfStatements;
		initialized = false;
		isReceive = false;
		log = myAgent.getLog();
		sizeIfStatement = 0;
		pozIfStatement = 0;
	}
	
	public void actionOnReceive(String source, String content) {
		if (!handleReceive(((ClaimFunctionCall) cbd.getStatements().get(currentStatement++)).getArguments(), source, content))
			return;
		action();
	}
	
	public void action()
	{
		if (sizeIfStatement != 0)
			pozIfStatement++;
		
		if (pozIfStatement > sizeIfStatement) {
			Vector<ClaimConstruct> struct = cbd.getStatements();
			
			int startPoint = currentStatement - 1;
			while (startPoint > currentStatement - sizeIfStatement)
				struct.remove(startPoint--);
			sizeIfStatement = 0;
			pozIfStatement = 0;
		}
		
		if(currentStatement != numberOfStatements)
		{
			if(!handleStatement(cbd.getStatements().get(currentStatement++)))
				currentStatement = 0;
			else
				if (isReceive) {
					isReceive = false;
					return;
				}
				action();
		}
		else
		{
			currentStatement = 0;
			if(st.getLog() == null)
				st.setLog(log);
			
			st.clearSymbolTable(); // reinitialize symbol table
			currentStatement = 0;
			
			if(!initialized)
				initialized = true;
			else
				switch(cbd.getBehaviorType())
				{
				case INITIAL:
					finished = true;
					break;
				case REACTIVE:
					break;
				case CYCLIC:
					break;
				case PROACTIVE:
					break;
				}
		}
		
	}
	
	public boolean done()
	{	
		return finished;
	}
	
	public ClaimBehaviorType getBehaviorType()
	{
		return cbd.getBehaviorType();
	}
	
	public ClaimBehaviorDefinition getBehaviorDefinition()
	{
		return cbd;
	}
	
	public ClaimConstruct getCurrentStatement()
	{
		return cbd.getStatements().get(currentStatement);
	}
	
	@Override
	public void receiveInput(String componentName, Vector<Object> arguments)
	{
		inputQueues.get(componentName).offer(arguments);
		// TODO emma
		// this.restart();
	}
	
	public void resetGui()
	{
		inputQueues.clear();
		// TODO emma
		// this.restart();
	}
	
	/**
	 * Gets a reference to the knowledge base of the agent from the containing {@link ClaimComponent} instance.
	 * 
	 * @return the knowledge base.
	 */
	public Graph getKBase()
	{
		return myAgent.getKBase();
	}
	
	/**
	 * Handle the statements of the behavior (execute a behavior)
	 * 
	 * @param statements
	 *            : vector of statements
	 */
	protected boolean handleStatement(ClaimConstruct statement)
	{
		switch(statement.getType())
		{
		// type function_call
		case FUNCTION_CALL:
			switch(((ClaimFunctionCall) statement).getFunctionType())
			{
			case RECEIVE:
			case INPUT:
				return handleCall((ClaimFunctionCall) statement);
			default:
				handleCall((ClaimFunctionCall) statement);
				return true;
			}
			// Type Condition
		case CONDITION:
			return handleCall(((ClaimCondition) statement).getCondition());
			
			// Type IF
		case IF:
			boolean condition = handleCall(((ClaimIf) statement).getCondition());
			if(condition)
			{
				log.trace("if condition satisfied " + currentStatement);
				
				// after "then" there's a true branch and a false branch
				Vector<ClaimConstruct> trueBranch = ((ClaimIf) statement).getTrueBranch();
				
				Vector<ClaimConstruct> struct = cbd.getStatements();
				struct.addAll(currentStatement, trueBranch);
				numberOfStatements += trueBranch.size();
				cbd.setStatements(struct);
				sizeIfStatement = trueBranch.size();
				return true;
				
			}
			else
			{
				log.trace("if condition not satisfied");
				Vector<ClaimConstruct> falseBranch = ((ClaimIf) statement).getFalseBranch();
				
				if(falseBranch != null) {
					Vector<ClaimConstruct> struct = cbd.getStatements();
					struct.addAll(currentStatement + 1, falseBranch);
					cbd.setStatements(struct);
					sizeIfStatement = falseBranch.size();
				}
					
				/*	for(ClaimConstruct falseBranchStatement : falseBranch)
						handleStatement(falseBranchStatement);
				*/
			}
			break;
		
		// Type ForAllK
		case FORALLK:
			// structure : struct knowledge v1 v2 ... vn
			// TODO emma
			// handleForAllK((ClaimForAllK) statement, getKBase());
			break;
		
		// Type While
		case WHILE:
			handleWhile((ClaimWhile) statement);
			break;
		}
		return true;
	}
	
	/**
	 * Handle a normal statement
	 * 
	 * @param function
	 *            : Claim construct / statement
	 * @return true if the behavior should continue (only deactivated by Condition, Input or Receive constructs) or if
	 *         the statement has returned true
	 */
	protected boolean handleCall(ClaimFunctionCall function)
	{
		// arguments of function
		Vector<ClaimConstruct> args = function.getArguments();
		
		/*************** Handle each function's type *****************/
		switch(function.getFunctionType())
		{
		case SEND:
			handleSend(args);
			return true;
		case RECEIVE:
			// this case will be treated when the coresponding message will be received
			currentStatement--;
			isReceive = true;
			return true;
		case IN:
			handleIn(args);
			return true;
		case OUT:
		case ACID:
			return handleAcid();
		case OPEN:
			return handleOpen(args);
		case NEW:
			return handleNew(args);
		case ADDK:
			System.out.println("de aici eeeeeeeeeeee");
		case REMOVEK:
		case READK:
			return handleKnowledgeManagement(function.getFunctionType(), args);
		case INPUT:
			return handleInput(args);
		case OUTPUT:
			handleOutput(args);
			return true;
		case WAIT:
			handleWait(args);
			return true;
		case JAVA:
			return handleJavaCall(function.getFunctionName(), args);
		}
		return true; // should be unreachable?
	}
	
	/**
	 * Creates a new agent, based on the given arguments
	 * 
	 * @param - args the arguments of the call to "new" construct. The first two elements of the vector are the name and
	 *        the class name of the agent. The rests are arguments that the created agent will process after creation
	 * @return - whether the creation was successful or not
	 */
	private boolean handleNew(Vector<ClaimConstruct> args)
	{
		/* ClaimValue agentName = (ClaimValue) args.get(0);
		ClaimValue agentClassName = (ClaimValue)args.get(1);
		
		/*try
		{
			((ParametricComponent) this.myAgent).getContainerController().createNewAgent((String) agentName.getValue(),
					"core.claim.ClaimAgent", args.subList(1, args.size()).toArray());
		} catch(StaleProxyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}*/
		
		return true;
	}
	
	/**
	 * This deletes the agent from the inside. It is a basic implementation of the S-CLAIM acid primitive, that has to
	 * be extended in the future
	 * 
	 * @return - always true
	 */
	private boolean handleAcid()
	{
		log.trace("Acid command received. I will delete myself!");
		//this.myAgent.getParametric().doDelete();
		this.finished = true;
		
		return true;
	}
	
	/**
	 * This deletes the agent with the name specified as the first element of the vector received as argument, from the
	 * outside.
	 * 
	 * @param - args the arguments of the call to "open" construct. The first element of the vector is the name of the
	 *        agent.
	 * @return - whether the deletion was successful or not
	 */
	
	private boolean handleOpen(Vector<ClaimConstruct> args)
	{
		ClaimValue agentName = (ClaimValue) args.get(0);
		
		/*try
		{
			((ParametricComponent) this.myAgent).getContainerController().getAgent((String) agentName.getValue()).kill();
		} catch(StaleProxyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch(ControllerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}*/
		
		return true;
	}
	
	/**
	 * The send construct.
	 * 
	 * <p>
	 * Variants:
	 * 
	 * <p>
	 * <code>(send agent-id (struct message field-1 field-2 ...))</code> - normal send
	 * 
	 * <p>
	 * <code>(send (struct message field-1 field-2 ...))</code> - send a reply to the received message; web service
	 * invocation
	 * 
	 * <p>
	 * TODO: - web service access
	 * 
	 * @param args
	 */
	protected void handleSend(Vector<ClaimConstruct> args)
	{
		// parse elements of the message from argument of function call
		// the first argument is the receiver
		String receiver = "";
		ACLMessage reply = null;
		int parametersAdjust = 0; // offset argument number if there is no receiver field
		ClaimConstructType receiverConstructType = args.get(0).getType();
		
		boolean replyAgentMode = false; // if true, the reply will be sent to an agent
		boolean replyServiceMode = false; // if true, the reply will be sent as response to a web service invocation
		boolean webServiceInvocationMode = false; // if true, the send is a web service invocation
		boolean simpleWebService = false; // see WSAgent
		boolean expectServiceReturn = false; // if true, wait for a reply (blocking call)
		
		// decide upon the receiver(s) of the message
		switch(receiverConstructType)
		{
		case VARIABLE:
			receiver = (String) getVariableValue(((ClaimVariable) args.get(0))).getValue();
			break;
		case VALUE:
			receiver = (String) ((ClaimValue) args.get(0)).getValue();
			break;
		case STRUCTURE:
			// there is no receiver information; should reply to the original sender
			// !! implemented only to make web service exposure of behaviors work; untested for agent replies !!
			log.trace("replying to message " + lastMessage);
			reply = lastMessage.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			reply.addUserDefinedParameter(ACLMessage.IGNORE_FAILURE, "true");
			parametersAdjust = 1;
			if(lastReceivedAction == null)
				replyAgentMode = true;
			else
				replyServiceMode = true;
			break;
		default:
			break;
		}
		
		/* if((args.size() > 2) && (myAgent instanceof WebserviceComponent))
		{
			webServiceInvocationMode = true;
			if(receiver.equals("null"))// FIXME null value should be handled specially
				// use simple web service access
				simpleWebService = true;
		}*/
		
		if(args.size() > 3)
			// FIXME check if in WS mode
			expectServiceReturn = true;
		
		if(receiver != null)
		{
			// second argument is a structure that represents all the message's fields
			ClaimStructure message = null;
			String simpleMessage = null;
			if(!simpleWebService)
				message = st.bindStructure((ClaimStructure) args.get(1 - parametersAdjust));
			else
			{
				if(args.get(1 - parametersAdjust).getType() == ClaimConstructType.VARIABLE)
					simpleMessage = (String) (this.st.get((ClaimVariable) args
							.get(1 - parametersAdjust))).getValue();
				else
					simpleMessage = (String) ((ClaimValue) args.get(1 - parametersAdjust)).getValue();
			}
			
			if(webServiceInvocationMode)
			{   // Web service invocation
				if(simpleWebService)
				{
					String serviceAddress = (String) ((ClaimValue) args.get(2)).getValue();
					log.info("accessing service at [" + serviceAddress + "]...");
					String result = this.myAgent.getWebService().doSimpleAccess(serviceAddress, simpleMessage);
					
					if(expectServiceReturn) // expect a result from the web service
					{ // integrate result
						log.trace("received result " + result + "; binding to "
								+ ((ClaimVariable) args.get(3)).getName());
						this.st.put((ClaimVariable) args.get(3), new ClaimValue(result));
					}
					
				}
				else
				{
					String serviceName = receiver;
					String serviceAddress = (String) ((ClaimValue) args.get(2)).getValue();
					log.info("accessing service [" + serviceName + "] at [" + serviceAddress + "]...");
					@SuppressWarnings("null")
					// it should be ok according to the logic above
					String result = this.myAgent.getWebService().doAccess(serviceAddress, serviceName, message.toString());
					ClaimStructure received = ClaimStructure.parseString(result, log);
					log.trace("received result " + received);
					
					if(expectServiceReturn) // expect a result from the web service
					{ // integrate result
						ClaimStructure pattern = (ClaimStructure) args.get(3);
						Vector<ClaimConstruct> newArgs = pattern.getFields();
						readMessage(st.bindStructure(received), newArgs);
					}
				}
			}
			else
			{
				@SuppressWarnings("null")
				// it should be ok according to the logic above
				Vector<ClaimConstruct> fields = message.getFields();
				
				// FIXME : first argument doesn't necessary be protocol of the message
				String protocol = (String) ((ClaimValue) fields.get(1)).getValue();
				
				// build the message for sending by JADE
				if(!replyAgentMode && !replyServiceMode)
					reply = new ClaimMessage(receiver, protocol);
				if(replyAgentMode && reply != null)
					reply.setProtocol(protocol);
				
				if(replyServiceMode)
				{ // WS return result 
					log.trace("sending WS result [" + message.toString() + "]");
					ContentElement ce = new Result(lastReceivedAction, message.toString());
					/*try
					{
						myAgent.getContentManager().fillContent(reply, ce);
					} catch(CodecException e)
					{
						e.printStackTrace();
					} catch(OntologyException e)
					{
						e.printStackTrace();
					}*/
				}
				else
					try
					{
						if(reply != null)
							reply.setContentObject(message);
					} catch(IOException e)
					{
						e.printStackTrace();
					}
				
				if (myAgent.getMessaging() != null)
				{
					//	receiver = MessagingComponent.makePathHelper(receiver, Vocabulary.VISUALIZATION.toString(),
					//					Vocabulary.VISUALIZATION_MONITOR.toString());
					myAgent.getMessaging().sendMessage(receiver, myAgent.getParametric().parVal(AgentParameterName.AGENT_NAME).toString(), message.toString());
				}
				log.info("sent " + message.toString());
			}
		}
	}
	
	/**
	 * Handle the <code>receive</code> construct.
	 * 
	 * <p>
	 * Format: <code>(receive field-1 field-2 ...)</code>
	 * 
	 * <p>
	 * Fields that are values or instantiated variables are used to match the received message; uninstantiated variables
	 * will be bound to the values in the message
	 * 
	 * @param args
	 *            : the arguments of the construct - the fields
	 * @return <code>true</code> if the received message matches the pattern (the behavior should continue);
	 *         <code>false</code> otherwise
	 * @throws InterruptedException 
	 */
	protected boolean handleReceive(Vector<ClaimConstruct> args, String source, String content)
	{
		int argsSize = args.size();
		boolean ret = true;
		
		/* TODO handle potential web service access to the agent 
		 MessageTemplate WStemplate = MessageTemplate.MatchOntology(WebServiceOntology.NAME);
		
		//msg = this.myAgent.getMessaging().receive(WStemplate);
		if(msg != null)
		{ 	// incoming web service access
			// check if right message
			
			log.trace("checking received ws access message [" + msg + "]");
			String messageContent = content;
			messageContent = (String)myAgent.getContentManager().extractContent(msg);
			ContentElement ce = null;
			try
			{
				ce = myAgent.getContentManager().extractContent(msg);
			} catch(UngroundedException e)
			{
				e.printStackTrace();
			} catch(CodecException e)
			{
				e.printStackTrace();
			} catch(OntologyException e)
			{
				e.printStackTrace();
			}
			if(ce != null)
			{
				log.trace("received content [" + ce + "]");
				ReceiveOperation op = (ReceiveOperation) ((Action) ce).getAction();
				messageContent = op.getOperationArgument();
				log.trace("received content detail [" + messageContent + "]");
				ClaimStructure messageStruct = ClaimStructure.parseString(messageContent, log);
				log.trace("received message [" + messageStruct + "]");
				
				String messageProtocol = (String) ((ClaimValue) messageStruct.getFields().get(1)).getValue();
				log.trace("received protocol [" + messageProtocol + "]; waiting for [" + protocol + "]");
				if(protocol.equals(messageProtocol))
				{
					// it's ok, the message should be received by this RECEIVE construct
					lastMessage = msg;
					lastReceivedAction = (Action) ce;
					received = messageStruct;
					log.info("received / ws access " + ClaimMessage.printMessage(msg));
				}
				else
				{   // put it back; discontinue behavior
					// myAgent.putBack(msg);
					ret = false;
					log.trace("ws access does not match protocol");
				}
			}
		}
		else */
		// normal JADE message
				
		ClaimStructure received = ClaimStructure.parseString(content);
		
		if(received != null)
		{
			Vector<ClaimConstruct> newArgs = ((ClaimStructure) args.get(argsSize - 1)).getFields();
			ClaimStructure cl = st.bindStructure(received);
			
			if(!readMessage(st.bindStructure(received), newArgs))
			{   // the message does not match the pattern
				log.trace("message not matching pattern [" + content + "]");
				ret = false;
			}
			else
			{
				log.trace("message received: [" + content + "]");
				if(argsSize == 2)
					st.put((ClaimVariable) args.get(0), new ClaimValue(source));
			}
		}
		else
			ret = false;
		
		return ret;
	}
	
	/**
	 * There are two mechanisms for handling input:
	 * 
	 * <p>
	 * For <i>active</i> inputs, the input will call the <code>receiveInput()</code> method of the behavior. The
	 * arguments will be stored in the input queue of the input, and in this method will be copied to the elements in
	 * the construct.
	 * 
	 * <p>
	 * For <i>passive</i> inputs, the behavior will use <code>getInput</code> to get the arguments of the input, which
	 * will be copied to the elements in the construct.
	 * 
	 * <p>
	 * The difference between active and passive inputs is done by looking if there are any events in the queue of the
	 * input. If there are, the input is active and is not queried for arguments. Otherwise, the input is passive.
	 * 
	 * <p>
	 * The behavior will only be immediately activated by an active input (that restarts the behavior in
	 * <code>receiveInput()</code>.
	 */
	protected boolean handleInput(Vector<ClaimConstruct> args)
	{
		AgentGui gui = myAgent.getVisualizable().getGUI();
		
		// FIXME: variables should be supported
		String inputComponent = ((ClaimValue) args.get(0)).toString();
		
		// FIXME: variables should be supported
		@SuppressWarnings("unused")
		ClaimValue inputComponentType = (ClaimValue) args.get(1); // unused anymore, for now.
		
		if(!inputQueues.containsKey(inputComponent) && gui != null)
		{
			gui.connectInput(inputComponent, this);
			inputQueues.put(inputComponent, new LinkedList<Vector<Object>>());
		}
		
		Vector<Object> inputArgs = null;
		if(inputQueues.get(inputComponent).isEmpty() && gui != null)
			// input was not activated
			inputArgs = gui.getinput(inputComponent);
		else
			// input has been activated; get an event from the queue
			inputArgs = inputQueues.get(inputComponent).poll();
		if(inputArgs == null)
			return false;
		// match arguments
		Vector<ClaimConstruct> args2 = new Vector<ClaimConstruct>(args);
		args2.remove(0);
		args2.remove(0);
		return readValues(objects2values(inputArgs), args2, 0, true, false);
	}
	
	protected boolean handleOutput(Vector<ClaimConstruct> args)
	{
		ClaimValue outputComponent = (ClaimValue) args.get(0);
		Vector<ClaimValue> outputV = new Vector<ClaimValue>();
		
		Iterator<ClaimConstruct> itArg = args.iterator();
		itArg.next();
		while(itArg.hasNext())
		{
			ClaimConstruct arg = itArg.next();
			if(arg instanceof ClaimValue)
				outputV.add((ClaimValue) arg);
			else
				outputV.add(this.st.get((ClaimVariable) arg));
		}
		
		AgentGui myAgentGUI = myAgent.getVisualizable().getGUI();
		Vector<Object> outV = new Vector<Object>();
		for(ClaimValue output : outputV)
			outV.add(output.getValue());
		myAgentGUI.doOutput((String) outputComponent.getValue(), outV);
		
		log.trace("The output \"" + outputV + "\" was written on " + (String) outputComponent.getValue());
		return true;
	}
	
	protected boolean handleIn(Vector<ClaimConstruct> args)
	{
		/*
		// reading the argument of the function
		ClaimConstruct goIn = args.get(0);
		
		// reading the value of the argument
		ClaimValue value = null;
		switch(goIn.getType())
		{
		case VARIABLE:
			value = getVariableValue((ClaimVariable) goIn);
			break;
		case VALUE:
			value = (ClaimValue) goIn;
			break;
		default:
			log.error("Unsupported argument type for the primitive \"in\".");
			break;
		}
		
		// changing the value of the "parent" parameter of the agent:
		st.put(new ClaimVariable("parent", true), value);
		
		// after IN there's always one variable of destination
		// check if this variable is bound or not
		
		if(value != null)
		{
			// name of agent destination
			String destination = (String) value.getValue();
			// find Container of destination
			SequentialBehaviour sb = new SequentialBehaviour();
			Behaviour b = new AskForLocation((ClaimComponent) this.myAgent, destination);
			b.setDataStore(sb.getDataStore());
			sb.addSubBehaviour(b);
			
			b = new AskForBeingChildBehaviour((ClaimComponent) this.myAgent, destination);
			sb.addSubBehaviour(b);
			
			b = new MoveToDest((ClaimComponent) this.myAgent);
			b.setDataStore(sb.getDataStore());
			sb.addSubBehaviour(b);
			
			((ClaimComponent) this.myAgent).addBehaviour(sb);
		}*/
		return true;
	}
	
	protected boolean handleWait(Vector<ClaimConstruct> args)
	{
		// after wait there's always a constant
		ClaimValue waitTimeConstant = (ClaimValue) args.get(0);
		
		long timeout = Integer.parseInt((String) waitTimeConstant.getValue());
		wakeUpAtTime = System.currentTimeMillis() + timeout;
		
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	protected boolean handleJavaCall(String functionName, Vector<ClaimConstruct> args)
	{
		Method method = null;
		for(Class<?> clazz : this.cbd.getMyAgent().getCodeAttachments())
			try
			{
				method = clazz.getDeclaredMethod(functionName, Vector.class);
				break;
			} catch(SecurityException e)
			{ // method not accessible here; carry on;
			} catch(NoSuchMethodException e)
			{ // method not found here; carry on;
			}
		if(method == null)
		{
			log.error("function [" + functionName + "] not found in code attachments");
			return false;
		}
		
		boolean returnValue = false;
		Vector<ClaimConstruct> arguments = new Vector<ClaimConstruct>(flattenConstructs(args, 0,
				KeepVariables.KEEP_NONE));
		
		log.trace("invoking code attachment function [" + functionName + "] with arguments: " + arguments);
		try
		{
			returnValue = ((Boolean) method.invoke(null, arguments)).booleanValue();
			readValues(arguments, args, 0, false, false);
			
		} catch(Exception e)
		{
			log.error("function [" + functionName + "] invocation failed: " + e);
			e.printStackTrace(); // FIXME: put this into the log
		}
		
		return returnValue;
	}
	
	protected boolean handleKnowledgeManagement(ClaimFunctionType construct, Vector<ClaimConstruct> args)
	{
		Graph graph = getKBase();
		
		String knowledge = constructs2OneStrings(((ClaimStructure) args.get(0)).getFields(), 1);
		
		switch(construct)
		{
		case ADDK:
		// function "add knowledge"
		{
			// after addK there's always knowledge (pattern condition will be after condition)  
			// TODO emma move this to condition
			if (knowledge.substring(0, knowledge.indexOf(" ")).equals("pattern"))
			{
				knowledge = knowledge.substring(knowledge.indexOf(" ") + 1);
				ContextPattern CP = new ContextPattern();
				myAgent.getCognitive().addPattern(
						(ContextPattern) new TextGraphRepresentation(CP).readRepresentation(new ContentHolder<String>(knowledge)));
			}
			else {
				myAgent.getCognitive().add(new TextGraphRepresentation(
						new SimpleGraph()).readRepresentation(new ContentHolder<String>(knowledge)));
			}
			log.info(" adds new knowledge " + knowledge + " in behavior " + this.cbd.getName());
			return true;
		}
		
		case READK:
		// function "check if agent already has the knowledge"
		// If yes : return true, also put the value of variable in knowledge pattern into the symbol table
		{
			// after readK there's always a pattern
			@SuppressWarnings("unused")
			ClaimStructure knowledgeStruct = (ClaimStructure) args.get(0); // struct knowledge <kl type> <kl fields>
			
			/* replace all ClaimVariables (represented as ?<name>) with an index */
			HashMap<Integer, ClaimVariable> intToVariable = new HashMap<Integer, ClaimVariable>();
			knowledge = constructsAndMaps(knowledgeStruct.getFields(), intToVariable, 1);
			
			GraphPattern CP = new GraphPattern();
			TextGraphRepresentation repr = new TextGraphRepresentation(CP);
			repr.readRepresentation(new ContentHolder<String>(knowledge));
			Match m = myAgent.getCognitive().read(CP);
			
			if (m == null)
				return false;
			
			for (Node node : CP.getNodes())
				if (node instanceof NodeP) {
					Node rez = m.getMatchedGraphNode(node); 
					st.put(intToVariable.get(((NodeP) node).genericIndex()), 
							new ClaimValue(rez.getLabel()));
				}
					
		}
		
		case REMOVEK:
		// function "remove knowledge" TODO remove patterns
		{
			/*myAgent.getCognitive().remove(new TextGraphRepresentation(
					new SimpleGraph()).readRepresentation(new ContentHolder<String>(knowledge)));
			*/
			return true;
		}
		
		default:
			// should not be here
			return true; // unreachable code
		}
		
		//return true;
	}
	
	/**
	 * Read all records of knowledge in the knowledge base that match the pattern. Used in forAllK function.
	 * <p>
	 * - First, check if knowledge type matches.
	 * <p>
	 * - If matches, bind variable in knowledge pattern that haven't been bound by the value of knowledge in the
	 * knowledge base that has the same type.
	 * <p>
	 * - If there's any field that doesn't match, the pattern doesn't match.
	 * <p>
	 * Execute the statements inside the forAllK after binding all variables
	 * 
	 * @param construct
	 *            : the construct of forAllK - Structure of knowledge :
	 *            <p>
	 *            "forAllK((struct knowledge &lt;knowledge Type&gt; &lt;knowledge field 1..n &gt;) &lt;statements&gt; )"
	 * @param knowledge
	 *            : the knowledge base
	 */
	protected void handleForAllK(ClaimForAllK construct, KnowledgeBase knowledgeBase)
	{
		ClaimStructure klStructure = construct.getStructure();
		Vector<ClaimConstruct> statements = construct.getStatements();
		
		// map of pairs <Variable, value> used to bind fields in knowledge pattern that haven't been bound (variable)
		HashMap<ClaimVariable, ClaimValue> map = new HashMap<ClaimVariable, ClaimValue>();
		boolean matches = false;
		
		Vector<ClaimConstruct> klStructureFields = klStructure.getFields();
		Collection<KnowledgeDescription> knowledge = knowledgeBase.getAll(structure2Knowledge(klStructure));
		// type
		String knowledgeType = (String) ((ClaimValue) klStructureFields.get(1)).getValue();
		// fields
		for(KnowledgeDescription kd : knowledge)
		{
			SimpleKnowledge klToMatch = (SimpleKnowledge) kd; // FIXME: support other types
			// if found knowledge that has the same type
			if(knowledgeType.equals(klToMatch.getKnowledgeType())) // FIXME: this is currently superfluous
			{
				matches = true;
				// map.clear(); // not needed, in order to be able to make the verification for previously bound
				// variables in this forAllK, by verifying which variables were unbound at the beginning of the
				// execution
				for(int j = 2; j < klStructureFields.size(); j++)
				{
					ClaimVariable fieldVariable = ((ClaimVariable) klStructureFields.get(j));
					ClaimValue value = getVariableValue(fieldVariable);
					// check if fieldVariable is in the symbol table or not
					// if variable is bound (value != null) and doesn't match with the knowledge, return false
					if(value != null)
					{
						if(!map.containsKey(fieldVariable))
						{
							if(!((String) value.getValue()).equals(klToMatch.getSimpleKnowledge().get(j - 2)))
							{
								matches = false;
								continue;
							}
						}
						else
						{
							map.put(fieldVariable, new ClaimValue(klToMatch.getSimpleKnowledge().get(j - 2)));
						}
					}
					// if not, bind with value in the knowledge base
					else
					{
						map.put(fieldVariable, new ClaimValue(klToMatch.getSimpleKnowledge().get(j - 2)));
					}
				}
				
				// if the pattern matches, put into behavior's symbol table all pairs <variable, value> that has been
				// found
				if(matches)
				{
					Set<Entry<ClaimVariable, ClaimValue>> set = map.entrySet();
					for(Iterator<Entry<ClaimVariable, ClaimValue>> it = set.iterator(); it.hasNext();)
					{
						Map.Entry<ClaimVariable, ClaimValue> entry = it.next();
						st.put(entry.getKey(), entry.getValue());
					}
					for(ClaimConstruct statement : statements)
						handleStatement(statement);
					/*
					 * // reset all variables bound in this procedure to null for(Iterator<Entry<ClaimVariable,
					 * ClaimValue>> it = set.iterator(); it.hasNext();) { Map.Entry<ClaimVariable, ClaimValue> entry =
					 * it.next(); cbd.getSymbolTable().put(entry.getKey(), null); }
					 */}
			}
		}
	}
	
	/**
	 * Initializes a while loop based on the condition contained in the variable {@code construct}
	 * 
	 * @param construct
	 *            : the intermediate code representation of while
	 */
	protected void handleWhile(ClaimWhile construct)
	{
		Vector<ClaimConstruct> statements = construct.getStatements();
		
		while(handleCall(construct.getCondition()))
		{
			for(ClaimConstruct statement : statements)
				handleStatement(statement);
		}
	}
	
	/**
	 * Read a received message. Content of the message is in the form of a claim structure
	 * 
	 * @param messageStructure
	 *            : structure received in message : (struct message f1 f2 f3 ... fn)
	 * @param args
	 *            : vector of {@link ClaimConstruct} (defined in adf2) : f1 f2 f3 ... fn
	 */
	protected boolean readMessage(ClaimStructure messageStructure, Vector<ClaimConstruct> args)
	{
		log.trace("read from [" + messageStructure + "] into [" + args + "]");
		
		return readValues(messageStructure.getFields(), args, 0, false, true);
	}
	
	/**
	 * The function has two goals: match the source and destination structures based on their structure and values; and
	 * instantiate variables in the destination structure based on the values in the source structure.
	 * 
	 * <p>
	 * If the destination field is a value, or an instantiated variable, it is checked if it matches the value of the
	 * source.
	 * 
	 * <p>
	 * If the destination field is an uninstantiated variable, it will be instanced to the value of the corresponding
	 * source field.
	 * 
	 * <p>
	 * If both destination and source field are structures, the function is called recursively.
	 * 
	 * @param sourceStructure
	 * @param destinationStructure
	 * @param ignore
	 *            : number of fields at the beginning to ignore in both structures
	 * @param mandatoryMatch
	 *            : states if it is mandatory to have a match before binding the fields of the destination
	 * @param rebindAllowed
	 *            : set to <code>true</code> to allow variables to be rebound; PLEASE do this only for Java calls;
	 *            FIXME: this is only allowed for re-assignable variables, which will always be reassignable. Therefore,
	 *            this parameter should be eliminated.
	 * @return <code>true</code> if the two structures match.
	 */
	protected boolean readStructure(ClaimStructure sourceStructure, ClaimStructure destinationStructure, int ignore,
			boolean mandatoryMatch, boolean rebindAllowed)
	{
		return readValues(sourceStructure.getFields(), destinationStructure.getFields(), ignore, mandatoryMatch,
				rebindAllowed);
	}
	
	/**
	 * see <code>readStructure()</code> above.
	 */
	protected boolean readValues(Vector<ClaimConstruct> sourceConstructs, Vector<ClaimConstruct> destinationConstructs,
			int ignore, boolean mandatoryMatch, boolean rebindAllowed)
	{
		Map<ClaimVariable, ClaimValue> bindings = new HashMap<ClaimVariable, ClaimValue>(); // to bind at the end if OK
		boolean match = readValues(sourceConstructs, destinationConstructs, ignore, mandatoryMatch, rebindAllowed,
				bindings);
		if(match || !mandatoryMatch)
			for(Map.Entry<ClaimVariable, ClaimValue> entry : bindings.entrySet())
				bindVariable(entry.getKey(), entry.getValue());
		
		return match;
	}
	
	/**
	 * see the other definition of <code>readStructure()</code> for details. This version only returns the bindings to
	 * be done, does not actually do the bindings.
	 * 
	 * @param bindingsOut
	 *            : the map will be filled with the bindings to perform.
	 */
	protected boolean readStructure(ClaimStructure sourceStructure, ClaimStructure destinationStructure, int ignore,
			boolean mandatoryMatch, boolean rebindAllowed, Map<ClaimVariable, ClaimValue> bindingsOut)
	{
		return readValues(sourceStructure.getFields(), destinationStructure.getFields(), ignore, mandatoryMatch,
				rebindAllowed, bindingsOut);
	}
	
	/**
	 * see <code>readStructure()</code>
	 */
	protected boolean readValues(Vector<ClaimConstruct> sourceConstructs, Vector<ClaimConstruct> destinationConstructs,
			int ignore, boolean mandatoryMatch, boolean rebindAllowed, Map<ClaimVariable, ClaimValue> bindingsOut)
	{
		// TODO: fill this with log traces
		boolean match = true;
		
		if(sourceConstructs.size() != destinationConstructs.size())
			match = false;
		for(int i = ignore; i < Math.min(sourceConstructs.size(), destinationConstructs.size()); i++)
		{
			ClaimConstruct field = destinationConstructs.get(i); // destination
			ClaimConstruct arg = sourceConstructs.get(i); // source
			
			if(field.getType() == ClaimConstructType.VALUE)
			{   // just check match
				switch(arg.getType())
				{
				case VALUE:
					match = ((ClaimValue) field).getValue().equals(((ClaimValue) arg).getValue());
					break;
				case VARIABLE:
					match = ((ClaimValue) field).getValue().equals(getVariableValue((ClaimVariable) arg).getValue());
					break;
				default:
					match = false;
				}
			}
			else if(field.getType() == ClaimConstructType.VARIABLE)
			{
				ClaimValue fieldValue = getVariableValue((ClaimVariable) field);
				boolean assignable = ((ClaimVariable) field).isAffectable();
				switch(arg.getType())
				{
				case VALUE:
					if(fieldValue == null)
						// instantiate variable
						bindingsOut.put((ClaimVariable) field, (ClaimValue) arg);
					else
					{
						match = fieldValue.getValue().equals(((ClaimValue) arg).getValue());
						if(assignable && !match)
							bindingsOut.put((ClaimVariable) field, (ClaimValue) arg);
					}
					break;
				case VARIABLE:
					ClaimValue argValue = getVariableValue((ClaimVariable) arg);
					if(argValue == null)
					{
						// don't really know what to do // TODO
					}
					else
					{
						if(fieldValue == null)
							// instantiate variable
							bindingsOut.put((ClaimVariable) field, getVariableValue((ClaimVariable) arg));
						else
						{
							match = fieldValue.getValue().equals(((ClaimValue) arg).getValue());
							if(assignable && !match)
								bindingsOut.put((ClaimVariable) field, (ClaimValue) arg);
						}
					}
					break;
				default:
					match = false;
				}
			}
			else if(field.getType() == ClaimConstructType.STRUCTURE)
			{
				if(arg.getType() == ClaimConstructType.STRUCTURE)
					// FIXME: the bindings in the recursive call should only be performed at the last level
					readStructure((ClaimStructure) arg, (ClaimStructure) field, 0, mandatoryMatch, rebindAllowed,
							bindingsOut);
				else
					log.error("unable to match [" + arg.getType() + "] construct to a destination field");
			}
			else
				log.error("unable to use [" + field.getType() + "] construct as destination field");
		}
		
		return match;
	}
	
	/**
	 * Create a {@link SimpleKnowledge} instance from a {@link ClaimStructure}.
	 * 
	 * @param knowledge
	 *            : a structure <code>(struct knowledge knowledge-type knowledge-field-1 knowledge-field-2 ...)</code>
	 * @return : Knowledge built from Structure
	 */
	protected SimpleKnowledge structure2Knowledge(ClaimStructure knowledge)
	{
		Vector<String> fields = constructs2Strings(knowledge.getFields(), 1); // ignore 'knowledge'
		
		SimpleKnowledge newKl = new SimpleKnowledge();
		newKl.setKnowledgeType(fields.remove(0));
		newKl.setSimpleKnowledge(new LinkedList<String>(fields));
		return newKl;
	}
	
	/**
	 * Create a {@link ClaimStructure} from a {@link SimpleKnowledge} instance.
	 * 
	 * @param knowledge
	 * @return a structure with the form
	 *         <code>(struct knowledge knowledge-type knowledge-field-1 knowledge-field-2 ...)</code>
	 */
	protected ClaimStructure knowledge2Structure(SimpleKnowledge knowledge)
	{
		ClaimStructure ret = new ClaimStructure();
		/*ClaimStructure ret = new ClaimStructure(cbd);
		Vector<ClaimConstruct> fields = new Vector<ClaimConstruct>();
		fields.add(new ClaimValue("knowledge"));
		fields.add(new ClaimValue(knowledge.getKnowledgeType()));
		for(Object fieldValue : knowledge.getSimpleKnowledge())
			fields.add(new ClaimValue(fieldValue));
		ret.setFields(fields);*/
		return ret;
	}
	
	@SuppressWarnings("static-method")
	protected Vector<ClaimConstruct> objects2values(Vector<Object> args)
	{
		Vector<ClaimConstruct> ret = new Vector<ClaimConstruct>(args.size());
		for(Object arg : args)
			ret.add(new ClaimValue(arg));
		return ret;
	}
	
	enum KeepVariables {
		/**
		 * Transforms all variables to values; unassigned / uninstantiated variables will be transformed to
		 * <code>null</code>.
		 */
		KEEP_NONE,
		/**
		 * Just like <code>KEEP_NONE</code>, but keeps reassignable variables as {@link ClaimVariable} instances.
		 */
		KEEP_REASSIGNABLE,
		/**
		 * Assigned variables are replaced by their values; Re-assignables and unassigned variables are kept as
		 * {@link ClaimVariable} instances.
		 */
		KEEP_UNISTANTIATED,
		/**
		 * All variables are kept as {@link ClaimVariable} instances.
		 * <p>
		 * <b>Warning</b> this should not be used, as we have re-assignable variables.
		 */
		@Deprecated
		KEEP_ALL
	}
	
	/**
	 * Converts a {@link List} of {@link ClaimConstruct} instances - presumably from a larger construct or a structure -
	 * to a {@link Vector} of constructs with only values or some variables (see below).
	 * 
	 * @param constructs
	 *            : the constructs to transform to values
	 * @param ignore
	 *            : number of leading constructs to ignore (e.g. 'knowledge', 'message', etc)
	 * @param keepVariables
	 *            : what to do with variables: transform to values (or <code>null</code>, if uninstantiated); keep only
	 *            uninstantiated variables (with the purpose of instantiating them); or keep all variables (with the
	 *            purpose of changing their value).
	 * 
	 * @return the values of the constructs or uninstantiated variables
	 */
	protected Vector<ClaimConstruct> flattenConstructs(List<ClaimConstruct> constructs, int ignore,
			KeepVariables keepVariables)
	{
		Vector<ClaimConstruct> args = new Vector<ClaimConstruct>();
		int toIgnore = ignore;
		for(ClaimConstruct cons : constructs)
		{
			if(toIgnore-- > 0)
				continue;
			
			ClaimConstructType constructType = cons.getType();
			switch(constructType)
			{
			case VARIABLE:
				// get the value from the symbol table (can be null)
				ClaimValue value = getVariableValue((ClaimVariable) cons);
				boolean assignable = ((ClaimVariable) cons).isAffectable();
				switch(keepVariables)
				{
				case KEEP_NONE:
					args.add(value);
					break;
				case KEEP_REASSIGNABLE:
					if(assignable)
						args.add(cons);
					else
						args.add(value);
					break;
				case KEEP_UNISTANTIATED:
					if((value == null) || assignable)
						args.add(cons);
					else
						args.add(value);
					break;
				case KEEP_ALL:
					args.add(cons);
					break;
				}
				break;
			case VALUE:
				args.add(cons);
				break;
			case FUNCTION_CALL: // FIXME: this should probably be limited
				if(handleCall((ClaimFunctionCall) cons))
					args.add(new ClaimValue(new Boolean(true)));
				else
					args.add(new ClaimValue(new Boolean(false)));
				break;
			default:
				log.error("illegal construct type [" + constructType + "] inside construct");
				break;
			}
		}
		return args;
	}
	
	protected Vector<ClaimValue> constructs2Values(List<ClaimConstruct> constructs, int ignore)
	{
		Vector<ClaimValue> args = new Vector<ClaimValue>();
		for(ClaimConstruct arg : flattenConstructs(constructs, ignore, KeepVariables.KEEP_NONE))
			args.add((ClaimValue) arg);
		return args;
		
	}
	
	protected Vector<Object> constructs2Objects(List<ClaimConstruct> constructs, int ignore)
	{
		Vector<Object> args = new Vector<Object>();
		for(ClaimConstruct arg : flattenConstructs(constructs, ignore, KeepVariables.KEEP_ALL))
			if(arg != null)
				if (arg instanceof ClaimValue)
					args.add(((ClaimValue) arg).getValue());
				else
					args.add((ClaimVariable) arg);
			else
				args.add(null);
		return args;
	}
	
	protected Vector<String> constructs2Strings(List<ClaimConstruct> constructs, int ignore)
	{
		Vector<String> args = new Vector<String>();
		for(Object arg : constructs2Objects(constructs, ignore))
			args.add((String) arg);
		return args;
	}
	
	protected String constructs2OneStrings(List<ClaimConstruct> constructs, int ignore)
	{
		String rez = new String();
		
		Vector<Object> objects = constructs2Objects(constructs, ignore);
		
		for(Object arg : objects) {
			if (arg instanceof ClaimVariable)
				rez = rez.concat(((ClaimVariable) arg).toString() + " ");
			else
				rez = rez.concat((String) arg + " ");
		}
		return rez;
	}
	
	protected String constructsAndMaps(List<ClaimConstruct> constructs, 
											HashMap<Integer, ClaimVariable> intToVariable, int ignore)
	{
		String rez = new String();
		
		int no = 1;
		Vector<Object> objects = constructs2Objects(constructs, ignore);
		objects.remove(0);
		
		for(Object arg : objects) {
			if (arg instanceof ClaimVariable) {
				intToVariable.put(no, (ClaimVariable) arg);
				rez = rez.concat("?#" + no + " ");
				no++;
			}
			else
				rez = rez.concat((String) arg + " ");
		}
		return rez;
	}
	
	/**
	 * Get value of variable from the symbol table. First, search the variable in behavior's symbol table. If not found,
	 * search in agent's symbol table
	 * 
	 * @param variable
	 *            : the variable
	 * @return : Value of variable if found; null otherwise
	 */
	protected ClaimValue getVariableValue(ClaimVariable variable)
	{
		ClaimValue value = null;
		value = st.get(variable);
		/*
		 * if(value == null) value = this.cbd.getMyAgent().getSymbolTable().get(variable);
		 */// not needed. The implementation of the symbol table already permits this
		return value;
	}
	
	/**
	 * Binds a variable to a value. Works only if the variable is previously unbound.
	 * 
	 * @param variable
	 *            : the variable
	 * @param value
	 *            : the value
	 */
	protected void bindVariable(ClaimVariable variable, ClaimValue value)
	{
		if(getVariableValue(variable) == null)
		{
			st.put(variable, value);
			log.trace("variable [" + variable.getName() + "] bound to [" + value + "]");
		}
		else
		{
			st.put(variable, value);
			log.warn("variable [" + variable.getName() + "] already bound; rebound to [" + value + "]");
			// FIXME: decide on whether variables can be rebound
		}
	}
	
}
