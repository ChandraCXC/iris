package cfa.vo.iris.sed.stil;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.Before;
import org.junit.Test;

import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;

public class SerializingStarTableTest {
    
    private Sed sed;
    
    private String utype = "utype$";
    private final String seg_flux_utype = "spec:Spectrum.Data.FluxAxis.Value";
    private final String seg_spec_utype = "spec:Spectrum.Data.SpectralAxis.Value";
    
    @Before
    public void setUp() throws Exception {
        sed = Sed.read(TestData.class.getResource("test.vot").openStream(), SedFormat.VOT);
        assertEquals(1, sed.getNumberOfSegments());
    }

    @Test
    public void testStarTable() throws Exception {
        StarTableAdapter<Segment> adapter = new SerializingStarTableAdapter();
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
}