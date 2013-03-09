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
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

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

import tatami.core.agent.BaseAgent;
import tatami.core.agent.claim.behavior.AskForBeingChildBehaviour;
import tatami.core.agent.claim.behavior.AskForLocation;
import tatami.core.agent.claim.parser.ClaimBehaviorDefinition;
import tatami.core.agent.claim.parser.ClaimCondition;
import tatami.core.agent.claim.parser.ClaimConstruct;
import tatami.core.agent.claim.parser.ClaimConstructType;
import tatami.core.agent.claim.parser.ClaimForAllK;
import tatami.core.agent.claim.parser.ClaimFunctionCall;
import tatami.core.agent.claim.parser.ClaimFunctionType;
import tatami.core.agent.claim.parser.ClaimIf;
import tatami.core.agent.claim.parser.ClaimStructure;
import tatami.core.agent.claim.parser.ClaimValue;
import tatami.core.agent.claim.parser.ClaimVariable;
import tatami.core.agent.claim.parser.ClaimWhile;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.visualization.VisualizableAgent;
import tatami.core.agent.webServices.WSAgent;
import tatami.core.agent.webServices.WebServiceOntology;
import tatami.core.agent.webServices.WebServiceOntology.ReceiveOperation;
import tatami.core.interfaces.AgentGui;
import tatami.core.interfaces.AgentGui.InputListener;
import tatami.core.interfaces.KnowledgeBase;
import tatami.core.interfaces.KnowledgeBase.KnowledgeDescription;
import tatami.core.interfaces.Logger;
import tatami.core.util.jade.JadeUtil;

/**
 * 
 * There is one {@link ClaimBehavior} instance for each behavior in a {@link ClaimAgent}.
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 * 
 */
public class ClaimBehavior extends Behaviour implements InputListener
{
	private boolean		finished;			// used to verify if the behavior is finished
	private long		wakeUpAtTime;		// used by the primitive wait
											
	private int			currentStatement;	// the current statement being processed
	private final int	numberOfStatements; // the number of statements of the behavior
											
	private boolean		initialized;		// tells if this behavior was initialized
											
	/**
	 * Behavior for agent moving.
	 * 
	 * <p>
	 * Wait for agent to find location of destination. Once agent has found the location, it'll move to this location.
	 * 
	 * see <code>handleIn()</code>
	 */
	private class MoveToDest extends WakerBehaviour
	{
		private static final long	serialVersionUID	= -5996155613563560744L;
		private static final long	findLocTime			= 3000;
		
		public MoveToDest(ClaimAgent agent)
		{
			super(agent, findLocTime);
		}
		
		@Override
		public void onWake()
		{
			Location loc = (Location) getDataStore().get(ClaimOntology.LOC_RTN);
			
			((VisualizableAgent) this.myAgent).getLog().info("Move to " + loc);
			((ClaimAgent) this.myAgent).doMove(loc);
		}
	}
	
	private static final long						serialVersionUID	= 1212301036264628711L;
	
	protected ClaimBehaviorDefinition				cbd;
	protected transient Logger						log					= null;
	
	/**
	 * for WS reply support; the last message that was received and validated by the behavior;
	 */
	protected ACLMessage							lastMessage			= null;
	/**
	 * for WS support (needed to reply)
	 */
	protected Action								lastReceivedAction	= null;
	
	/**
	 * Contains the queues of events for the input(s); the events are registered by <code>receiveInput</code>
	 */
	protected Map<String, Queue<Vector<Object>>>	inputQueues			= null;
	
	public ClaimBehavior(ClaimBehaviorDefinition behaviorDefinition)
	{
		this.cbd = behaviorDefinition;
		inputQueues = new HashMap<String, Queue<Vector<Object>>>();
		finished = false;
		wakeUpAtTime = -1;
		numberOfStatements = cbd.getStatements().size();
		currentStatement = numberOfStatements;
		initialized = false;
	}
	
