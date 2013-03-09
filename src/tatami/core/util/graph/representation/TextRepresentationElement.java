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
package tatami.core.util.graph.representation;

import java.util.LinkedList;
import java.util.List;



import tatami.core.interfaces.Logger;
import tatami.core.util.ContentHolder;
import tatami.core.util.graph.Edge;
import tatami.core.util.graph.GraphComponent;
import tatami.core.util.graph.Node;
import tatami.core.util.graph.GraphPattern.NodeP;

public class TextRepresentationElement extends RepresentationElement
{
	enum Type {
		EXTERNAL_LINK, INTERNAL_LINK, BRANCH, SUBGRAPH, ELEMENT_CONTAINER, NODE
	}
	
	enum Symbol {
		SUBGRAPH_SEPARATOR(";"),
		
		INTERNAL_LINK_PREFIX("*"),
		
		EXTERNAL_LINK_PREFIX("^"),
		
		BRANCH_IN("("),
		
		BRANCH_OUT(")"),
		
		EDGE_LIMIT("-"),
		
		EDGE_ENDING_FORWARD(">"),
		
		EDGE_ENDING_BACKWARD("<"),
		
		;
		
		private String	symbol;
		
		private Symbol(String symb)
		{
			symbol = symb;
		}
		
		@Override
		public String toString()
		{
			return symbol;
		}
	}
	
	public static class TextRepElementConfig extends RepElementConfig
	{
		int		nestingLevel	= 0;		// TODO: not currently used
		boolean	isLastChild		= false;
		boolean	isOnlyChild		= false;
		boolean	isFirstSubgraph	= false;
		Type	linkType;
		
		public TextRepElementConfig(GraphRepresentation root, Type type)
		{
			super(root, null);
			
			if(type != Type.ELEMENT_CONTAINER)
				throw new IllegalArgumentException();
			linkType = type;
		}
		
		public TextRepElementConfig(GraphRepresentation root, Type type, boolean first)
		{
			super(root, null);
			
			if(type != Type.SUBGRAPH)
				throw new IllegalArgumentException();
			
			isFirstSubgraph = first;
			linkType = type;
		}
		
		public TextRepElementConfig(GraphRepresentation root, GraphComponent representedEdge, Type type, int level, boolean lastChild, boolean alone)
		{
			super(root, null);
			
			if(!isEdgeType(type))
				throw new IllegalArgumentException();
			
			representedComponent = representedEdge;
			
			linkType = type;
			nestingLevel = level;
			isLastChild = lastChild;
			isOnlyChild = alone;
		}
		
		public TextRepElementConfig(GraphRepresentation root, GraphComponent representedNode, Type type)
		{
			super(root, null);
			
			if(type != Type.NODE)
				throw new IllegalArgumentException();
			
			representedComponent = representedNode;
			linkType = type;
		}
	}
	
	List<TextRepresentationElement>	content	= null;
	
	public TextRepresentationElement(TextRepElementConfig conf)
	{
		super(conf);
		
		content = new LinkedList<TextRepresentationElement>();
		
		if(conf.representedComponent != null)
			conf.representedComponent.addRepresentation(this);
	}
	
	protected TextRepresentationElement addSub(TextRepresentationElement sub)
	{
		content.add(sub);
		return this;
	}
	
	protected TextRepresentationElement addSub(List<TextRepresentationElement> subs)
	{
		content.addAll(subs);
		return this;
	}
	
	public String toString(String indent, String indentIncrement, int indentLimit)
	{
		String displayedIndent = indent;
		String displayedIndentIncrement = indentIncrement;
		TextRepElementConfig conf = (TextRepElementConfig)this.config;
		Type linkType = conf.linkType;
		
		String ret = "";
		
		if((conf.linkType == Type.SUBGRAPH) && !conf.isFirstSubgraph)
			ret += Symbol.SUBGRAPH_SEPARATOR;
		
		if(indentLimit == 0)
		{
			displayedIndent = "";
			displayedIndentIncrement = "";
		}
		if(!conf.isOnlyChild && linkType != Type.NODE)
		{
			ret += displayedIndent;
		}
		
		if(isEdgeType(linkType) && !conf.isOnlyChild && !conf.isLastChild)
			ret += Symbol.BRANCH_IN;
		
		if(isEdgeType(linkType)) // FIXME: should not use Edge's toString function, should represent manually
			ret += ((Edge)conf.representedComponent).toStringShort(((TextGraphRepresentation.GraphConfig)config.rootRepresentation.config).isBackwards);
		
		if(linkType == Type.NODE)
			ret += conf.representedComponent.toString(); // FIXME: should not use Node's toString function, should represent manually
			
		if(linkType == Type.EXTERNAL_LINK)
			ret += Symbol.EXTERNAL_LINK_PREFIX;
		if(linkType == Type.INTERNAL_LINK)
			ret += Symbol.INTERNAL_LINK_PREFIX;
		
		if(content != null)
		{
			boolean oneChild = content.size() <= 1;
			for(TextRepresentationElement el : content)
				ret += el.toString(((linkType == Type.ELEMENT_CONTAINER || oneChild) ? displayedIndent : displayedIndent + displayedIndentIncrement), displayedIndentIncrement, (!isEdgeType(linkType) ? indentLimit : indentLimit - 1));
		}
		
		if(isEdgeType(linkType) && !conf.isOnlyChild && !conf.isLastChild)
			ret += Symbol.BRANCH_OUT;
		
		return ret;
	}
	
