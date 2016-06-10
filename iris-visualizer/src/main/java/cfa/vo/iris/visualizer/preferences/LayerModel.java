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

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.plotter.ErrorBarType;
import cfa.vo.iris.visualizer.plotter.LayerType;
import cfa.vo.iris.visualizer.plotter.ShadingType;
import cfa.vo.iris.visualizer.plotter.ShapeType;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;

import static cfa.vo.iris.visualizer.plotter.PlotPreferences.*;

public class LayerModel {
    
    // see http://www.star.bris.ac.uk/~mbt/stilts/sun256/sun256.html#plot2plane
    // for a list of all the configurable plot properties
    
    private static final Logger logger = Logger.getLogger(LayerModel.class.getName());
    
    // Override-able Settings
    public static final String IN = "in";
    public static final String X_COL = "x";
    public static final String Y_COL = "y";
    public static final String X_ERR_HI = "xerrhi";
    public static final String Y_ERR_HI = "yerrhi";
    public static final String X_ERR_LO = "xerrlo";
    public static final String Y_ERR_LO = "yerrlo";
    public static final String COLOR = "color";
    
    private static final String ERROR_SUFFIX = "_ERROR";
    
    // for the plot legend
    public static final String LEGEND_LABEL = "leglabel";
    public static final String LEGEND_SEQUENCE = "legseq";
    
    private String suffix;
    
    private boolean showErrorBars;
    private boolean showMarks;
    
    private IrisStarTable inSource;
    
    // Inherited from the SED layer, but can be overridden
    private ErrorBarType errorBarType;
    private ShapeType markType;
    private Integer size;
    private ShadingType markShading;
    private ShadingType errorShading;
    private String markColor;
    private String errorColor;
    private Double markColorWeight;
    private Double errorColorWeight;
    
    private String leglabel;
    private String[] legseq;
    
    public LayerModel(IrisStarTable table) {
        
        if (table == null) {
            throw new InvalidParameterException("star table cannot be null");
        }
        
        this.setInSource(table);
        this.suffix = table.getName();
        
        this.showErrorBars = true;
        this.showMarks = true;
    }

    /**
     * 
     * @return a map of all preferences currently set for this layer.
     */
    public Map<String, Object> getPreferences() {
        Map<String, Object> preferences = new HashMap<String, Object>();
        
        if (showMarks) {
            addMarkFields(suffix, preferences);
        }
        
        // This needs to be represented as a separate layer, so we copy all preferences
        // for the base layer into the error bar layer.
        if (showErrorBars) {
            addErrorFields(suffix + ERROR_SUFFIX, preferences);
        }
        
        return preferences;
    }
    
    private void addErrorFields(String suffix, Map<String, Object> prefs) {

        // Clear and add table values as necessary.
        
        // If error columns are available in the underlying star table, the we
        // add them here.
        ColumnIdentifier id = new ColumnIdentifier(inSource);
        if (shouldAddErrorColumn(Column.Spectral_Error, id)) {
            prefs.put(X_ERR_HI + suffix, Column.Spectral_Error.name());
        }
        if (shouldAddErrorColumn(Column.Flux_Error, id)) {
            prefs.put(Y_ERR_HI + suffix, Column.Flux_Error.name());
        }
        
        // If lower and upper ranges are specified then we override existing
        // single bounds.
        if (shouldAddErrorColumn(Column.Spectral_Error_Low, id)) {
            prefs.put(X_ERR_HI + suffix, Column.Spectral_Error_High.name());
            prefs.put(X_ERR_LO + suffix, Column.Spectral_Error_Low.name());
        }
        if (shouldAddErrorColumn(Column.FLux_Error_Low, id)) {
            prefs.put(Y_ERR_HI + suffix, Column.Flux_Error_High.name());
            prefs.put(Y_ERR_LO + suffix, Column.FLux_Error_Low.name());
        }
        
        prefs.put(TYPE + suffix, LayerType.xyerror.name());
        if (errorShading != null)
            prefs.put(SHADING + suffix, markColor);
        if (errorBarType != null)
            prefs.put(ERROR_BAR_TYPE + suffix, errorBarType.name());
        if (errorColor != null)
            prefs.put(COLOR + suffix, errorColor);
        if (errorColorWeight != null)
            prefs.put(errorShading.name + suffix, errorColorWeight);
        
        addCommonFields(suffix, prefs);
    }
    
