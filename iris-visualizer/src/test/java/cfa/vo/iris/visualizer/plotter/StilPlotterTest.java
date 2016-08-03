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

import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.test.Ws;
import cfa.vo.iris.visualizer.plotter.PlotPreferences.PlotType;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.plotter.PlotterView;
import cfa.vo.iris.visualizer.preferences.FittingRangeModel;
import cfa.vo.iris.visualizer.preferences.FunctionModel;
import cfa.vo.iris.visualizer.preferences.LayerModel;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.swing.SwingConstants;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import uk.ac.starlink.task.StringParameter;
import uk.ac.starlink.ttools.plot2.PlotLayer;
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurfaceFactory.Profile;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.task.BooleanParameter;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurfaceFactory;

public class StilPlotterTest {
    
    static {
        UISpec4J.init();
    }
    
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
        StringParameter par = new StringParameter("shape");
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
        par.setName("errorbar");
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
        preferences.getDataStore().remove(sed, seg);
        
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
    
    @Test
    public void testPan() throws Exception {
        ExtSed sed = new ExtSed("test", false);
        StilPlotter plot = setUpTests(sed);
        
        // Xlog, Y linear for testing both cases
        plot.setPlotType(PlotType.X_LOG);
        
        // Get initial bounds
        PlotDisplay<Profile, PlaneAspect> display = plot.getPlotDisplay();
        PlaneAspect aspect = display.getAspect();
        
        // Verifying the default values, in case they ever change
        assertEquals(10, aspect.getXMax(), 0.01);
        assertEquals(1, aspect.getYMax(), 0.01);
        assertEquals(1.0, aspect.getXMin(), 0.01);
        assertEquals(0, aspect.getYMin(), 0.01);
        
        /*
         * TEST LOG SCALES
         */
        
        // Pan to the right (EAST)
        // Verify new X-Values shifted by factor of 3
        plot.dataPan(SwingConstants.EAST);
        aspect = display.getAspect();
        assertEquals(30, aspect.getXMax(), 0.01);
        assertEquals(1, aspect.getYMax(), 0.01);
        assertEquals(3, aspect.getXMin(), 0.01);
        assertEquals(0, aspect.getYMin(), 0.01);
        
        // Shift back, should get to original values
        plot.dataPan(SwingConstants.WEST);
        aspect = display.getAspect();
        assertEquals(10, aspect.getXMax(), 0.01);
        assertEquals(1, aspect.getYMax(), 0.01);
        assertEquals(1.0, aspect.getXMin(), 0.01);
        assertEquals(0, aspect.getYMin(), 0.01);
        
        // Test negative X shift
        // Verify new X-Values shifted by factor of 3
        plot.dataPan(SwingConstants.WEST);
        aspect = display.getAspect();
        assertEquals(3 + 1.0/3, aspect.getXMax(), 0.01);
        assertEquals(1, aspect.getYMax(), 0.01);
        assertEquals(1.0/3, aspect.getXMin(), 0.01);
        assertEquals(0, aspect.getYMin(), 0.01);
        
        // Shift back, should get to original values
        plot.dataPan(SwingConstants.EAST);
        aspect = display.getAspect();
        assertEquals(10, aspect.getXMax(), 0.01);
        assertEquals(1, aspect.getYMax(), 0.01);
        assertEquals(1.0, aspect.getXMin(), 0.01);
        assertEquals(0, aspect.getYMin(), 0.01);
        
        /*
         * TEST LINEAR SCALES
         */
        
        // Pan up
        // Verify new Y-Values shifted by factor of .25 (width of Y scale)
        plot.dataPan(SwingConstants.NORTH);
        aspect = display.getAspect();
        assertEquals(10, aspect.getXMax(), 0.01);
        assertEquals(1.25, aspect.getYMax(), 0.01);
        assertEquals(1, aspect.getXMin(), 0.01);
        assertEquals(0.25, aspect.getYMin(), 0.01);
        
        // Pan back down should return to original values
        // Verify new Y-Values shifted by factor of .25 (width of Y scale)
        plot.dataPan(SwingConstants.SOUTH);
        aspect = display.getAspect();
        assertEquals(10, aspect.getXMax(), 0.01);
        assertEquals(1, aspect.getYMax(), 0.01);
        assertEquals(1.0, aspect.getXMin(), 0.01);
        assertEquals(0, aspect.getYMin(), 0.01);
    }
    
