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
package cfa.vo.iris.test.unit;

import cfa.vo.iris.AbstractIrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.desktop.IrisDesktop;
import cfa.vo.iris.desktop.IrisWorkspace;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;

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
public class IrisApplicationStub extends AbstractIrisApplication {

    public static void main(String[] args) {
        launch(IrisApplicationStub.class, args);
    }
    
    private List<IrisComponent> irisComponents = 
            Collections.synchronizedList(new ArrayList<IrisComponent>());
    
    /**
     * Allows users to reset desktop components then reload the view at the start
     * of unit tests.
     * 
     */
    public void setComponents(Collection<IrisComponent> comps) {
        components = new TreeMap<>();
        irisComponents.removeAll(irisComponents);
        irisComponents.addAll(comps);
        initComponents();
        for (final IrisComponent component : components.values()) {
            component.init(this, ws);
        }
        desktop.reset(irisComponents);
    }
    
    public IrisWorkspace getWorkspace() {
        return ws;
    }
    
    public IrisDesktop getDesktop() {
        return desktop;
    }
    
    @Override
    public void sampSetup() {}

    @Override
    public String getName() {
        return "Iris Stub Application";
    }
    
    @Override
    public String getDescription() {
        return "Iris Stub Application";
    }

    @Override
    public URL getSAMPIcon() {
        return getClass().getResource("/tool_tiny.png");
    }

    @Override
    public List<IrisComponent> getComponents() throws Exception {
        return irisComponents;
    }

    @Override
    public JDialog getAboutBox() {
        return null;
    }

    @Override
    public URL getDesktopIcon() {
        return getClass().getResource("/tool.png");
    }

    @Override
    public void setProperties(List<String> properties) {
        Logger.getLogger("").setLevel(Level.ALL);
    }

    @Override
    public URL getHelpURL() {
        return null;
    }
}
