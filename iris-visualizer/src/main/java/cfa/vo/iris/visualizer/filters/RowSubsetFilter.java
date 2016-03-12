package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

/**
 * Filter based on row indices for a specific StarTable. Applying this
 * filter to other tables will not have any effect. Any row index in rows[] will
 * be filtered in the corresponding table.
 *
 */
public class RowSubsetFilter implements Filter {
    
    private BitSet mask;
    private IrisStarTable table;
    
    public RowSubsetFilter(int[] rows, IrisStarTable table) {
        this.table = table;
        this.mask = new BitSet((int) table.getRowCount());
        
        for (int i : rows) {
            mask.set(i);
        }
    }

    @Override
    public BitSet getFilteredRows(IrisStarTable table) {
        if (this.table == table) {
            return mask;
        }
        
        return new NullFilter().getFilteredRows(table);
    }

}
