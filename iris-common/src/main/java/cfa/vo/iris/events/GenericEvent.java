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
