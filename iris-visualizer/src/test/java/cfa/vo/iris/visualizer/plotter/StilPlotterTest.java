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

import cfa.vo.iris.visualizer.plotter.StilPlotter;
import static org.junit.Assert.*;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.Ws;
import cfa.vo.iris.visualizer.plotter.PlotterView;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;

import java.lang.reflect.Field;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import uk.ac.starlink.task.StringParameter;
import uk.ac.starlink.ttools.plot2.PlotLayer;
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurfaceFactory.Profile;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.task.BooleanParameter;

public class StilPlotterTest {
    
    private Ws ws = new Ws();
    private VisualizerComponentPreferences preferences;
    
    @Test
    public void testAddSed() throws Exception {
        
        ExtSed sed = ExtSed.read(TestData.class.getResource("3c273.vot").openStream(), SedFormat.VOT);
        StilPlotter plot = setUpTests(sed);
        
        PlotDisplay<?, ?> display = plot.getPlotDisplay();
        
        // check that plot env is correctly set
        MapEnvironment env = plot.getEnv();

        // check shape
        StringParameter par = new StringParameter("shape3C 273");
        env.acquireValue(par);
        assertEquals(par.objectValue(env), "open_circle");
        
        // check xlog and ylog
        BooleanParameter log = new BooleanParameter("xlog");
        env.acquireValue(log);
        assertEquals(log.objectValue(env), true);
        log.setName("ylog");
        env.acquireValue(log);
        assertEquals(log.objectValue(env), true);
        
        // check errorbars shape
        par.setName("errorbar3C 273_ERROR");
        env.acquireValue(par);
        assertEquals(par.objectValue(env), "capped_lines");
        
        // using reflection to access layers in plot display
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        
        // there should be two layers: one for the error bars, one for the 
        // (x, y) values
        assertTrue(!ArrayUtils.isEmpty(layers));
        assertEquals(ArrayUtils.getLength(layers), 2);
        assertEquals(layers[0].getDataSpec().getSourceTable().getRowCount(), 
                layers[1].getDataSpec().getSourceTable().getRowCount());
        
        // assert that the plot has the same amount of data as the original SED
        assertEquals(sed.getSegment(0).getLength(), 
                layers[0].getDataSpec().getSourceTable().getRowCount());
    }
    
    @Test
    public void testAddTwoSegments() throws Exception {
        
        ExtSed sed = ExtSed.read(TestData.class.getResource("3c273.vot").openStream(), SedFormat.VOT);
        sed.addSegment(ExtSed.read(TestData.class.getResource("test.vot").openStream(), SedFormat.VOT).getSegment(0));

        StilPlotter plot = setUpTests(sed);
        PlotDisplay<?, ?> display = plot.getPlotDisplay();

        // using reflection to access layers in plot display
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        
        // there should be four layers: two for error bars of 3C273 and test,
        // and two for the corresponding (x, y) values.
        assertEquals(ArrayUtils.getLength(layers), 4);
        assertEquals(layers[0].getDataSpec().getSourceTable().getRowCount(), 
                layers[1].getDataSpec().getSourceTable().getRowCount());
        assertEquals(layers[2].getDataSpec().getSourceTable().getRowCount(), 
                layers[3].getDataSpec().getSourceTable().getRowCount());
        
        // assert that the plot has the same amount of data as the original SED
        assertEquals(sed.getSegment(0).getLength(), 
                layers[0].getDataSpec().getSourceTable().getRowCount());
        
        // TODO: we should check that each segment has a different color in the
        // future when iris-dev#14 is done
    }
    
