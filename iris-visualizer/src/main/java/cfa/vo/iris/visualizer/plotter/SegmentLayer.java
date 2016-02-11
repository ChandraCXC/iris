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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import cfa.vo.iris.sed.stil.SegmentStarTable.Column;
import uk.ac.starlink.table.StarTable;
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
    
    private StarTable inSource;
    private ErrorBarType errorBarType;
    private ShapeType markType;
    private Integer size;
    private ShadingType markShading;
    private ShadingType errorShading;
    private String markColor;
    private String errorColor;
    private Double markColorWeight;
    private Double errorColorWeight;
    
    private String xCol;
    private String yCol;
    private String xErrHi;
    private String xErrLo;
    private String yErrHi;
    private String yErrLo;
    
    public SegmentLayer(StarTable table) {
        
        if (table == null) {
            throw new InvalidParameterException("star table cannot be null");
        }
        
        this.setInSource(table);
        this.suffix = '_' + table.getName();
        
        // Setting default values here
        this.setxCol(Column.SPECTRAL_COL.name())
            .setyCol(Column.FLUX_COL.name())
            .setErrorBarType(ErrorBarType.capped_lines)
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

        if (StringUtils.isNotBlank(xErrHi))
            prefs.put(X_ERR_HI + suffix, xErrHi);
        if (StringUtils.isNotBlank(xErrLo))
            prefs.put(X_ERR_LO + suffix, xErrLo);
        if (StringUtils.isNotBlank(yErrHi))
            prefs.put(Y_ERR_HI + suffix, yErrHi);
        if (StringUtils.isNotBlank(yErrLo))
            prefs.put(Y_ERR_LO + suffix, yErrLo);
        
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
        
        // Required
        if (StringUtils.isBlank(xCol) || StringUtils.isBlank(yCol)) {
            throw new RuntimeException("Must define column names for plotting");
        }

        prefs.put(IN + suffix, inSource);
        prefs.put(X_COL + suffix, xCol);
        prefs.put(Y_COL + suffix, yCol);
        
        if (size != null)
            prefs.put(SIZE + suffix, size);
    }

    public StarTable getInSource() {
        return inSource;
    }
    
    public SegmentLayer setInSource(StarTable table) {
        if (table == null) {
            throw new InvalidParameterException("StarTable cannot be null!");
        }
        
        this.inSource = table;
        
        // Clear and add table values as necessary.
        
        ColumnIdentifier id = new ColumnIdentifier(table);
        // Add spectral error values if they're available.
        setxErrHi(null);
        if (shouldAddErrorColumn(Column.SPECTRAL_ERR_HI, id)) {
            setxErrHi(Column.SPECTRAL_ERR_HI.name());
        }
        setxErrLo(null);
        if (shouldAddErrorColumn(Column.SPECTRAL_ERR_LO, id)) {
            setxErrLo(Column.SPECTRAL_ERR_LO.name());
        }
        
        // Add flux error values if they're available.
        setyErrHi(null);
        if (shouldAddErrorColumn(Column.FLUX_ERR_HI, id)) {
            setyErrHi(Column.FLUX_ERR_HI.name());
        }
        setyErrLo(null);
        if (shouldAddErrorColumn(Column.FLUX_ERR_LO, id)) {
            setyErrLo(Column.FLUX_ERR_LO.name());
        }
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
     * TODO: These setter methods should be made public when we support plotting arbitrary columns.
     */
    public String getxCol() {
        return xCol;
    }

    private SegmentLayer setxCol(String xCol) {
        this.xCol = xCol;
        return this;
    }

    public String getyCol() {
        return yCol;
    }

    private SegmentLayer setyCol(String yCol) {
        this.yCol = yCol;
        return this;
    }

    public String getxErrHi() {
        return xErrHi;
    }

    private SegmentLayer setxErrHi(String xErrHi) {
        this.xErrHi = xErrHi;
        return this;
    }

    public String getxErrLo() {
        return xErrLo;
    }

    private SegmentLayer setxErrLo(String xErrLo) {
        this.xErrLo = xErrLo;
        return this;
    }

    public String getyErrHi() {
        return yErrHi;
    }

    private SegmentLayer setyErrHi(String yErrHi) {
        this.yErrHi = yErrHi;
        return this;
    }

    public String getyErrLo() {
        return yErrLo;
    }

    private SegmentLayer setyErrLo(String yErrLo) {
        this.yErrLo = yErrLo;
        return this;
    }
}
