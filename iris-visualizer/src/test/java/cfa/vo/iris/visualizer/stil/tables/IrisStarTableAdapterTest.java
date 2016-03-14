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

import java.util.concurrent.Executors;

import org.junit.Test;
import org.uispec4j.utils.ArrayUtils;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.table.StarTable;

public class IrisStarTableAdapterTest {
    
    private IrisStarTableAdapter adapter = new IrisStarTableAdapter(Executors.newSingleThreadExecutor());
    
    @Test
    public void testSerialization() throws Exception {
        Segment seg = TestUtils.createSampleSegment();
        
        IrisStarTable table = adapter.convertSegment(seg);
        
        SegmentStarTable plotterTable = table.getPlotterTable();
        StarTable dataTable = table.getSegmentDataTable();
        assertEquals(table.getName(), plotterTable.getName());
        
        // Name, spec values, flux values, original flux values
        assertEquals(4, table.getColumnCount());
        
        // just flux and spec values
        assertEquals(2, dataTable.getColumnCount());
    }
    
    @Test
    public void testVOCompliantTable() throws Exception {
        // This file hasn't been showing up in the plotter, so verifying that serialization works here.
        Segment seg = ExtSed.read(TestData.class.getResource("m87_saved.vot").openStream(), SedFormat.VOT).getSegment(0);
        
        final IrisStarTable table = adapter.convertSegment(seg);

        assertEquals(356, table.getRowCount());
        assertEquals(356, table.getSegmentDataTable().getRowCount());
        
        // Incl. flux error
        assertEquals(5, table.getColumnCount());
        ArrayUtils.assertEquals(new Object[] {"MESSIER 087",1.21E25,1.45E-13,1.45E-13,3.14E-14}, table.getRow(0));
    }
    
    @Test
    public void testConvertAsync() throws Exception {
        Segment seg = TestUtils.createSampleSegment();
        
        final IrisStarTable table = adapter.convertSegmentAsync(seg);
        
        // Name, spec values, flux values, original flux values
        assertEquals(4, table.getColumnCount());
        
        // Wait for serialization to finish
        TestUtils.invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(2, table.getSegmentDataTable().getColumnCount());
            }
        });
    }
}
