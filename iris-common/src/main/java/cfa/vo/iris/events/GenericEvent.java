/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public abstract class GenericEvent<SourceClass, ListenerClass extends IListener<SourceClass, PayloadClass>, PayloadClass> implements IEvent<SourceClass, ListenerClass, PayloadClass> {

    private List<ListenerClass> listeners = new ArrayList();

    @Override
    public void add(ListenerClass listener) {
        listeners.add(listener);
    }

    @Override
    public void remove(ListenerClass listener) {
        listeners.remove(listener);
    }

    @Override
    public void fire(SourceClass source, PayloadClass payload) {
        for(ListenerClass listener : listeners) {
            listener.process(source, payload);
        }
    }

}
