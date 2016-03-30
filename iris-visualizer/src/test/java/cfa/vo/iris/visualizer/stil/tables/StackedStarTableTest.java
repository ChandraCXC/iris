/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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

import org.junit.Test;
import org.uispec4j.utils.ArrayUtils;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.PrimitiveArrayColumn;
import uk.ac.starlink.table.StarTable;

public class StackedStarTableTest extends VisualizerStarTableTest {
    
    @Test
    public void testStackedStarTable() throws Exception {
        
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
    
    @Test
    public void testEmptyStarTable() throws Exception {
        StackedStarTable test = new StackedStarTable(new ArrayList<StarTable>(), null);

        assertEquals(0, test.getColumnCount());
        assertEquals(0, test.getRowCount());
    }
    
    @Test
    public void testVOTableData() throws Exception {
        
        IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
        
        Segment seg1 = ExtSed.read(TestData.class.getResource("3c273.vot").openStream(), SedFormat.VOT).getSegment(0);
        IrisStarTable table1 = adapter.convertSegment(seg1);
        
        StarTable dataTable = table1.getSegmentDataTable();
        
        List<StarTable> tables = new ArrayList<>();
        tables.add(dataTable);
        
        StackedStarTable test = new StackedStarTable(tables, matcher);
        assertEquals(16, test.getColumnCount());
        for (int i=0; i<test.getColumnCount(); i++) {
            assertNotNull(test.getRow(0)[i]);
        }
        
        Segment seg2 = TestUtils.createSampleSegment(new double[] {1,2,3}, new double[] {1,2,3});
        IrisStarTable table2 = adapter.convertSegment(seg2);
        tables.add(table2);

        StarTable pt1 = table1.getPlotterTable();
        StarTable pt2 = table2.getPlotterTable();
        
        tables.clear();
        tables.add(pt1);
        tables.add(pt2);
        
        test = new StackedStarTable(tables, matcher);
        
        // pt2 fits inside pt1 since it doesn't have errors
        assertEquals(pt1.getColumnCount(), test.getColumnCount());
        assertEquals(pt1.getRowCount() + pt2.getRowCount(), test.getRowCount());
        
        // First rows should match values
        ArrayUtils.assertEquals(pt1.getRow(0), test.getRow(0));
        
        // First row of pt2 should have a null value at the end of it - in the error column
        long start = pt1.getRowCount();
        ArrayUtils.assertEquals(pt2.getRow(0), Arrays.copyOfRange(test.getRow(start), 0, pt2.getColumnCount()));
        assertNull(test.getRow(start)[test.getColumnCount() - 1]);
    }
    
    @Test
    public void testDifferentUtypePrefixes() {
        
        // These should line up to the same column
        String utype1 = "spec:Spectrum.Char.FluxAxis.Accuracy.StatError";
        String utype2 = "phot:Spectrum.Char.FluxAxis.Accuracy.StatError";
        
        ColumnInfo c1 = new ColumnInfo("c1");
        ColumnInfo c2 = new ColumnInfo("c2");
        
        c1.setUtype(utype1);
        c2.setUtype(utype2);
        
        ColumnStarTable data1 = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 1;
            }
        };
        data1.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c1, new double[] {1.0}));
        
        ColumnStarTable data2 = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 1;
            }
        };
        data2.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c2, new double[] {2.0}));
        
        List<StarTable> tables = new ArrayList<>(2);
        tables.add(data1);
        tables.add(data2);
        
        StackedStarTable test = new StackedStarTable(tables, matcher);
        assertEquals(1, test.getColumnCount());
    }

}
