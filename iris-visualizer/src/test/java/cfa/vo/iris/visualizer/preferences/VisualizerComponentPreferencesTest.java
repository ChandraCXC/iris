package cfa.vo.iris.visualizer.preferences;

import static cfa.vo.iris.test.unit.TestUtils.createSampleSegment;
import static org.junit.Assert.*;

import org.junit.Test;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.StubWorkspace;
import cfa.vo.sedlib.Segment;

public class VisualizerComponentPreferencesTest {

    @Test
    public void testPreferences() throws Exception {
        ExtSed sed = new ExtSed("test");
        IWorkspace ws = new StubWorkspace();
        
        VisualizerComponentPreferences prefs = new VisualizerComponentPreferences(ws) {
            @Override
            protected void addSedListeners() {}
        };
        assertEquals(0, prefs.getSelectedLayers().size());
        assertEquals(0, prefs.getSedPreferences().size());
        assertEquals(0, prefs.getSelectedLayers().size());
        assertNull(prefs.getSelectedSedPreferences());
        
        // Add SED
        prefs.update(sed);
        assertEquals(1, prefs.getSedPreferences().size());
        assertEquals(0, prefs.getSedPreferences(sed).getAllSegmentPreferences().size());
        
        // Add segment to SED
        Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);
        prefs.update(sed, seg1);
        assertEquals(1, prefs.getSedPreferences().size());
        assertEquals(1, prefs.getSedPreferences(sed).getAllSegmentPreferences().size());
        
        // Add another segment
        Segment seg2 = createSampleSegment(new double[] {1}, new double[] {2});
        sed.addSegment(seg2);
        prefs.update(sed, seg2);
        assertEquals(1, prefs.getSedPreferences().size());
        assertEquals(2, prefs.getSedPreferences(sed).getAllSegmentPreferences().size());
        
        // Remove the first segment
        prefs.remove(sed, seg1);
        assertEquals(1, prefs.getSedPreferences().size());
        assertEquals(1, prefs.getSedPreferences(sed).getAllSegmentPreferences().size());
        
        // Remove the SED
        prefs.remove(sed);
        assertEquals(0, prefs.getSelectedLayers().size());
        assertEquals(0, prefs.getSedPreferences().size());
        assertEquals(0, prefs.getSelectedLayers().size());
    }
}
