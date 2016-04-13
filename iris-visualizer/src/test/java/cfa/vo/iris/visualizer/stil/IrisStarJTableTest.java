/*
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
package cfa.vo.iris.visualizer.stil;

import static org.junit.Assert.*;

import java.awt.event.MouseEvent;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.stil.IrisStarJTable.StarJTableHeader;

public class IrisStarJTableTest {
    
    @Test
    public void testColumnHeaderTooltips() throws Exception {
        IrisStarJTable table = new IrisStarJTable();
        SegmentStarTable segTable = new SegmentStarTable(TestUtils.createSampleSegment());
        
        table.setStarTable(segTable);
        StarJTableHeader header = (StarJTableHeader) table.getTableHeader();
        TableColumnModel columnModel = table.getColumnModel();
        
        // Location of x pointer
        int xLoc = 0;
        
        // Index
        TableColumn col = columnModel.getColumn(0);
        MouseEvent evt = new MouseEvent(header, 0, 0, 0, xLoc, 0, 1, false);
        String text = header.getToolTipText(evt);
        assertTrue(StringUtils.contains(text, "name: Index"));
        
        // Segment_Id
        xLoc = xLoc + col.getWidth() + 1;
        col = columnModel.getColumn(1);
        evt = new MouseEvent(header, 0, 0, 0, xLoc, 0, 1, false);
        text = header.getToolTipText(evt);
        assertTrue(StringUtils.contains(text, "name: Segment_Id"));
        
        // Spectral Value
        xLoc = xLoc + col.getWidth() + 1;
        col = columnModel.getColumn(2);
        evt = new MouseEvent(header, 0, 0, 0, xLoc, 0, 1, false);
        text = header.getToolTipText(evt);
        assertTrue(StringUtils.contains(text, "name: Spectral_Value"));
        assertTrue(StringUtils.contains(text, "unit: Angstrom"));
        
        // Switch Spectral and Flux value columns
        columnModel.moveColumn(3, 2);
        evt = new MouseEvent(header, 0, 0, 0, xLoc, 0, 1, false);
        text = header.getToolTipText(evt);
        assertTrue(StringUtils.contains(text, "name: Flux_Value"));
        assertTrue(StringUtils.contains(text, "unit: Jy"));
    }
}
