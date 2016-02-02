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
package cfa.vo.iris.visualizer.stil;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.sed.stil.SegmentStarTableAdapter;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.test.App;
import cfa.vo.iris.test.Ws;
import cfa.vo.sedlib.ISegment;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.awt.Color;
import java.lang.reflect.Field;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.task.StringParameter;
import uk.ac.starlink.ttools.plot2.PlotLayer;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;
import cfa.vo.testdata.TestData;
import java.lang.reflect.Array;
import java.util.List;
import uk.ac.starlink.task.BooleanParameter;
import uk.ac.starlink.task.Environment;
import uk.ac.starlink.task.Parameter;
import uk.ac.starlink.task.TaskException;
import uk.ac.starlink.ttools.plot.Style;
import uk.ac.starlink.ttools.plot2.LayerOpt;
import uk.ac.starlink.ttools.plot2.config.ConfigKey;
import uk.ac.starlink.ttools.plot2.layer.AbstractPlotLayer;
import uk.ac.starlink.ttools.plot2.layer.ShapeMode;

/**
 *
 * @author jbudynk
 */
public class StilPlotterTest { //extends AbstractComponentGUITest {
    
//    private VisualizerComponent comp;
    private ExtSed sed;
    private Ws ws = new Ws();
    private final App app = new App();
    private StarTableAdapter<ISegment> adapter;
    
    public StilPlotterTest() {
        //comp = new VisualizerComponent();
        adapter = new SegmentStarTableAdapter();
        
    }

    @Test
    public void testAddSed() throws Exception {
        
        sed = ExtSed.read(TestData.class.getResource("3c273.vot").getFile(), SedFormat.VOT);

        StilPlotter plot = new StilPlotter(app, ws, adapter);
        plot.reset(sed, true);
        PlotDisplay display = plot.getPlotDisplay();
        
        // check that plot env is correctly set
        MapEnvironment env = plot.getEnv();

        // check colors
        StringParameter par = new StringParameter("color");
        env.acquireValue(par);
        assertEquals(par.objectValue(env), "blue");
        
        // check shape
        par.setName("shape_3C 273");
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
        par.setName("errorbar_3C 273_ERROR");
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
    }
    
    @Test
    public void testAddTwoSegments() throws Exception {
        sed = ExtSed.read(TestData.class.getResource("3c273.vot").getFile(), SedFormat.VOT);
        sed.addSegment(ExtSed.read(TestData.class.getResource("test300k_VO.fits").getFile(), SedFormat.FITS).getSegment(0));

        StilPlotter plot = new StilPlotter(app, ws, adapter);
        plot.reset(sed, true);
        PlotDisplay display = plot.getPlotDisplay();
        
        // check that plot env is correctly set
        MapEnvironment env = plot.getEnv();
        
        // using reflection to access layers in plot display
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        
        // there should be four layers: two for error bars of 3C273 and 300k_VO,
        // and two for the corresponding (x, y) values.
        assertEquals(ArrayUtils.getLength(layers), 4);
        assertEquals(layers[0].getDataSpec().getSourceTable().getRowCount(), 
                layers[1].getDataSpec().getSourceTable().getRowCount());
        assertEquals(layers[2].getDataSpec().getSourceTable().getRowCount(), 
                layers[3].getDataSpec().getSourceTable().getRowCount());
        
        // TODO: we should check that each segment has a different color
    }
}