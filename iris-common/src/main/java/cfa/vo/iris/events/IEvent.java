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
