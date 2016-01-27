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
import cfa.vo.iris.visualizer.plotter.PlotterView;
import cfa.vo.iris.visualizer.stil.StilPlotter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;
import javax.swing.*;
import java.net.URL;
import org.apache.commons.lang.ArrayUtils;

import static org.junit.Assert.*;
import uk.ac.starlink.task.StringParameter;
import uk.ac.starlink.ttools.plot2.PlotLayer;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;

public class VisualizerComponentTest extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String windowName;
    
    //private SedlibSedManager manager;

    @Before
    public void setUp() throws Exception {
        windowName = comp.getName();
        //manager = (SedlibSedManager) app.getWorkspace().getSedManager();
        //comp.show();
        
        //desktop.containsWindow(windowName).check();
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    
    // check that the metadata browser opens when clickied on
    @Test
    public void testMetadataBrowserPresent() throws Exception {

        org.uispec4j.Button mbButton = desktop.getWindow(windowName).getButton("Metadata");
        mbButton.click();
        
        assertTrue(desktop.containsWindow("Metadata Browser").isTrue());
    }
    
    
    // basic test to check default layer settings on plot window when SED is
    // added to the plotter.
    @Test
    public void testAddSEDToPlotter() throws Exception {
        
        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(windowName)
                .getSubMenu(windowName)
                .click();
        
        String filename = getClass().getResource("/test_data/3c273.vot").getFile();
        ExtSed sed = ExtSed.read(filename, SedFormat.VOT, false);
//        SedlibSedManager manager = (SedlibSedManager) app.getWorkspace().getSedManager();
//        manager.add(sed);
        
        StilPlotter plot = comp.getDefaultPlotterView().getPlot();
        
        assertTrue(desktop.containsWindow(windowName).isTrue());
        
//        StilPlotter plot = comp.getDefaultPlotterView().getPlot();
//        PlotDisplay display = plot.getPlotDisplay();
//        
//        // using reflection to access layers in plot display
//        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
//        layers_.setAccessible(true);
//        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
//        
//        assertTrue(!ArrayUtils.isEmpty(layers));
//        
//        // create the default plot view for an SED
//        //plot.createPlotComponent(sed);
//        
//        // check that plot env is correctly set
//        MapEnvironment env = plot.getEnv();
//        for (String string : env.getNames()) {
//            
//        }
//        StringParameter par = new StringParameter("color");
//        env.acquireValue(par);
//        assertEquals(par.objectValue(env), "blue");
//        for (String string : env.getNames()) {
//            System.err.println(string);
//        }
        
        
    }
    
    @Test
    public void testResetEmptySed() {
        
        comp.show();
        
        org.uispec4j.Button resetButton = desktop.getWindow(windowName).getButton("Reset");
        resetButton.click();
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

    @Test(timeout=2500)
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

    private Segment createSampleSegment() throws SedNoDataException {
        double[] x = new double[]{1.0, 2.0, 3.0};
        double[] y = new double[]{1.0, 2.0, 3.0};
        Segment segment = new Segment();
        segment.setFluxAxisValues(y);
        segment.setFluxAxisUnits("Jy");
        segment.setSpectralAxisValues(x);
        segment.setSpectralAxisUnits("Angstrom");
        return segment;
    }
}
