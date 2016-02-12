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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.stil.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;

/**
 * Maintains visualizer preferences for an SED currently in the 
 * workspace.
 *
 */
public class SedPreferences {
    
    IrisStarTableAdapter adapter;
    final Map<MapKey, SegmentLayer> segmentPreferences;
    final ExtSed sed;
    
    private String xunits;
    private String yunits;
    
    public SedPreferences(ExtSed sed, IrisStarTableAdapter adapter) {
        this.sed = sed;
        this.segmentPreferences = Collections.synchronizedMap(new LinkedHashMap<MapKey, SegmentLayer>());
        this.adapter = adapter;
        
        refresh();
    }
    
    /**
     * Returns a map of each segment and its preferences currently in use by this
     * SED. Since Segment equality is broken for normal HashMaps, this uses an
     * IdentityHashMap for a memory location check. Order is not guaranteed.
     *  
     * @return
     *  A map of all segments and layer preferences currently in use by this SED.
     */
    public Map<Segment, SegmentLayer> getAllSegmentPreferences() {
        Map<Segment, SegmentLayer> ret = new IdentityHashMap<>();
        
        for (MapKey me : segmentPreferences.keySet()) {
            ret.put(me.segment, segmentPreferences.get(me));
        }
        
        return Collections.unmodifiableMap(ret);
    }
    
    public SegmentLayer getSegmentPreferences(Segment seg) {
        return segmentPreferences.get(new MapKey(seg));
    }
    
    void refresh() {
        for (int i=0; i < sed.getNumberOfSegments(); i++) {
            addSegment(sed.getSegment(i));
        }
        
        clean();
    }

    void removeAll() {
        segmentPreferences.clear();
    }
    
    void addSegment(Segment seg) {
        
        MapKey me = new MapKey(seg);
        
        // If the segment is already in the map remake the star table
        if (segmentPreferences.containsKey(me)) {
            segmentPreferences.get(me).setInSource(adapter.convertStarTable(seg));
            return;
        }
        
        // Ensure that the layer has a unique identifier in the list of segments
        SegmentLayer layer = new SegmentLayer(adapter.convertStarTable(seg));
        int count = 0;
        while (!isUniqueLayerSuffix(layer.getSuffix())) {
            count++;
            layer.setSuffix(layer.getSuffix() + " " + count);
        }
        
        setUnits(seg, layer);
        segmentPreferences.put(me, layer);
    }
    
    /**
     * Sets x and y units to the given SED if units are not already set.
     * 
     */
    void setUnits(Segment seg, SegmentLayer layer) {
        try {
            if (StringUtils.isEmpty(xunits)) {
                xunits = seg.getSpectralAxisUnits();
            }
            if (StringUtils.isEmpty(yunits)) {
                yunits = seg.getFluxAxisUnits();
            }
        } catch (SedNoDataException e) {
            // ignore;
        }
        
        try {
            layer.setXUnits(xunits);
            layer.setYUnits(yunits);
        } catch (UnitsException e) {
            throw new RuntimeException(e);
        }
    }
    
    // Removes any segments that are no longer in the SED
    void clean() {
        
        // Use iterator for concurrent modification
        Iterator<Entry<MapKey, SegmentLayer>> it = segmentPreferences.entrySet().iterator();
        
        boolean shouldRemove = true;
        while (it.hasNext()) {
            MapKey me = it.next().getKey();
            
            // Need to manual check for location equality test
            for (int i=0; i<sed.getNumberOfSegments(); i++) {
                if (me.segment == sed.getSegment(i)) {
                    shouldRemove = false;
                    break;
                }
            }
            if (shouldRemove) {
                it.remove();
            }
        }
    }
    
    boolean isUniqueLayerSuffix(String suffix) {
        for (SegmentLayer layer : segmentPreferences.values()) {
            if (StringUtils.equals(layer.getSuffix(), suffix)) {
                return false;
            }
        }
        return true;
    }
    
    public String getXunits() {
        return xunits;
    }

    public void setXunits(String xunits) throws UnitsException {
        this.xunits = xunits;
        for (SegmentLayer layer : segmentPreferences.values()) {
            layer.setXUnits(xunits);
        }
    }

    public String getYunits() {
        return yunits;
    }

    public void setYunits(String yunits) throws UnitsException {
        this.yunits = yunits;
        for (SegmentLayer layer : segmentPreferences.values()) {
            layer.setYUnits(yunits);
        }
    }

    /**
     * Segment equality is based on flux and spectral axis values, whereas we
     * require the memory location. This is a simple wrapper class for our segment
     * preferences map to override the usual map expectation of .equals with a
     * memory location check.
     *
     */
    static class MapKey {
        
        public Segment segment;
        
        public MapKey(Segment segment) {
            this.segment = segment;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            
            if (!(o instanceof MapKey)) {
                return false;
            }
            
            MapKey other = (MapKey) o;
            return this.segment == other.segment;
        }
        
        @Override
        public int hashCode() {
            return segment.hashCode();
        }
    }
}
