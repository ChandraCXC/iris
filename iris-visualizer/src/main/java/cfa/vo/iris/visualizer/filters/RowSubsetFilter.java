package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

/**
 * Filter based on row indices for a specific StarTable. Applying this
 * filter to other tables will not have any effect. Any row index in rows[] will
 * be filtered in the corresponding table.
 *
 */
public class RowSubsetFilter extends Filter {
    
    private int size;
    private BitSet mask;
    private IrisStarTable table;
    
    public RowSubsetFilter(int[] rows, IrisStarTable table) {
        this(rows, 0, table);
    }
    
    /**
     * Create a filter which filters out the given rows in the star table, presuming
     * that the first row of the star table is indexed by startIndex. Rows that do not 
     * apply to this star table are ignored.
     * 
     * @param rows
     * @param startIndex
     * @param table
     */
    public RowSubsetFilter(int[] rows, int startIndex, IrisStarTable table) {
        this.table = table;
        
        // The size of this filter is the size of the underlying plot table.
        this.size = (int) table.getPlotterTable().getRowCount();
        this.mask = new BitSet();
        
        for (int i : rows) {
            int index = i - startIndex;
            if (index >= 0 && index < size) {
                mask.set(index);
            }
            
        }
    }
    
    @Override
    public BitSet getFilteredRows(IrisStarTable table) {
        // These filters only apply to a single star table.
        if (this.table == table) {
            return mask;
        }
        return new BitSet();
    }
    
    @Override
    public void invert() {
        mask.flip(0, size);
    }
}
