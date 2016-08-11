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

package cfa.vo.iris.visualizer.preferences;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cfa.vo.iris.test.unit.TestUtils.*;

import org.junit.Test;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.TextParam;
import cfa.vo.sherpa.Data;

public class SedPreferencesTest {
    
    @Test
    public void testPreferences() throws Exception {
        ExtSed sed = new ExtSed("test");
        sed.setManaged(false);
        
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        SedModel model = new SedModel(sed, adapter);
        
        assertEquals(0, model.getLayerModels().size());
        
        Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);
        
        // Should pick up the change
        model.refresh();
        assertEquals(1, model.getLayerModels().size());
        
        // Re-adding the same segment should not alter the map
        model.addSegment(seg1);
        assertEquals(1, model.getLayerModels().size());
        
        // Whereas adding an identical (but new) segment should add a new map element
        Segment seg2 = createSampleSegment();
        sed.addSegment(seg2);
        model.refresh();
        assertEquals(2, model.getLayerModels().size());

        // Same segments should still have different suffixes
        LayerModel layer1 = model.getSegmentModel(seg1);
        LayerModel layer2 = model.getSegmentModel(seg2);
        assertFalse(layer1.getSuffix().equals(layer2.getSuffix()));
        
        // Check that the colors for each segment are different
        String color2 = model.getSegmentModel(seg2).getMarkColor();
        assertNotEquals(
                model.getSegmentModel(seg1).getMarkColor(),
                model.getSegmentModel(seg2).getMarkColor());
        
        // Ensure we get the right startables back
        assertEquals(3, layer1.getInSource().getRowCount());
        assertEquals(3, layer2.getInSource().getRowCount());
        
        // Remove a segment works
        sed.remove(seg1);
        model.refresh();
        assertEquals(1, model.getDataTables().size());

        // The color for seg2 should still be the same as it was before 
        // seg1 was removed
        assertEquals(color2, model.getSegmentModel(seg2).getMarkColor());
        
        assertNotNull(model.getSegmentModel(seg2));
        assertNotNull(model.getSegmentModel(seg2).getInSource());
        
        // Units are correct
        assertEquals(sed.getSegment(0).getFluxAxisUnits(), model.getYUnits());
        assertEquals(sed.getSegment(0).getSpectralAxisUnits(), model.getXUnits());
    }
    
    @Test
    public void testAddMultipleSegments() throws Exception {
        
        final ExtSed sed = new ExtSed("sed");
        sed.addSegment(createSampleSegment());
        sed.addSegment(createSampleSegment(new double[] {1}, new double[] {2}));
        
        // Set target names to same name
        sed.getSegment(0).createTarget();
        sed.getSegment(0).getTarget().setName(new TextParam("target1"));
        sed.getSegment(1).createTarget();
        sed.getSegment(1).getTarget().setName(new TextParam("target1"));
        
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        SedModel model = new SedModel(sed, adapter);
        
        assertEquals("target1", model.getSegmentModel(sed.getSegment(0)).getSuffix());
        assertEquals("target1", model.getSegmentModel(sed.getSegment(1)).getSuffix());
    }
    
    @Test
    public void testSedModelVersioning() throws Exception {
        
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(executor);
        try {
            runTestSedModelVersioning(adapter);
        } finally {
            executor.shutdown();
        }
    }
    
    private void runTestSedModelVersioning(IrisStarTableAdapter adapter) throws Exception {
        
        final ExtSed sed = new ExtSed("sed", false);
        
        // Empty SED -> 0 version
        SedModel model = new SedModel(sed, adapter);
        int h1 = model.getVersion();
        assertEquals(13, h1);
        
        // Add basic segment
        Segment segment1 = createSampleSegment();//createSampleSegment(new double[] {1,1}, new double[] {1,1});
        sed.addSegment(segment1);
        model.refresh();
        int h2 = model.getVersion();
        assertFalse(h1 == h2);

        // Additional segment changes hashcode
        Segment segment2 = createSampleSegment(new double[] {1,2}, new double[] {2,2});
        sed.addSegment(segment2);
        model.refresh();
        int h3 = model.getVersion();
        assertFalse(h2 == h3);
        
        // Remove 2nd segment
        sed.remove(segment2);
        model.refresh();
        int h4 = model.getVersion();
        assertTrue(h2 == h4);
        
        // Changing values changes version
        segment1.setSpectralAxisValues(new double[] {1, 2, 100});
        model.refresh();
        int h5 = model.getVersion();
        assertFalse(h4 == h5);
        
        // Masking points changes version
        model.getDataTables().get(0).applyMasks(new int[] {0});
        int h6 = model.getVersion();
        assertFalse(h5 == h6);
        
        // Remove mask
        model.getDataTables().get(0).clearMasks();
        int h7 = model.getVersion();
        assertTrue(h5 == h7);
    }
    
    @Test
    public void testFittingDataMethod() throws Exception {
        
        final ExtSed sed = new ExtSed("sed", false);
        sed.addSegment(createSampleSegment(new double[] {1,2,3}, new double[] {100,200,300}), 0);
        sed.addSegment(createSampleSegment(new double[] {4,5}, new double[] {400,500}), 1);

        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        SedModel model = new SedModel(sed, adapter);
        
        // Mask last row in both tables
        List<IrisStarTable> tables = model.getDataTables();
        tables.get(0).applyMasks(new int[] {2});
        tables.get(1).applyMasks(new int[] {1});
        
        Data allData = SAMPFactory.get(Data.class);
        Data maskedData = SAMPFactory.get(Data.class);
        
        model.getFittingData(allData, true);
        model.getFittingData(maskedData, false);
        
        // Verify allData is correct
        assertArrayEquals(new double[] {1,2,3,4,5}, allData.getX(), .01);
        assertArrayEquals(new double[] {100,200,300,400,500}, allData.getY(), .01);
        assertArrayEquals(new double[] {Double.NaN,Double.NaN,Double.NaN,Double.NaN,Double.NaN}, allData.getStaterror(), .01);
        
        // Verify masked data is masked...
        assertArrayEquals(new double[] {1,2,4}, maskedData.getX(), .01);
        assertArrayEquals(new double[] {100,200,400}, maskedData.getY(), .01);
        assertArrayEquals(new double[] {Double.NaN,Double.NaN,Double.NaN}, maskedData.getStaterror(), .01);
    }
}
