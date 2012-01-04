/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

/**
 * Generic interface for listeners. Extending interfaces must constrain the parameter
 * to the class of sources of interest for the listener.
 *
 * @author olaurino
 */
public interface IListener<SourceClass, PayloadClass> {
    /**
     * Callback method that is called when an event to which this listener has subscribed is fired.
     *
     * @param source The object for which this event is being fired.
     * @param command The EventCommand attached to the event.
     */
    void process(SourceClass source, PayloadClass payload);
}
