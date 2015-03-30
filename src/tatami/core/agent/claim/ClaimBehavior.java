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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import net.xqhs.graphs.context.ContextPattern;
import net.xqhs.graphs.graph.Graph;
import net.xqhs.graphs.graph.Node;
import net.xqhs.graphs.graph.SimpleGraph;
import net.xqhs.graphs.matcher.Match;
import net.xqhs.graphs.pattern.GraphPattern;
import net.xqhs.graphs.pattern.NodeP;
import net.xqhs.graphs.representation.text.TextGraphRepresentation;
import net.xqhs.graphs.util.ContentHolder;
import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.claim.ClaimComponent.Vocabulary;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGui.InputListener;
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

/**
 * There is one {@link ClaimBehavior} instance for each behavior in a {@link ClaimComponent}.
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 * 
 */
public class ClaimBehavior
{
	/**
	 * Enumeration of the methods by which a behavior may be activated.
	 * 
	 * @author Andrei Olaru
	 */
	public static enum ActivationMethod {
		
		WEB_SERVICE,
		
		MESSAGE,
	}
	
	public static class ActivationRecord
	{
		ActivationMethod	method;
		AgentEvent			activationEvent	= null;
		
		public ActivationRecord(AgentEvent event)
		{
			activationEvent = event;
			switch(event.getType())
			{
			case AGENT_MESSAGE:
				// TODO
				break;
			
			default:
				break;
			}
		}
		
		protected ActivationMethod getActivationMethod()
		{
			return method;
		}
	}
	
	/**
	 * The behavior definition.
	 */
	protected ClaimBehaviorDefinition	cbd;
	/**
	 * The local symbol table of the behavior.
	 */
	protected SymbolTable				st;
	/**
	 * The {@link ClaimComponent} instance this behavior is currently added to.
	 */
	protected ClaimComponent			claimComponent;
	/**
	 * The {@link Logger} instance to use.
	 */
	protected transient Logger			log					= null;
	/**
	 * Information about how the behavior was activated.
	 */
	protected ActivationRecord			activationRecord	= null;
	/**
	 * The number (index) of the statement currently being processed.
	 */
	protected int						currentStatement;
	
	/**
	 * Creates an instance that manages the execution of a claim behavior, as described by its definition.
	 * 
	 * @param behaviorDefinition
	 *            - the {@link ClaimBehaviorDefinition}.
	 * @param agentST
	 *            - the {@link SymbolTable} that is associated with the {@link ClaimComponent}.
	 * @param parentComponent
	 *            - the {@link ClaimComponent} this behavior is part of.
	 * @param logLink
	 *            - the Logger instance to use, if any; <code>null</code> otherwise.
	 */
	public ClaimBehavior(ClaimBehaviorDefinition behaviorDefinition, SymbolTable agentST,
			ClaimComponent parentComponent, Logger logLink)
	{
		cbd = behaviorDefinition;
		claimComponent = parentComponent;
		st = new SymbolTable(agentST);
		currentStatement = 0;
		log = logLink;
		st.setLogLink(log);
	}
	
	/**
	 * This method is called whenever the behavior may be executed. It tests for the activation conditions and executes
	 * all the statements.
	 */
	public void action()
	{
		while(currentStatement < cbd.getStatements().size())
			if(!handleStatement(cbd.getStatements().get(currentStatement++)))
			{
				// stop behavior and exit execution
				currentStatement = 0;
				st.clearSymbolTable(); // reinitialize symbol table
				return;
			}
	}
	
	/**
	 * @return the type of the behavior.
	 */
	public ClaimBehaviorType getBehaviorType()
	{
		return cbd.getBehaviorType();
	}
	
