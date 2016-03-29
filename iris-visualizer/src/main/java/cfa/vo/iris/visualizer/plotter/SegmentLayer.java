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

package cfa.vo.iris.visualizer.plotter;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cfa.vo.iris.sed.stil.SegmentStarTable.Column;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;

public class SegmentLayer {
    
    private static final Logger logger = Logger.getLogger(SegmentLayer.class.getName());
    
    // Override-able Settings
    public static final String SHAPE = "shape";
    public static final String TYPE = "layer";
    public static final String IN = "in";
    public static final String X_COL = "x";
    public static final String Y_COL = "y";
    public static final String X_ERR_HI = "xerrhi";
    public static final String Y_ERR_HI = "yerrhi";
    public static final String X_ERR_LO = "xerrlo";
    public static final String Y_ERR_LO = "yerrlo";
    public static final String COLOR = "color";
    public static final String ERROR_BAR_TYPE = "errorbar";
    public static final String SIZE = "size";
    public static final String SHADING = "shading";
    
    private static final String ERROR_SUFFIX = "_ERROR";
    
    private String suffix;
    
    private boolean showErrorBars;
    private boolean showMarks;
    
    private IrisStarTable inSource;
    private ErrorBarType errorBarType;
    private ShapeType markType;
    private Integer size;
    private ShadingType markShading;
    private ShadingType errorShading;
    private String markColor;
    private String errorColor;
    private Double markColorWeight;
    private Double errorColorWeight;
    
    public SegmentLayer(IrisStarTable table) {
        
        if (table == null) {
            throw new InvalidParameterException("star table cannot be null");
        }
        
        this.setInSource(table);
        this.suffix = table.getName();
        
        // Setting default values here
        this.setErrorBarType(ErrorBarType.capped_lines)
            .setMarkType(ShapeType.open_circle)
            .setSize(4);
        
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
    
    public SegmentLayer setInSource(IrisStarTable table) {
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

    public SegmentLayer setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public boolean isShowErrorBars() {
        return showErrorBars;
    }

    public SegmentLayer setShowErrorBars(boolean showErrorBars) {
        this.showErrorBars = showErrorBars;
        return this;
    }

    public boolean isShowMarks() {
        return showMarks;
    }

    public SegmentLayer setShowMarks(boolean showMarks) {
        this.showMarks = showMarks;
        return this;
    }

    public ErrorBarType getErrorBarType() {
        return errorBarType;
    }

    public SegmentLayer setErrorBarType(ErrorBarType errorBarType) {
        this.errorBarType = errorBarType;
        return this;
    }

    public ShapeType getMarkType() {
        return markType;
    }

    public SegmentLayer setMarkType(ShapeType markType) {
        this.markType = markType;
        return this;
    }

    public int getSize() {
        return size;
    }

    public SegmentLayer setSize(int size) {
        this.size = size;
        return this;
    }

    public ShadingType getMarkShading() {
        return markShading;
    }

    public SegmentLayer setMarkShading(ShadingType markShading) {
        this.markShading = markShading;
        return this;
    }

    public ShadingType getErrorShading() {
        return errorShading;
    }

    public SegmentLayer setErrorShading(ShadingType errorShading) {
        this.errorShading = errorShading;
        return this;
    }

    public String getMarkColor() {
        return markColor;
    }

    public SegmentLayer setMarkColor(String markColor) {
        this.markColor = markColor;
        return this;
    }

    public String getErrorColor() {
        return errorColor;
    }

    public SegmentLayer setErrorColor(String errorColor) {
        this.errorColor = errorColor;
        return this;
    }

    public double getMarkColorWeight() {
        return markColorWeight;
    }

    public SegmentLayer setMarkColorWeight(double markColorWeight) {
        this.markColorWeight = markColorWeight;
        return this;
    }

    public double getErrorColorWeight() {
        return errorColorWeight;
    }

    public SegmentLayer setErrorColorWeight(double errorColorWeight) {
        this.errorColorWeight = errorColorWeight;
        return this;
    }

    /*
     * TODO: Add setter methods for columns that allow users to plot any double column from 
     * the data star table.
     */
}
