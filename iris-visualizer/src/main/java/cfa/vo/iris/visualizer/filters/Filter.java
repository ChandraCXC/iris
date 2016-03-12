package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public interface Filter {
    
    public BitSet getFilteredRows(IrisStarTable table);
    
}
