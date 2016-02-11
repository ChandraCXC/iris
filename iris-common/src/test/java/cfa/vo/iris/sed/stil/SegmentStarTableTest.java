package cfa.vo.iris.sed.stil;

import org.junit.Before;
import org.junit.Test;

import cfa.vo.iris.sed.stil.SegmentStarTable.Column;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.io.SedFormat;
import uk.ac.starlink.table.RowSequence;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class SegmentStarTableTest {
    
    private Sed sed;
    
    @Before
    public void setUp() throws Exception {
        sed = Sed.read(getClass().getResource("/test_data/test.vot").getPath(), SedFormat.VOT);
        assertEquals(1, sed.getNumberOfSegments());
    }

    @Test
    public void testStarTable() throws Exception {
        SegmentStarTable table = new SegmentStarTable(sed.getSegment(0)); 
        
        assertTrue(!StringUtils.isEmpty(table.getName()));
        assertEquals(455, table.getRowCount());
        assertTrue(table.isRandom());
        
        assertEquals(new Double("6.17E23"), (Double) table.getCell(0, 0), 100);
        assertEquals(new Double("6.17E23"), (Double) table.getRow(0)[0], 100);

        assertEquals(3, table.getColumnCount());
        assertEquals(Column.SPECTRAL_COL.name(), table.getColumnInfo(0).getName());
        assertEquals(Column.FLUX_COL.name(), table.getColumnInfo(1).getName());
        assertEquals(Column.FLUX_ERR_HI.name(), table.getColumnInfo(2).getName());
        
        RowSequence seq = table.getRowSequence();
        assertTrue(seq.next());
        assertEquals(new Double("6.17E23"), (Double) seq.getRow()[0]);
        assertTrue(seq.next());
    }
}
