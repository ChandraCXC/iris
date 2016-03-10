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
package cfa.vo.iris.visualizer;

import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.SedlibSedManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.WindowInterceptor;

public class FittingToolComponentTest extends AbstractComponentGUITest {

    private FittingToolComponent comp = new FittingToolComponent();
    private String windowName;

    @Before
    public void setUp() throws Exception {
        windowName = "Fitting Tool";
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    @Test
    public void testFittingNoSed() throws Exception {
        WindowInterceptor wi = WindowInterceptor.init(
            window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(comp.getName())
                .getSubMenu(comp.getName())
                .triggerClick()
        );
        
        wi.process(BasicHandler.init().triggerButtonClick("OK")).run();
    }
    
    @Test
    public void testFittingSed() throws Exception {
        
        SedlibSedManager sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        sedManager.newSed("Sed0");
        
        window.getMenuBar()
            .getMenu("Tools")
            .getSubMenu(comp.getName())
            .getSubMenu(comp.getName())
            .click();
        
        assertTrue(desktop.containsWindow(windowName).isTrue());
    }
    
}
