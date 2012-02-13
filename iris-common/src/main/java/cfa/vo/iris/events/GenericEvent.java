/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import java.awt.EventQueue;
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
    public void fire(final SourceClass source, final PayloadClass payload) {
        for(final ListenerClass listener : listeners) {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    listener.process(source, payload);
                }
            });
            
        }
    }

}
