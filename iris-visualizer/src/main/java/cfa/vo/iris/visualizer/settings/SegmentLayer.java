package cfa.vo.iris.visualizer.settings;

import java.util.HashMap;
import java.util.Map;

import cfa.vo.iris.sed.stil.SegmentStarTableWrapper;
import cfa.vo.iris.sed.stil.SegmentStarTableWrapper.ColumnName;

import static cfa.vo.iris.visualizer.settings.PlotPreferences.*;

public class SegmentLayer {

    private Map<String, Object> preferences;
    private String suffix;

    public SegmentLayer(SegmentStarTableWrapper table) {
        this.preferences = new HashMap<String, Object>();
        this.suffix = '_' + table.getId();
        
        this.setInSource(table)
            .setXCol(ColumnName.X_COL.name())
            .setYCol(ColumnName.Y_COL.name())
            .setXerrhi(ColumnName.X_ERR_HI.name())
            .setXerrlo(ColumnName.X_ERR_LO.name())
            .setYerrhi(ColumnName.Y_ERR_HI.name())
            .setYerrlo(ColumnName.Y_ERR_LO.name());
    }

    /**
     * StarTable input (this could be something else).
     */
    public SegmentLayer setInSource(Object arg1) {
        this.preferences.put(IN + suffix, arg1);
        return this;
    }

    public Map<String, Object> getPreferences() {
        return preferences;
    }

    public SegmentLayer setType(String arg1) {
        this.preferences.put(TYPE + suffix, arg1);
        return this;
    }

    public SegmentLayer setColor(String arg1) {
        this.preferences.put(COLOR + suffix, arg1);
        return this;
    }

    public SegmentLayer setShape(String arg1) {
        this.preferences.put(SHAPE + suffix, arg1);
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
