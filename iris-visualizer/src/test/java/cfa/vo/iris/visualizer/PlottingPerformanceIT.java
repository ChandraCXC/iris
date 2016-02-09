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
import java.util.Map;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.sedlib.ISegment;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.table.StarTable;

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
    
    @Test(timeout=20000)
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
        while(comp.getDefaultPlotterView().getSed() == null) {
            Thread.sleep(100);
        }
        
        
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                
                // Verify the SED has loaded into the sed
                assertSame(sed, comp.getDefaultPlotterView().getSed());
                
                // Verify the startable has loaded correctly
                Map<Segment, SegmentLayer> segmentMap = 
                        comp.getDefaultPlotterView().getSegmentsMap();
                
                assertEquals(1, segmentMap.size());
                for (SegmentLayer seg : segmentMap.values()) {
                    StarTable table = (StarTable) seg.getInSource();
                    assertEquals(303706, table.getRowCount());
                }
            }
        });
    }
}