    @Test
    public void testPlotFunctionModel() throws Exception {
        
        // create a sed
        Segment seg = TestUtils.createSampleSegment();
        ExtSed sed = new ExtSed("my_sed", false);
        sed.addSegment(seg);
        
        StilPlotter plot = setUpTests(sed);
        
        SedModel model = plot.getDataModel().getSedModel(sed);
        model.getDataTables().get(0).getPlotterDataTable().setModelValues(seg.getFluxAxisValues());
        model.getDataTables().get(0).getPlotterDataTable().setRatioValues(seg.getSpectralAxisValues());
        model.getDataTables().get(0).getPlotterDataTable().setResidualValues(seg.getSpectralAxisValues());
        plot.getDataModel().refresh();
        
        FunctionModel functionModel = model.getFunctionModel();
        
        LayerModel layer = functionModel.getFunctionLayerModel();
        String functionLayerName = layer.getSuffix();
        
        assertEquals("red", layer.getLineColor());
        assertEquals("line", layer.getLayerType());
        
        // replot with residuals
        plot.setShowResiduals(true);
        
        PlotDisplay<?, ?> display = plot.getPlotDisplay();
        
        // check that plot env is correctly set
        MapEnvironment env = plot.getEnv();

        // check color is "ff1e02" (red)
        StringParameter par = new StringParameter("color" + functionLayerName);
        env.acquireValue(par);
        assertEquals("ff1e02", par.objectValue(env));
        
        // check that a line is plotted
        par.setName("layer" + functionLayerName);
        env.acquireValue(par);
        assertEquals(par.objectValue(env), "line");
        
        // using reflection to access layers in plot display
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        
        // there should be 3 layers for the function/model, flux, and errs
        assertTrue(!ArrayUtils.isEmpty(layers));
        assertEquals(3, ArrayUtils.getLength(layers));
        
        // check that plot env is correctly set
        MapEnvironment resEnv = plot.getResEnv();
        par = new StringParameter("color" + functionLayerName);
        resEnv.acquireValue(par);
        assertEquals(par.objectValue(env), "black");
        
        // Verify the residuals are plotted
        display = plot.getResidualsPlotDisplay();
        
        // using reflection to access layers in plot display
        layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        layers = (PlotLayer[]) layers_.get(display);
        
        // there should be 1 layer for residuals
        assertTrue(!ArrayUtils.isEmpty(layers));
        assertEquals(1, ArrayUtils.getLength(layers));
        
        // now, change the units on the plotter and verify the model units change
        plot.getDataModel().setUnits("Angstrom", "mJy");
        for (int i=0; i<seg.getLength(); i++) {
            assertNotEquals(seg.getFluxAxisValues()[i], model.getDataTables().get(0).getPlotterDataTable().getModelValues()[i]);
        }
    }
    
    @Test
    public void testCoplottedFunctionModel() throws Exception {
        
        // create a sed
        Segment seg1 = TestUtils.createSampleSegment();
        ExtSed sed1 = new ExtSed("my_sed1", false);
        sed1.addSegment(seg1);
        
        StilPlotter plot = setUpTests(sed1);
        
        // Add additional SED
        Segment seg2 = TestUtils.createSampleSegment();
        ExtSed sed2 = new ExtSed("my_sed2", false);
        sed2.addSegment(seg2);
        
        preferences.getDataStore().update(sed2);
        preferences.getDataModel().setSelectedSeds(Arrays.asList(sed1, sed2));
        
        // Add model functions
        SedModel model1 = plot.getDataModel().getSedModel(sed1);
        model1.getDataTables().get(0).getPlotterDataTable().setModelValues(seg1.getFluxAxisValues());
        model1.getDataTables().get(0).getPlotterDataTable().setRatioValues(seg1.getSpectralAxisValues());
        model1.getDataTables().get(0).getPlotterDataTable().setResidualValues(seg1.getSpectralAxisValues());
        
        SedModel model2 = plot.getDataModel().getSedModel(sed2);
        model2.getDataTables().get(0).getPlotterDataTable().setModelValues(seg2.getFluxAxisValues());
        model2.getDataTables().get(0).getPlotterDataTable().setRatioValues(seg2.getSpectralAxisValues());
        model2.getDataTables().get(0).getPlotterDataTable().setResidualValues(seg2.getSpectralAxisValues());
        
        // Refresh the plotter
        plot.getDataModel().refresh();
        
        FunctionModel functionModel1 = model1.getFunctionModel();
        LayerModel layer1 = functionModel1.getFunctionLayerModel();
        String functionLayerName1 = layer1.getSuffix();

        FunctionModel functionModel2 = model2.getFunctionModel();
        LayerModel layer2 = functionModel2.getFunctionLayerModel();
        String functionLayerName2 = layer2.getSuffix();
        
        // replot with residuals
        plot.setShowResiduals(true);
        
        PlotDisplay<?, ?> display = plot.getPlotDisplay();
        
        // check that plot env is correctly set
        MapEnvironment env = plot.getEnv();

        // check colors are different
        StringParameter par = new StringParameter("color" + functionLayerName1);
        env.acquireValue(par);
        assertEquals("ff1e02", par.objectValue(env));
        par = new StringParameter("color" + functionLayerName2);
        env.acquireValue(par);
        assertEquals("fe75ff", par.objectValue(env));
        
        // using reflection to access layers in plot display
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        
        // there should be 6 layers for the functions/models, fluxes, and errs
        assertTrue(!ArrayUtils.isEmpty(layers));
        assertEquals(6, ArrayUtils.getLength(layers));
        
        // Verify the residuals are plotted
        display = plot.getResidualsPlotDisplay();
        
        // using reflection to access layers in plot display
        layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        layers = (PlotLayer[]) layers_.get(display);
        
        // there should be 2 layers for residuals
        assertTrue(!ArrayUtils.isEmpty(layers));
        assertEquals(2, ArrayUtils.getLength(layers));
    }
    
