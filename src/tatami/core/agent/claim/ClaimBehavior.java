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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import net.xqhs.util.logging.DumbLogger;
import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.kb.CognitiveComponent;
import tatami.core.agent.kb.KnowledgeBase;
import tatami.core.agent.kb.KnowledgeBase.KnowledgeDescription;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.sclaim.constructs.basic.ClaimBehaviorDefinition;
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
 * <p>
 * A behavior is activated by an agent event.
 * <p>
 * TODO: currently, a behavior is activated by one event and will be executed sequentially, without interruptions. The
 * Claim Component, as well as the whole agent, will wait for the completion of the behavior before processing any other
 * events. In the future, this may be change so as to allow the interruption of behaviors.
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 * 		
 */
public class ClaimBehavior
{
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
	 * The {@link Logger} instance to use. It will never be <code>null</code>, though it may fall back to a
	 * {@link DumbLogger}.
	 */
	protected transient Logger			log				= null;
	/**
	 * Information about how the behavior was activated. For future use, more activation records may be present.
	 * Currently, only one is used.
	 */
	protected AgentEvent				activationEvent	= null;
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
		if(log == null)
			log = DumbLogger.get();
		st.setLogLink(log);
	}
	
	/**
	 * This method is called whenever the behavior may be executed. It tests for the activation conditions and executes
	 * all the statements.
	 * 
	 * @param activatingEvent
	 *            - the {@link AgentEvent} that activates the behavior (according to the value of
	 *            {@link #getActivationType()}).
	 */
	public void activate(AgentEvent activatingEvent)
	{
		activationEvent = activatingEvent;
		while(currentStatement < cbd.getStatements().size())
			if(!handleStatement(cbd.getStatements().get(currentStatement++)))
			{
				// stop behavior and exit execution
				currentStatement = 0;
				st.clearSymbolTable(); // reinitialize symbol table
				return;
			}
		// end of behavior: stop behavior and exit execution
		currentStatement = 0;
		st.clearSymbolTable(); // reinitialize symbol table
	}
	
	/**
	 * @return the type of the behavior.
	 */
	public AgentEventType getActivationType()
	{
		switch(cbd.getBehaviorType())
		{
		case INITIAL:
			return AgentEventType.SIMULATION_START;
		case REACTIVE:
		{
			ClaimConstruct statement = cbd.getStatements().get(currentStatement);
			if(statement.getType() != ClaimConstructType.FUNCTION_CALL)
				throw new IllegalStateException("illegal start of behavior.");
			switch(((ClaimFunctionCall) statement).getFunctionType())
			{
			case RECEIVE:
				return AgentEventType.AGENT_MESSAGE;
			case INPUT:
				return AgentEventType.GUI_INPUT;
			default:
				throw new IllegalStateException("illegal start of behavior.");
			}
		}
		case PROACTIVE:
		case CYCLIC:
			throw new IllegalStateException("Unimplemented activation");
		}
		throw new IllegalStateException("behavior type not handled.");
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
				log.trace("if condition satisfied ", new Integer(currentStatement));
				
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
			return handleInput(args);
		case OUTPUT:
			handleOutput(args);
			return true;
		case PRINT:
			handlePrint(args);
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
	 * @param -
	 *            args the arguments of the call to "open" construct. The first element of the vector is the name of the
	 *            agent.
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
		
		boolean replyMode = false; // if true, the reply will be sent to an agent
		// TODO remove this and rebrand it as general communication?
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
			switch(activationEvent.getType())
			{
			case AGENT_MESSAGE:
				// log.trace("replying to message ", lastMessage);
				replyMode = true;
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
			claimComponent.sendMessage(message.toString(), receiver);
			log.info("sent ", message.toString());
		}
	}
	
	/**
	 * Handle the <code>receive</code> construct.
	 * <p>
	 * Format: <code>(receive (struct message field-1 field-2 ...))</code> or
	 * <code>(receive ?from (struct message field-1 field-2 ...))</code>
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
		String content = (String) activationEvent.getParameter(MessagingComponent.CONTENT_PARAMETER);
		String sender = (String) activationEvent.getParameter(MessagingComponent.SOURCE_PARAMETER);
		ClaimStructure received = ClaimStructure.parseString(content);
		
		if(received == null)
			return false;
			
		ClaimStructure toBind = (ClaimStructure) args.get(args.size() - 1); // the last part is the message
		
		if(!readValues(received.getFields(), toBind.getFields(), 0))
		{ // the message does not match the pattern
			
			log.trace("message not matching pattern []", content);
			return false;
		}
		
		log.trace("message received: []", content);
		// capture sender, if necessary
		if(args.size() >= 2)
			st.put((ClaimVariable) args.get(0), new ClaimValue(sender));
		return true;
	}
	
	/**
	 * There are two mechanisms for handling input:
	 * <p>
	 * For <i>active</i> inputs, the input will generate an agent event which will be handled by {@link ClaimComponent}.
	 * <p>
	 * For <i>passive</i> inputs, the behavior will use <code>getInput</code> to get the arguments of the input, which
	 * will be copied to the elements in the construct.
	 * <p>
	 * The difference between active and passive inputs is done by looking if the behavior has been activated by an
	 * input. If it has, the input is active.
	 * 
	 * @param args
	 *            - elements in the <code>input</code> construct.
	 * @return <code>true</code> if the input has been activated. TODO: what that actually means
	 */
	@SuppressWarnings("unchecked")
	protected boolean handleInput(Vector<ClaimConstruct> args)
	{
		Vector<Object> receivedInput;
		ClaimConstruct a0 = args.get(0);
		String inputComponent = ((a0.getType() == ClaimConstructType.VARIABLE) ? st.get((ClaimVariable) a0)
				: (ClaimValue) a0).toString();
				
		if(activationEvent.getType() == AgentEventType.GUI_INPUT)
			// FIXME this will make all inputs in the behavior active
			// input is active
			if(activationEvent.getParameter(VisualizableComponent.GUI_COMPONENT_EVENT_PARAMETER_NAME)
					.equals(inputComponent))
				receivedInput = (Vector<Object>) activationEvent
						.getParameter(VisualizableComponent.GUI_ARGUMENTS_EVENT_PARAMETER_NAME);
			else
				// incorrect input activated
				return false;
		else
			// input is passive
			receivedInput = claimComponent.getVisualizable().inputFromGUI(inputComponent);
			
		Vector<ClaimConstruct> sourceArgs = objects2values(receivedInput);
		Vector<ClaimConstruct> destinationArgs = new Vector<ClaimConstruct>(args);
		destinationArgs.remove(0); // this is the name of the component
		destinationArgs.remove(0); // this is the extraneous element that should be removed FIXME
		
		log.trace("reading input from [] to []", sourceArgs, destinationArgs);
		return readValues(sourceArgs, destinationArgs);
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
		claimComponent.getVisualizable().outputToGUI((String) outputComponent.getValue(), outV);
		log.trace("The output [] was written on []", outputV, outputComponent.getValue());
		return true;
	}
	
	protected boolean handlePrint(Vector<ClaimConstruct> args)
	{
		// log.trace("The message [] was printed on []", ,);
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
		 * // changing the value of the "parent" parameter of the agent: st.put(new ClaimVariable(parent here, true),
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
		
		// TODO
		
		// long timeout = Integer.parseInt((String) waitTimeConstant.getValue());
		// wakeUpAtTime = System.currentTimeMillis() + timeout;
		//
		// try
		// {
		// Thread.sleep(timeout);
		// } catch(InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		
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
			log.error("function [] not found in code attachments", functionName);
			return false;
		}
		
		boolean returnValue = false;
		Vector<ClaimConstruct> arguments = new Vector<ClaimConstruct>(
				flattenConstructs(args, 0, KeepVariables.KEEP_NONE));
				
		log.trace("invoking code attachment function [] with arguments: []", functionName, arguments);
		try
		{
			returnValue = ((Boolean) method.invoke(null, arguments)).booleanValue();
			readValues(arguments, args, 0);
			
		} catch(Exception e)
		{
			log.error("function [] invocation failed: ", functionName, e);
			e.printStackTrace(); // FIXME: put this into the log
		}
		
		return returnValue;
	}
	
	protected boolean handleKnowledgeManagement(ClaimFunctionType construct, Vector<ClaimConstruct> args)
	{
		KnowledgeBase kb = claimComponent.getKBase();
		switch(construct)
		{
		case ADDK:
		// function "add knowledge"
		{
			// after addK there's always a structure of knowledge
			SimpleKnowledge newKl = structure2Knowledge((ClaimStructure) args.get(0));
			
			// add knowledge to the knowledge base of agent
			kb.add(newKl);
			log.info(" added new knowledge " + newKl.printlnKnowledge() + " in behavior " + this.cbd.getName());
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
			return readValues(knowledge2Structure((SimpleKnowledge) result).getFields(), knowledgeStruct.getFields(),
					0);
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
	 */
	protected void handleForAllK(ClaimForAllK construct)
	{
		ClaimStructure klStructure = construct.getStructure();
		Vector<ClaimConstruct> statements = construct.getStatements();
		
		// map of pairs <Variable, value> used to bind fields in knowledge pattern that haven't been bound (variable)
		HashMap<ClaimVariable, ClaimValue> map = new HashMap<ClaimVariable, ClaimValue>();
		boolean matches = false;
		
		Vector<ClaimConstruct> klStructureFields = klStructure.getFields();
		Collection<KnowledgeDescription> knowledge = claimComponent.getKBase().getAll(structure2Knowledge(klStructure));
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
					 * it.next(); st.put(entry.getKey(), null); }
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
	 * Same as {@link #readValues(Vector, Vector, int)}, but without ignoring any elements.
	 * 
	 * @param sourceConstructs
	 *            : the constructs to take values from; must be (recursively) of type {@link ClaimValue},
	 *            {@link ClaimVariable} or {@link ClaimStructure}.
	 * @param destinationConstructs
	 *            : the constructs that could be bound to the values in the sources; same restriction as above applies
	 *            here too.
	 * @return <code>true</code> if the two lists of constructs match.
	 */
	protected boolean readValues(Vector<ClaimConstruct> sourceConstructs, Vector<ClaimConstruct> destinationConstructs)
	{
		return readValues(sourceConstructs, destinationConstructs, 0);
	}
	
	/**
	 * This method calls {@link #readValues(Vector, Vector, int, Map)} and performs the bindings returned.
	 * 
	 * @param sourceConstructs
	 *            : the constructs to take values from; must be (recursively) of type {@link ClaimValue},
	 *            {@link ClaimVariable} or {@link ClaimStructure}.
	 * @param destinationConstructs
	 *            : the constructs that could be bound to the values in the sources; same restriction as above applies
	 *            here too.
	 * @param ignore
	 *            : number of fields at the beginning to ignore in both lists
	 * @return <code>true</code> if the two lists of constructs match.
	 */
	protected boolean readValues(Vector<ClaimConstruct> sourceConstructs, Vector<ClaimConstruct> destinationConstructs,
			int ignore)
	{
		Map<ClaimVariable, ClaimValue> bindings = new HashMap<ClaimVariable, ClaimValue>(); // to bind at the end if OK
		boolean match = readValues(sourceConstructs, destinationConstructs, ignore, bindings);
		if(match)
			for(Map.Entry<ClaimVariable, ClaimValue> entry : bindings.entrySet())
				st.put(entry.getKey(), entry.getValue());
		return match;
	}
	
	/**
	 * The method matches the source and destination lists of constructs, based on their structure and values; it
	 * identifies potential bindings that can be done if the constructs match (instantiating variables in the
	 * destination structure based on the values in the source structure), but does not perform any binding.
	 * <p>
	 * If the destination field is a value, or an instantiated variable, it is checked if it matches the value of the
	 * source.
	 * <p>
	 * If the destination field is an uninstantiated variable, it will be instanced to the value of the corresponding
	 * source field (if the source is instantiated).
	 * <p>
	 * If both destination and source field are structures, they must have the same number of fields and the function is
	 * called recursively.
	 * <p>
	 * Even if some constructs do not match, the structures are explored completely and bindings are created.
	 * 
	 * @param sourceConstructs
	 *            : the constructs to take values from; must be (recursively) of type {@link ClaimValue},
	 *            {@link ClaimVariable} or {@link ClaimStructure}.
	 * @param destinationConstructs
	 *            : the constructs that could be bound to the values in the sources; same restriction as above applies
	 *            here too.
	 * @param ignore
	 *            : number of fields at the beginning to ignore in both lists
	 * @param bindingsOut
	 *            : the bindings that should be performed if the constructs match. Only destination constructs will be
	 *            bound.
	 * @return <code>true</code> if the two lists of constructs match.
	 */
	protected boolean readValues(Vector<ClaimConstruct> sourceConstructs, Vector<ClaimConstruct> destinationConstructs,
			int ignore, Map<ClaimVariable, ClaimValue> bindingsOut)
	{
		boolean match = true;
		if(sourceConstructs.size() != destinationConstructs.size())
			match = false;
		for(int i = ignore; i < Math.min(sourceConstructs.size(), destinationConstructs.size()); i++)
		{
			boolean res = readValue(sourceConstructs.get(i), destinationConstructs.get(i), bindingsOut);
			match = match && res;
		}
		
		return match;
	}
	
	/**
	 * The method checks if two claim constructs match and indicates what bindings should be done to match the
	 * destination to the source.
	 * <p>
	 * See {@link #readValues(Vector, Vector, int, Map)}.
	 * 
	 * @param sourceField
	 *            : the construct to take values from. Must be a value, variable or structure.
	 * @param destField
	 *            : the construct to put bind values to. Must be a value, variable or structure.
	 * @param bindingsOut
	 *            : bindings that should be performed in case the constructs match.
	 * @return <code>true</code> if the constructs can be unified.
	 */
	protected boolean readValue(ClaimConstruct sourceField, ClaimConstruct destField,
			Map<ClaimVariable, ClaimValue> bindingsOut)
	{
		ClaimConstructType destType = destField.getType();
		ClaimConstructType sourceType = sourceField.getType();
		
		if(destType == ClaimConstructType.STRUCTURE || sourceType == ClaimConstructType.STRUCTURE)
		{
			if(destType == sourceType)
				return readValues(((ClaimStructure) sourceField).getFields(), ((ClaimStructure) destField).getFields(),
						0, bindingsOut);
			return false;
		}
		
		if(!((destType == ClaimConstructType.VALUE || destType == ClaimConstructType.VARIABLE)
				&& (sourceType == ClaimConstructType.VALUE || sourceType == ClaimConstructType.VARIABLE)))
			return false;
			
		ClaimValue destValue = (destType == ClaimConstructType.VALUE) ? (ClaimValue) destField
				: getVariableValue((ClaimVariable) destField);
		ClaimValue sourceValue = (sourceType == ClaimConstructType.VALUE) ? (ClaimValue) sourceField
				: getVariableValue((ClaimVariable) sourceField);
		boolean assignable = (destType == ClaimConstructType.VARIABLE)
				&& ((destValue == null) || ((ClaimVariable) destField).isAssignable());
				
		if(assignable)
		{
			if((sourceValue != null) && ((destValue == null) || !destValue.getValue().equals(sourceValue.getValue())))
				bindingsOut.put((ClaimVariable) destField, (ClaimValue) sourceField);
			return true;
		}
		
		if((destValue == null) && (sourceValue == null))
			return true;
		if((destValue == null) || (sourceValue == null))
			return false;
		return destValue.getValue().equals(sourceValue.getValue());
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
		Vector<ClaimConstruct> fields = new Vector<ClaimConstruct>();
		fields.add(new ClaimValue("knowledge"));
		fields.add(new ClaimValue(knowledge.getKnowledgeType()));
		for(Object fieldValue : knowledge.getSimpleKnowledge())
			fields.add(new ClaimValue(fieldValue));
		ret.setFields(fields);
		return ret;
	}
	
	/**
	 * The method creates a vector of {@link ClaimConstruct} instances (more precisely {@link ClaimValue} instances),
	 * each of them containing an object from the argument vector.
	 * <p>
	 * The method uses {@link ClaimConstruct} instead of {@link ClaimValue} for compatibility with other methods in this
	 * file (such as {@link #readValues}).
	 * 
	 * @param args
	 * @return
	 */
	protected static Vector<ClaimConstruct> objects2values(Vector<Object> args)
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
		KEEP_NONE, /**
					 * Just like <code>KEEP_NONE</code>, but keeps reassignable variables as {@link ClaimVariable}
					 * instances.
					 */
		KEEP_REASSIGNABLE, /**
							 * Assigned variables are replaced by their values; Re-assignables and unassigned variables
							 * are kept as {@link ClaimVariable} instances.
							 */
		KEEP_UNISTANTIATED
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
				boolean assignable = ((ClaimVariable) cons).isAssignable();
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
				log.error("illegal construct type [] inside construct", constructType);
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
}
