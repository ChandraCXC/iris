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

import cfa.vo.interop.SAMPController;
import cfa.vo.iris.sed.ExtSed;
import java.io.File;
import java.net.URL;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;

/**
 *
 * This interface must implemented by enclosing application. An instance of this
 * interface is provided to the components when they are initialized, so that they can
 * access application-wide information and operations.
 *
 * @author olaurino
 */
public interface IrisApplication {
    /**
     * Get a pointer to the directory that contains all the configuration files for this component.
     *
     * @return A File instance for the configuration directory.
     */
    File getConfigurationDir();
    /**
     * Return whether or not SAMP was enabled for this session. SAMP features could be required to be switched
     * on and off when the application is launched.
     *
     * @return True if SAMP is enabled in this session.
     */
    boolean isSampEnabled();
    /**
     * Convenience shortcut that allows components to broadcast a SED message to the SAMP hub.
     *
     * @param sed The SED file that has to be sent through SAMP
     * @param sedId The ID of the SED file sent
     * @throws SampException If an exception is thrown while the message is being sent
     */
    void sendSedMessage(ExtSed sed) throws SampException;

    void sendSampMessage(Message msg) throws SampException;

    SAMPController getSAMPController();

    URL getHelpURL();
}
