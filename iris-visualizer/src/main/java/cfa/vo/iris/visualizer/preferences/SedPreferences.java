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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.sedlib.Segment;

/**
 * Maintains visualizer preferences for an SED currently in the 
 * workspace.
 *
 */
public class SedPreferences {
    
    private StarTableAdapter<Segment> adapter;
    private final Map<Segment, SegmentLayer> segmentPreferences;
    private final ExtSed sed;
    
    public SedPreferences(ExtSed sed, StarTableAdapter<Segment> adapter) {
        this.sed = sed;
        this.segmentPreferences = Collections.synchronizedMap(new LinkedHashMap<Segment, SegmentLayer>());
        this.adapter = adapter;
        
        refresh();
    }
    
    public Map<Segment, SegmentLayer> getSegmentPreferences() {
        return segmentPreferences;
    }
    
    protected void refresh() {
        for (int i=0; i < sed.getNumberOfSegments(); i++) {
            addSegment(sed.getSegment(i));
        }
        
        clean();
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
    
    // Removes any segments that are no longer in the SED
    private void clean() {
        for (Segment seg : segmentPreferences.keySet()) {
            if (sed.indexOf(seg) < 0) {
                segmentPreferences.remove(seg);
            }
        }
    }
    
    private boolean isUniqueLayerSuffix(String suffix) {
        for (SegmentLayer layer : segmentPreferences.values()) {
            if (StringUtils.equals(layer.getSuffix(), suffix)) {
                return false;
            }
        }
        
        return true;
    }

    protected void remove() {
        
        for (int i=0; i < sed.getNumberOfSegments(); i++) {
            
            Segment seg = sed.getSegment(i);
            if (segmentPreferences.containsKey(seg)) {
                segmentPreferences.remove(seg);
            }
        }
    }
}
