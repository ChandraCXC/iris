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
import cfa.vo.iris.sed.SedException;
import cfa.vo.iris.sed.quantities.SPVYQuantity;
import cfa.vo.iris.sed.quantities.SPVYUnit;
import cfa.vo.iris.visualizer.plotter.ColorPalette;
import cfa.vo.iris.visualizer.plotter.HSVColorPalette;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains visualizer preferences for an SED currently in the 
 * workspace.
 *
 */
public class SedPreferences {
    
    IrisStarTableAdapter adapter;
    final Map<MapKey, SegmentLayer> segmentPreferences;
    final ExtSed sed;
    final ColorPalette colors;
    final PlotPreferences plotPreferences;
    
    private String xunits;
    private String yunits;
    
    public SedPreferences(ExtSed sed, IrisStarTableAdapter adapter) {
        this.sed = sed;
        this.segmentPreferences = Collections.synchronizedMap(new LinkedHashMap<MapKey, SegmentLayer>());
        this.adapter = adapter;
        this.colors = new HSVColorPalette();
        this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
        
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
    
    /**
     * Reserializes all segments within the sed.
     */
    void refresh() {
        for (int i=0; i < sed.getNumberOfSegments(); i++) {
            addSegment(sed.getSegment(i));
        }
        
        clean();
    }

    void removeAll() {
        segmentPreferences.clear();
    }
    
    /**
     * Add a segment to the sed preferences map.
     * @param seg
     */
    void addSegment(Segment seg) {
        
        // Do not keep track of empty segments
        if (seg == null) return;
        
        MapKey me = new MapKey(seg);
        
        // If the segment is already in the map remake the star table
        if (segmentPreferences.containsKey(me)) {
            segmentPreferences.get(me).setInSource(convertSegment(seg));
            return;
        }
        
        // Ensure that the layer has a unique identifier in the list of segments
        SegmentLayer layer = new SegmentLayer(convertSegment(seg));
        int count = 0;
        String id = layer.getSuffix();
        while (!isUniqueLayerSuffix(id)) {
            count++;
            id = layer.getSuffix() + " " + count;
        }
        layer.setSuffix(id);
        layer.getInSource().setName(id);
        
        // add colors to segment layer
        String hexColor = ColorPalette.colorToHex(colors.getNextColor());
        layer.setMarkColor(hexColor);
        
        // update legend settings
        layer.setLabel(id);
        
        // set the units
        setUnits(seg, layer);
        
        segmentPreferences.put(me, layer);
    }
    
    private IrisStarTable convertSegment(Segment seg) {
        // Convert segments with more than 3000 points asynchronously.
        if (seg.getLength() > 3000) {
            return adapter.convertSegmentAsync(seg);
        }
        return adapter.convertSegment(seg);
    }
    
    /**
     * Removes a segment from the sed preferences map.
     * @param segment
     */
    void removeSegment(Segment seg) {
        // Do not keep track of empty segments
        if (seg == null) return;
        
        MapKey me = new MapKey(seg);
        segmentPreferences.remove(me);
    }
    
    /**
     * Sets x and y units of the given segment to the preferred units.
     * If preferred units have not been set, the given segment's units
     * are used and set as the SED's preferred units.
     * 
     */
    private void setUnits(Segment seg, SegmentLayer layer) {
        // if this is the first segment to be added to the SED preferences,
        // set the preferred X and Y units to the segment's
        if (StringUtils.isEmpty(xunits) || StringUtils.isEmpty(xunits))
            setInitialUnits(seg);
        
        // set the layer units for this segment
        try {
            layer.setXUnits(xunits);
            layer.setYUnits(yunits);
        } catch (UnitsException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Set the SED units for the first time. Only should be called when the
     * first segment is added to an empty SED. This function sets the X and Y
     * units to that of the segment's.
     */
    private void setInitialUnits(Segment seg) {
        try {
            if (StringUtils.isEmpty(xunits))
                xunits = seg.getSpectralAxisUnits();
            if (StringUtils.isEmpty(yunits))
                yunits = seg.getFluxAxisUnits();
        } catch (SedNoDataException ex) {
            throw new RuntimeException(ex);
        }
        
        // set the plot axes labels
        plotPreferences.setXlabel(xunits);
        plotPreferences.setYlabel(yunits);
        
        // if in magnitudes, flip the direction of the Y-axis
        flipYifMag(yunits);
    }
    
    /**
     * Sets the x and y units of the SED
     * 
     * @param xunit    the X unit
     * @param yunit    the Y unit
     */
    public void setUnits(String xunit, String yunit) {
        xunits = xunit;
        yunits = yunit;
        plotPreferences.setXlabel(xunit);
        plotPreferences.setYlabel(yunit);
        
        // update the segment layers with the new units
        for (SegmentLayer seg : segmentPreferences.values()) {
            try {
                seg.setXUnits(xunits);
                seg.setYUnits(yunits);
            } catch (UnitsException ex) {
                Logger.getLogger(SedPreferences.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        // if in magnitudes, flip the direction of the Y-axis
        flipYifMag(yunit);
    }
    
    /**
     * Flips the Y-axis if yunit is a magnitude. A lower magnitude = brighter 
     * source  higher on Y-axis)
     * @param yunit 
     */
    private void flipYifMag(String yunit) {
        try {
            if (SPVYQuantity.MAGNITUDE.getPossibleUnits()
                    .contains(SPVYUnit.getFromUnitString(yunit))) {
                plotPreferences.setYflip(true);
            } else {
                plotPreferences.setYflip(false);
            }
        } catch (SedException ex) {
            Logger.getLogger(SedPreferences.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    // Removes any segments that are no longer in the SED
    private void clean() {
        
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
     * @return
     *  Top level plot preferences for the stil plotter.
     */
    public PlotPreferences getPlotPreferences() {
        return plotPreferences;
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
    
    /**
     * Strip an ID of its _ERROR suffix.
     * @param id the ID to strip "_ERROR" from
     */
    private String strip(String id) {
        
        int index = id.lastIndexOf("_");
        
        // if _ERROR not found, return the unmodified id.
        if (index < 0) {
            return id;
        }
        return id.substring(0, index);
    }
}