    @Test
    public void testModelFunctionValidity() throws Exception {
        
        // create a sed
        Segment seg1 = TestUtils.createSampleSegment();
        final ExtSed sed1 = new ExtSed("my_sed", false);
        sed1.addSegment(seg1);
        
        // There should be no window shown as there's no model
        final StilPlotter plot = setUpTests(sed1);
        
        // Add model function to SED
        SedModel model = plot.getDataModel().getSedModel(sed1);
        model.getDataTables().get(0).getPlotterDataTable().setModelValues(seg1.getFluxAxisValues());
        model.getDataTables().get(0).getPlotterDataTable().setRatioValues(seg1.getSpectralAxisValues());
        model.getDataTables().get(0).getPlotterDataTable().setResidualValues(seg1.getSpectralAxisValues());
        
        // SedModel now has a version
        model.setModelVersion(model.getVersion());
        model.setHasModelFunction(true);
        
        // Reset the plotter, no message should be sent
        plot.setSeds(Arrays.asList(sed1));
        
        // Update the sed with a new segment and replot
        Segment seg2 = TestUtils.createSampleSegment();
        sed1.addSegment(seg2);
        preferences.getDataStore().update(sed1, seg2);

        // Reset the plotter, since the segment has changed we expect a warning message
        WindowInterceptor.init(new Trigger() {
            @Override
            public void run() throws Exception {
                plot.setSeds(Arrays.asList(sed1));
            }
        }).process(new WindowHandler() {
            @Override
            public Trigger process(Window warning) throws Exception {
                assertTrue(StringUtils.contains(warning.getTitle(), "Warning"));
                assertTrue(StringUtils.contains(
                        warning.getTextBox("Warning").getText(), sed1.getId()));
                return Trigger.DO_NOTHING;
            }
        }).run();
        
        // Now that it has been run, the dialog should not show up again
        plot.setSeds(Arrays.asList(sed1));
    }
    
    @Test
    public void testResiduals() throws Exception {
        ExtSed sed = new ExtSed("test", false);
        StilPlotter plot = setUpTests(sed);
        
        assertFalse(plot.isShowResiduals());
        assertNull(plot.getResEnv());
        assertNull(plot.getResidualsPlotDisplay());
        
        // Only one component for primary display
        assertEquals(1, plot.getComponentCount());
        
        // Plot residuals (set by default)
        plot.setShowResiduals(true);
        MapEnvironment resEnv = plot.getResEnv();
        PlotDisplay<Profile, PlaneAspect> res = plot.getResidualsPlotDisplay();
        
        assertNotNull(resEnv);
        assertNotNull(res);
        
        // Should now be two components for display and residuals
        assertEquals(2, plot.getComponentCount());
        
        // Verify y axis label
        StringParameter par = new StringParameter("ylabel");
        resEnv.acquireValue(par);
        assertEquals(par.objectValue(resEnv), "Residuals");
        
        // Plot ratios
        plot.setResidualsOrRatios("Ratios");
        res = plot.getResidualsPlotDisplay();
        resEnv = plot.getResEnv();
        
        // Reverify settings
        par = new StringParameter("ylabel");
        resEnv.acquireValue(par);
        assertEquals(par.objectValue(resEnv), "Ratios");
        
        // Hide the ratios and verify
        plot.setShowResiduals(false);
        assertNull(plot.getResEnv());
        assertNull(plot.getResidualsPlotDisplay());
        assertEquals(1, plot.getComponentCount());
    }

