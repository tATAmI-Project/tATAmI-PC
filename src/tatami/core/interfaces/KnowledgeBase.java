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
package tatami.core.interfaces;

import java.util.Collection;

/**
 * Interface for the various type of classes that implement an agent's knowledge base.
 * <p>
 * It contains the {@link KnowledgeDescription}, which the knowledge base can read.
 * 
 * @author Andrei Olaru
 */
public interface KnowledgeBase
{
	/**
	 * Implementing classes can describe pieces of knowledge, in a format that can be converted to a text representation
	 * that can be read by a {@link KnowledgeBase} -implementing class
	 * 
	 * @author Andrei Olaru
	 */
	public interface KnowledgeDescription
	{
		/**
		 * Returns the text representation of the piece of knowledge described by the object. Should be used by
		 * knowledge bases to create / understand pieces of knowledge described by the CLAIM code.
		 * <p>
		 * A particular {@link KnowledgeBase} -implementing class can use its own {@link KnowledgeDescription}
		 * implementation that is not transformable to a text representation, although that is not recommended.
		 * 
		 * @return A {@link String} representation of the knowledge, that can be implemented by a {@link KnowledgeBase}
		 *         -implementing class.
		 */
		public String getTextRepresentation();
	}
	
	/**
	 * Integrates a piece of knowledge in the knowledge base. How exactly that is done depends on the particular
	 * implementation of the knowledge base.
	 * 
	 * @param piece
	 *            the description of the piece of knowledge to add.
	 * @return <code>true</code> if the knowledge base has been modified (as in {@link Collection} classes);
	 *         <code>false</code> otherwise.
	 */
	public boolean add(KnowledgeDescription piece);
	
	/**
	 * Integrates several pieces of knowledge in the knowledge base. How exactly that is done depends on the particular
	 * implementation of the knowledge base.
	 * 
	 * @param piece
	 *            a {@link Collection} of descriptions of the pieces of knowledge to add. The signature is meant to be
	 *            the same as the signature in <code>Collection.addAll</code>.
	 * @return <code>true</code> if the knowledge base has been modified (as in Collection classes); <code>false</code>
	 *         otherwise.
	 */
	public boolean addAll(Collection<? extends KnowledgeDescription> piece);
	
	/**
	 * Retrieves the first (order guarantees depend on implementation) piece of knowledge that matches the given
	 * pattern.
	 * 
	 * @param pattern
	 *            the pattern, as it may be interpreted by the knowledge base.
	 * @return a piece of knowledge that matches the pattern. The return may or may not be the same at subsequent calls
	 *         with the same pattern.
	 */
	public KnowledgeDescription getFirst(KnowledgeDescription pattern);
	
	/**
	 * Retrieves all (order guarantees depend on implementation) pieces of knowledge that match the given pattern.
	 * 
	 * @param pattern
	 *            the pattern, as it may be interpreted by the knowledge base.
	 * @return a collection of pieces of knowledge that match the pattern.
	 */
	public Collection<KnowledgeDescription> getAll(KnowledgeDescription pattern);
	
	/**
	 * Removes the first (order guarantees depend on implementation) piece of knowledge that matches the given pattern.
	 * <p>
	 * In case the {@link KnowledgeBase} implementation does not match the same piece of knowledge at subsequent calls
	 * with the same pattern, a <code>remove</code> following a <code>get</code> (with the same pattern) may not remove
	 * the same piece of knowledge as it has been found by <code>get</code>. Beware!
	 * 
	 * @param pattern
	 *            the pattern, as it may be interpreted by the knowledge base.
	 * @return <code>true</code> if the knowledge base has been modified (i.e. a match was found); <code>false</code>
	 *         otherwise.
	 */
	public boolean remove(KnowledgeDescription pattern);
	
	/**
	 * Removes all pieces of knowledge that match the given pattern.
	 * 
	 * @param pattern
	 *            the pattern, as it may be interpreted by the knowledge base.
	 * @return <code>true</code> if the knowledge base has been modified (i.e. at least one match was found);
	 *         <code>false</code> otherwise.
	 */
	public boolean removeAll(KnowledgeDescription pattern);
}
