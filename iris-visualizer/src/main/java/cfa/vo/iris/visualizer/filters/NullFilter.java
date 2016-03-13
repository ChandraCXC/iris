package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public class NullFilter extends Filter {
    
    private boolean inverted = false;
    
    @Override
    public BitSet getFilteredRows(IrisStarTable table) {
        BitSet bitSet = new BitSet();
        if (inverted) {
            // Filter all rows in the plotter table.
            bitSet.set(0, (int) table.getPlotterTable().getRowCount());
        }
        return bitSet;
    }
    
    @Override 
    public void invert() {
        inverted = !inverted;
    }
}
