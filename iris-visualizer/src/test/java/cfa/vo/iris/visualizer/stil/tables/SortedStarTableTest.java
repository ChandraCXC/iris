package cfa.vo.iris.visualizer.stil.tables;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.PrimitiveArrayColumn;

public class SortedStarTableTest {
    
    @Test
    public void testSortedStarTable() throws Exception {
        
        ColumnInfo c1 = new ColumnInfo("c1");
        c1.setContentClass(Integer.class);
        ColumnInfo c2 = new ColumnInfo("c1");
        c2.setContentClass(Integer.class);
        
        ColumnStarTable base = ColumnStarTable.makeTableWithRows(3);
        base.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c1, new int[] {2, 1, 3}));
        base.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c2, new int[] {3, 2, 1}));
        
        // Sort on c2, ascending
        SortedStarTable t1 = new SortedStarTable(base, 1, true);
        
        // Verify columns sorted on second values
        assertEquals((int) t1.getCell(0, 1), 1);
        assertEquals((int) t1.getCell(1, 1), 2);
        assertEquals((int) t1.getCell(2, 1), 3);
        
        // First column in reverse order
        assertEquals((int) t1.getCell(0, 0), 3);
        assertEquals((int) t1.getCell(1, 0), 1);
        assertEquals((int) t1.getCell(2, 0), 2);
        
        // Base table still in same order?
        assertEquals((int) base.getCell(0, 1), 3);
        assertEquals((int) base.getCell(1, 1), 2);
        assertEquals((int) base.getCell(2, 1), 1);
        
        //
        // Sort on c1, descending
        //
        SortedStarTable t2 = new SortedStarTable(base, 0, false);
        
        // First column in sorted, descending order
        assertEquals((int) t2.getCell(0, 0), 3);
        assertEquals((int) t2.getCell(1, 0), 2);
        assertEquals((int) t2.getCell(2, 0), 1);
        
        // Second column matches first
        assertEquals((int) t2.getCell(0, 1), 1);
        assertEquals((int) t2.getCell(1, 1), 3);
        assertEquals((int) t2.getCell(2, 1), 2);
    }
}
