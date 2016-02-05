package cfa.vo.iris.visualizer.preferences;

import static org.junit.Assert.*;

import static cfa.vo.iris.test.unit.TestUtils.*;

import org.junit.Test;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTableAdapter;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.sedlib.Segment;

public class SedPreferencesTest {
    
    @Test
    public void testPreferences() throws Exception {
        ExtSed sed = new ExtSed("test");
        StarTableAdapter<Segment> adapter = new SegmentStarTableAdapter();
        
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
        
        // Remove a segment works
        sed.remove(seg1);
        prefs.refresh();
        assertEquals(1, prefs.getAllSegmentPreferences().size());
    }
}
