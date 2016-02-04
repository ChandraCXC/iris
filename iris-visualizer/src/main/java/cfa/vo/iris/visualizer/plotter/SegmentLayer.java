package cfa.vo.iris.visualizer.plotter;

import static cfa.vo.iris.visualizer.plotter.PlotPreferences.*;

import java.util.HashMap;
import java.util.Map;

import cfa.vo.iris.sed.stil.SegmentStarTable.ColumnName;
import uk.ac.starlink.table.StarTable;

public class SegmentLayer {
    
    private static String ERROR_SUFFIX = "_ERROR";
    
    private Map<String, Object> preferences;
    private String suffix;
    
    private boolean showErrorBars;
    private boolean showMarks;
    
    
    public SegmentLayer(StarTable table) {
        this.preferences = new HashMap<String, Object>();
        this.suffix = '_' + table.getName();
        
        this.setInSource(table)
        
            // TODO: put options into enums
            .setXCol(ColumnName.X_COL.name())
            .setYCol(ColumnName.Y_COL.name())
            .setXerrhi(ColumnName.X_ERR_HI.name())
            .setXerrlo(ColumnName.X_ERR_LO.name())
            .setYerrhi(ColumnName.Y_ERR_HI.name())
            .setYerrlo(ColumnName.Y_ERR_LO.name())
            
            // Setting default values here
            .setErrBarShape(ErrorBarType.capped_lines)
            .setMarkShape(MarkType.open_circle)
            .setMarkSize(4);
        
        this.showErrorBars = true;
        this.showMarks = true;
    }

    public Map<String, Object> getPreferences() {
        Map<String, Object> newPreferences = new HashMap<String, Object>(preferences);
        
        // This needs to be represented as a separate layer, so we copy all preferences
        // for the base layer into the error bar layer.
        if (showErrorBars) {
            newPreferences.put(LAYER + suffix + ERROR_SUFFIX, "xyerror");
            
            for (String key : preferences.keySet()) {
                newPreferences.put(key + ERROR_SUFFIX, preferences.get(key));
            }
        }
        
        if (showMarks) {
            newPreferences.put(LAYER + suffix, "mark");
        }
        
        return newPreferences;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    public SegmentLayer setShowMarks(boolean showMarks) {
        this.showMarks = showMarks;
        return this;
    }
    
    public SegmentLayer setShowErrorBars(boolean showErrorBars) {
        this.showErrorBars = showErrorBars;
        return this;
    }

    public SegmentLayer setMarkShape(MarkType arg1) {
        this.preferences.put(SHAPE + suffix, arg1.name());
        return this;
    }
    
    public SegmentLayer setErrBarShape(ErrorBarType arg1) {
        this.preferences.put(ERROR_BAR + suffix, arg1.name());
        return this;
    }
    
    public SegmentLayer setMarkSize(int arg1) {
        this.preferences.put(SIZE + suffix, arg1);
        return this;
    }

    /**
     * StarTable input
     */
    public SegmentLayer setInSource(StarTable arg1) {
        this.preferences.put(IN + suffix, arg1);
        return this;
    }
    
    public StarTable getInSource() {
        return (StarTable) this.preferences.get(IN + suffix);
    }

    public SegmentLayer setColor(String arg1) {
        this.preferences.put(COLOR + suffix, arg1);
        return this;
    }

    public SegmentLayer setXCol(String arg1) {
        this.preferences.put(X_COL + suffix, arg1);
        return this;
    }

    public SegmentLayer setYCol(String arg1) {
        this.preferences.put(Y_COL + suffix, arg1);
        return this;
    }

    public SegmentLayer setXerrhi(String arg1) {
        this.preferences.put(X_ERR_HI + suffix, arg1);
        return this;
    }

    public SegmentLayer setXerrlo(String arg1) {
        this.preferences.put(X_ERR_LO + suffix, arg1);
        return this;
    }

    public SegmentLayer setYerrhi(String arg1) {
        this.preferences.put(Y_ERR_HI + suffix, arg1);
        return this;
    }

    public SegmentLayer setYerrlo(String arg1) {
        this.preferences.put(Y_ERR_LO + suffix, arg1);
        return this;
    }
}
