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

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.IrisComponent;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import org.junit.Before;
import org.junit.Test;
import javax.swing.*;
import java.net.URL;

import static org.junit.Assert.*;

public class VisualizerComponentTest extends AbstractComponentGUITest {
    
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

    @Test
    public void testVisualizerStub() throws Exception {
        
        window.getMenuBar()
            .getMenu("Tools")
            .getSubMenu(windowName)
            .getSubMenu(windowName)
            .click();
        
        assertTrue(desktop.containsWindow(windowName).isTrue());
        
        org.uispec4j.Button mbButton = desktop.getWindow(windowName).getButton("Metadata");
        mbButton.click();
        
        assertTrue(desktop.containsWindow("Metadata Browser").isTrue());
    }

    @Test
    public void testEventSubscription() throws Exception {
        SedlibSedManager sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();

        // No view when starting application up
        assertNull(comp.getDefaultPlotterView());

        // When the viewer button is clicked, the displayed SED should be the one selected in the SedManager
        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(windowName)
                .getSubMenu(windowName)
                .click();
        ExtSed initialSed = sedManager.getSelected();
        assertNull(initialSed);

        // Test the plotter reacts to a sed event
        final ExtSed sed = sedManager.newSed("sampleSed");
        // Make sure this is enqueued in the Swing EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assertSame(sed, comp.getDefaultPlotterView().getSed());
            }
        });


        // Test the plotter reacts to a segment event (although this only requires subscribing to SedEvents)
        final Segment segment = createSampleSegment();
        sed.addSegment(segment);

        // Make sure this is enqueued in the Swing EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assertTrue(comp.getDefaultPlotterView().getSegmentsMap().containsKey(segment));
            }
        });

        // Just double checking this works more than once.
        final ExtSed sed2 = sedManager.newSed("oneMoreSed");
        // Make sure this is enqueued in the Swing EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assertSame(sed2, comp.getDefaultPlotterView().getSed());
            }
        });

        // Just double checking we can go back through select
        sedManager.select(sed);
        // Make sure this is enqueued in the Swing EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assertSame(sed, comp.getDefaultPlotterView().getSed());
            }
        });


//        assertEquals("sampleSed", comp.getDefaultPlotterView().getLegend().getTitle());
    }

    @Test(timeout=10000)
    public void testReadPerformance() throws Exception {
        URL benchmarkURL = TestData.class.getResource("test300k_VO.fits");
        final ExtSed sed = ExtSed.read(benchmarkURL.openStream(), SedFormat.FITS);
        SedlibSedManager manager = (SedlibSedManager) app.getWorkspace().getSedManager();
        manager.add(sed);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                window.getMenuBar()
                        .getMenu("Tools")
                        .getSubMenu(windowName)
                        .getSubMenu(windowName)
                        .click();
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assertSame(sed, comp.getDefaultPlotterView().getSed());
            }
        });
    }
}
