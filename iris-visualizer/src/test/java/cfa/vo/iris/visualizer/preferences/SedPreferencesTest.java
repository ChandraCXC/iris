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

import static cfa.vo.iris.test.unit.TestUtils.*;

import org.junit.Test;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.Target;
import cfa.vo.sedlib.TextParam;

public class SedPreferencesTest {
    
    @Test
    public void testPreferences() throws Exception {
        ExtSed sed = new ExtSed("test");
        sed.setManaged(false);
        
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        SedModel model = new SedModel(sed, adapter);
        
        assertEquals(0, model.getAllSegmentModels().size());
        
        Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);
        
        // Should pick up the change
        model.refresh();
        assertEquals(1, model.getAllSegmentModels().size());
        
        // Re-adding the same segment should not alter the map
        model.addSegment(seg1);
        assertEquals(1, model.getAllSegmentModels().size());
        
        // Whereas adding an identical (but new) segment should add a new map element
        Segment seg2 = createSampleSegment();
        sed.addSegment(seg2);
        model.refresh();
        assertEquals(2, model.getAllSegmentModels().size());

        // Same segments should still have different suffixes
        SegmentModel layer1 = model.getSegmentModel(seg1);
        SegmentModel layer2 = model.getSegmentModel(seg2);
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
        assertEquals(1, model.getAllSegmentModels().size());
        
        // The color for seg2 should still be the same as it was before 
        // seg1 was removed
        assertEquals(color2, model.getSegmentModel(seg2).getMarkColor());
        
        assertNotNull(model.getSegmentModel(seg2));
        assertNotNull(model.getSegmentModel(seg2).getInSource());
        
        // Units are correct
        assertEquals(sed.getSegment(0).getFluxAxisUnits(), model.getYunits());
        assertEquals(sed.getSegment(0).getSpectralAxisUnits(), model.getXunits());
    }
    
    @Test
    public void testSuffixesWithSameTargetNames() throws Exception {
        ExtSed sed = new ExtSed("test");
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        SedModel model = new SedModel(sed, adapter);
        
        // create two segments with the same Target name
        Target targ = new Target();
        targ.setName(new TextParam("my segment"));
        
        Segment seg1 = createSampleSegment();
        seg1.setTarget(targ);
        Segment seg2 = createSampleSegment();
        seg2.setTarget(targ);
        
        sed.addSegment(seg1);
        sed.addSegment(seg2);
        
        model.refresh();
        assertEquals("my segment", model.getSegmentModel(seg1).getSuffix());
        assertEquals("my segment 1", model.getSegmentModel(seg2).getSuffix());
        
        // add another segment of the same target name
        // suffix number should go up 1
        Segment seg3 = createSampleSegment();
        seg3.setTarget(targ);
        sed.addSegment(seg3);
        
        model.refresh();
        assertEquals("my segment 2", model.getSegmentModel(seg3).getSuffix());
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
        assertEquals("target1 1", model.getSegmentModel(sed.getSegment(1)).getSuffix());
    }
}
