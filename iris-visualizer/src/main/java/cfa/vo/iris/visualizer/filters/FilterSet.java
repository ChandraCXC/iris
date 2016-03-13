package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;
import java.util.LinkedHashSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public class FilterSet extends LinkedHashSet<Filter> {
    
    private static final long serialVersionUID = 1L;
    
    private IrisStarTable table;
    private BitSet masked;
    
    public FilterSet(IrisStarTable table) {
        super();
        this.table = table;
        this.masked = new BitSet();
    }
    
    @Override
    public boolean add(Filter filter) {
        boolean changed = super.add(filter);
        if (changed) {
            masked.or(filter.getFilteredRows(table));
        }
        return changed;
    }
    
    public boolean remove(Filter filter) {
        boolean changed = super.remove(filter);
        if (changed) {
            this.masked = updateMasked();
        }
        return changed;
    }
    
    public BitSet getMasked() {
        return masked;
    }

    public long cardinality() {
        return masked.cardinality();
    }
    
    public void invert() {
        for (Filter f : this) {
            f.invert();
        }
        
        this.masked = updateMasked();
    }
    
    private BitSet updateMasked() {
        BitSet mask = new BitSet();
        for (Filter f : this) {
            mask.or(f.getFilteredRows(table));
        }
        return mask;
    }
}
