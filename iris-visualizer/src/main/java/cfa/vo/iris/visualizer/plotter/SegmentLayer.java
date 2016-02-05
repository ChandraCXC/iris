package cfa.vo.iris.visualizer.plotter;

import static cfa.vo.iris.visualizer.plotter.PlotPreferences.*;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.sed.stil.SegmentStarTable.ColumnName;
import uk.ac.starlink.table.StarTable;

public class SegmentLayer {
    
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
        
        this.suffix = '_' + table.getName();
        
        this.setInSource(table)
        
            // TODO: put options into enums
            .setxCol(ColumnName.X_COL.name())
            .setyCol(ColumnName.Y_COL.name())
            .setxErrHi(ColumnName.X_ERR_HI.name())
            .setxErrLo(ColumnName.X_ERR_LO.name())
            .setyErrHi(ColumnName.Y_ERR_HI.name())
            .setyErrLo(ColumnName.Y_ERR_LO.name())
            
            // Setting default values here
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
        
        if (errorShading != null)
            prefs.put(SHADING + suffix, markColor);
        if (errorBarType != null)
            prefs.put(ERROR_BAR_TYPE + suffix, errorBarType);
        if (errorColor != null)
            prefs.put(COLOR + suffix, errorColor);
        if (errorColorWeight != null)
            prefs.put(errorShading.name + suffix, errorColorWeight);
        
        addCommonFields(suffix, prefs);
    }
    
    private void addMarkFields(String suffix, Map<String, Object> prefs) {
        if (markType != null)
            prefs.put(SHAPE + suffix, markType);
        if (markShading != null)
            prefs.put(SHADING + suffix, markShading);
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
        
        if (StringUtils.isNotBlank(xErrHi))
            prefs.put(X_ERR_HI + suffix, xErrHi);
        if (StringUtils.isNotBlank(xErrLo))
            prefs.put(X_ERR_LO + suffix, xErrLo);
        if (StringUtils.isNotBlank(yErrHi))
            prefs.put(Y_ERR_HI + suffix, yErrHi);
        if (StringUtils.isNotBlank(yErrLo))
            prefs.put(Y_ERR_LO + suffix, yErrLo);
        if (size != null)
            prefs.put(SIZE + suffix, size);
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

    public StarTable getInSource() {
        return inSource;
    }

    public SegmentLayer setInSource(StarTable inSource) {
        if (inSource == null) {
            throw new InvalidParameterException("StarTable cannot be null!");
        }
        
        this.inSource = inSource;
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

    public String getxCol() {
        return xCol;
    }

    public SegmentLayer setxCol(String xCol) {
        this.xCol = xCol;
        return this;
    }

    public String getyCol() {
        return yCol;
    }

    public SegmentLayer setyCol(String yCol) {
        this.yCol = yCol;
        return this;
    }

    public String getxErrHi() {
        return xErrHi;
    }

    public SegmentLayer setxErrHi(String xErrHi) {
        this.xErrHi = xErrHi;
        return this;
    }

    public String getxErrLo() {
        return xErrLo;
    }

    public SegmentLayer setxErrLo(String xErrLo) {
        this.xErrLo = xErrLo;
        return this;
    }

    public String getyErrHi() {
        return yErrHi;
    }

    public SegmentLayer setyErrHi(String yErrHi) {
        this.yErrHi = yErrHi;
        return this;
    }

    public String getyErrLo() {
        return yErrLo;
    }

    public SegmentLayer setyErrLo(String yErrLo) {
        this.yErrLo = yErrLo;
        return this;
    }
}
