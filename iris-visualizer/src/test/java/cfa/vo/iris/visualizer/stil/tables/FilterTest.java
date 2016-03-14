package cfa.vo.iris.visualizer.stil.tables;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        
        // verify values
        checkEquals(new double[] {7,8,9}, test.getFluxDataValues());
        checkEquals(new double[] {2,3,4}, test.getSpectralDataValues());
        
        // Apply a filter to row 1
        RowSubsetFilter rowFilter2 = new RowSubsetFilter(new int[] {2}, test);
        test.addFilter(rowFilter2);
        assertEquals(2, test.getRowCount());
        
        // verify values
        checkEquals(new double[] {7,9}, test.getFluxDataValues());
        checkEquals(new double[] {2,4}, test.getSpectralDataValues());
        
        // Remove the first filter
        test.removeFilter(rowFilter);
        assertEquals(4, test.getRowCount());
        
        // verify values
        checkEquals(new double[] {6,7,9,10}, test.getFluxDataValues());
        checkEquals(new double[] {1,2,4,5}, test.getSpectralDataValues());
        
        // Invert the FilterSet, since there is a null filter everything should be empty.
        test.getFilters().invert();
        assertEquals(0, test.getRowCount());
        checkEquals(new double[] {}, test.getFluxDataValues());
        checkEquals(new double[] {}, test.getSpectralDataValues());
        
        // Remove the NullFilter, one row (2) should not be filtered
        test.removeFilter(nullFilter);
        assertEquals(1,  test.getRowCount());
        checkEquals(new double[] {8}, test.getFluxDataValues());
        checkEquals(new double[] {3}, test.getSpectralDataValues());
    }
    
    @Test
    public void testMultipleFilters() throws Exception {

        double[] x = new double[] {100,200,300};
        double[] y = new double[] {100,200,300};
        
        Segment seg1 = TestUtils.createSampleSegment();
        Segment seg2 = TestUtils.createSampleSegment(x,y);
        
        IrisStarTable t1 = adapter.convertSegment(seg1);
        IrisStarTable t2 = adapter.convertSegment(seg2);
        
        List<IrisStarTable> tables = new LinkedList<>();
        tables.add(t1); 
        tables.add(t2);
        
        // Filter the 1st row in t1, and 1st and 3rd row in t2.
        IrisStarTable.applyFilters(tables, new int[] {0,3,5});
        
        assertEquals(1, t1.getFilters().size());
        assertEquals(1, t2.getFilters().size());

        assertEquals(2, t1.getRowCount());
        assertEquals(1, t2.getRowCount());
        
        checkEquals(new double[] {2,3}, t1.getFluxDataValues());
        checkEquals(new double[] {2,3}, t1.getSpectralDataValues());
        
        checkEquals(new double[] {200}, t2.getFluxDataValues());
        checkEquals(new double[] {200}, t2.getSpectralDataValues());
    }
    
    
    public static void checkEquals(double[] expected, double[] values) {

        String msg = String.format("expected: %s but was: %s", 
                Arrays.toString(expected), Arrays.toString(values));
        
        if (expected.length != values.length) {
            throw new AssertionError(msg);
        }
        
        for (int i=0; i<expected.length; i++) {
            if (expected[i] != values[i]) {
                throw new AssertionError(msg);
            }
        }
    }
}
