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
package cfa.vo.iris.sed.stil;

import org.junit.Before;
import org.junit.Test;
import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.iris.units.spv.YUnits;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;

import static org.junit.Assert.*;

import java.util.BitSet;

import org.apache.commons.lang.StringUtils;

public class SegmentStarTableTest {
    
    private Sed sed;
    
    @Before
    public void setUp() throws Exception {
        sed = Sed.read(TestData.class.getResource("test.vot").openStream(), SedFormat.VOT);
        assertEquals(1, sed.getNumberOfSegments());
    }

    @Test
    public void testStarTable() throws Exception {
        SegmentStarTable table = new SegmentStarTable(sed.getSegment(0));
        ColumnIdentifier id = new ColumnIdentifier(table);
        
        assertTrue(!StringUtils.isEmpty(table.getName()));
        assertEquals("3C 273", table.getName());
        assertEquals(455, table.getRowCount());
        assertTrue(table.isRandom());
        
        assertEquals(new Double("6.17E23"), (Double) table.getCell(0, 1), 100);
        assertEquals(new Double("6.17E23"), (Double) table.getRow(0)[1], 100);

        assertEquals(5, table.getColumnCount());
        assertEquals(Column.Segment_Id.name(), table.getColumnInfo(0).getName());
        assertEquals(Column.Spectral_Value.name(), table.getColumnInfo(1).getName());
        assertEquals(Column.Flux_Value.name(), table.getColumnInfo(2).getName());
        assertEquals(Column.Original_Flux_Value.name(), table.getColumnInfo(3).getName());
        assertEquals(Column.Flux_Error.name(), table.getColumnInfo(4).getName());
        assertEquals(new XUnits("Hz"), table.getSpecUnits());
        assertEquals(new YUnits("Jy"), table.getFluxUnits());
        
        int col = id.getColumnIndex(Column.Flux_Error.name());
        assertTrue(col >= 0);
        assertEquals(new Double("4.42E-12"), (Double) table.getCell(0, col));
        
        RowSequence seq = table.getRowSequence();
        assertTrue(seq.next());
        assertEquals(new Double("6.17E23"), (Double) seq.getRow()[1]);
        
        // Basic unit conversion test
        table.setSpecUnits(new XUnits('\u03BC' + "m")); // microns
        table.setFluxUnits(new YUnits("ergs/cm**2/s/a"));
        assertEquals(new Double("4.85E-10"), (Double) table.getSpecValues()[0], 1E-12);
        assertEquals(new Double("4.85E-10"), (Double) table.getCell(0, 1), 1E-12);
        
        col = id.getColumnIndex(Column.Flux_Error.name());
        assertTrue(col >= 0);
        assertEquals(new Double("5.61E-6"), (Double) table.getCell(0, col), 1E-8);
    }
    
    @Test
    public void testMasking() throws Exception {
        SegmentStarTable table = new SegmentStarTable(sed.getSegment(0));
        
        // No filter column should be present
        assertEquals(Column.Segment_Id.name(), table.getColumnInfo(0).getName());
        assertEquals(Column.Spectral_Value.name(), table.getColumnInfo(1).getName());
        assertFalse(table.columns.contains(table.filteredColumn));
        
        // Apply a mask
        BitSet mask = new BitSet();
        mask.set(0);
        table.setMasked(mask);

        ColumnIdentifier id = new ColumnIdentifier(table);
        assertEquals(0, id.getColumnIndex(Column.Masked.name()));

        assertEquals(Column.Masked.name(), table.getColumnInfo(0).getName());
        assertEquals(Column.Segment_Id.name(), table.getColumnInfo(1).getName());
        assertEquals(Column.Spectral_Value.name(), table.getColumnInfo(2).getName());

        assertEquals(true, table.getCell(0, 0));
        assertEquals(false, table.getCell(1, 0));
        
        // Remove the mask and the column should be gone
        table.setMasked(new BitSet());
        assertEquals(Column.Segment_Id.name(), table.getColumnInfo(0).getName());
        assertEquals(Column.Spectral_Value.name(), table.getColumnInfo(1).getName());
        assertFalse(table.columns.contains(table.filteredColumn));
    }
}