	/**
	 * for debugging only; should not be used in the source
	 */
	@Override
	public String toString()
	{
		TextGraphRepresentation.GraphConfig c = new TextGraphRepresentation.GraphConfig(config.rootRepresentation.theGraph);
		return toString(c.indent, c.indentIncrement, c.incrementLimit);
	}
	
	static boolean isEdgeType(Type linkType)
	{
		return (linkType == Type.INTERNAL_LINK) || (linkType == Type.EXTERNAL_LINK) || (linkType == Type.BRANCH);
	}
	
	static TextRepresentationElement readRepresentation(ContentHolder<String> input, TextGraphRepresentation root, Logger log)
	{
		return readRepresentation(input, Type.ELEMENT_CONTAINER, true, root, log);
	}
	
	static TextRepresentationElement readRepresentation(ContentHolder<String> input, Type type, boolean firstSibling, TextGraphRepresentation root, Logger log)
	{
		log.trace("reading [" + type + "] from: " + input);
		TextRepresentationElement ret = null;
		int nSiblings = 0;
		switch(type)
		{
		case ELEMENT_CONTAINER:
			ret = new TextRepresentationElement(new TextRepElementConfig(root, type));
			while(input.get().length() > 0)
				ret.addSub(readRepresentation(input, Type.SUBGRAPH, (nSiblings++ == 0), root, log));
			break;
		case SUBGRAPH:
		{
			String rez[] = input.get().split(Symbol.SUBGRAPH_SEPARATOR.toString(), 2);
			// should have current subgraph in rez[0]
			if(rez.length > 0 && rez[0].length() > 0)
			{
				input.set(rez[0]);
				log.info("create new subgraph");
				ret = new TextRepresentationElement(new TextRepElementConfig(root, Type.SUBGRAPH, firstSibling));
				ret.addSub(readRepresentation(input, Type.NODE, true, root, log));
			}
			if(rez.length > 1)
				input.set(rez[1]); // TODO: should check if there is an index 1 in rez?
			else
				input.set("");
			break;
		}
		case EXTERNAL_LINK:
		case INTERNAL_LINK:
			// these two above are not really used, as we can't know beforehand if an edge is external, internal, or normal
		case BRANCH:
		{
			String edgeString, rest;
			boolean lastChild;
			// isolate branch
			input.set(input.get().trim());
			if(input.get().startsWith(Symbol.BRANCH_IN.toString()))
			{
				// locate corresponding closing symbol
				int lastIndex = 0, openBranches = 0, pastLength = 1;
				String str = input.get().substring(pastLength), open = Symbol.BRANCH_IN.toString(), close = Symbol.BRANCH_OUT.toString();
				while(lastIndex == 0)
				{
					int openIndex = str.indexOf(open), closeIndex = str.indexOf(close);
					if(openIndex >= 0 && openIndex < closeIndex)
					{ // next a branch opens
						openBranches++;
						pastLength += openIndex + 1;
						str = str.substring(openIndex + 1);
					}
					else
					{ // next a branch closes
						if(openBranches == 0)
							// it's our branch
							lastIndex = pastLength + closeIndex;
						else
						{
							openBranches--;
							pastLength += closeIndex + 1;
							str = str.substring(closeIndex + 1);
						}
					}
				}
				
				edgeString = input.get().substring(1, lastIndex).trim();
				rest = input.get().substring(lastIndex + 1);
				lastChild = false;
			}
			else
			{
				lastChild = true;
				edgeString = input.get().trim();
				rest = "";
			}
			log.trace("identified " + (lastChild ? "[last]" : "") + " edge [" + edgeString + "]");
			
			// read branch
			// get the label
			String edgeName;
			int firstIndex, lastIndex;
			String nextNode;
			
			if(!((LinearGraphRepresentation.GraphConfig)root.config).isBackwards)
			{ // - forward-edge [-]> next node
				String rez[] = edgeString.split(Symbol.EDGE_ENDING_FORWARD.toString(), 2);
				firstIndex = rez[0].indexOf(Symbol.EDGE_LIMIT.toString()) + 1;
				if(rez[0].length() < 2)
					lastIndex = -1;
				else
					lastIndex = (rez[0].substring(rez[0].length() - 1).equals(Symbol.EDGE_LIMIT.toString())) ? rez[0].length() - 1 : rez[0].length();
				nextNode = rez[1].trim();
			}
			else
			{ // <[-] backward-edge - next node
				// assume first character is (EDGE_ENDING_BACKWARD) (edgeString is trimmed)
				// identify edge name ending by last EDGE_LIMIT before the next EDGE_ENDING_BACKWARD
				edgeString = edgeString.substring(1);
				String rez[] = edgeString.split(Symbol.EDGE_ENDING_BACKWARD.toString(), 2);
				lastIndex = rez[0].lastIndexOf(Symbol.EDGE_LIMIT.toString());
				nextNode = edgeString.substring(lastIndex).trim();
				firstIndex = rez[0].substring(1, 1).equals(Symbol.EDGE_LIMIT) ? 2 : 1;
				nextNode = edgeString.substring(lastIndex + 1);
			}
			if(firstIndex <= lastIndex)
			{
				// log.trace("====="+firstIndex+"==="+lastIndex);
				edgeName = edgeString.substring(firstIndex, lastIndex).trim();
				if(edgeName.length() == 0)
					edgeName = null;
			}
			else
				// unnamed edge
				edgeName = null;
			
			// get the type
			// nextNode has been trimmed
			String edgeTypeChar = nextNode.substring(0, 1);
			Type edgeType;
			if(edgeTypeChar.equals(Symbol.EXTERNAL_LINK_PREFIX.toString()))
				edgeType = Type.EXTERNAL_LINK;
			else if(edgeTypeChar.equals(Symbol.INTERNAL_LINK_PREFIX.toString()))
				edgeType = Type.INTERNAL_LINK;
			else
				edgeType = Type.BRANCH;
			if(edgeType != Type.BRANCH)
				nextNode = nextNode.substring(1);
			
			// create
			log.info("create new [" + edgeTypeChar + "][" + edgeType + "] edge: [" + edgeName + "]");
			Edge edge = new Edge(null, null, edgeName); // node names will be filled in later (in LinearGraphRepresentation)
			// FIXME: what level to give; does it matter? remove level?
			ret = new TextRepresentationElement(new TextRepElementConfig(root, edge, edgeType, -1, lastChild, lastChild && firstSibling));
			ret.addSub(readRepresentation(input.set(nextNode), Type.NODE, true, root, log));
			input.set(rest);
			break;
		}
		case NODE:
		{
			String parts[] = input.get().split("[" + Symbol.EDGE_LIMIT + Symbol.BRANCH_IN + Symbol.EDGE_ENDING_BACKWARD + "]", 2);
			// TODO: should check if there is anything in parts[0]?
			String nodeName = parts[0].trim();
			Node node = null;
			if(nodeName.substring(0, 0 + NodeP.NODEP_LABEL.length()).equals(NodeP.NODEP_LABEL) && nodeName.substring(NodeP.NODEP_LABEL.length(), NodeP.NODEP_LABEL.length() + NodeP.NODEP_INDEX_MARK.length()).equals(NodeP.NODEP_INDEX_MARK))
			{
				int index = Integer.parseInt(nodeName.substring(NodeP.NODEP_LABEL.length() + NodeP.NODEP_INDEX_MARK.length()));
				log.info("create new pattern node #[" + index + "]");
				node = new NodeP(index); // for internal links, this should not be a new node; it will be replaced later
			}
			else
			{
				log.info("create new node: [" + nodeName + "]");
				node = new Node(nodeName); // for internal links, this should not be a new node; it will be replaced later
			}
			ret = new TextRepresentationElement(new TextRepElementConfig(root, node, Type.NODE));
			if(parts.length > 1)
			{
				String startedWith = input.get().substring(parts[0].length(), parts[0].length() + 1);// remember what was after the node name
				input.set(startedWith + parts[1]);
				while(input.get().length() > 0)
					ret.addSub(readRepresentation(input, Type.BRANCH, (nSiblings++ == 0), root, log));
			}
			break;
		}
		}
		return ret;
	}
}
