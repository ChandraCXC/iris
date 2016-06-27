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

import cfa.vo.iris.fitting.FitConfiguration;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cfa.vo.iris.fitting.FitConfiguration;
import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.ColorPalette;
import cfa.vo.iris.visualizer.plotter.HSVColorPalette;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.iris.visualizer.stil.tables.SegmentColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.StackedStarTable;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.table.StarTable;

/**
 * Maintains visualizer preferences for an SED currently in the 
 * workspace.
 *
 */
public class SedModel {
    
    IrisStarTableAdapter adapter;
    final Map<Segment, IrisStarTable> starTableData;
    final Map<Segment, LayerModel> tableLayerModels;
    final ExtSed sed;
    final ColorPalette colors;
    
    // Default unit settings
    private String xunits;
    private String yunits;
    
    private FunctionModel evalModel;
    
    public SedModel(ExtSed sed, IrisStarTableAdapter adapter) {
        this.sed = sed;
        this.adapter = adapter;
        this.colors = new HSVColorPalette();
        
        this.starTableData = Collections.synchronizedMap(new IdentityHashMap<Segment, IrisStarTable>());
        this.tableLayerModels = Collections.synchronizedMap(new IdentityHashMap<Segment, LayerModel>());
        refresh();
    }
    
    /**
     * @param seg
     * @return The IrisStarTable corresponding to the specified SED.
     */
    public IrisStarTable getStarTable(Segment seg) {
        return starTableData.get(seg);
    }
    
    /**
     * @param seg
     * @return The LayerModel for the specified segment.
     */
    public LayerModel getSegmentModel(Segment seg) {
        return tableLayerModels.get(seg);
    }
    
    /**
     * @return A a Layer that represents this SED as a single layer in the plot.
     */
    public LayerModel getSedLayerModel() {
        StarTable table = new StackedStarTable(getDataTables(), new SegmentColumnInfoMatcher());
        table.setName(sed.getId());
        
        LayerModel ret = new LayerModel(table);
        ret.setLabel(sed.getId());
        
        return ret;
    }
    
    /**
     * @return A list of IrisStarTables for each Segment in this SED. List is in the same
     * order as they appear in the SED.
     */
    public List<IrisStarTable> getDataTables() {
        List<IrisStarTable> ret = new LinkedList<>();
        for (Segment seg : sed.getSegments()) {
            // If this isn't available then it hasn't yet been serialized in the DataStore
            if (starTableData.containsKey(seg)) {
                ret.add(starTableData.get(seg));
            }
        }
        return ret;
    }
    
    /**
     * @return A list of all LayerModels for each Segment in this SED. 
     * List of Segment layers is in the same order as they appear in the SED.
     */
    public List<LayerModel> getLayerModels() {
        List<LayerModel> ret = new LinkedList<>();
        for (Segment seg : sed.getSegments()) {
            // If this isn't available then it hasn't yet been serialized in the DataStore
            if (starTableData.containsKey(seg)) {
                ret.add(tableLayerModels.get(seg));
            }
        }
        
        return ret;
    }
    
    /**
     * Reserializes all segments within the sed.
     */
    void refresh() {
        for (Segment seg : sed.getSegments()) {
            addSegment(seg);
        }
    }
    
    void removeAll() {
        starTableData.clear();
        tableLayerModels.clear();
    }
    
    /**
     * Add a segment to the sed model map.
     * @param seg
     * @return true if the sed was added to the model.
     */
    boolean addSegment(Segment seg) {
        
        // Do not keep track of empty segments
        if (seg == null) return false;
        
        // If the segment is already in the map remake the star table
        if (starTableData.containsKey(seg)) {
            IrisStarTable table = starTableData.get(seg);
            LayerModel mod = tableLayerModels.get(seg);
            
            // Preserve table name on reserialization
            IrisStarTable newTable = convertSegment(seg, table.getName());
            
            starTableData.put(seg, newTable);
            mod.setInSource(newTable);
            return false;
        }
        
        IrisStarTable newTable = convertSegment(seg, null);

        // Ensure that the layer has a unique identifier in the list of segments
        LayerModel layer = new LayerModel(newTable);
        int count = 0;
        String id = layer.getSuffix();
        while (!isUniqueLayerSuffix(id)) {
            count++;
            id = layer.getSuffix() + " " + count;
        }
        layer.setSuffix(id);
        newTable.setName(id);
        
        // add colors to segment layer
        String hexColor = ColorPalette.colorToHex(colors.getNextColor());
        layer.setErrorColor(hexColor);
        layer.setMarkColor(hexColor);
        
        // update legend settings
        layer.setLabel(id);
        
        // set the units
        setUnits(seg, newTable);
        
        starTableData.put(seg, newTable);
        tableLayerModels.put(seg, layer);
        return true;
    }
    
    boolean isUniqueLayerSuffix(String suffix) {
        for (LayerModel layer : tableLayerModels.values()) {
            if (StringUtils.equals(layer.getSuffix(), suffix)) {
                return false;
            }
        }
        return true;
    }
    
    private IrisStarTable convertSegment(Segment seg, String name) {
        // Convert segments with more than 3000 points asynchronously.
        if (seg.getLength() > 3000) {
            return adapter.convertSegmentAsync(seg, name);
        }
        IrisStarTable table = adapter.convertSegment(seg, name);
        
        return table;
    }
    
    /**
     * Removes a segment from the sed preferences map.
     * @param segment
     * @return true if the segment was removed from the models map
     */
    boolean removeSegment(Segment seg) {
        // Do not keep track of empty segments
        if (seg == null) return false;
        
        starTableData.remove(seg);
        return tableLayerModels.remove(seg) != null;
    }
    
    /**
     * Attaches an evaluated model to the Sed.
     * @param model
     */
    public void setFunctionModel(FunctionModel model) {
        this.evalModel = model;
    }
    
    /**
     * Returns the evaluated model belonging to this ExtSed.
     */
    public FunctionModel getFunctionModel() {
        return evalModel;
    }
    
    /**
     * Sets x and y units of the given segment to the preferred units.
     * If preferred units have not been set, the given segment's units
     * are used and set as the SED's preferred units.
     * 
     */
    private void setUnits(Segment seg, IrisStarTable table) {
        // if this is the first segment to be added to the SED preferences,
        // set the preferred X and Y units to the segment's
        if (StringUtils.isEmpty(xunits) || StringUtils.isEmpty(xunits)) {
            try {
                xunits = seg.getSpectralAxisUnits();
                yunits = seg.getFluxAxisUnits();
            } catch (SedNoDataException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        // set the layer units for this segment
        try {
            table.setXUnits(xunits);
            table.setYUnits(yunits);
        } catch (UnitsException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Sets the x and y units of the SED
     * 
     * @param xunit    the X unit
     * @param yunit    the Y unit
     */
    public void setUnits(String xunit, String yunit) {
        
        this.xunits = xunit;
        this.yunits = yunit;
        
        // update the segment layers with the new units
        for (IrisStarTable table : starTableData.values()) {
            try {
                table.setXUnits(xunits);
                table.setYUnits(yunits);
            } catch (UnitsException ex) {
                Logger.getLogger(SedModel.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String getXUnits() {
        return xunits;
    }

    public String getYUnits() {
        return yunits;
    }

    public FitConfiguration getFitConfiguration() {
        return sed.getFit();
    }
}