    @Test
    public void testReset() throws Exception {

        ExtSed sed = ExtSed.read(TestData.class.getResource("3c273.vot").openStream(), SedFormat.VOT);
        Segment seg = sed.getSegment(0);
        StilPlotter plot = setUpTests(sed);
        
        // Get initial bounds
        PlotDisplay<Profile, PlaneAspect> display = plot.getPlotDisplay();
        PlaneAspect aspect1 = display.getAspect();
        
        // Fix the plot
        plot.getPlotPreferences().setFixed(true);
        
        // Remove the segment
        sed.removeSegment(0);
        preferences.remove(sed, seg);
        
        plot.resetPlot(false, false);
        display = plot.getPlotDisplay();

        // Verify no layers
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        assertEquals(0, ArrayUtils.getLength(layers));
        
        // Bounds should not have changed
        PlaneAspect aspect2 = display.getAspect();
        assertEquals(aspect1.getXMax(), aspect2.getXMax(), .0001);
        assertEquals(aspect1.getYMax(), aspect2.getYMax(), .0001);
        assertEquals(aspect1.getXMin(), aspect2.getXMin(), .0001);
        assertEquals(aspect1.getYMin(), aspect2.getYMin(), .0001);

        plot.resetPlot(true, false);
        display = plot.getPlotDisplay();
        
        // Bounds should reset
        PlaneAspect aspect3 = display.getAspect();
        assertEquals(10, aspect3.getXMax(), .0001);
        assertEquals(10, aspect3.getYMax(), .0001);
        assertEquals(1, aspect3.getXMin(), .0001);
        assertEquals(1, aspect3.getYMin(), .0001);
    }
    
    @Test
    public void testZoom() throws Exception {
        ExtSed sed = ExtSed.read(TestData.class.getResource("3c273.vot").openStream(), SedFormat.VOT);
        StilPlotter plot = setUpTests(sed);
        
        // Get initial bounds
        PlotDisplay<Profile, PlaneAspect> display = plot.getPlotDisplay();
        PlaneAspect aspect1 = display.getAspect();
        
        // zoom in
        plot.zoom(1.5);
        
        PlaneAspect aspect = (PlaneAspect) plot.getPlotDisplay().getAspect();
        double xmax = aspect.getXMax();
        double xmin = aspect.getXMin();
        double ymax = aspect.getYMax();
        double ymin = aspect.getYMin();
        
        // expected ranges (log space)
        double expectedXmax = 1.7779708E21;
        double expectedXmin = 5.7953144E9;
        double expectedYmax = 7.3199692;
        double expectedYmin = 3.1956419E-9;
        
//        DecimalFormat df = new DecimalFormat("#.#####");
//        String df_format = df.format(xmax);
//        double ndf = Double.valueOf(df_format);
        
        assertEquals(Math.log10(expectedXmax), Math.log10(xmax), 0.000001);
        assertEquals(Math.log10(expectedXmin), Math.log10(xmin), 0.000001);
        assertEquals(Math.log10(expectedYmax), Math.log10(ymax), 0.000001);
        assertEquals(Math.log10(expectedYmin), Math.log10(ymin), 0.000001);
        
        // zoom out. Plot should be back at the ranges it was at before.
        // scale factor: 1 - PlotterView.ZOOM_SCALE * 2/3
        plot.zoom(1 - PlotterView.ZOOM_SCALE * 2/3);
        
        aspect = (PlaneAspect) plot.getPlotDisplay().getAspect();
        xmax = aspect.getXMax();
        xmin = aspect.getXMin();
        ymax = aspect.getYMax();
        ymin = aspect.getYMin();
        
        assertEquals(Math.log10(xmax), Math.log10(aspect1.getXMax()), 0.000001);
        assertEquals(Math.log10(xmin), Math.log10(aspect1.getXMin()), 0.000001);
        assertEquals(Math.log10(ymax), Math.log10(aspect1.getYMax()), 0.000001);
        assertEquals(Math.log10(ymin), Math.log10(aspect1.getYMin()), 0.000001);
        
    }
    
    private StilPlotter setUpTests(ExtSed sed) throws Exception {
        preferences = new VisualizerComponentPreferences(ws);
        preferences.update(sed);
        preferences.getDataModel().setSelectedSed(sed);
        
        StilPlotter plot = new StilPlotter(preferences);
        plot.resetPlot(false, false);
        return plot;
    }
}
