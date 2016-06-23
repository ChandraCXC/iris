/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.preferences;

import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.iris.units.spv.YUnits;
import cfa.vo.iris.visualizer.plotter.LayerType;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * Evaluated model preferences. This describes the STIL preferences for plotting 
 * an evaluated model.
 */
public class FunctionModel {

    // see http://www.star.bris.ac.uk/~mbt/stilts/sun256/sun256.html#plot2plane
    // for a list of all the configurable plot properties
    
    private static final Logger logger = Logger.getLogger(FunctionModel.class.getName());
    
    // Override-able Settings
    public static final String TYPE = "layer";
    public static final String IN = "in";
    public static final String X_COL = "x";
    public static final String Y_COL = "y";
    public static final String COLOR = "color";
    public static final String THICK = "thick";
    public static final String DASH = "dash";
    public static final String SIZE = "size";
    
    // for the plot legend
    public static final String LEGEND_LABEL = "leglabel";
    public static final String LEGEND_SEQUENCE = "legseq";
    
    private String suffix;
    
    private SegmentStarTable inSource;
    private Integer size;
    private String color;
    private Double dash;
    private Integer thickness;
    
    private String leglabel;
    private String[] legseq;
    
    private boolean show; // show the evaluated model
    
    public FunctionModel(SegmentStarTable table) {
        if (table == null) {
            throw new InvalidParameterException("star table cannot be null");
        }
        
        this.setInSource(table);
        this.suffix = table.getName();
        this.color = "red";
        this.thickness = 1;
        
        // Setting default values here
        this.show = true;
    }
    
    /**
     * Show or hide the evaluated model
     * @param bool
     */
    public void setShowModel(boolean bool) {
        this.show = bool;
    }
    
    /**
     * Returns whether the model should be shown or not
     */
    public boolean isShowModel() {
        return this.show;
    }

    /**
     * 
     * @return a map of all preferences currently set for this layer.
     */
    public Map<String, Object> getPreferences() {
        Map<String, Object> preferences = new HashMap<String, Object>();
        
        if (show) {
            addMarkFields(suffix, preferences);
        }
        
        // TODO: In the future, we may want to have shaded regions for model
        // upper/lower limits.
        
        return preferences;
    }
    
    private void addMarkFields(String suffix, Map<String, Object> prefs) {
        prefs.put(TYPE + suffix, LayerType.line.name());
        if (dash != null)
            prefs.put(DASH + suffix, dash);
        if (color != null)
            prefs.put(COLOR + suffix, color);
        if (thickness != null)
            prefs.put(THICK + suffix, thickness);
        
        addCommonFields(suffix, prefs);

    }
    
    private void addCommonFields(String suffix, Map<String, Object> prefs) {
        prefs.put(IN + suffix, inSource);
        prefs.put(X_COL + suffix, Column.Spectral_Value.name());
        prefs.put(Y_COL + suffix, Column.Flux_Value.name());
        
        // for the legend. set the flux and error layer legened names
        // to the same name
        prefs.put(LEGEND_LABEL + suffix, getLabel());
        
        if (size != null)
            prefs.put(SIZE + suffix, size);
    }
    
    public String getXUnits() {
        return inSource.getSpecUnits().toString();
    }
    
    public void setXUnits(String xunits) throws UnitsException {
        inSource.setSpecUnits(new XUnits(XUnits.getCorrectSpelling(xunits)));
    }
    
    public String getYUnits() {
        return inSource.getFluxUnits().toString();
    }
    
    public void setYUnits(String yunits) throws UnitsException {
        inSource.setFluxUnits(new YUnits(YUnits.getCorrectSpelling(yunits)));
    }

    public SegmentStarTable getInSource() {
        return inSource;
    }
    
    public FunctionModel setInSource(SegmentStarTable table) {
        if (table == null) {
            throw new InvalidParameterException("StarTable cannot be null!");
        }
        
        this.inSource = table;
        return this;
    }
    
    public String getSuffix() {
        return suffix;
    }

    public FunctionModel setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public int getSize() {
        return size;
    }

    public FunctionModel setSize(int size) {
        this.size = size;
        return this;
    }

    public String getColor() {
        return color;
    }

    public FunctionModel setColor(String color) {
        this.color = color;
        return this;
    }
    
    public double getDash() {
        return dash;
    }

    public FunctionModel setDash(double dash) {
        this.dash = dash;
        return this;
    }
    
    public int getThickness() {
        return thickness;
    }

    public FunctionModel setThickness(int thickness) {
        this.thickness = thickness;
        return this;
    }
    
    public FunctionModel setLayerSequence(String[] layerSequence) {
        this.legseq = layerSequence;
        return this;
    }
    
    public String[] getLayerSequence() {
        return this.legseq;
    }
    
    // the suffix is the layer suffix name from the SedPreferences
    public FunctionModel setLabel(String label) {
        this.leglabel = label;
        return this;
    }
    
    public String getLabel() {
        return this.leglabel;
    }    
}
