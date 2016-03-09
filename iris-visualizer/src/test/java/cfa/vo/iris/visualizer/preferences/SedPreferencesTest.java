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
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.Target;
import cfa.vo.sedlib.TextParam;

public class SedPreferencesTest {
    
    @Test
    public void testPreferences() throws Exception {
        ExtSed sed = new ExtSed("test");
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        SedPreferences prefs = new SedPreferences(sed, adapter);
        
        assertEquals(0, prefs.getAllSegmentPreferences().size());
        
        Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);
        
        // Should pick up the change
        prefs.refresh();
        assertEquals(1, prefs.getAllSegmentPreferences().size());
        
        // Re-adding the same segment should not alter the map
        prefs.addSegment(seg1);
        assertEquals(1, prefs.getAllSegmentPreferences().size());
        
        // Whereas adding an identical (but new) segment should add a new map element
        Segment seg2 = createSampleSegment();
        sed.addSegment(seg2);
        prefs.refresh();
        assertEquals(2, prefs.getAllSegmentPreferences().size());

        // Same segments should still have different suffixes
        SegmentLayer layer1 = prefs.getSegmentPreferences(seg1);
        SegmentLayer layer2 = prefs.getSegmentPreferences(seg2);
        assertFalse(layer1.getSuffix().equals(layer2.getSuffix()));
        
        // Check that the colors for each segment are different
        String color2 = prefs.getSegmentPreferences(seg2).getMarkColor();
        assertNotEquals(
                prefs.getSegmentPreferences(seg1).getMarkColor(),
                prefs.getSegmentPreferences(seg2).getMarkColor());
        
        // Ensure we get the right startables back
        assertEquals(3, layer1.getInSource().getRowCount());
        assertEquals(3, layer2.getInSource().getRowCount());
        
        // Remove a segment works
        sed.remove(seg1);
        prefs.refresh();
        assertEquals(1, prefs.getAllSegmentPreferences().size());
        
        // The color for seg2 should still be the same as it was before 
        // seg1 was removed
        assertEquals(color2, prefs.getSegmentPreferences(seg2).getMarkColor());
        
        assertNotNull(prefs.getSegmentPreferences(seg2));
        assertNotNull(prefs.getSegmentPreferences(seg2).getInSource());
        
        // Units are correct
        assertEquals(sed.getSegment(0).getFluxAxisUnits(), prefs.getYunits());
        assertEquals(sed.getSegment(0).getSpectralAxisUnits(), prefs.getXunits());
    }
    
    @Test
    public void testSuffixesWithSameTargetNames() throws Exception {
        ExtSed sed = new ExtSed("test");
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        SedPreferences prefs = new SedPreferences(sed, adapter);
        
        // create two segments with the same Target name
        Target targ = new Target();
        targ.setName(new TextParam("my segment"));
        
        Segment seg1 = createSampleSegment();
        seg1.setTarget(targ);
        Segment seg2 = createSampleSegment();
        seg2.setTarget(targ);
        
        sed.addSegment(seg1);
        sed.addSegment(seg2);
        
        prefs.refresh();
        assertEquals("my segment", prefs.getSegmentPreferences(seg1).getSuffix());
        assertEquals("my segment 1", prefs.getSegmentPreferences(seg2).getSuffix());
        
        // add another segment of the same target name
        // suffix number should go up 1
        Segment seg3 = createSampleSegment();
        seg3.setTarget(targ);
        sed.addSegment(seg3);
        
        prefs.refresh();
        assertEquals("my segment 2", prefs.getSegmentPreferences(seg3).getSuffix());
    }
}
