package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public abstract class Filter {
    
    public final String id;
    
    public Filter() {
        this.id = UUID.randomUUID().toString();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Filter)) {
            return false;
        }
        Filter o = (Filter) other;
        return StringUtils.equals(id, o.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    public abstract BitSet getFilteredRows(IrisStarTable table);
    
    public abstract void invert();
}
