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
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractGUITest;
import cfa.vo.iris.visualizer.VisualizerComponent;
import cfa.vo.sedlib.io.SedFormat;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbudynk
 */
public class PlotterViewTest extends AbstractGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private PlotterView plotter;
    private SedlibSedManager manager;
    private String windowName;
    
    public PlotterViewTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Override
    public List<IrisComponent> getComponents() {
        return Arrays.asList(new IrisComponent[]{comp}); //, builder);
    }

    @Test
    public void testAddSEDToPlotter() throws Exception {
        
        comp.show();
        
        manager = new SedlibSedManager();
        String filename = getClass().getResource("/test_data/3c273.vot").getFile();
        ExtSed sed = ExtSed.read(filename, SedFormat.VOT, true);
        manager.add(sed);
        
        // show that manager 
        
        assertTrue(desktop.containsWindow(windowName).isTrue());
        
        org.uispec4j.Button resetButton = desktop.getWindow(windowName).getButton("Reset");
        resetButton.click();
    }
    
}
