package cfa.vo.iris.visualizer.preferences;

import java.util.Arrays;

import org.junit.Test;

import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.sed.quantities.XUnit;
import uk.ac.starlink.table.ColumnStarTable;

import static org.junit.Assert.*;

public class FittingModelTest {
    
    @Test
    public void testFittingModel() throws Exception {
        FittingRange range1 = new FittingRange(1000, 10, XUnit.NM);
        FittingRange range2 = new FittingRange(5, 3, XUnit.CM);
        
        FittingRangeModel model = new FittingRangeModel(Arrays.asList(range1, range2), "Angstrom", 1);
        
        ColumnStarTable table = (ColumnStarTable) model.getInSource();
        assertArrayEquals(new Object[] {10000.0, 100.0, 1.0}, table.getRow(0));
        assertArrayEquals(new Object[] {5E8, 3E8, 1.0}, table.getRow(1));
        assertEquals(2, table.getRowCount());
        
        assertEquals(1, model.getNumberOfLayers());
        assertFalse(model.isShowMarks());
        assertFalse(model.getShowLines());
        assertTrue(model.isShowErrorBars());
    }

}
