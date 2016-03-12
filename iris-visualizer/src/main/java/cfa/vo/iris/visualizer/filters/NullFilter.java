package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public class NullFilter implements Filter {
    
    @Override
    public BitSet getFilteredRows(IrisStarTable table) {
        return new BitSet();
    }
    
}
