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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.xqhs.util.logging.DumbLogger;
import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.LoggerSimple;
import net.xqhs.util.logging.Unit;
import tatami.HMI.src.PC.VisualizableComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.io.AgentActiveIO;
import tatami.core.agent.io.AgentIO;
import tatami.core.agent.kb.KnowledgeBase;
import tatami.core.agent.kb.KnowledgeBase.KnowledgeDescription;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.messaging.MessagingComponent;
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
 * <p>
 * There are three types of behaviors: initial, reactive, proactive. A behavior is activated by an agent event. A
 * behavior must have two parts (the separation is not emphasized in the adf source, however): the activation part and
 * the body.
 * <ul>
 * <li>an initial behavior is activated at the start of the simulation, by {@link AgentEventType#SIMULATION_START}.
 * There is usually no activation part, or there may be a set of <code>condition</code> statements.
 * <li>a reactive behavior is activated by receiving a message or by activating an active input -- events
 * {@link AgentEventType#AGENT_MESSAGE} and {@link AgentEventType#GUI_INPUT}, respectively. The activation begins with a
 * <code>receive</code> or <code>input</code> statement and may continue with a set of <code>condition</code>
 * statements.
 * <li>proactive behaviors are TODO: proactive behaviors.
 * </ul>
 * <p>
 * TODO: currently, a behavior is activated by one event and will be executed sequentially, without interruptions. The
 * Claim Component, as well as the whole agent, will wait for the completion of the behavior before processing any other
 * events. In the future, this may be change so as to allow the interruption of behaviors.
 * <p>
 * The class is structured into three parts: activation-related methods; methods for handling individual constructs, and
 * methods for converting S-CLAIM data structures from/into objects that can be easily processed by other classes.
 * <p>
 * Each S-CLAIM behavior has a reference to the S-CLAIM component ({@link ClaimComponent} instance) that owns it. The
 * component is able to obtain references to other components in the agent.
 * <p>
 * The behavior has its own symbol table, which is linked at construction with the symbol table of the component.
 * <p>
 * Once activated, the behavior executes statements in order until a non-nested statement returns false. This may be a
 * <code>condition</code> that is not fulfilled, a <code>receive</code>, <code>readK</code> or <code>input</code> that
 * fails (is unable to obtain a result matching with the given arguments), or a java function call that returns
 * <code>false</code>. For statements inside a <code>forAllK</code> statement, only the current cycle is discontinued
 * and the next cycle then follows (equivalent with the <code>continue</code> Java statement. // FIXME what about the
 * <code>while</code> statement?
 * <p>
 * If the execution of the behavior stops in the body of the behavior, a warning will be issued (otherwise, a trace
 * logging message will be issued).
 * <p>
 * S-CLAIM statements are internally divided into simple statements (called function calls), which begin with the name
 * of the function and continue with a list of arguments of which none contains an executable function, and complex
 * statements, which inside them contain other executable statements. Complex statements are <code>if</code>,
 * <code>condition</code>, <code>while</code>, <code>forAllK</code>. Simple statements can either be primitive calls, or
 * Java function calls.
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Marius-Tudor Benea
 * @author Andrei Olaru
 * 
 */
public class ClaimBehavior
{
	/**
	 * Constants used in converting S-CLAIM variables to objects (in
	 * {@link ClaimBehavior#evaluateConstruct(List, KeepVariables)}).
	 * 
	 * @author Andrei Olaru
	 */
	protected enum KeepVariables
	{
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
		 * Assigned variables are replaced by their values; Re-assignable and unassigned variables are kept as
		 * {@link ClaimVariable} instances.
		 */
		KEEP_ASSIGNABLE,

		/**
		 * All variables are kept as they are.
		 */
		KEEP_ALL,
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
	 * The {@link Logger} instance to use. It will never be <code>null</code>, though it may fall back to a
	 * {@link DumbLogger}.
	 */
	protected transient Logger			log							= null;
	/**
	 * Information about how the behavior was activated. For future use, more activation records may be present.
	 * Currently, only one is used.
	 */
	protected AgentEvent				activationEvent				= null;
	/**
	 * Is <code>true</code> while the execution of the behavior is in its activation part, before beginning the body.
	 */
	protected boolean					activating;
	/**
	 * Is <code>true</code> before the behavior finishes executing the first statement in a reactive behavior (i.e. a
	 * receive or an input).
	 */
	protected boolean					firstStatement;
	/**
	 * The prefix added to the first argument in an input/output construct that indicates that input/output should be
	 * taken from an agent component that implements {@link AgentIO} or {@link AgentActiveIO}.
	 */
	public static final String			IO_COMPONENT_NAME_PREFIX	= "@";
	// ============= What follows are elements for tracing and debugging, especially the trace() method.
	/**
	 * The number (index) of the statement currently being processed. This should only be used for debugging.
	 */
	protected int						currentStatement;
	/**
	 * If <code>true</code>, the tracing messages in the behavior will be buffered and the entire buffer (
	 * {@link #logBuffer} displayed at the end.
	 */
	protected boolean					bufferLog					= true;
	/**
	 * The buffer to hold the tracing messages.
	 */
	protected String					logBuffer					= null;

	/**
	 * The list of agent names that should be debugged (output tracing messages), if {@link #debugAllAgents} is set to
	 * <code>false</code>.
	 */
	static final protected String[]	debuggedAgents		= new String[] {};
	/**
	 * If true, all agents will be traced, regardless of the value of {@link #debuggedAgents}.
	 */
	static final protected boolean	debugAllAgents		= true;
	/**
	 * The list of behavior names that should be debugged (output tracing messages), if {@link #debugAllAgents} is set
	 * to <code>false</code>.
	 */
	static final protected String[]	debuggedBehaviors	= new String[] {};
	/**
	 * If true, all behaviors will be traced, regardless of the value of {@link #debuggedBehaviors}.
	 */
	static final protected boolean	debugAllBehaviors	= true;
	/**
	 * The value is computed at the creation of the behavior to know if the behavior should be traced or not.
	 */
	protected boolean				isDebugging;

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
		if (log == null)
			log = DumbLogger.get();
		st.setLogLink(log);

		initDebugging();
	}

	/**
	 * Initializes the {@link #isDebugging} member to know if this particular behavior should be debugged or not.
	 */
	@SuppressWarnings("unused")
	protected void initDebugging()
	{
		Arrays.sort(debuggedAgents);
		Arrays.sort(debuggedBehaviors);
		isDebugging = (debugAllAgents || Arrays.binarySearch(debuggedAgents, claimComponent.getAgentName()) > 0)
				&& (debugAllBehaviors || Arrays.binarySearch(debuggedBehaviors, cbd.getName()) > 0);
	}

	/**
	 * Traces the execution of an S-CLAIM statement, in case the behavior is being debugged. If so, the arguments are
	 * passed verbatim to {@link Logger#trace(String, Object...)}, with the exception that the message is prepended with
	 * the statement counter.
	 * 
	 * @param message
	 *            - the message to be displayed.
	 * @param arguments
	 *            - the objects to be inserted into the message.
	 */
	protected void trace(String message, Object... arguments)
	{
		if (isDebugging)
		{
			if (bufferLog)
			{
				logBuffer += "\t\t\t\t\t\t\t[" + currentStatement + "] " + compose(message, arguments) + "\n";
			} else
				log.trace("\t\t\t [SCLAIM:" + cbd.getName() + ":" + currentStatement + "] " + message, arguments);
		}
	}

	/**
	 * Creates a single logging message with the entire log of the behavior execution. See {@link #bufferLog}.
	 * 
	 * @param shorten
	 *            - if <code>true</code>, the buffer will be reduced to one line, with no tabulation.
	 */
	protected void putLog(boolean shorten)
	{
		if (isDebugging && (logBuffer != null))
		{
			if (shorten)
				logBuffer = logBuffer.replace('\n', '|').replace('\t', ' ');
			log.trace("\t\t\t SCLAIM:" + cbd.getName() + " []", logBuffer);
		}
	}

	/**
	 * Copy of the <code>compose</code> method in {@link Unit}.
	 * 
	 * @param message
	 *            - message.
	 * @param objects
	 *            - arguments.
	 * @return assemply.
	 */
	protected static String compose(String message, Object[] objects)
	{
		String[] parts = message.split(LoggerSimple.ARGUMENT_PLACEHOLDER, objects.length + 1);
		// there are enough objects for all parts
		// there may be more objects than parts
		String ret = parts[0];
		for (int i = 0; i < parts.length - 1; i++)
		{
			ret += LoggerSimple.ARGUMENT_BEGIN + objects[i] + LoggerSimple.ARGUMENT_END;
			ret += parts[i + 1];
		}
		// deal with the rest of the objects
		for (int i = parts.length - 1; i < objects.length; i++)
			ret += LoggerSimple.ARGUMENT_BEGIN + objects[i] + LoggerSimple.ARGUMENT_END;

		return ret;
	}

	/**
	 * @return the type of the behavior, as the type of the agent event that may trigger the behavior.
	 */
	public AgentEventType getActivationType()
	{
		switch (cbd.getBehaviorType())
		{
		case INITIAL:
			return AgentEventType.SIMULATION_START;
		case REACTIVE:
		{
			ClaimConstruct statement = cbd.getStatements().firstElement();
			if (statement.getType() != ClaimConstructType.FUNCTION_CALL)
				throw new IllegalStateException("illegal start of behavior.");
			switch (((ClaimFunctionCall) statement).getFunctionType())
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
	 * This method is called whenever the behavior may be executed. It tests for the activation conditions and executes
	 * all the statements.
	 * <p>
	 * Execution stops (and the statement counter is reset) when a statement returns <code>false</code>, indicating some
	 * sort of failure (see {@link ClaimBehavior}).
	 * <p>
	 * <b>Note</b> that the statements inside <code>if</code>, <code>forAllK</code> and <code>while</code> are treated
	 * (including return values) by the methods handling those respective primitives. The statement counter is, as well,
	 * handled by the respective methods while inside the statements.
	 * 
	 * @param activatingEvent
	 *            - the {@link AgentEvent} that activates the behavior (according to the value of
	 *            {@link #getActivationType()}).
	 */
	public void execute(AgentEvent activatingEvent)
	{
		activationEvent = activatingEvent;
		activating = true;
		firstStatement = true;
		boolean executionFailure = false;
		if (bufferLog)
			logBuffer = "\n";
		trace("entering with event []", activatingEvent);

		for (ClaimConstruct statement : cbd.getStatements())
		{ // the behavior stops whenever a statement returns false (for statements that are not nested)
			if (cbd.getBehaviorType() == ClaimBehaviorType.INITIAL)
				activating = false;
			if (activating && firstStatement
					&& !((statement.getType() == ClaimConstructType.FUNCTION_CALL)
							&& ((((ClaimFunctionCall) statement).getFunctionType() == ClaimFunctionType.RECEIVE)
									|| (((ClaimFunctionCall) statement).getFunctionType() == ClaimFunctionType.INPUT))))
				activating = false;
			if (activating && !firstStatement && (statement.getType() != ClaimConstructType.CONDITION))
				activating = false; // move into behavior body

			if (statement.getType() == ClaimConstructType.FUNCTION_CALL)
				trace("executing statement []:[] with args [].", statement.getType(),
						((ClaimFunctionCall) statement).getFunctionType(),
						((ClaimFunctionCall) statement).getArguments());
			else
				trace("executing statement [].", statement.getType());

			if (!handleStatement(statement))
			{ // stop behavior and exit execution
				executionFailure = !activating;
				break;
			}
			currentStatement++;
			firstStatement = false;
		}
		// end of behavior: stop behavior and exit execution
		currentStatement = 0;
		st.clearSymbolTable(); // reinitialize symbol table
		if (activating)
			logBuffer = "activation failed.\n" + logBuffer;
		if (executionFailure)
			if (bufferLog)
				log.warn("\t\t\t SCLAIM:[] [] behavior terminated (statement failure) at statement [].", cbd.getName(),
						logBuffer, new Integer(currentStatement));
			else
				log.warn("behavior [] terminated (statement failure) at statement [].", cbd.getName(),
						new Integer(currentStatement));
		else if (bufferLog)
			putLog(activating);
	}

	/**
	 * Handle one statement in the behavior. The code here handles complex statements and deferres to
	 * {@link #handleCall(ClaimFunctionCall)} the handling of simple statements.
	 * 
	 * @param statement
	 *            - the current statement.
	 * @return <code>true</code> if the behavior should continue; <code>false</code> otherwise.
	 */
	protected boolean handleStatement(ClaimConstruct statement)
	{
		switch (statement.getType())
		{
		case FUNCTION_CALL:
			return handleCall((ClaimFunctionCall) statement);
		case CONDITION:
			return handleCall(((ClaimCondition) statement).getCondition());
		case IF:
			boolean condition = handleCall(((ClaimIf) statement).getCondition());
			if (condition)
			{
				trace("if condition satisfied.");

				Vector<ClaimConstruct> trueBranch = ((ClaimIf) statement).getTrueBranch();
				for (ClaimConstruct trueBranchStatement : trueBranch)
					if (!handleStatement(trueBranchStatement))
						return false;
			} else
			{
				trace("if condition not satisfied.");

				Vector<ClaimConstruct> falseBranch = ((ClaimIf) statement).getFalseBranch();
				if (falseBranch != null)
					for (ClaimConstruct falseBranchStatement : falseBranch)
						if (!handleStatement(falseBranchStatement))
							return false;
			}
			break;
		case FORALLK:
			handleForAllK((ClaimForAllK) statement);
			break;
		case WHILE:
			handleWhile((ClaimWhile) statement);
			break;
		default:
			log.warn("Unreachable area reached");
			return false;
		}
		return true; // normal exit from forAllK, while, if
	}

	/**
	 * Handle a simple statement. The return value indicates the success of the statement. The value of all statements
	 * is returned directly, although it may not have sense for some statement types to return false.
	 * 
	 * @param function
	 *            : Claim construct / statement
	 * @return <code>true</code> if the behavior should continue normally or if the condition represented by the
	 *         statement is true. <code>false</code> if the current behavior or cycle should stop.
	 */
	protected boolean handleCall(ClaimFunctionCall function)
	{
		// arguments of the function
		Vector<ClaimConstruct> args = function.getArguments();

		switch (function.getFunctionType())
		{
		case SEND:
			return handleSend(args);
		case RECEIVE:
			return handleReceive(args);
		case IN:
			return handleIn(args);
		case OUT:
			// TODO
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
			return handleOutput(args);
		case PRINT:
			return handlePrint(args);
		case WAIT:
			return handleWait(args);
		case JAVA:
			return handleJavaCall(function.getFunctionName(), args);
		}
		log.warn("Unreachable area reached");
		return false;
	}

	// ==================================== ^^ -- ACTIVATION AND GENERAL EXECUTION -- ^^ =============================
	// ===============================================================================================================
	// ==================================== vv --------- STATEMENT HANDLING --------- vv =============================

	// ==================================== AGENT MANAGEMENT

	/**
	 * Creates a new agent, based on the given arguments
	 * 
	 * @param args
	 *            - the arguments of the call to "new" construct. The first two elements of the vector are the name and
	 *            the class name of the agent. The rests are arguments that the created agent will process after
	 *            creation
	 * @return - whether the creation was successful or not
	 */
	@SuppressWarnings("static-method")
	protected boolean handleNew(Vector<ClaimConstruct> args)
	{
		/*
		 * ClaimValue agentName = (ClaimValue) args.get(0); ClaimValue agentClassName = (ClaimValue)args.get(1);
		 * 
		 * /*try { ((ParametricComponent) this.myAgent).getContainerController().createNewAgent((String)
		 * agentName.getValue(), "core.claim.ClaimAgent", args.subList(1, args.size()).toArray()); }
		 * catch(StaleProxyException e) { e.printStackTrace(); return false; }
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
	 * @param args
	 *            - the arguments of the call to "open" construct. The first element of the vector is the name of the
	 *            agent.
	 * @return - whether the deletion was successful or not
	 */
	@SuppressWarnings("static-method")
	protected boolean handleOpen(Vector<ClaimConstruct> args)
	{
		// ClaimValue agentName = (ClaimValue) args.get(0);

		/*
		 * try { ((ParametricComponent) this.myAgent).getContainerController().getAgent((String)
		 * agentName.getValue()).kill(); } catch(StaleProxyException e) { e.printStackTrace(); return false; }
		 * catch(ControllerException e) { e.printStackTrace(); return false; }
		 */

		// TODO

		return true;
	}

	/**
	 * @param args
	 * @return
	 */
	@SuppressWarnings("static-method")
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

		// TODO
		return true;
	}

	// ==================================== AGENT COMMUNICATION

	/**
	 * The send construct.
	 * <p>
	 * Variants:
	 * <p>
	 * <code>(send agent-id (struct message field-1 field-2 ...))</code> - normal send
	 * <p>
	 * <code>(send (struct message field-1 field-2 ...))</code> - send a reply to the received message
	 * <p>
	 * TODO: - web service access
	 * 
	 * @param args
	 * @return should always return <code>true</code>, except when something goes wrong with sending the message.
	 */
	protected boolean handleSend(Vector<ClaimConstruct> args)
	{
		String receiver = null; // receiver of the message to be sent
		int parametersAdjust = 0; // offset argument number if there is no receiver field

		boolean replyMode = false; // if true, the reply will be sent to an agent
		// TODO remove this and rebrand it as general communication?
		boolean webServiceInvocation = false; // if true, the send is a web service invocation
		boolean expectServiceReturn = false; // if true, wait for a reply (blocking call)

		// decide upon the receiver(s) of the message
		switch (args.get(0).getType())
		{
		case VARIABLE:
			receiver = (String) getVariableValue(((ClaimVariable) args.get(0))).getValue();
			break;
		case VALUE:
			receiver = (String) ((ClaimValue) args.get(0)).getValue();
			break;
		case STRUCTURE:
			// there is no receiver information; should reply to the original sender
			switch (activationEvent.getType())
			{
			case AGENT_MESSAGE:
				// trace("replying to message ", lastMessage);
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

		if (args.size() > 3)
		{
			if (webServiceInvocation)
				expectServiceReturn = true;
			else
				log.error("Too many arguments for send primitive when not in web service mode.");
		}

		if (receiver == null)
		{
			log.error("Unable to determine message receiver");
			return false;
		}

		// second argument is a structure that represents all the message's fields
		ClaimStructure message = null;
		message = st.bindStructure((ClaimStructure) args.get(1 - parametersAdjust));

		if (webServiceInvocation)
		{
			// TODO: Web service invocation
		} else
		{
			claimComponent.sendMessage(message.toString(), receiver);
			trace("sent to [] message [].", receiver, message.toString());
		}
		return true;
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
		String content = activationEvent.get(MessagingComponent.CONTENT_PARAMETER);
		String sender = activationEvent.get(MessagingComponent.SOURCE_PARAMETER);
		ClaimStructure received = ClaimStructure.parseString(content);

		if (received == null)
			return false;

		ClaimStructure toBind = (ClaimStructure) args.get(args.size() - 1); // the last part is the message

		if (!readValues(received.getFields(), toBind.getFields(), 0))
		{ // the message does not match the pattern

			trace("message [] not matching pattern []", content, toBind);
			return false;
		}

		trace("message received from []: []", sender, content);
		// capture sender, if necessary
		if (args.size() >= 2)
			st.put((ClaimVariable) args.get(0), new ClaimValue(sender));
		return true;
	}

	// ==================================== INPUT / OUTPUT

	/**
	 * There are two mechanisms for handling input:
	 * <p>
	 * For <i>active</i> inputs, the input will generate an agent event which will be handled by {@link ClaimComponent}.
	 * <p>
	 * For <i>passive</i> inputs, the behavior will use <code>getInput</code> to get the arguments of the input, which
	 * will be copied to the elements in the construct.
	 * <p>
	 * The difference between active and passive inputs is done by looking if the behavior has been activated by an
	 * input. If it has, and we are in the activation part of the behavior, then the input is active.
	 * <p>
	 * If the first argument of the construct begins with {@value #IO_COMPONENT_NAME_PREFIX}, it will be considered as a
	 * component name to get input from (normally input is retrieved from the <code>VisualizableComponent</code>. The
	 * component must implement {@link AgentIO}. The rest of the arguments will be considered normally.
	 * 
	 * @param args
	 *            - elements in the <code>input</code> construct.
	 * @return <code>true</code> if the input has been activated (for active inputs) and if value transfer happened with
	 *         no errors.
	 */
	@SuppressWarnings("unchecked")
	protected boolean handleInput(Vector<ClaimConstruct> args)
	{
		Vector<Object> receivedInput;
		Vector<ClaimConstruct> workArgs = new Vector<ClaimConstruct>(args); // FIXME: workaround for error-prone behavior

		String IOcomponent = null;
		ClaimConstruct arg0 = workArgs.get(0);
		if ((arg0.getType() == ClaimConstructType.VALUE)
				&& ((ClaimValue) arg0).toString().startsWith(IO_COMPONENT_NAME_PREFIX))
		{
			IOcomponent = ((ClaimValue) arg0).toString().substring(1);
			workArgs.remove(0);
		}

		// get input component
		arg0 = workArgs.get(0);
		String inputComponent = ((arg0.getType() == ClaimConstructType.VARIABLE) ? st.get((ClaimVariable) arg0)
				: (ClaimValue) arg0).toString();

		// get the input
		if (activating)
		{
			// input is active / the behavior is input-activated
			if (activationEvent.getType() != AgentEventType.GUI_INPUT)
				throw new IllegalStateException(
						"input cannot be in activation area if behavior is not input-activated");
			if (activationEvent.get(VisualizableComponent.GUI_COMPONENT_EVENT_PARAMETER_NAME)
					.equals(inputComponent))
				receivedInput = (Vector<Object>) activationEvent
						.getObject(VisualizableComponent.GUI_ARGUMENTS_EVENT_PARAMETER_NAME);
			else
			{
				log.error("incorrect input activated: expected [] vs activated [].", inputComponent,
						activationEvent.get(VisualizableComponent.GUI_COMPONENT_EVENT_PARAMETER_NAME));
				return false;
			}
			trace("The input is active.");
		} else
		{
			// input is passive
			trace("The input is passive.");
			receivedInput = claimComponent.inputFromIO(IOcomponent, inputComponent);
		}

		if (receivedInput == null)
		{
			log.error("Received input from []:[] was null.", IOcomponent == null ? "GUI" : IOcomponent, inputComponent);
			return false;
		}

		// must copy values from source (activated / read input) into destination (input construct elements)
		Vector<ClaimConstruct> sourceArgs = objects2values(receivedInput);
		Vector<ClaimConstruct> destinationArgs = new Vector<ClaimConstruct>(workArgs);
		destinationArgs.remove(0); // this is the name of the component

		trace("reading input from []:[] : [] to []", IOcomponent == null ? "GUI" : IOcomponent, inputComponent,
				sourceArgs, destinationArgs);
		return readValues(sourceArgs, destinationArgs);
	}

	/**
	 * The construct outputs to an output port.
	 * <p>
	 * If the first argument of the construct begins with {@value #IO_COMPONENT_NAME_PREFIX}, it will be considered as a
	 * component name to put output to (normally output is sent to the <code>VisualizableComponent</code>. The component
	 * must implement {@link AgentIO}. The rest of the arguments will be considered normally.
	 * 
	 * @param args
	 *            - elements in the <code>input</code> construct.
	 * @return normally <code>true</code>, i.e. the output has been done correctly (output component was found).
	 */
	protected boolean handleOutput(Vector<ClaimConstruct> args)
	{
		String IOcomponent = null;
		Vector<ClaimConstruct> workArgs = new Vector<ClaimConstruct>(args); // FIXME: workaround for error-prone behavior
		
		ClaimConstruct arg0 = workArgs.get(0);
		if ((arg0.getType() == ClaimConstructType.VALUE)
				&& ((ClaimValue) arg0).toString().startsWith(IO_COMPONENT_NAME_PREFIX))
		{
			IOcomponent = ((ClaimValue) arg0).toString().substring(1);
			workArgs.remove(0);
		}

		String outputComponent = (String) ((ClaimValue) workArgs.get(0)).getValue();
		Vector<Object> outputV = constructs2Objects(workArgs, 1); // get all but the component name

		claimComponent.outputToIO(IOcomponent, outputComponent, outputV);
		trace("output [] was written on []:[]", outputV, IOcomponent == null ? "GUI" : IOcomponent, outputComponent);
		return true;
	}

	// ==================================== EXECUTION MONITORING & CONTROL

	/**
	 * The primitive prints out the given arguments to the console.
	 * 
	 * @param args
	 *            - elements in the <code>print</code> construct.
	 * @return always returns <code>true</code>.
	 */
	protected boolean handlePrint(Vector<ClaimConstruct> args)
	{
		Object[] outputV = constructs2Objects(args, 0).toArray();
		String msg = compose("", outputV);

		System.out.println(msg);

		trace("The message [] was printed on console", msg);
		return true;
	}

	@SuppressWarnings("static-method")
	protected boolean handleWait(Vector<ClaimConstruct> args)
	{
		// after wait there's always a constant
		// ClaimValue waitTimeConstant = (ClaimValue) args.get(0);

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

	/**
	 * Initializes a while loop based on the condition contained in the variable {@code construct}
	 * 
	 * @param construct
	 *            : the intermediate code representation of while
	 */
	protected void handleWhile(ClaimWhile construct)
	{
		Vector<ClaimConstruct> statements = construct.getStatements();

		while (handleCall(construct.getCondition()))
		{
			for (ClaimConstruct statement : statements)
				handleStatement(statement);
		}
	}

	// ==================================== KNOWLEDGE MANAGEMENT

	/**
	 * Method that handles all knowledge management primitives (except <code>forAllK</code>), namely <code>addK</code>,
	 * <code>readK</code> and <code>removeK</code>.
	 * <p>
	 * For <code>addK</code> and <code>removeK</code>, the method only affects the knowledge base, and nothing else. A
	 * <code>readK</code> call affects both the execution (the return value influences the control flow of the behavior)
	 * and the symbol table of the behavior (the primitive can bind variables to values found in the knowledge base).
	 * <p>
	 * Considering a knowledge management primitive <code>KP</code>, the construct is always of the form
	 * <code>(KP (struct knowledge arg1 arg2 arg3 ...))</code>
	 * 
	 * @param construct
	 *            - the construct that was used in the code.
	 * @param args
	 *            - the arguments received by the construct.
	 * @return for <code>addK</code> and <code>removeK</code>, always <code>true</code>; for <code>readK</code>, returns
	 *         <code>true</code> if a matching piece of knowledge was found, and <code>false</code> otherwise (no
	 *         matching knowledge found).
	 */
	protected boolean handleKnowledgeManagement(ClaimFunctionType construct, Vector<ClaimConstruct> args)
	{
		KnowledgeBase kb = claimComponent.getKBase();

		// the primitive is always followed by the piece of knowledge (may be a pattern).
		// TODO: support multiple types of knowledge, not just SimpleKnowledge.
		ClaimStructure kStruct = (ClaimStructure) args.get(0);
		SimpleKnowledge k = structure2Knowledge(kStruct);

		switch (construct)
		{
		case ADDK:
		// function "add knowledge"
		{
			if (kb.add(k))
				trace("added new knowledge [].", k.getTextRepresentation());
			else
				trace("adding knowledge [] had no effect.", k.getTextRepresentation());

			return true;
		}
		case READK:
		// function "check if agent already has the knowledge"
		// If yes : return true, also put the value of variable in knowledge pattern into the symbol table
		{
			// first find the result
			KnowledgeDescription result = kb.getFirst(k);
			if (result != null)
			{ // then [attempt to] bind the variables
				if (readValues(knowledge2Structure((SimpleKnowledge) result).getFields(), kStruct.getFields(), 0))
				{
					trace("knowledge [] read into [].", k.getTextRepresentation(), kStruct);
					return true;
				}
				trace("knowledge [] could not be matched into [].", k.getTextRepresentation(), kStruct);
				return false;
			}
			trace("no knowledge matching pattern [] was found.", k);
			return false; // knowledge was not found
		}
		case REMOVEK:
		// function "remove knowledge"
		{
			if (kb.remove(k))
				trace("removed knowledge [].", k.getTextRepresentation());
			else
				trace("removing knowledge [] had no effect.", k.getTextRepresentation());
			return true;
		}
		default:
			// should not be here
			return true; // unreachable code
		}
	}

	/**
	 * Read all records of knowledge in the knowledge base that match the pattern. For each possible match, execute the
	 * subordinate statements (forming a cycle).
	 * <p>
	 * As the cycle can be executed an undetermined number of times, it is required that any variable that is not bound
	 * when entering the <code>forAllK</code> construct is a re-assignable variable. // TODO implement this
	 * <p>
	 * Otherwise, if the first cycle succeeds, it will make a first binding for the variables, and then at the second
	 * cycle the re-binding will fail, leading to a non-intuitive behavior where the cycle will only be performed once
	 * although more matches exist. By failing early (before executing the first cycle and before binding any variables,
	 * the behavior is consistent: either no cycle is performed, either all of them are performed).
	 * 
	 * @param construct
	 *            - the entire description of the <code>forAllK</code> construct.
	 * @return <code>true</code>, or <code>false</code> if not fulfilling the requirement that all un-assigned variables
	 *         are re-assignable.
	 */
	protected boolean handleForAllK(ClaimForAllK construct)
	{
		ClaimStructure kStruct = construct.getStructure();

		// check condition that the arguments will be assignable multiple times
		for (ClaimConstruct c : evaluateConstructs(kStruct.getFields(), 1, KeepVariables.KEEP_REASSIGNABLE))
			if ((c instanceof ClaimVariable) && !((ClaimVariable) c).isReAssignable())
			{
				log.error("The variable [] is not assigned, nor re-assignable", c);
				return false;
			}

		// get all possible matches
		// FIXME: support other types of knowledge, other than SimpleKnowledge.
		Collection<KnowledgeDescription> knowledge = claimComponent.getKBase().getAll(structure2Knowledge(kStruct));

		int startStatement = currentStatement;
		int nStatements = construct.getStatements().size();
		for (KnowledgeDescription kd : knowledge)
		{
			currentStatement = startStatement;

			// attempt to match with construct
			ClaimStructure kbMatch = knowledge2Structure((SimpleKnowledge) kd);

			if (readValues(kbMatch.getFields(), kStruct.getFields(), 1))
			{ // we are able to read the source into the destination, bindings already done
				// execute cycle
				trace("executing for match [].", kbMatch);
				for (ClaimConstruct statement : construct.getStatements())
				{
					currentStatement++;
					if (!handleStatement(statement))
						// statement fails -> abort this cycle
						break;
				}
			} else
				trace("Unable to read match [] into arguments [].", kbMatch, kStruct);
		}
		currentStatement += nStatements - 1;
		return true;
	}

	/**
	 * Create a {@link SimpleKnowledge} instance from a {@link ClaimStructure}. // TODO: support other types of
	 * knowledge, other than SimpleKnowledge.
	 * 
	 * @param knowledge
	 *            : a structure <code>(struct knowledge knowledge-type knowledge-field-1 knowledge-field-2 ...)</code>
	 * @return : a {@link SimpleKnowledge} instance build from the structure.
	 */
	protected SimpleKnowledge structure2Knowledge(ClaimStructure knowledge)
	{
		Vector<String> fields = constructs2Strings(knowledge.getFields(), 1, false); // ignore 'knowledge'

		SimpleKnowledge newKl = new SimpleKnowledge();
		newKl.setKnowledgeType(fields.remove(0));
		newKl.setSimpleKnowledge(new LinkedList<String>(fields));
		return newKl;
	}

	/**
	 * Create a {@link ClaimStructure} from a {@link SimpleKnowledge} instance.
	 * 
	 * @param knowledge
	 *            - the knowledge record.
	 * @return a structure with the form
	 *         <code>(struct knowledge knowledge-type knowledge-field-1 knowledge-field-2 ...)</code>
	 */
	protected static ClaimStructure knowledge2Structure(SimpleKnowledge knowledge)
	{
		ClaimStructure ret = new ClaimStructure();
		Vector<ClaimConstruct> fields = new Vector<ClaimConstruct>();
		fields.add(new ClaimValue("knowledge"));
		fields.add(new ClaimValue(knowledge.getKnowledgeType()));
		for (Object fieldValue : knowledge.getSimpleKnowledge())
			fields.add(new ClaimValue(fieldValue));
		ret.setFields(fields);
		return ret;
	}

	// ==================================== JAVA FUNCTIONS

	protected boolean handleJavaCall(String functionName, Vector<ClaimConstruct> args)
	{
		Method method = null;
		for (Class<?> clazz : this.cbd.getMyAgent().getCodeAttachments())
			try
			{
				method = clazz.getDeclaredMethod(functionName, Vector.class);
				break;
			} catch (SecurityException e)
			{ // method not accessible here; carry on;
			} catch (NoSuchMethodException e)
			{ // method not found here; carry on;
			}
		if (method == null)
		{
			log.error("function [] not found in code attachments", functionName);
			return false;
		}

		boolean returnValue = false;
		Vector<ClaimConstruct> arguments = new Vector<ClaimConstruct>(
				evaluateConstructs(args, 0, KeepVariables.KEEP_NONE));

		trace("invoking code attachment function [] with arguments: []", functionName, arguments);
		try
		{
			returnValue = ((Boolean) method.invoke(null, arguments)).booleanValue();
			readValues(arguments, args, 0);

		} catch (Exception e)
		{
			log.error("function [] invocation failed: ", functionName, e);
			e.printStackTrace(); // FIXME: put this into the log
		}
		trace("code attachment function result [] with arguments [].", returnValue ? "OK" : "fail", arguments);

		return returnValue;
	}

	// ==================================== ^^ --------- STATEMENT HANDLING --------- ^^ =============================
	// ===============================================================================================================
	// ==================================== vv --- AUXILIARY / CONVERSION METHODS --- vv =============================

	// ==================================== READ VALUES (from a set of S-CLAIM values / variables into another)

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

		if (destType == ClaimConstructType.STRUCTURE || sourceType == ClaimConstructType.STRUCTURE)
		{
			if (destType == sourceType)
				return readValues(((ClaimStructure) sourceField).getFields(), ((ClaimStructure) destField).getFields(),
						0, bindingsOut);
			return false;
		}

		if (!((destType == ClaimConstructType.VALUE || destType == ClaimConstructType.VARIABLE)
				&& (sourceType == ClaimConstructType.VALUE || sourceType == ClaimConstructType.VARIABLE)))
			return false;

		ClaimValue destValue = (destType == ClaimConstructType.VALUE) ? (ClaimValue) destField
				: getVariableValue((ClaimVariable) destField);
		ClaimValue sourceValue = (sourceType == ClaimConstructType.VALUE) ? (ClaimValue) sourceField
				: getVariableValue((ClaimVariable) sourceField);
		boolean assignable = (destType == ClaimConstructType.VARIABLE)
				&& ((destValue == null) || ((ClaimVariable) destField).isReAssignable());

		if (assignable)
		{
			if ((sourceValue != null) && ((destValue == null) || !destValue.getValue().equals(sourceValue.getValue())))
				bindingsOut.put((ClaimVariable) destField, (ClaimValue) sourceField);
			return true;
		}

		if ((destValue == null) && (sourceValue == null))
			return true;
		if ((destValue == null) || (sourceValue == null))
			return false;
		return destValue.getValue().equals(sourceValue.getValue());
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
		if (sourceConstructs.size() != destinationConstructs.size())
			match = false;
		for (int i = ignore; i < Math.min(sourceConstructs.size(), destinationConstructs.size()); i++)
		{
			boolean res = readValue(sourceConstructs.get(i), destinationConstructs.get(i), bindingsOut);
			match = match && res;
		}

		return match;
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
		if (match)
			for (Map.Entry<ClaimVariable, ClaimValue> entry : bindings.entrySet())
				st.put(entry.getKey(), entry.getValue());
		return match;
	}

	// ==================================== OBJECT - CLAIM VALUE conversion

	/**
	 * Get value of variable from the symbol table. First, search the variable in behavior's symbol table. If not found,
	 * search in agent's symbol table (this functionality is covered by the {@link SymbolTable} class.
	 * 
	 * @param variable
	 *            : the variable
	 * @return : Value of variable if found; <code>null</code> otherwise
	 */
	protected ClaimValue getVariableValue(ClaimVariable variable)
	{
		return st.get(variable);
	}

	/**
	 * The method creates a vector of {@link ClaimConstruct} instances (more precisely {@link ClaimValue} instances),
	 * each of them containing an object from the argument vector.
	 * <p>
	 * The method uses {@link ClaimConstruct} instead of {@link ClaimValue} for compatibility with other methods in this
	 * file (such as {@link #readValues}).
	 * 
	 * @param args
	 *            - the objects to convert to S-CLAIM values.
	 * @return the values obtained.
	 */
	protected static Vector<ClaimConstruct> objects2values(Vector<Object> args)
	{
		Vector<ClaimConstruct> ret = new Vector<ClaimConstruct>(args.size());
		for (Object arg : args)
			ret.add(new ClaimValue(arg));
		return ret;
	}

	/**
	 * Evaluates a construct to a value (or sometimes a variable).
	 * <p>
	 * The method only works on S-CLAIM variables, values, or function calls (these are evaluated and transformed to
	 * boolean values); structures are not accepted.
	 * <p>
	 * Variables are treated as follows, depending on the {@link KeepVariables} setting: they are transformed to values
	 * (or <code>null</code>, if uninstantiated); only uninstantiated variables are kept (with the purpose of
	 * instantiating them later) and convert the other to values; or all variables are kept (with the purpose of
	 * changing their value).
	 * <p>
	 * In case unassigned variables are not kept, the return is a {@link ClaimValue} instance containing
	 * <code>null</code>.
	 * 
	 * @param construct
	 *            - the construct to evaluate.
	 * @param keepVariables
	 *            - indicates the desired behavior regarding variables.
	 * @return a construct that is either a {@link ClaimValue} instance, or a {@link ClaimVariable} instance (if the
	 *         {@link KeepVariables} allows it. In case of an incorrect call, <code>null</code> is returned.
	 */
	protected ClaimConstruct evaluateConstruct(ClaimConstruct construct, KeepVariables keepVariables)
	{
		ClaimConstructType constructType = construct.getType();
		switch (constructType)
		{
		case VARIABLE:
			// get the value from the symbol table (can be null)
			ClaimValue value = getVariableValue((ClaimVariable) construct);
			boolean assigned = (value == null);
			if (value == null)
				value = new ClaimValue(null);
			boolean assignable = ((ClaimVariable) construct).isReAssignable();
			switch (keepVariables)
			{
			case KEEP_NONE:
				return value;
			case KEEP_REASSIGNABLE:
				if (assignable)
					return construct;
				return value;
			case KEEP_ASSIGNABLE:
				if (assignable || !assigned)
					return construct;
				return value;
			case KEEP_ALL:
				return construct;
			}
			break;
		case VALUE:
			return construct;
		case FUNCTION_CALL: // FIXME: this should probably be limited
			if (handleCall((ClaimFunctionCall) construct))
				return new ClaimValue(new Boolean(true));
			return new ClaimValue(new Boolean(false));
		default:
			break;
		}
		log.error("illegal construct type [] inside construct", constructType);
		return null;
	}

	/**
	 * Converts a {@link List} of {@link ClaimConstruct} instances - presumably from a larger construct or a structure -
	 * to a {@link Vector} of constructs with only values or some variables (see
	 * {@link #evaluateConstruct(ClaimConstruct, KeepVariables)}, which is used in the implementation).
	 * <p>
	 * The method only works on lists that contain as elements variables, values, or function calls (these are evaluated
	 * and transformed to boolean values); structures are not accepted.
	 * 
	 * @param constructs
	 *            : the constructs to evaluate.
	 * @param ignore
	 *            : number of leading constructs to ignore (e.g. 'knowledge', 'message', etc).
	 * @param keepVariables
	 *            : what variables to keep (see {@link #evaluateConstruct(ClaimConstruct, KeepVariables)}.
	 * 
	 * @return the values of the constructs or sometimes variables. <code>null</code> is returned if the evaluation of
	 *         any of the constructs fails.
	 */
	protected Vector<ClaimConstruct> evaluateConstructs(List<ClaimConstruct> constructs, int ignore,
			KeepVariables keepVariables)
	{
		Vector<ClaimConstruct> ret = new Vector<ClaimConstruct>();
		int toIgnore = ignore;
		for (ClaimConstruct cons : constructs)
		{
			if (toIgnore-- > 0)
				continue;
			ClaimConstruct result = evaluateConstruct(cons, keepVariables);
			if (result == null)
				return null;
			ret.add(result);
		}
		return ret;
	}

	/**
	 * Convert a list of S-CLAIM constructs to S-CLAIM values. The list must fulfill the requirements of
	 * {@link #evaluateConstructs(List, int, KeepVariables)}, which is used in the implementation. No variables will be
	 * kept.
	 * 
	 * @param constructs
	 *            - the constructs to evaluate.
	 * @param ignore
	 *            - number of leading constructs to ignore.
	 * @return a list of {@link ClaimValue} instances to which the constructs have been evaluated. Some instances may
	 *         contain the <code>null</code> reference.
	 */
	protected Vector<ClaimValue> constructs2Values(List<ClaimConstruct> constructs, int ignore)
	{
		Vector<ClaimValue> args = new Vector<ClaimValue>();
		for (ClaimConstruct arg : evaluateConstructs(constructs, ignore, KeepVariables.KEEP_NONE))
			args.add((ClaimValue) arg);
		return args;

	}

	/**
	 * Convert a list of S-CLAIM constructs to Objects. The list must fulfill the requirements of
	 * {@link #evaluateConstructs(List, int, KeepVariables)}, which is used in the implementation. No variables will be
	 * kept.
	 * <p>
	 * <code>null</code> values (e.g. from unassigned variables) are translated to <code>null</code> elements.
	 * 
	 * @param constructs
	 *            - the constructs to evaluate.
	 * @param ignore
	 *            - number of leading constructs to ignore.
	 * @return a list of {@link Object} instances to which the constructs have been evaluated.
	 */
	protected Vector<Object> constructs2Objects(List<ClaimConstruct> constructs, int ignore)
	{
		Vector<Object> args = new Vector<Object>();
		for (ClaimConstruct arg : evaluateConstructs(constructs, ignore, KeepVariables.KEEP_NONE))
			args.add(((ClaimValue) arg).getValue());
		return args;
	}

	/**
	 * Convert a list of S-CLAIM constructs to Strings. The list must fulfill the requirements of
	 * {@link #evaluateConstructs(List, int, KeepVariables)}, which is used in the implementation. No variables will be
	 * kept. The strings are obtained by evaluating the constructs to S-CLAIM values and then calling
	 * {@link Object#toString()} on the results of {@link ClaimValue#getValue()}.
	 * <p>
	 * <code>null</code> values (e.g. from unassigned variables) are translated to <code>null</code> elements, if
	 * <code>transcribeNulls</code> is <code>false</code>, or to {@link ClaimValue#NULL_VALUE_OUTPUT} otherwise.
	 * 
	 * @param constructs
	 *            - the constructs to evaluate.
	 * @param ignore
	 *            - number of leading constructs to ignore.
	 * @param transcribeNulls
	 *            - specifies if S-CLAIM values containing the <code>null</code> reference (e.g. from unassigned
	 *            variables) should be left as <code>null</code> references or transcribed into String instances.
	 * @return a list of {@link String} instances to which the constructs have been evaluated.
	 */
	protected Vector<String> constructs2Strings(List<ClaimConstruct> constructs, int ignore, boolean transcribeNulls)
	{
		Vector<String> args = new Vector<String>();
		for (Object arg : constructs2Objects(constructs, ignore))
			if (arg != null)
				args.add(arg.toString());
			else
				args.add(transcribeNulls ? ClaimValue.NULL_VALUE_OUTPUT : null);
		return args;
	}

	/**
	 * Calls {@link #constructs2Strings(List, int)} and concatenates the result, with spaces ion between.
	 * 
	 * @param constructs
	 *            - the list of constructs.
	 * @param ignore
	 *            - number of constructs to ignore.
	 * @return a single string.
	 */
	protected String constructs2SingleString(List<ClaimConstruct> constructs, int ignore)
	{
		String rez = new String();
		for (String str : constructs2Strings(constructs, ignore, true))
			rez = rez.concat(str + " ");
		if (rez.length() > 0)
			rez = rez.substring(0, rez.length() - 1);
		return rez;
	}

	// TODO this appears to be used in the graph representation. investigate.
	// protected String constructsAndMaps(List<ClaimConstruct> constructs, HashMap<Integer, ClaimVariable>
	// intToVariable,
	// int ignore)
	// {
	// String rez = new String("[");
	//
	// int no = 1;
	// Vector<Object> objects = constructs2Objects(constructs, ignore);
	// objects.remove(0);
	//
	// for(Object arg : objects)
	// {
	// if(st.containsSymbol(new ClaimVariable(arg.toString().substring(1))))
	// rez = rez + st.get(new ClaimVariable(arg.toString().substring(1))).getValue().toString() + " ";
	// else if(arg instanceof ClaimVariable)
	// {
	// intToVariable.put(no, (ClaimVariable) arg);
	// rez = rez.concat("?#" + no + " ");
	// no++;
	// }
	// else
	// rez = rez.concat((String) arg + " ");
	// }
	// rez = rez + "]";
	// return rez;
	// }

}
