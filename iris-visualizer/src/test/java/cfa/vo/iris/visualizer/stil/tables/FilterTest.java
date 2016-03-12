package cfa.vo.iris.visualizer.stil.tables;

import static org.junit.Assert.*;

import org.junit.Test;
import org.uispec4j.utils.ArrayUtils;

import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.filters.Filter;
import cfa.vo.iris.visualizer.filters.NullFilter;
import cfa.vo.iris.visualizer.filters.RowSubsetFilter;
import cfa.vo.sedlib.Segment;
import uk.ac.starlink.table.RowSequence;

public class FilterTest {
    
    IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
    
    @Test
    public void testFilters() throws Exception {
        RowSequence seq;
        
        double[] x = new double[] {1,2,3,4,5};
        double[] y = new double[] {6,7,8,9,10};
        
        Segment seg = TestUtils.createSampleSegment(x, y);
        
        IrisStarTable test = adapter.convertSegment(seg);
        
        assertEquals(5, test.getRowCount());
        seq = test.getRowSequence();
        seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 1.0, 6.0, 6.0}, seq.getRow());
        
        // Move to end of StarTable
        seq.next(); seq.next(); seq.next(); seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 5.0, 10.0, 10.0}, test.getRow(4));
        
        // Add a null filter
        Filter nullFilter = new NullFilter();
        test.addFilter(nullFilter);
        assertEquals(5, test.getRowCount());
        
        seq = test.getRowSequence();
        seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 1.0, 6.0, 6.0}, seq.getRow());
        
        // Move to end of StarTable
        seq.next(); seq.next(); seq.next(); seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 5.0, 10.0, 10.0}, seq.getRow());
        
        // Apply a filter to rows 0 and 4
        Filter rowFilter = new RowSubsetFilter(new int[] {0,4}, test);
        test.addFilter(rowFilter);
        assertEquals(3, test.getRowCount());
        
        seq = test.getRowSequence();
        seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 2.0, 7.0, 7.0}, seq.getRow());
        
        // Move to end of StarTable
        seq.next(); seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 4.0, 9.0, 9.0}, test.getRow(3));
    }

}
