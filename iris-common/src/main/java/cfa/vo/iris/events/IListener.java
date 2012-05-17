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