    // TODO: Make this work when the secondary plot is attached to the plot zoom.
    @Test
    @Ignore
    public void testResidualsZoom() throws Exception {
        ExtSed sed = new ExtSed("test", false);
        StilPlotter plot = setUpTests(sed);

        plot.setShowResiduals(true);

        PlotDisplay<Profile, PlaneAspect> disp = plot.getPlotDisplay();
        PlotDisplay<Profile, PlaneAspect> res = plot.getResidualsPlotDisplay();
        
        plot.zoom(1.05);
        assertEquals(disp.getAspect().getXMax(), res.getAspect().getXMax(), 0.01);
        assertEquals(disp.getAspect().getXMin(), res.getAspect().getXMin(), 0.01);
    }
    
    @Test
    public void testFittingRangePlot() throws Exception {
        ExtSed sed = new ExtSed("test", false);
        
        // Fix plot at initial bounds (1,10) (1,10)
        final StilPlotter plot = setUpTests(sed);
        plot.getPlotPreferences().setFixed(true);
        
        // Just make sure plot aspect hasn't changed
        assertEquals(1, plot.getPlotDisplay().getAspect().getYMin(), 0.001);
        assertEquals(10, plot.getPlotDisplay().getAspect().getYMax(), 0.001);
        
        // Add fitting range
        FitConfiguration fit = new FitConfiguration();
        fit.addFittingRange(new FittingRange(1, 9));
        sed.setFit(fit);
        plot.setSeds(Arrays.asList(sed));
        plot.resetPlot(false, false);
        
        TestUtils.invokeWithRetry(100, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(plot.fittingRanges != null);
                assertEquals(FittingRangeModel.FITTING_LAYER, plot.fittingRanges.getInSource().getName());
            }
        });

        PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> display = plot.getPlotDisplay();
        
        // Just make sure plot aspect hasn't changed
        assertEquals(1, display.getAspect().getYMin(), 0.001);
        assertEquals(10, display.getAspect().getYMax(), 0.001);
        
        // using reflection to access layers in plot display
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        
        // there should be 2 layers for the FunctionModel, one for marks and one for errors
        assertEquals(2, ArrayUtils.getLength(layers));
        
        FittingRangeModel model = plot.fittingRanges;
        StarTable fitStarTable = model.getInSource();
        
        // Validate values
        assertEquals(1, fitStarTable.getRowCount());
        Object[] row = fitStarTable.getRow(0);
        assertEquals(4, row.length);
        
        assertEquals(5.0, (double) row[0], 0.00001);
        assertEquals(4.0, (double) row[1], 0.00001);
        assertEquals(4.0, (double) row[2], 0.00001);
        assertEquals(1.258925, (double) row[3], 0.00001);
    }
    
    @Test
    public void testFittingRangeCalculation() {
        
        // Linear tests
        PlaneAspect aspect = new PlaneAspect(new double[] {0,10}, new double[] {.1,10});
        assertEquals(1.09, StilPlotter.computeFittingLocation(aspect, false), 0.0001);

        aspect = new PlaneAspect(new double[] {0,10}, new double[] {-10,1});
        assertEquals(-8.9, StilPlotter.computeFittingLocation(aspect, false), 0.0001);
        
        // Log tests
        aspect = new PlaneAspect(new double[] {0,10}, new double[] {.1,.5});
        assertEquals(.11745, StilPlotter.computeFittingLocation(aspect, true), 0.0001);
        
        aspect = new PlaneAspect(new double[] {0,10}, new double[] {.1,5});
        assertEquals(.14787, StilPlotter.computeFittingLocation(aspect, true), 0.0001);
    }
    
    private StilPlotter setUpTests(ExtSed sed) throws Exception {
        preferences = new VisualizerComponentPreferences(ws);
        preferences.getDataStore().update(sed);
        preferences.updateSelectedSed(sed);
        
        StilPlotter plot = new StilPlotter(preferences);
        
        return plot;
    }
}