	/**
	 * Handle one statement in the behavior.
	 * 
	 * @param statement
	 *            - the current statement.
	 * @return <code>true</code> if the behavior should continue; <code>false</code> otherwise.
	 */
	protected boolean handleStatement(ClaimConstruct statement)
	{
		switch(statement.getType())
		{
		case FUNCTION_CALL:
			switch(((ClaimFunctionCall) statement).getFunctionType())
			{
			case RECEIVE:
			case INPUT:
				// value returned indicates behavior termination
				return handleCall((ClaimFunctionCall) statement);
			default:
				handleCall((ClaimFunctionCall) statement);
				return true;
			}
		case CONDITION:
		{
			return handleCall(((ClaimCondition) statement).getCondition());
		}
		case IF:
			boolean condition = handleCall(((ClaimIf) statement).getCondition());
			if(condition)
			{
				log.trace("if condition satisfied " + currentStatement);
				
				// after "then" there's a true branch and a false branch
				Vector<ClaimConstruct> trueBranch = ((ClaimIf) statement).getTrueBranch();
				for(ClaimConstruct trueBranchStatement : trueBranch)
					handleStatement(trueBranchStatement);
			}
			else
			{
				Vector<ClaimConstruct> falseBranch = ((ClaimIf) statement).getFalseBranch();
				if(falseBranch != null)
					for(ClaimConstruct falseBranchStatement : falseBranch)
						handleStatement(falseBranchStatement);
			}
			break;
		case FORALLK:
			handleForAllK((ClaimForAllK) statement);
			break;
		case WHILE:
			handleWhile((ClaimWhile) statement);
			break;
		default:
			break;
		}
		return true;
	}
	
	/**
	 * Handle a normal statement. The return value indicates the success of the statement, such as for conditions or
	 * activation events.
	 * 
	 * @param function
	 *            : Claim construct / statement
	 * @return <code>true</code> if the behavior should continue normally or if the condition represented by the
	 *         statement is true.
	 */
	protected boolean handleCall(ClaimFunctionCall function)
	{
		// arguments of function
		Vector<ClaimConstruct> args = function.getArguments();
		
		switch(function.getFunctionType())
		{
		case SEND:
			handleSend(args);
			return true;
		case RECEIVE:
			return handleReceive(args);
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
			handleInput(args);
			return true;
		case OUTPUT:
			handleOutput(args);
			return true;
		case WAIT:
			handleWait(args);
			return true;
		case JAVA:
			return handleJavaCall(function.getFunctionName(), args);
		}
		log.warn("Unreachable area reached");
		return true; // should be unreachable?
	}
	
	/**
	 * Creates a new agent, based on the given arguments
	 * 
	 * @param args
	 *            - the arguments of the call to "new" construct. The first two elements of the vector are the name and
	 *            the class name of the agent. The rests are arguments that the created agent will process after
	 *            creation
	 * @return - whether the creation was successful or not
	 */
	protected boolean handleNew(Vector<ClaimConstruct> args)
	{
		/*
		 * ClaimValue agentName = (ClaimValue) args.get(0); ClaimValue agentClassName = (ClaimValue)args.get(1);
		 * 
		 * /*try { ((ParametricComponent) this.myAgent).getContainerController().createNewAgent((String)
		 * agentName.getValue(), "core.claim.ClaimAgent", args.subList(1, args.size()).toArray()); }
		 * catch(StaleProxyException e) { // TODO Auto-generated catch block e.printStackTrace(); return false; }
		 */
		
		// TODO
		
		return true;
	}
	
