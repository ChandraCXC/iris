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

import java.util.Iterator;

/**
 * Abstract class for wrapping iterators coming from other implementations.
 * The reason of this class is to provide a convenient way of creating iterators that fire events
 * when the remove() method is called.
 *
 * Extended classes only need to implement the fireEvent method according to their
 * semantics and use cases.
 *
 * @author olaurino
 */
public abstract class AbstractIterator implements Iterator {

    private Iterator it;

    /**
     * This class delegates its methods to a wrapped iterator.
     *
     * @param it The iterator that has to be wrapped.
     */
    public AbstractIterator(Iterator it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Object next() {
        return it.next();
    }

    @Override
    public void remove() {
        it.remove();
        fireEvent();
    }

    /**
     * When a client calls the remove() method this callback method is invoked for
     * firing the appropriate event.
     */
    protected abstract void fireEvent();

}
