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

import org.junit.Test;
import org.uispec4j.utils.ArrayUtils;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.MetadataStarTable;
import uk.ac.starlink.table.PrimitiveArrayColumn;
import uk.ac.starlink.table.StarTable;

public class ColumnMappingStarTableTest {
    
    @Test
    public void testColumnMappingStarTable() throws Exception {
        
        ColumnInfoMatcher matcher = new UtypeColumnInfoMatcher();
        
        ColumnInfo c1 = new ColumnInfo("c1");
        ColumnInfo c2 = new ColumnInfo("c2");
        ColumnInfo c3 = new ColumnInfo("c3");
        ColumnInfo c4 = new ColumnInfo("c4");
        
        c1.setUtype("c1");
        c2.setUtype("c2");
        c3.setUtype("c3");
        c4.setUtype("c4");
        
        StarTable metadata = new MetadataStarTable(new ColumnInfo[] {c1,c2,c3,c4});
        
        ColumnStarTable base = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 1;
            }
        };
        base.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c1, new double[] {1.0}));
        base.addColumn(PrimitiveArrayColumn.makePrimitiveColumn(c3, new double[] {3.0}));
        
        ColumnMappingStarTable test = new ColumnMappingStarTable(base, metadata, matcher);

        ArrayUtils.assertEquals(new int[] {0,2,1,3}, test.getColumnMap());
        ArrayUtils.assertEquals(new Object[] {1.0, null, 3.0, null}, test.getRow(0));
        assertEquals(4, test.getColumnCount());
        assertEquals(1, test.getRowCount());
        assertEquals(base, test.getOriginalTable());
    }
}
