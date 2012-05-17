/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.utils;

import cfa.vo.iris.sed.SedException;
import java.util.Collection;

/**
 *
 * In the SED domain, list of objects might be inconsistent even though the objects
 * come from the same classe. For example an SED must have segments with interconvertible units.
 *
 * This interfaces allows to define "more-than-type-safe" lists, since its add methods
 * throw an exception when some sort of consistency check fails.
 *
 * Also, this interface supports IDs for lists.
 *
 * @author olaurino
 */
public interface IList<T> extends Iterable<T> {
    /**
     * Get the ID string for this list.
     *
     * @return The ID string.
     */
    String getId();
    /**
     * Set the ID string for this list.
     *
     * @param id The ID string.
     */
    void setId(String id);
    /**
     * Add an element to this list.
     *
     * @param element The element to be added.
     * @return
     * @throws SedException An Exception is thrown when the element fails a compatibility check.
     */
    boolean add(T element) throws SedException;
    /**
     * Add a collection of elements this list.
     *
     * @param elements The elements to be added.
     * @return
     * @throws SedException An Exception is thrown when an element in the collection fails a compatibility check.
     */
    boolean addAll(Collection<? extends T> elements) throws SedException;
    /**
     * Add an IList of elements to this list.
     *
     * @param elements The elements to be added.
     * @return
     * @throws SedException An Exception is thrown when an element in the IList fails a compatibility check.
     */
    boolean addAll(IList<? extends T> elements) throws SedException;
    /**
     * Add an element in a certain position.
     *
     * @param position The position in which the element has to be added.
     * @param element The element to be added.
     * @throws SedException SedException An Exception is thrown when the element fails a compatibility check.
     */
    void add(int position, T element) throws SedException;
    /**
     * Remove an element from this list.
     *
     * @param element The element to be removed.
     * @return
     */
    boolean remove(T element);
    /**
     * Get the size of this list.
     *
     * @return the size of this list as a int.
     */
    int size();
    /**
     * Check whether this list is empty.
     *
     * @return True if the list is empty.
     */
    boolean isEmpty();
    /**
     *
     */
    T get(int i);
}
