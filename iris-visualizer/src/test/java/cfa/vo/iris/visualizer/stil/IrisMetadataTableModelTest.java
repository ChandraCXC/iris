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

package cfa.vo.iris.visualizer.stil;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

import cfa.vo.iris.visualizer.stil.MetadataJTable.IrisMetadataTableModel;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.DefaultValueInfo;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.MetadataStarTable;
import uk.ac.starlink.table.StarTable;

public class IrisMetadataTableModelTest {

    @Test
    public void testTableModel() throws Exception {
        
        DefaultValueInfo d1 = new DefaultValueInfo("d1");
        DefaultValueInfo d2 = new DefaultValueInfo("d2");
        DefaultValueInfo d3 = new DefaultValueInfo("d3");
        
        Object o1 = 1;
        Object o2 = "hi";
        Object o3 = new ArrayList<Object>();
        
        DescribedValue v1 = new DescribedValue(d1, o1);
        DescribedValue v2 = new DescribedValue(d2, o2);
        DescribedValue v3 = new DescribedValue(d3, o3);
        
        StarTable table1 = new MetadataStarTable(new ColumnInfo[0]);
        StarTable table2 = new MetadataStarTable(new ColumnInfo[0]);
        
        table1.setParameter(v1);
        table1.setParameter(v2);
        table1.setName("table1");
        
        table2.setParameter(v2);
        table2.setParameter(v3);
        table2.setName("table2");
        
        ArrayList<StarTable> tableList = new ArrayList<>();
        tableList.add(table1);
        tableList.add(table2);
        
        IrisMetadataTableModel model = new IrisMetadataTableModel();
        model.setStarTableList(tableList);
        
        assertEquals(4, model.getColumnCount());
        assertEquals(2, model.getRowCount());
        
        assertEquals(o1.toString(), model.getValueAt(0, 1));
        assertEquals(o2.toString(), model.getValueAt(0, 2));
        assertEquals(null, model.getValueAt(0, 3));
        
        assertEquals(null, model.getValueAt(1, 1));
        assertEquals(o2.toString(), model.getValueAt(1, 2));
        assertEquals(o3.toString(), model.getValueAt(1, 3));
    }
}
