package cfa.vo.iris.visualizer.stil.tables;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uispec4j.utils.ArrayUtils;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.PrimitiveArrayColumn;
import uk.ac.starlink.table.StarTable;

public class StackedStarTableTest {
    
    @Test
    public void testStackedStarTable() throws Exception {
        
        ColumnMatcher matcher = new UtypeColumnMatcher();
        
        ColumnInfo c1 = new ColumnInfo("c1");
        ColumnInfo c2 = new ColumnInfo("c2");
        ColumnInfo c3 = new ColumnInfo("c3");
        ColumnInfo c4 = new ColumnInfo("c4");
        
        c1.setUtype("c1");
        c2.setUtype("c2");
        c3.setUtype("c3");
        c4.setUtype("c4");
        
        ColumnStarTable data1 = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 1;
            }
        };
        data1.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c1, new double[] {1.0}));
        data1.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c3, new double[] {3.0}));
        
        ColumnStarTable data2 = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 1;
            }
        };
        data2.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c2, new double[] {2.0}));
        data2.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c3, new double[] {3.0}));
        data2.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c4, new double[] {4.0}));
        
        List<StarTable> tables = new ArrayList<>(2);
        tables.add(data1);
        tables.add(data2);
        
        StackedStarTable test = new StackedStarTable(tables, matcher);
        
        assertEquals(4, test.getColumnCount());
        assertEquals(2, test.getRowCount());
        ArrayUtils.assertEquals(new Object[] {1.0, 3.0, null, null}, test.getRow(0));
        ArrayUtils.assertEquals(new Object[] {null, 3.0, 2.0, 4.0}, test.getRow(1));
    }

}