	@Override
	public void action()
	{
		log = ((VisualizableAgent) myAgent).getLog();
		
		// if we have an active wait, we will put the behavior to sleep again
		if(wakeUpAtTime != -1)
		{
			long blockTime = wakeUpAtTime - System.currentTimeMillis();
			if(blockTime > 0)
				block(blockTime);
			else
			{
				wakeUpAtTime = -1;
			}
		}
		else if(currentStatement != numberOfStatements)
		{
			if(!handleStatement(cbd.getStatements().get(currentStatement++)))
				currentStatement = numberOfStatements;
		}
		else
		{
			if(cbd.getSymbolTable().getLog() == null)
				cbd.getSymbolTable().setLog(log);
			
			cbd.clearSymbolTable(); // reinitialize symbol table
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
					block();
					break;
				case CYCLIC:
					break;
				case PROACTIVE:
					break;
				}
		}
	}
	
	@Override
	public boolean done()
	{
		
		return finished;
	}
	
	@Override
	public void receiveInput(String componentName, Vector<Object> arguments)
	{
		inputQueues.get(componentName).offer(arguments);
		this.restart();
	}
	
	public void resetGui()
	{
		// hhg
		inputQueues.clear();
		this.restart();
	}
	
	/**
	 * Gets a reference to the knowledge base of the agent from the containing {@link ClaimAgent} instance.
	 * 
	 * @return the knowledge base.
	 */
	public KnowledgeBase getKBase()
	{
		return ((ClaimAgent) this.myAgent).getKBase();
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
				((ClaimAgent) this.myAgent).getLog().trace("if condition satisfied");
				// after "then" there's a true branch and a false branch
				Vector<ClaimConstruct> trueBranch = ((ClaimIf) statement).getTrueBranch();
				for(ClaimConstruct trueBranchStatement : trueBranch)
					handleStatement(trueBranchStatement);
			}
			else
			{
				((ClaimAgent) this.myAgent).getLog().trace("if condition not satisfied");
				Vector<ClaimConstruct> falseBranch = ((ClaimIf) statement).getFalseBranch();
				if(falseBranch != null)
					for(ClaimConstruct falseBranchStatement : falseBranch)
						handleStatement(falseBranchStatement);
			}
			break;
		
		// Type ForAllK
		case FORALLK:
			// structure : struct knowledge v1 v2 ... vn
			handleForAllK((ClaimForAllK) statement, getKBase());
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
			return handleReceive(args, function.getMyBehavior());
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
		ClaimValue agentName = (ClaimValue) args.get(0);
		// ClaimValue agentClassName = (ClaimValue)args.get(1);
		
		try
		{
			((BaseAgent) this.myAgent).getContainerController().createNewAgent((String) agentName.getValue(),
					"core.claim.ClaimAgent", args.subList(1, args.size()).toArray());
		} catch(StaleProxyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
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
		((BaseAgent) this.myAgent).doDelete();
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
		
		try
		{
			((BaseAgent) this.myAgent).getContainerController().getAgent((String) agentName.getValue()).kill();
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
		}
		
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
		
		if((args.size() > 2) && (myAgent instanceof WSAgent))
		{
			webServiceInvocationMode = true;
			if(receiver.equals("null"))// FIXME null value should be handled specially
				// use simple web service access
				simpleWebService = true;
		}
		
		if(args.size() > 3)
			// FIXME check if in WS mode
			expectServiceReturn = true;
		
		if(receiver != null)
		{
			// second argument is a structure that represents all the message's fields
			ClaimStructure message = null;
			String simpleMessage = null;
			if(!simpleWebService)
				message = ((ClaimStructure) args.get(1 - parametersAdjust)).bindStructure();
			else
			{
				if(args.get(1 - parametersAdjust).getType() == ClaimConstructType.VARIABLE)
					simpleMessage = (String) (this.cbd.getSymbolTable().get((ClaimVariable) args
							.get(1 - parametersAdjust))).getValue();
				else
					simpleMessage = (String) ((ClaimValue) args.get(1 - parametersAdjust)).getValue();
			}
			
			if(webServiceInvocationMode)
			{ // Web service invocation
				if(simpleWebService)
				{
					String serviceAddress = (String) ((ClaimValue) args.get(2)).getValue();
					log.info("accessing service at [" + serviceAddress + "]...");
					String result = ((WSAgent) this.myAgent).doSimpleAccess(serviceAddress, simpleMessage);
					
					if(expectServiceReturn) // expect a result from the web service
					{ // integrate result
						log.trace("received result " + result + "; binding to "
								+ ((ClaimVariable) args.get(3)).getName());
						this.cbd.getSymbolTable().put((ClaimVariable) args.get(3), new ClaimValue(result));
					}
					
				}
				else
				{
					String serviceName = receiver;
					String serviceAddress = (String) ((ClaimValue) args.get(2)).getValue();
					log.info("accessing service [" + serviceName + "] at [" + serviceAddress + "]...");
					@SuppressWarnings("null")
					// it should be ok according to the logic above
					String result = ((WSAgent) this.myAgent).doAccess(serviceAddress, serviceName, message.toString());
					ClaimStructure received = ClaimStructure.parseString(result, log);
					log.trace("received result " + received);
					
					if(expectServiceReturn) // expect a result from the web service
					{ // integrate result
						ClaimStructure pattern = (ClaimStructure) args.get(3);
						Vector<ClaimConstruct> newArgs = pattern.getFields();
						readMessage(received.bindStructure(), newArgs);
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
					try
					{
						myAgent.getContentManager().fillContent(reply, ce);
					} catch(CodecException e)
					{
						e.printStackTrace();
					} catch(OntologyException e)
					{
						e.printStackTrace();
					}
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
				
				this.myAgent.send(reply);
				log.info("sent " + ClaimMessage.printMessage(reply));
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
	 */
	protected boolean handleReceive(Vector<ClaimConstruct> args, ClaimBehaviorDefinition myBehavior)
	{
		ACLMessage msg = null;
		
		String protocol = null;
		int argsSize = args.size();
		if(argsSize == 2)
			protocol = ((ClaimValue) ((ClaimStructure) args.get(1)).getFields().get(1)).toString();
		else if(argsSize == 1)
			protocol = ((ClaimValue) ((ClaimStructure) args.get(0)).getFields().get(1)).toString();
		else
			log.error("Unexpected number of arguments for \"receive\".");
		
		ClaimStructure received = null;
		boolean ret = true;
		
		// handle potential web service access to the agent
		MessageTemplate WStemplate = MessageTemplate.MatchOntology(WebServiceOntology.NAME);
		
		msg = this.myAgent.receive(WStemplate);
		if(msg != null)
		{ // incoming web service access
			// check if right message
			log.trace("checking received ws access message [" + msg + "]");
			String messageContent = null;
			// messageContent = (String)myAgent.getContentManager().extractContent(msg);
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
				{ // put it back; discontinue behavior
					myAgent.putBack(msg);
					ret = false;
					log.trace("ws access does not match protocol");
				}
			}
		}
		else
		{ // normal JADE message
			// build the pattern for message received (protocol, language, ontology)
			
			MessageTemplate mt = JadeUtil.templateAssemble(ClaimOntology.template(),
					MessageTemplate.MatchProtocol(protocol));
			
			msg = this.myAgent.receive(mt);
			if(msg != null)
			{
				lastMessage = msg;
				lastReceivedAction = null;
				
				// read message in the form of claim structure
				
				log.trace("received " + ClaimMessage.printMessage(msg));
				// FIXME either drop "message" in sending struct, either add "message" in receiving struct
				try
				{
					received = (ClaimStructure) msg.getContentObject();
				} catch(UnreadableException e)
				{
					log.error("unable to extract content");
					e.printStackTrace();
				}
			}
		}
		
		if(received != null)
		{
			Vector<ClaimConstruct> newArgs = ((ClaimStructure) args.get(argsSize - 1)).getFields();
			if(!readMessage(received.bindStructure(), newArgs))
			{ // the message does not match the patter
				log.trace("message not matching pattern [" + ClaimMessage.printMessage(msg) + "]");
				myAgent.putBack(msg);
				ret = false;
			}
			else
			{
				log.trace("message received: [" + ClaimMessage.printMessage(msg) + "]");
				if(argsSize == 2)
					myBehavior.getSymbolTable().put((ClaimVariable) args.get(0),
							new ClaimValue(msg.getSender().getLocalName()));
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
		AgentGui gui = ((VisualizableAgent) myAgent).getGUI();
		
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
				outputV.add(this.cbd.getSymbolTable().get((ClaimVariable) arg));
		}
		
		AgentGui myAgentGUI = ((VisualizableAgent) myAgent).getGUI();
		Vector<Object> outV = new Vector<Object>();
		for(ClaimValue output : outputV)
			outV.add(output.getValue());
		myAgentGUI.doOutput((String) outputComponent.getValue(), outV);
		
		log.trace("The output \"" + outputV + "\" was written on " + (String) outputComponent.getValue());
		return true;
	}
	
	protected boolean handleIn(Vector<ClaimConstruct> args)
	{
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
		cbd.getSymbolTable().put(new ClaimVariable("parent", true), value);
		
		// after IN there's always one variable of destination
		// check if this variable is bound or not
		
		if(value != null)
		{
			// name of agent destination
			String destination = (String) value.getValue();
			// find Container of destination
			SequentialBehaviour sb = new SequentialBehaviour();
			Behaviour b = new AskForLocation((ClaimAgent) this.myAgent, destination);
			b.setDataStore(sb.getDataStore());
			sb.addSubBehaviour(b);
			
			b = new AskForBeingChildBehaviour((ClaimAgent) this.myAgent, destination);
			sb.addSubBehaviour(b);
			
			b = new MoveToDest((ClaimAgent) this.myAgent);
			b.setDataStore(sb.getDataStore());
			sb.addSubBehaviour(b);
			
			((ClaimAgent) this.myAgent).addBehaviour(sb);
		}
		return true;
	}
	
	@SuppressWarnings("static-method")
	protected boolean handleWait(Vector<ClaimConstruct> args)
	{
		// after wait there's always a constant
		ClaimValue waitTimeConstant = (ClaimValue) args.get(0);
		
		long timeout = Integer.parseInt((String) waitTimeConstant.getValue());
		wakeUpAtTime = System.currentTimeMillis() + timeout;
		block(timeout);
		
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
		KnowledgeBase kb = getKBase();
		
		switch(construct)
		{
		case ADDK:
		// function "add knowledge"
		{
			// after addK there's always a structure of knowledge
			SimpleKnowledge newKl = structure2Knowledge((ClaimStructure) args.get(0));
			
			// add knowledge to the knowledge base of agent
			kb.add(newKl);
			log.info(this.myAgent.getLocalName() + " adds new knowledge " + newKl.printlnKnowledge() + " in behavior "
					+ this.cbd.getName());
			return true;
		}
		case READK:
		// function "check if agent already has the knowledge"
		// If yes : return true, also put the value of variable in knowledge pattern into the symbol table
		{
			// after readK there's always a structure of knowledge
			ClaimStructure knowledgeStruct = (ClaimStructure) args.get(0); // struct knowledge <kl type> <kl fields>
			SimpleKnowledge pattern = structure2Knowledge(knowledgeStruct);
			KnowledgeDescription result = kb.getFirst(pattern);
			// FIXME: support multiple types
			if(result == null)
				return false; // knowledge was not found
			return readStructure(knowledge2Structure((SimpleKnowledge) result), knowledgeStruct, 0, true, false);
		}
		case REMOVEK:
		// function "remove knowledge"
		{
			ClaimStructure knowledgeStruct = (ClaimStructure) args.get(0); // struct knowledge <kl type> <kl fields>
			SimpleKnowledge pattern = structure2Knowledge(knowledgeStruct);
			kb.remove(pattern);
			return true;
		}
		default:
			// should not be here
			return true; // unreachable code
		}
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
						cbd.getSymbolTable().put(entry.getKey(), entry.getValue());
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
			{ // just check match
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
		ClaimStructure ret = new ClaimStructure(cbd);
		Vector<ClaimConstruct> fields = new Vector<ClaimConstruct>();
		fields.add(new ClaimValue("knowledge"));
		fields.add(new ClaimValue(knowledge.getKnowledgeType()));
		for(Object fieldValue : knowledge.getSimpleKnowledge())
			fields.add(new ClaimValue(fieldValue));
		ret.setFields(fields);
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
		for(ClaimConstruct arg : flattenConstructs(constructs, ignore, KeepVariables.KEEP_NONE))
			if(arg != null)
				args.add(((ClaimValue) arg).getValue());
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
		value = this.cbd.getSymbolTable().get(variable);
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
			cbd.getSymbolTable().put(variable, value);
			log.trace("variable [" + variable.getName() + "] bound to [" + value + "]");
		}
		else
		{
			cbd.getSymbolTable().put(variable, value);
			// log.warn("variable [" + variable.getName() + "] already bound; rebound to [" + value + "]");
			// // FIXME: decide on whether variables can be rebound
		}
	}
	
}
