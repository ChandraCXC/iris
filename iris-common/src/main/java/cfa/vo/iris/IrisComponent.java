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

package cfa.vo.iris;

import java.util.List;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * Interface implemented by Iris components. By implementing this interface the components
 * allow the framework to retrieve the information needed to run and initialize them.
 *
 * @author olaurino
 */
public interface IrisComponent {
    /**
     * This method is invoked to initialize the component. If the component has to launch windows, frames or
     * background services, this is the right method to do so. Otherwise the component will be called only if a callback
     * is invoked.
     *
     * @param app A reference to the running application
     * @param workspace A reference to the application's workspace
     */
    void init(IrisApplication app, IWorkspace workspace);
    /**
     * Return the name of this component. This name might be listed in a widget along with the other registered components.
     * @return The component's name as a String.
     */
    String getName();
    /**
     * Get e description for this component. The description might be listed in a widget along with the other
     * registered components.
     *
     * @return The component's description as a String.
     */
    String getDescription();
    /**
     * Get a command line interface object for this component.
     * @return A CLI object
     */
    ICommandLineInterface getCli();
    /**
     * Initialize the Command Line Application interface
     * @param app Reference to the enclosing application
     */
    void initCli(IrisApplication app);
    /**
     * The component can contribute menu items and desktop buttons to the enclosing GUI applications
     * by providing a list of MenuItems.
     *
     * @return A list of the menu items this component will contribute to the application.
     */
    List<IMenuItem> getMenus();
    /**
     * The component can register any number of SAMP message listeners by providing a list of them.
     *
     * @return A list of the SAMP message listeners that have to be registered to the SAMP hub.
     */
    List<MessageHandler> getSampHandlers();

    /**
     * Callback invoked when the component is shutdown
     */
    void shutdown();
}
