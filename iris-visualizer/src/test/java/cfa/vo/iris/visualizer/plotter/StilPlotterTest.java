/*
 * Copyright 2015 Chandra X-Ray Observatory.
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

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.iris.visualizer.test.App;
import cfa.vo.iris.visualizer.test.Ws;
import cfa.vo.sedlib.io.SedFormat;

import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author jbudynk
 */
public class StilPlotterTest {
    
    private IrisApplication app;
    private IWorkspace ws;
    
    private StilPlotter plotter;
    
    public StilPlotterTest() {        
    }
    
    @Before
    public void setUp() {
        app = new App();
        ws = new Ws();
        plotter = new StilPlotter(null, app, ws);
    }
    
    @After
    public void tearDown() {
    }

    // Check that (X, Y, Y-error) points are loaded from a StarTable
    @Test
    public void testPointsLoadedFromStarTable() throws Exception {
        // load the test dataset into a SED. This is the same dataset loaded
        // into the protoype plotter.
        String url = this.getClass().getResource("/test_data/3c273.vot").getFile();
        ExtSed sed = ExtSed.read(url, SedFormat.VOT);
        
        // check that x values are present and correct
        double[] xAxis = convertToDoubles(plotter.getXAxis());
        assertArrayEquals(xAxis, sed.getSegment(0).getSpectralAxisValues(), 0.0000001);
        
        // check that y values are present and correct
        double[] yAxis = convertToDoubles(plotter.getYAxis());
        assertArrayEquals(yAxis, sed.getSegment(0).getFluxAxisValues(), 0.0000001);
        
        // check that y-errors are present and correct
        double[] yAxisErrors = convertToDoubles(plotter.getYErrors());
        assertArrayEquals(yAxisErrors, (double[]) sed.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR), 0.0000001);
    }
    
    // Check that Plotter behaves correctly when there are no points in the StarTable
    @Ignore
    @Test
    public void testEmptyStarTable() throws Exception {
        fail("StilPlotter does not test for displaying empty StarTables.");
        
        // create empty StarTable
//        ColumnInfo[] colInfos = new ColumnInfo[ 3 ];
//        colInfos[ 0 ] = new ColumnInfo( "Name", String.class, "Object name" );
//        colInfos[ 1 ] = new ColumnInfo( "RA", Double.class, "Right Ascension" );
//        colInfos[ 2 ] = new ColumnInfo( "Dec", Double.class, "Declination" );
//
//        // Construct a new, empty table with these columns.
//        RowListStarTable table = new RowListStarTable( colInfos );
//        
//        // add StarTable and preferences to the StilPlotter
//        plotter.addTable(table);
//        
//        // check that x values are present and correct
//        List<Double> xAxis = plotter.getXAxis();
//        assertTrue(xAxis.isEmpty());
//        
//        // check that y values are present and correct
//        List<Double> yAxis = plotter.getYAxis();
//        assertTrue(yAxis.isEmpty());
    }
    
    @Ignore
    @Test
    public void testEmptySed() throws Exception {
        fail("Test not implemented yet.");
        
          // create empty SED
//        ExtSed sed = new ExtSed("EmptySed");
//        
//        // add the SED
//        plotter.addSegment(sed.getSegment(0));
//        
//        // check that x values are present and correct
//        List<Double> xAxis = plotter.getXAxis();
//        assertTrue(xAxis.isEmpty());
//        
//        // check that y values are present and correct
//        List<Double> yAxis = plotter.getYAxis();
//        assertTrue(yAxis.isEmpty());
    }
    
    @Ignore
    @Test
    public void testYErrsDisplay() {
        fail("Test not implemented yet.");
    }
    
    public double[] convertToDoubles(ArrayList<Double> doubles) {
        double[] target = new double[doubles.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = doubles.get(i);
        }
        return target;
    }
    
}
