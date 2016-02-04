package cfa.vo.iris.visualizer.preferences;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang.StringUtils;

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
            addSegment(sed.getSegment(i));
        }
    }
    
    private void addSegment(Segment seg) {
        
        // If the segment is already in the map remake the star table
        if (segmentPreferences.containsKey(seg)) {
            segmentPreferences.get(seg).setInSource(adapter.convertStarTable(seg));
            return;
        }
        
        // Ensure that the layer has a unique identifier in the list of segments
        SegmentLayer layer = new SegmentLayer(adapter.convertStarTable(seg));
        int count = 0;
        while (!isUniqueLayerSuffix(layer.getSuffix())) {
            count++;
            layer.setSuffix(layer.getSuffix() + " " + count);
        }
        
        segmentPreferences.put(seg, layer);
    }
    
    private boolean isUniqueLayerSuffix(String suffix) {
        for (SegmentLayer layer : segmentPreferences.values()) {
            if (StringUtils.equals(layer.getSuffix(), suffix)) {
                return false;
            }
        }
        
        return true;
    }

    protected void remove(ExtSed sed) {
        
        for (int i=0; i < sed.getNumberOfSegments(); i++) {
            
            Segment seg = sed.getSegment(i);
            if (segmentPreferences.containsKey(seg)) {
                segmentPreferences.remove(seg);
            }
        }
    }
}
