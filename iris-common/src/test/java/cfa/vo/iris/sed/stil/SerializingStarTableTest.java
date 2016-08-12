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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;

public class SerializingStarTableTest {
    
    private String utype = "utype$";
    private final String seg_flux_utype = "spec:Spectrum.Data.FluxAxis.Value";
    private final String seg_spec_utype = "spec:Spectrum.Data.SpectralAxis.Value";

    @Test
    public void testStarTable() throws Exception {
        Sed sed = Sed.read(TestData.class.getResource("test.vot").openStream(), SedFormat.VOT);
        assertEquals(1, sed.getNumberOfSegments());
        
        SerializingSegmentAdapter adapter = new SerializingSegmentAdapter();
        Segment seg = sed.getSegment(0);
        
        StarTable table = adapter.convertStarTable(seg);
        
        assertTrue(!StringUtils.isEmpty(table.getName()));
        assertEquals(455, table.getRowCount());
        assertTrue(table.isRandom());
        
        int cc = table.getColumnCount();
        assertEquals(cc, 16);
        
        ColumnIdentifier id = new ColumnIdentifier(table);

        assertTrue(id.getColumnIndex(utype + seg_flux_utype) >= 0);
        assertTrue(id.getColumnIndex(utype + seg_spec_utype) >= 0);
        
        // Verify no non-null parameters
        for (Object o : table.getParameters()) {
            DescribedValue v = (DescribedValue) o;
            assertNotNull(v.getValue());
        }
    }
    
    @Test
    public void testSedSerialization() throws Exception {
        Segment seg1 = TestUtils.createSampleSegment(new double[] {0}, new double[] {0});
        Segment seg2 = TestUtils.createSampleSegment(new double[] {1,2}, new double[] {1,2});
        Segment seg3 = TestUtils.createSampleSegment(new double[] {3,4,5}, new double[] {3,4,5});
        
        Sed sed = new Sed();
        sed.addSegment(Arrays.asList(seg1, seg2, seg3));
        
        SerializingSegmentAdapter adapter = new SerializingSegmentAdapter();
        
        List<StarTable> tables = adapter.convertSed(sed);
        
        assertEquals(1, tables.get(0).getRowCount());
        assertEquals(2, tables.get(1).getRowCount());
        assertEquals(3, tables.get(2).getRowCount());
    }
}