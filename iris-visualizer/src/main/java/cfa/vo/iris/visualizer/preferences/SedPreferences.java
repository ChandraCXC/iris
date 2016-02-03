package cfa.vo.iris.visualizer.preferences;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.sedlib.Segment;

/**
 * Maintains visualizer preferences for each SED currently in the 
 * workspace.
 *
 */
public class SedPreferences {
    
    private StarTableAdapter<Segment> adapter;
    private final Map<Segment, SegmentLayer> segmentPreferences;
    
    public SedPreferences(ExtSed sed, StarTableAdapter<Segment> adapter) {
        this.segmentPreferences = Collections.synchronizedMap(new WeakHashMap<Segment, SegmentLayer>());
        this.adapter = adapter;
        
        refresh(sed);
    }
    
    public Map<Segment, SegmentLayer> getSegmentPreferences() {
        return segmentPreferences;
    }
    
    protected void refresh(ExtSed sed) {
        
        for (int i=0; i < sed.getNumberOfSegments(); i++) {
            Segment seg = sed.getSegment(i);
            
            if (!segmentPreferences.containsKey(seg)) {
                segmentPreferences.put(seg, new SegmentLayer(adapter.convertStarTable(seg)));
            } else {
                segmentPreferences.get(seg).setInSource(adapter.convertStarTable(seg));
            }
        }
    }
}
