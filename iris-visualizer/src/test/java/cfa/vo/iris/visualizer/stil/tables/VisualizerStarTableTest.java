package cfa.vo.iris.visualizer.stil.tables;

import org.junit.Before;

import cfa.vo.sedlib.common.Utypes;
import uk.ac.starlink.table.ColumnInfo;

public abstract class VisualizerStarTableTest {
    
    ColumnInfoMatcher matcher = new UtypeColumnInfoMatcher();
    
    ColumnInfo c1;
    ColumnInfo c2;
    ColumnInfo c3;
    ColumnInfo c4;
    
    String utype1;
    String utype2;
    String utype3;
    String utype4;
    
    @Before
    public void setup() {
        matcher = new UtypeColumnInfoMatcher();
        
        c1 = new ColumnInfo("c1");
        c2 = new ColumnInfo("c2");
        c3 = new ColumnInfo("c3");
        c4 = new ColumnInfo("c4");
        
        utype1 = Utypes.getName(Utypes.SEG_CHAR_CHARAXIS);
        utype2 = Utypes.getName(Utypes.SEG_CHAR_FLUXAXIS);
        utype3 = Utypes.getName(Utypes.SEG_CHAR_SPECTRALAXIS);
        utype4 = Utypes.getName(Utypes.SEG_DATAID_CREATIONTYPE);
        
        c1.setUtype(utype1);
        c2.setUtype(utype2);
        c3.setUtype(utype3);
        c4.setUtype(utype4);
    }
}
