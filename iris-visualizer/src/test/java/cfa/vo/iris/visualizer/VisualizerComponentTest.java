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
import org.junit.Before;
import org.junit.Test;
import cfa.vo.iris.IrisComponent;

public class VisualizerComponentTest extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String windowName;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        windowName = comp.getName();
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    @Test
    public void testVisualizerStub() throws Exception {
        
        window.getMenuBar()
            .getMenu("Tools")
            .getSubMenu(windowName)
            .getSubMenu(windowName)
            .click();
        
        desktop.containsWindow(windowName);
        
        org.uispec4j.Button mbButton = desktop.getWindow(windowName).getButton("Metadata");
        mbButton.click();
        
        desktop.containsWindow("Metadata Browser");
    }
}
