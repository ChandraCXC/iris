/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

/**
 *
 * Events are singletons that can register listeners and, when fired, notify
 * the registered listeners by calling their callback methods. This is a generic
 * interface that needs to be constrained by the implementing classes with the class of
 * sources for which the event will be fired, the class of listeners bound to the event class
 * and the class of the Payload object.
 *
 * Events and Listeners constitute the Extensible Event Framework
 *
 * @author olaurino
 */
public interface IEvent<SourceClass, ListenerClass, PayloadClass> {
    /**
     * This method adds a new listener to the event.
     * @param listener
     */
    void add(ListenerClass listener);
    /**
     * This method removes a listener from the event.
     * @param listener
     */
    void remove(ListenerClass listener);
    /**
     * This method can be invoked to fire the EventCommand command for the source object.
     *
     * @param source The object for which the event is being fired.
     * @param payload The semantic content of the event, in a payload object.
     */
    void fire(SourceClass source, PayloadClass payload);
}
