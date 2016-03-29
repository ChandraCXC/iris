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

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.ConstantColumn;
import uk.ac.starlink.table.StarTable;

public class ColumnMetadataStarTableTest {
    
    @Test
    public void testColumnMetadataStarTable() {
        
        ColumnInfoMatcher matcher = new UtypeColumnInfoMatcher();
        
        ColumnInfo c1 = new ColumnInfo("c1");
        ColumnInfo c2 = new ColumnInfo("c2");
        ColumnInfo c3 = new ColumnInfo("c3");
        
        c1.setUtype("c1");
        c2.setUtype("c2");
        c3.setUtype("c3");
        
        ColumnData d1 = new ConstantColumn(c1, null);
        ColumnData d2 = new ConstantColumn(c2, null);
        ColumnData d3 = new ConstantColumn(c3, null);
        
        StarTable s1 = getStarTable(new ColumnData[] {d1, d2});
        StarTable s2 = getStarTable(new ColumnData[] {d2, d3});
        
        List<StarTable> tables = new LinkedList<>();
        tables.add(s1);
        tables.add(s2);
        
        ColumnMetadataStarTable test = new ColumnMetadataStarTable(tables, matcher);
        
        assertEquals(3, test.getColumnCount());
    }
    
    private static StarTable getStarTable(ColumnData[] data) {
        ColumnStarTable c = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 0;
            }
        };
        
        for (ColumnData d : data) {
            c.addColumn(d);
        }
        
        return c;
    }
}
