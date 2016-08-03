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

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cfa.vo.iris.fitting.FitConfiguration;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.IrisDataStarTable;
import cfa.vo.iris.visualizer.plotter.ColorPalette;
import cfa.vo.iris.visualizer.plotter.HSVColorPalette;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.iris.visualizer.stil.tables.SegmentColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.StackedStarTable;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.Data;

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
    
    // Evaluated model version number
    private int modelVersion = 13;
    private boolean hasModelFunction = false;

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
     * @return FunctionModel for the fit on this SED, if available.
     */
    public FunctionModel getFunctionModel() {
        // TODO: Only return a model if this SedModel has been fitted.
        StarTable table = new StackedStarTable(getIrisDataTables(true), new SegmentColumnInfoMatcher());
        table.setName(sed.getId());
        
        return new FunctionModel(table);
    }
    
    /**
     * @return A list of IrisStarTables for each Segment in this SED. List is in the same
     * order as they appear in the SED.
     */
    public List<IrisStarTable> getDataTables() {
        List<IrisStarTable> ret = new ArrayList<>();
        for (Segment seg : sed.getSegments()) {
            // If this isn't available then it hasn't yet been serialized in the DataStore
            if (starTableData.containsKey(seg)) {
                ret.add(starTableData.get(seg));
            }
        }
        return ret;
    }
    
    /**
     * @return A list of IrisDataStarTables for each Segment in this SED. List is in the same
     * order as they appear in the SED. Rows are masked if specified.
     * 
     * NOTE: The values returned are in the current units of the SED model.
     * 
     * @param includeMasked if true the returned table will include all points, if false
     * it will not include points that have been specified as 'masked'.
     */
    public List<IrisDataStarTable> getIrisDataTables(boolean includeMasked) {
        List<IrisStarTable> tables = getDataTables();
        List<IrisDataStarTable> dataTables = new ArrayList<>(tables.size());;
        
        if (includeMasked) {
            for (IrisStarTable t : tables) dataTables.add(t.getPlotterDataTable());
        } else {
            for (IrisStarTable t : tables) dataTables.add(t);
        }
        
        return dataTables;
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
     * Updated the passed {@link Data} object with the fitting data from this SED. As of 
     * now that include flux, spectral, and stat error values.
     * 
     * @param includeMasked if true the returned table will include all points, if false
     * it will not include points that have been specified as 'masked'.
     * 
     */
    // TODO: Make this better! e.g., should have some mechanism to just have a stacked
    // IrisStarTable that would allow us to more easily and efficiently extract data. Still, 
    // performance wise much better than sedlib.
    public void getFittingData(final Data data, boolean includeMasked) {
        
        List<IrisDataStarTable> dataTables = this.getIrisDataTables(includeMasked);
        
        // Extract and construct column data
        double[] specData = new double[] {};
        double[] fluxData = new double[] {};
        double[] fluxErrData = new double[] {};
        
        for (IrisDataStarTable table : dataTables) {
            specData = ArrayUtils.addAll(specData, table.getOriginalSpecValues());
            fluxData = ArrayUtils.addAll(fluxData, table.getOriginalFluxValues());
            fluxErrData = ArrayUtils.addAll(fluxErrData, table.getOriginalFluxErrValues());
        }
        
        data.setX(specData);
        data.setY(fluxData);
        data.setStaterror(fluxErrData);
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
     * @param seg
     * @return true if the segment was removed from the models map
     */
    boolean removeSegment(Segment seg) {
        // Do not keep track of empty segments
        if (seg == null) return false;
        
        starTableData.remove(seg);
        return tableLayerModels.remove(seg) != null;
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

    public FitConfiguration getFit() {
        return sed.getFit();
    }

    public void setFit(FitConfiguration fit) {
        sed.setFit(fit);
    }
    
    public int getVersion() {
        HashCodeBuilder hcb = new HashCodeBuilder(13,31);
        for (IrisStarTable table : getDataTables()) {
            hcb.append(table.getPlotterDataTable().hashCode());
            hcb.append(table.getMasked());
        }
        return hcb.hashCode();
    }
    
    public int getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(int modelVersion) {
        this.modelVersion = modelVersion;
    }

    public boolean getHasModelFunction() {
        return hasModelFunction;
    }

    public void setHasModelFunction(boolean hasModelFunction) {
        this.hasModelFunction = hasModelFunction;
    }
    
    public ExtSed getSed() {
        return sed;
    }
}
