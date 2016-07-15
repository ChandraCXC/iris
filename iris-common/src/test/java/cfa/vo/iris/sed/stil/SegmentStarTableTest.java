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
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.iris.units.spv.YUnits;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.Utypes;
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
        assertEquals("Hz", table.getColumnInfo(id.getColumnIndex(Column.Spectral_Value.name())).getUnitString());
        assertEquals("Jy", table.getColumnInfo(id.getColumnIndex(Column.Flux_Value.name())).getUnitString());
        assertEquals("Jy", table.getColumnInfo(id.getColumnIndex(Column.Original_Flux_Value.name())).getUnitString());
        assertEquals("Jy", table.getColumnInfo(id.getColumnIndex(Column.Flux_Error.name())).getUnitString());
        
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
        
        assertEquals('\u03BC' + "m", table.getColumnInfo(id.getColumnIndex(Column.Spectral_Value.name())).getUnitString());
        assertEquals("erg/s/cm2/Angstrom", table.getColumnInfo(id.getColumnIndex(Column.Flux_Value.name())).getUnitString());
        assertEquals("Jy", table.getColumnInfo(id.getColumnIndex(Column.Original_Flux_Value.name())).getUnitString()); // shouldn't change
        assertEquals("erg/s/cm2/Angstrom", table.getColumnInfo(id.getColumnIndex(Column.Flux_Error.name())).getUnitString());
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

    @Test
    public void testException() throws Exception {
        SegmentStarTable table = new SegmentStarTable(sed.getSegment(0));
        try {
            table.getColumnData(200);
        } catch (IllegalArgumentException e) {
            assertTrue(StringUtils.contains(e.getMessage(), "index out of bounds"));
            return;
        }
        fail();
    }
    
    @Test
    public void testUnits() throws Exception {
        
        double[] x = new double[]{1.0, 2.0, 3.0};
        double[] y = new double[]{1.0, 2.0, 3.0};
        double[] err = new double[]{0.1, 0.1, 0.1};
        
        Segment segment = new Segment();
        segment.setFluxAxisValues(y);
        segment.setFluxAxisUnits("erg/s/cm**2/Hz");
        segment.createChar().createFluxAxis().setUcd("ucdf");
        segment.setSpectralAxisValues(x);
        segment.setSpectralAxisUnits("Hz");
        segment.getChar().createSpectralAxis().setUcd("ucds");
        segment.getData().setDataValues(err, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
        
        SegmentStarTable table = new SegmentStarTable(segment);
        
        // erg/s/cm2/Hz to ABMAG
        table.setFluxUnits(new YUnits("ABMAG"));
        
        // m_AB = -2.5 log10(f_nu(erg/s/cm2/Hz)) - 48.60
        // err(m_ab) = -2.5 log10(1 + err(f_nu)/f_nu)
        double[] expectedY = new double[]{-48.6, -49.35257499, -49.79280314};
        double[] expectedYerr = new double[]{0.10348171, 0.05297325, 0.0356011};
        for (int i=0; i < table.getFluxValues().length; i++) {
            assertEquals(expectedY[i], table.getFluxValues()[i], 0.000001);
            assertEquals(expectedYerr[i], table.getFluxErrValues()[i], 0.000001);
        }
        
        // convert between ABMAG and erg/s/cm2/Hz and make sure the values 
        // switch back and forth correctly
        
        // ABMAG back to erg/s/cm2/Hz
        // f_nu(erg/s/cm2/Hz) = 10**(-0.4 (m_ab + 48.6))
        // f_nu_err(erg/s/cm2/Hz) = 10**(-0.4 (m_ab + m_ab_err + 48.6)) - f_nu
        table.setFluxUnits(new YUnits("erg/s/cm**2/Hz"));
        for (int i=0; i < table.getFluxValues().length; i++) {
            assertEquals(y[i], table.getFluxValues()[i], 0.000001);
            assertEquals(0.1, table.getFluxErrValues()[i], 0.000001);
        }
        
        // erg/s/cm2/Hz to ABMAG
        table.setFluxUnits(new YUnits("ABMAG"));
        for (int i=0; i < table.getFluxValues().length; i++) {
            assertEquals(expectedY[i], table.getFluxValues()[i], 0.000001);
            assertEquals(expectedYerr[i], table.getFluxErrValues()[i], 0.000001);
        }
        
        // ABMAG back to erg/s/cm2/Hz
        table.setFluxUnits(new YUnits("erg/s/cm**2/Hz"));
        for (int i=0; i < table.getFluxValues().length; i++) {
            assertEquals(y[i], table.getFluxValues()[i], 0.000001);
            assertEquals(0.1, table.getFluxErrValues()[i], 0.000001);
        }
    }
    
    @Test
    public void hashCodeTest() throws Exception {
        Segment segment = TestUtils.createSampleSegment(new double[] {1}, new double[] {1});
        SegmentStarTable table = new SegmentStarTable(segment);
        
        assertFalse(table.validHashCode);
        
        // Compute initial hashCode
        int h1 = table.hashCode();
        assertTrue(table.validHashCode);
        
        // Update values
        table.setSpecErrValues(new double[] {2});
        assertFalse(table.validHashCode);
        int h2 = table.hashCode();
        assertFalse(h1 == h2);
        assertTrue(table.validHashCode);
        
        // Update values
        table.setFluxErrValues(new double[] {2});
        assertFalse(table.validHashCode);
        int h3 = table.hashCode();
        assertFalse(h2 == h3);
        assertTrue(table.validHashCode);
    }
}
