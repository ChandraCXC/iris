/**
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.stil.tables;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.uispec4j.utils.ArrayUtils;

import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sedlib.Segment;
import uk.ac.starlink.table.RowSequence;

public class MaskingTest {
    
    IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
    
    @Test
    public void testMasks() throws Exception {
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
        assertEquals(5, test.getRowCount());
        
        seq = test.getRowSequence();
        seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 1.0, 6.0, 6.0}, seq.getRow());
        
        // Move to end of StarTable
        seq.next(); seq.next(); seq.next(); seq.next();
        ArrayUtils.assertEquals(new Object[] {test.getName(), 5.0, 10.0, 10.0}, seq.getRow());
        
        // Apply a filter to rows 0 and 4
        test.applyMasks(new int[] {0, 4}, 0);;
        assertEquals(3, test.getRowCount());
        
        seq = test.getRowSequence();
        seq.next();
        ArrayUtils.assertEquals(new Object[] {false, test.getName(), 2.0, 7.0, 7.0}, seq.getRow());
        
        // Move to end of StarTable
        seq.next(); seq.next();
        ArrayUtils.assertEquals(new Object[] {false, test.getName(), 4.0, 9.0, 9.0}, test.getRow(3));
        
        // verify values
        checkEquals(new double[] {7,8,9}, test.getFluxDataValues());
        checkEquals(new double[] {2,3,4}, test.getSpectralDataValues());
        
        // Apply a filter to row 2
        test.applyMasks(new int[] {2}, 0);
        assertEquals(2, test.getRowCount());
        
        // verify values
        checkEquals(new double[] {7,9}, test.getFluxDataValues());
        checkEquals(new double[] {2,4}, test.getSpectralDataValues());
        
        // Remove the first filter
        test.clearMasks(new int[] {0, 4}, 0);
        assertEquals(4, test.getRowCount());
        
        // verify values
        checkEquals(new double[] {6,7,9,10}, test.getFluxDataValues());
        checkEquals(new double[] {1,2,4,5}, test.getSpectralDataValues());
    }
    
    
    @Test
    public void testMaskedRowSequence() throws Exception {
        RowSequence seq;
        
        double[] x = new double[] {100,200,300};
        double[] y = new double[] {100,200,300};
        Segment seg = TestUtils.createSampleSegment(x, y);
        
        // Should have no rows filtered, and the spec axis values should all match
        IrisStarTable test = adapter.convertSegment(seg);
        seq = test.getRowSequence();
        for (int i=0; i<x.length; i++) {
            assertTrue(seq.next());
            assertEquals(x[i], seq.getRow()[2]);
        }
        assertFalse(seq.next());
        
        // Filter first point
        test.applyMasks(new int[] {0}, 0);
        seq = test.getRowSequence();
        
        // Should not have the first point
        double[] check = new double[] {200,300};
        for (int i=0; i<check.length; i++) {
            assertTrue(seq.next());
            assertEquals(check[i], seq.getRow()[2]);
        }
        assertFalse(seq.next());
        
        // Verify clearing filters returns table to normal.
        test.clearMasks();
        seq = test.getRowSequence();
        for (int i=0; i<x.length; i++) {
            assertTrue(seq.next());
            assertEquals(x[i], seq.getRow()[2]);
        }
        assertFalse(seq.next());
        
        // Filter first and last rows
        test.applyMasks(new int[] {0, 2}, 0);
        seq = test.getRowSequence();
        
        check = new double[] {200};
        for (int i=0; i<check.length; i++) {
            assertTrue(seq.next());
            assertEquals(check[i], seq.getRow()[2]);
        }
        assertFalse(seq.next());
        
        // Filter all points
        test.clearMasks();
        test.applyMasks(new int[] {0, 1, 2}, 0);
        seq = test.getRowSequence();
        assertFalse(test.getRowSequence().next());
        
        // Check exception
        try {
            seq.getRow();
        } catch (IllegalStateException e) {
            assertTrue(StringUtils.contains(e.getMessage(), "No current row"));
            return;
        }
        
        fail();
    }
    
    @Test
    public void testMultipleTables() throws Exception {
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        IrisStarTable table1 = adapter.convertSegment(TestUtils.createSampleSegment());
        IrisStarTable table2 = adapter.convertSegment(TestUtils.createSampleSegment());

        List<IrisStarTable> tables = new ArrayList<>();
        tables.add(table1);
        tables.add(table2);
       
        // Mask first row in first table, first and last in second table. 
        // Also anything >= 6 should be superfluous, as there are only 6 rows
        IrisStarTable.applyFilters(tables, new int[] {0, 3, 5, 7});
        
        assertEquals(2, table1.getRowCount());
        assertEquals(1, table2.getRowCount());
        
        checkEquals(new double[] {2,3}, table1.getFluxDataValues());
        checkEquals(new double[] {2}, table2.getFluxDataValues());
        
        // Clear filter from second star table. Non-masked values should have no effect.
        IrisStarTable.clearFilters(tables, new int[] {3,4});
        
        assertEquals(2, table1.getRowCount());
        assertEquals(2, table2.getRowCount());
        
        checkEquals(new double[] {2,3}, table1.getFluxDataValues());
        checkEquals(new double[] {1,2}, table2.getFluxDataValues());
        
        // Remove all filters
        IrisStarTable.clearAllFilters(tables);
        
        assertEquals(3, table1.getRowCount());
        assertEquals(3, table2.getRowCount());
        
        checkEquals(new double[] {1,2,3}, table1.getFluxDataValues());
        checkEquals(new double[] {1,2,3}, table2.getFluxDataValues());
    }
    
    @Test
    public void testRowMappingWithMasks() throws Exception {
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        IrisStarTable table1 = adapter.convertSegment(TestUtils.createSampleSegment());
        IrisStarTable table2 = adapter.convertSegment(TestUtils.createSampleSegment());

        List<IrisStarTable> tables = new ArrayList<>();
        tables.add(table1);
        tables.add(table2);
        
        // Check masked indexes line up
        assertEquals(2, table1.getBaseTableRow(2));
        assertEquals(0, table2.getBaseTableRow(0));
        assertEquals(-1, table2.getBaseTableRow(6));
       
        // Mask first row in first table, first in second table. 
        // Also anything >= 6 should be superfluous, as there are only 6 rows
        IrisStarTable.applyFilters(tables, new int[] {0, 3});
        
        // 2nd row in the first table should now point to 3rd row of base table
        assertEquals(2, table1.getBaseTableRow(1));
        
        // 1st row of second table should now point to 2nd row of base table
        assertEquals(1, table2.getBaseTableRow(0));
        
        // Apply filters to 1st row and 4th and 5th rows.
        IrisStarTable.clearAllFilters(tables);
        IrisStarTable.applyFilters(tables, new int[] {0,3,4});
        
        assertEquals(1, table1.getBaseTableRow(0));
        assertEquals(2, table2.getBaseTableRow(0));
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
