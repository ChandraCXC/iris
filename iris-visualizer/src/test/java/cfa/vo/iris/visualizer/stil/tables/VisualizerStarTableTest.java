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
