/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.test.unit;

import cfa.vo.interop.ISAMPController;
import cfa.vo.iris.*;
import com.google.common.io.Files;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;

import java.io.File;
import java.net.URL;

/**
 * A basic Iris desktop application for unit testing of Iris components.
 * Only a basic iris desktop application window is available with no functionality
 * accessible through the workspace. Tests are responsible for adding components 
 * and manually specifying workspace behavior.
 * 
 * This should be extended to include data control/expose interfaces for mocking
 * basic application i/o.
 *
 */
public class ApplicationStub implements IrisApplication {
    private StubWorkspace wSpace = new StubWorkspace();
    
    public IWorkspace getWorkspace() {
        return wSpace;
    }

    @Override
    public File getConfigurationDir() {
        return Files.createTempDir();
    }

    @Override
    public boolean isSampEnabled() {
        return false;
    }

    @Override
    public void sendSampMessage(Message msg) throws SampException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ISAMPController getSAMPController() {
        return null;
    }

    public void addComponent(IrisComponent component) {
        wSpace.addComponent(component);
    }

    @Override
    public URL getHelpURL() {
        return null;
    }
}
