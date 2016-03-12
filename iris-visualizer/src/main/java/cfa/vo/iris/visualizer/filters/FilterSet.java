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
    
    private BitSet updateMasked() {
        BitSet mask = new BitSet((int) table.getRowCount());
        mask.set(0, mask.size());
        for (Filter f : this) {
            mask.and(f.getFilteredRows(table));
        }
        return mask;
    }
}
