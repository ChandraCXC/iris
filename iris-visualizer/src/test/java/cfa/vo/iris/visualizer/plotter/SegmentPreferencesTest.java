/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.plotter;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.visualizer.VisualizerComponent;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Window;

/**
 *
 * @author jbudynk
 */
public class SegmentPreferencesTest extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String windowName;
    private String prefsName;

    @Before
    public void setUp() throws Exception {
        windowName = comp.getName();
        prefsName = "Preferences";
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }
    
    @Test
    public void testPreferences() throws Exception {
        
        // Initialize the plotter
        window.getMenuBar()
              .getMenu("Tools")
              .getSubMenu(windowName)
              .getSubMenu(windowName)
              .click();
        
        // open properties window
        desktop.getWindow(windowName).getMenuBar().getMenu("File").getSubMenu(prefsName).click();
        assertTrue(desktop.containsWindow(prefsName).isTrue());
        assertTrue(desktop.getWindow(prefsName).isVisible().isTrue());
        
        Window prefs = desktop.getWindow(prefsName);
        
        assertTrue(prefs.getPanel("contentPane").containsLabel("Color:").isTrue());
    }
}
