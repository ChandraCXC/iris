/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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

import java.net.URL;
import java.util.List;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import static org.junit.Assert.*;

public class PlottingPerformanceIT extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String windowName;

    @Before
    public void setUp() throws Exception {
        windowName = comp.getName();
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    // Ignoring this until we can optimize SED conversion.
    @Ignore
    @Test(timeout=60000)
    public void testReadPerformance() throws Exception {
        
        // Initialize the plotter
        window.getMenuBar()
              .getMenu("Tools")
              .getSubMenu(windowName)
              .getSubMenu(windowName)
              .click();

        // Load the SED into the workspace
        URL benchmarkURL = TestData.class.getResource("test300k_VO.fits");
        final ExtSed sed = ExtSed.read(benchmarkURL.openStream(), SedFormat.FITS);
        SedlibSedManager manager = (SedlibSedManager) app.getWorkspace().getSedManager();
        manager.add(sed);
        
        // Wait for the plotting component to load the new selected SED
        final VisualizerComponentPreferences prefs = comp.getPreferences();
        while(CollectionUtils.isEmpty(prefs.getDataModel().getSelectedSeds())) {
            Thread.sleep(100);
        }
        
        
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                
                // Verify the SED has loaded into the sed
                assertTrue(prefs.getDataModel().getSelectedSeds().contains(sed));
                
                // Verify the startable has loaded correctly
                List<IrisStarTable> tables =
                        prefs.getDataStore().getSedModel(sed).getDataTables();
                
                assertEquals(1, tables.size());
                assertEquals(303706, tables.get(0).getRowCount());
            }
        });
    }
}