    private void addMarkFields(String suffix, Map<String, Object> prefs) {
        prefs.put(TYPE + suffix, LayerType.mark.name());
        if (markType != null)
            prefs.put(SHAPE + suffix, markType.name());
        if (markShading != null)
            prefs.put(SHADING + suffix, markShading.name());
        if (markColor != null)
            prefs.put(COLOR + suffix, markColor);
        if (markColorWeight != null)
            prefs.put(markShading.name + suffix, markColorWeight);
        
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
        return inSource.getXUnits();
    }
    
    public void setXUnits(String xunits) throws UnitsException {
        inSource.setXUnits(xunits);
    }
    
    public String getYUnits() {
        return inSource.getYUnits();
    }
    
    public void setYUnits(String yunits) throws UnitsException {
        inSource.setYUnits(yunits);
    }

    public IrisStarTable getInSource() {
        return inSource;
    }
    
    public LayerModel setInSource(IrisStarTable table) {
        if (table == null) {
            throw new InvalidParameterException("StarTable cannot be null!");
        }
        
        this.inSource = table;
        return this;
    }
    
    private boolean shouldAddErrorColumn(Column column, ColumnIdentifier id) {
        try {
            if (id.getColumnIndex(column.name()) >= 0) {
                return true;
            }
        } catch (IOException e) {
            // Ignore
            logger.fine("could not add column " + column.name() + " : " + e.getMessage());
        }
        return false;
    }
    
    public String getSuffix() {
        return suffix;
    }

    public LayerModel setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public boolean isShowErrorBars() {
        return showErrorBars;
    }

    public LayerModel setShowErrorBars(boolean showErrorBars) {
        this.showErrorBars = showErrorBars;
        return this;
    }

    public boolean isShowMarks() {
        return showMarks;
    }

    public LayerModel setShowMarks(boolean showMarks) {
        this.showMarks = showMarks;
        return this;
    }

    public ErrorBarType getErrorBarType() {
        return errorBarType;
    }

    public LayerModel setErrorBarType(ErrorBarType errorBarType) {
        this.errorBarType = errorBarType;
        return this;
    }

    public ShapeType getMarkType() {
        return markType;
    }

    public LayerModel setMarkType(ShapeType markType) {
        this.markType = markType;
        return this;
    }

    public int getSize() {
        return size;
    }

    public LayerModel setSize(int size) {
        this.size = size;
        return this;
    }

    public ShadingType getMarkShading() {
        return markShading;
    }

    public LayerModel setMarkShading(ShadingType markShading) {
        this.markShading = markShading;
        return this;
    }

    public ShadingType getErrorShading() {
        return errorShading;
    }

    public LayerModel setErrorShading(ShadingType errorShading) {
        this.errorShading = errorShading;
        return this;
    }

    public String getMarkColor() {
        return markColor;
    }

    public LayerModel setMarkColor(String markColor) {
        this.markColor = markColor;
        return this;
    }

    public String getErrorColor() {
        return errorColor;
    }

    public LayerModel setErrorColor(String errorColor) {
        this.errorColor = errorColor;
        return this;
    }

    public double getMarkColorWeight() {
        return markColorWeight;
    }

    public LayerModel setMarkColorWeight(double markColorWeight) {
        this.markColorWeight = markColorWeight;
        return this;
    }

    public double getErrorColorWeight() {
        return errorColorWeight;
    }

    public LayerModel setErrorColorWeight(double errorColorWeight) {
        this.errorColorWeight = errorColorWeight;
        return this;
    }
    
    public LayerModel setLayerSequence(String[] layerSequence) {
        this.legseq = layerSequence;
        return this;
    }
    
    public String[] getLayerSequence() {
        return this.legseq;
    }
    
    // the suffix is the layer suffix name from the SedPreferences
    public LayerModel setLabel(String label) {
        this.leglabel = label;
        return this;
    }
    
    public String getLabel() {
        return this.leglabel;
    }

    /*
     * TODO: Add setter methods for columns that allow users to plot any double column from 
     * the data star table.
     */
}
