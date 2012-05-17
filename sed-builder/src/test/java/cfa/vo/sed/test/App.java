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
package cfa.vo.sed.test;

import cfa.vo.interop.SAMPController;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.sed.ExtSed;
import java.io.File;
import java.net.URL;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;

/**
 *
 * @author olaurino
 */
public class App implements IrisApplication {

    @Override
    public File getConfigurationDir() {
        return new File(System.getProperty("user.home") + "/.vao/iris/importer/");
    }

    @Override
    public boolean isSampEnabled() {
        return false;
    }

    @Override
    public void sendSedMessage(ExtSed sed) throws SampException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendSampMessage(Message msg) throws SampException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SAMPController getSAMPController() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public URL getHelpURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
