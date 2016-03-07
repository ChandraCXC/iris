package cfa.vo.iris.visualizer.stil.tables;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.ConstantColumn;
import uk.ac.starlink.table.StarTable;

public class ColumnMetadataStarTableTest {
    
    @Test
    public void testColumnMetadataStarTable() {
        
        UtypeColumnMatcher matcher = new UtypeColumnMatcher();
        
        ColumnInfo c1 = new ColumnInfo("c1");
        ColumnInfo c2 = new ColumnInfo("c2");
        ColumnInfo c3 = new ColumnInfo("c3");
        
        c1.setUtype("c1");
        c2.setUtype("c2");
        c3.setUtype("c3");
        
        ColumnData d1 = new ConstantColumn(c1, null);
        ColumnData d2 = new ConstantColumn(c2, null);
        ColumnData d3 = new ConstantColumn(c3, null);
        
        StarTable s1 = getStarTable(new ColumnData[] {d1, d2});
        StarTable s2 = getStarTable(new ColumnData[] {d2, d3});
        
        List<StarTable> tables = new LinkedList<>();
        tables.add(s1);
        tables.add(s2);
        
        ColumnMetadataStarTable test = new ColumnMetadataStarTable(tables, matcher);
        
        assertEquals(3, test.getColumnCount());
    }
    
    private static StarTable getStarTable(ColumnData[] data) {
        ColumnStarTable c = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 0;
            }
        };
        
        for (ColumnData d : data) {
            c.addColumn(d);
        }
        
        return c;
    }
}
