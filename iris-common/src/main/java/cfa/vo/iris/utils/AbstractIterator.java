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