	/**
	 * This deletes the agent from the inside. It is a basic implementation of the S-CLAIM acid primitive, that has to
	 * be extended in the future
	 * 
	 * @return - always true
	 */
	protected boolean handleAcid()
	{
		log.trace("Acid command received. I will delete myself!");
		// this.myAgent.getParametric().doDelete();
		
		// TODO
		
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
	
	protected boolean handleOpen(Vector<ClaimConstruct> args)
	{
		ClaimValue agentName = (ClaimValue) args.get(0);
		
		/*
		 * try { ((ParametricComponent) this.myAgent).getContainerController().getAgent((String)
		 * agentName.getValue()).kill(); } catch(StaleProxyException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); return false; } catch(ControllerException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); return false; }
		 */
		
		// TODO
		
		return true;
	}
	
	/**
	 * The send construct.
	 * <p>
	 * Variants:
	 * <p>
	 * <code>(send agent-id (struct message field-1 field-2 ...))</code> - normal send
	 * <p>
	 * <code>(send (struct message field-1 field-2 ...))</code> - send a reply to the received message; web service
	 * invocation
	 * <p>
	 * TODO: - web service access
	 * 
	 * @param args
	 */
	protected void handleSend(Vector<ClaimConstruct> args)
	{
		String receiver = null; // receiver of the message to be sent
		int parametersAdjust = 0; // offset argument number if there is no receiver field
		
		boolean replyToAgent = false; // if true, the reply will be sent to an agent
		boolean replyToWebServiceInvocation = false; // if true, the reply is a response to a web service invocation
		boolean webServiceInvocation = false; // if true, the send is a web service invocation
		boolean expectServiceReturn = false; // if true, wait for a reply (blocking call)
		
		// decide upon the receiver(s) of the message
		switch(args.get(0).getType())
		{
		case VARIABLE:
			receiver = (String) getVariableValue(((ClaimVariable) args.get(0))).getValue();
			break;
		case VALUE:
			receiver = (String) ((ClaimValue) args.get(0)).getValue();
			break;
		case STRUCTURE:
			// there is no receiver information; should reply to the original sender
			switch(activationRecord.getActivationMethod())
			{
			case MESSAGE:
				// log.trace("replying to message " + lastMessage);
				replyToAgent = true;
				// TODO
				break;
			case WEB_SERVICE:
				replyToWebServiceInvocation = true;
				// TODO
				break;
			default:
				// TODO
				break;
			}
			// TODO check ok
			parametersAdjust = 1;
			break;
		default:
			break;
		}
		
		if(args.size() > 3)
		{
			if(webServiceInvocation)
				expectServiceReturn = true;
			else
				log.error("Too many arguments for send primitive when not in web service mode.");
		}
		
		if(receiver == null)
		{
			log.error("Unable to determine message receiver");
			return;
		}
		
		// second argument is a structure that represents all the message's fields
		ClaimStructure message = null;
		message = st.bindStructure((ClaimStructure) args.get(1 - parametersAdjust));
		
		if(webServiceInvocation)
		{
			// TODO: Web service invocation
		}
		else
		{
			if(replyToWebServiceInvocation)
			{ // WS return result
				log.trace("sending WS result [" + message.toString() + "]");
				// TODO
			}
			else
			{
				claimComponent.sendMessage(message.toString(), receiver);
			}
			log.info("sent " + message.toString());
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
	protected boolean handleReceive(Vector<ClaimConstruct> args)
	{
		int argsSize = args.size();
		boolean ret = true;
		
		/*
		 * TODO handle potential web service access to the agent MessageTemplate WStemplate =
		 * MessageTemplate.MatchOntology(WebServiceOntology.NAME);
		 * 
		 * //msg = this.myAgent.getMessaging().receive(WStemplate); if(msg != null) { // incoming web service access //
		 * check if right message
		 * 
		 * log.trace("checking received ws access message [" + msg + "]"); String messageContent = content;
		 * messageContent = (String)myAgent.getContentManager().extractContent(msg); ContentElement ce = null; try { ce
		 * = myAgent.getContentManager().extractContent(msg); } catch(UngroundedException e) { e.printStackTrace(); }
		 * catch(CodecException e) { e.printStackTrace(); } catch(OntologyException e) { e.printStackTrace(); } if(ce !=
		 * null) { log.trace("received content [" + ce + "]"); ReceiveOperation op = (ReceiveOperation) ((Action)
		 * ce).getAction(); messageContent = op.getOperationArgument(); log.trace("received content detail [" +
		 * messageContent + "]"); ClaimStructure messageStruct = ClaimStructure.parseString(messageContent, log);
		 * log.trace("received message [" + messageStruct + "]");
		 * 
		 * String messageProtocol = (String) ((ClaimValue) messageStruct.getFields().get(1)).getValue();
		 * log.trace("received protocol [" + messageProtocol + "]; waiting for [" + protocol + "]");
		 * if(protocol.equals(messageProtocol)) { // it's ok, the message should be received by this RECEIVE construct
		 * lastMessage = msg; lastReceivedAction = (Action) ce; received = messageStruct;
		 * log.info("received / ws access " + ClaimMessage.printMessage(msg)); } else { // put it back; discontinue
		 * behavior // myAgent.putBack(msg); ret = false; log.trace("ws access does not match protocol"); } } } else
		 */
		// normal JADE message
		
		ClaimStructure received = ClaimStructure.parseString(content);
		
		if(received != null)
		{
			
			Vector<ClaimConstruct> newArgs = ((ClaimStructure) args.get(argsSize - 1)).getFields();
			ClaimStructure cl = st.bindStructure(received);
			
			if(!readMessage(st.bindStructure(received), newArgs))
			{ // the message does not match the pattern
			
				log.trace("message not matching pattern [" + content + "]");
				ret = false;
			}
			else
			{
				
				log.trace("--------- message received: [" + content + "]");
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
		AgentGui gui = null; // FIXME = myAgent.getVisualizable().getInteractivGUI();
		
		// FIXME: variables should be supported
		String inputComponent = ((ClaimValue) args.get(0)).toString();
		
		// FIXME: variables should be supported
		// @SuppressWarnings("unused")
		// ClaimValue inputComponentType = (ClaimValue) args.get(1); // unused anymore, for now.
		
		if(!inputQueues.containsKey(inputComponent) && gui != null)
		{
			gui.connectInput(inputComponent, this);
			inputQueues.put(inputComponent, new LinkedList<Vector<Object>>());
			return false;
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
		// return true;
		return readValues(objects2values(inputArgs), args2, 1, true, false);
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
		
		AgentGui myAgentGUI = null; // FIXME: myAgent.getVisualizable().getInteractivGUI();
		Vector<Object> outV = new Vector<Object>();
		for(ClaimValue output : outputV)
			outV.add(output.getValue());
		if(myAgentGUI != null)
			myAgentGUI.doOutput((String) outputComponent.getValue(), outV);
		
		log.trace("The output \"" + outputV + "\" was written on " + (String) outputComponent.getValue());
		return true;
	}
	
	protected boolean handleIn(Vector<ClaimConstruct> args)
	{
		/*
		 * 
		 * // reading the argument of the function ClaimConstruct goIn = args.get(0);
		 * 
		 * // reading the value of the argument ClaimValue value = null; switch(goIn.getType()) { case VARIABLE: value =
		 * getVariableValue((ClaimVariable) goIn); break; case VALUE: value = (ClaimValue) goIn; break; default:
		 * log.error("Unsupported argument type for the primitive \"in\"."); break; }
		 * 
		 * // changing the value of the "parent" parameter of the agent: st.put(new ClaimVariable("parent", true),
		 * value);
		 * 
		 * // after IN there's always one variable of destination // check if this variable is bound or not
		 * 
		 * if(value != null) { // name of agent destination String destination = (String) value.getValue(); // find
		 * Container of destination SequentialBehaviour sb = new SequentialBehaviour(); Behaviour b = new
		 * AskForLocation((ClaimComponent) this.myAgent, destination); b.setDataStore(sb.getDataStore());
		 * sb.addSubBehaviour(b);
		 * 
		 * b = new AskForBeingChildBehaviour((ClaimComponent) this.myAgent, destination); sb.addSubBehaviour(b);
		 * 
		 * b = new MoveToDest((ClaimComponent) this.myAgent); b.setDataStore(sb.getDataStore()); sb.addSubBehaviour(b);
		 * 
		 * ((ClaimComponent) this.myAgent).addBehaviour(sb); }
		 */
		return true;
	}
	
	protected boolean handleWait(Vector<ClaimConstruct> args)
	{
		// after wait there's always a constant
		ClaimValue waitTimeConstant = (ClaimValue) args.get(0);
		
		long timeout = Integer.parseInt((String) waitTimeConstant.getValue());
		wakeUpAtTime = System.currentTimeMillis() + timeout;
		
		try
		{
			Thread.sleep(timeout);
		} catch(InterruptedException e)
		{
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
			if(knowledge.substring(0, knowledge.indexOf(" ")).equals("pattern"))
			{
				knowledge = knowledge.substring(knowledge.indexOf(" ") + 1);
				ContextPattern CP = new ContextPattern();
				claimComponent.getCognitive().addPattern(
						(ContextPattern) new TextGraphRepresentation(CP).readRepresentation(new ContentHolder<String>(
								knowledge)));
			}
			else
			{
				// since it is not a pattern, it should not contain ?variables -> we should get their value from
				// symbolTable
				String[] elements = knowledge.split(" ");
				knowledge = "";
				for(String element : elements)
				{
					if(element.startsWith("?") && st.containsSymbol(new ClaimVariable(element.substring(1))))
					{
						knowledge += st.get(new ClaimVariable(element.substring(1))).getValue().toString() + " ";
					}
					else
					{
						knowledge += element + " ";
					}
				}
				
				claimComponent.getCognitive().add(
						new TextGraphRepresentation(new SimpleGraph()).readRepresentation(new ContentHolder<String>(
								knowledge)));
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
			
			/* replace all ClaimVariables (represented as ?<name>) with an index if they are not already stored */
			HashMap<Integer, ClaimVariable> intToVariable = new HashMap<Integer, ClaimVariable>();
			knowledge = constructsAndMaps(knowledgeStruct.getFields(), intToVariable, 1);
			
			GraphPattern CP = new GraphPattern();
			TextGraphRepresentation repr = new TextGraphRepresentation(CP);
			repr.readRepresentation(new ContentHolder<String>(knowledge));
			
			Match m = claimComponent.getCognitive().read(CP);
			if(m == null)
				return false;
			
			for(Node node : CP.getNodes())
				if(node instanceof NodeP)
				{
					Node rez = m.getMatchedGraphNode(node);
					if(!st.containsSymbol(intToVariable.get(((NodeP) node).genericIndex())))
					{
						st.put(intToVariable.get(((NodeP) node).genericIndex()), new ClaimValue(rez.getLabel()));
					}
				}
			return true;
		}
		
		case REMOVEK:
		// function "remove knowledge" TODO remove patterns
		{
			String[] elements = knowledge.split(" ");
			knowledge = "";
			for(String element : elements)
			{
				if(element.startsWith("?") && st.containsSymbol(new ClaimVariable(element.substring(1))))
				{
					knowledge += st.get(new ClaimVariable(element.substring(1))).getValue().toString() + " ";
				}
				else
				{
					knowledge += element + " ";
				}
			}
			claimComponent.getCognitive().remove(knowledge);
			return true;
		}
		
		default:
			// should not be here
			return true; // unreachable code
		}
		
		// return true;
	}
	
	/**
	 * Read all records of knowledge in the knowledge base that match the pattern. Used in forAllK function.
	 */
	protected void handleForAllK(ClaimForAllK construct)
	{
		ClaimStructure knowledgeStruct = construct.getStructure();
		Vector<ClaimConstruct> statements = construct.getStatements();
		
		// replace all ClaimVariables (represented as ?<name>) with an index
		HashMap<Integer, ClaimVariable> intToVariable = new HashMap<Integer, ClaimVariable>();
		String knowledge = constructsAndMaps(knowledgeStruct.getFields(), intToVariable, 1);
		
		GraphPattern CP = new GraphPattern();
		TextGraphRepresentation repr = new TextGraphRepresentation(CP);
		repr.readRepresentation(new ContentHolder<String>(knowledge));
		
		List<Match> matches = claimComponent.getCognitive().readAll(CP);
		for(Match match : matches)
		{
			st = new SymbolTable(st);
			
			for(Node node : CP.getNodes())
				if(node instanceof NodeP)
				{
					Node rez = match.getMatchedGraphNode(node);
					int genericIndex = ((NodeP) node).genericIndex();
					st.put(intToVariable.get(genericIndex), new ClaimValue(rez.toString()));
				}
			for(ClaimConstruct claimConstruct : construct.getStatements())
				handleStatement(claimConstruct);
			
			st = st.prev;
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
		ClaimStructure ret = new ClaimStructure();
		/*
		 * ClaimStructure ret = new ClaimStructure(cbd); Vector<ClaimConstruct> fields = new Vector<ClaimConstruct>();
		 * fields.add(new ClaimValue("knowledge")); fields.add(new ClaimValue(knowledge.getKnowledgeType())); for(Object
		 * fieldValue : knowledge.getSimpleKnowledge()) fields.add(new ClaimValue(fieldValue)); ret.setFields(fields);
		 */
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
				if(arg instanceof ClaimValue)
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
		
		for(Object arg : objects)
		{
			if(arg instanceof ClaimVariable)
				rez = rez.concat(((ClaimVariable) arg).toString() + " ");
			else
				rez = rez.concat((String) arg + " ");
		}
		return rez;
	}
	
	protected String constructsAndMaps(List<ClaimConstruct> constructs, HashMap<Integer, ClaimVariable> intToVariable,
			int ignore)
	{
		String rez = new String("[");
		
		int no = 1;
		Vector<Object> objects = constructs2Objects(constructs, ignore);
		objects.remove(0);
		
		for(Object arg : objects)
		{
			if(st.containsSymbol(new ClaimVariable(arg.toString().substring(1))))
				rez = rez + st.get(new ClaimVariable(arg.toString().substring(1))).getValue().toString() + " ";
			else if(arg instanceof ClaimVariable)
			{
				intToVariable.put(no, (ClaimVariable) arg);
				rez = rez.concat("?#" + no + " ");
				no++;
			}
			else
				rez = rez.concat((String) arg + " ");
		}
		rez = rez + "]";
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
