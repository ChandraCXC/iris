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
import java.util.Arrays;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.uispec4j.utils.ArrayUtils;

import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.stil.IrisStarJTable.RowSelection;
import cfa.vo.iris.visualizer.stil.IrisStarJTable.StarJTableHeader;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;

public class IrisStarJTableTest {
    
    private IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
    
    @Test
    public void testColumnHeaderTooltips() throws Exception {
        IrisStarJTable table = new IrisStarJTable();
        IrisStarTable segTable = adapter.convertSegment(TestUtils.createSampleSegment());
        
        List<IrisStarTable> tables = Arrays.asList(segTable);
        table.setSelectedStarTables(tables);
        
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
    
    @Test
    public void testMaskingColumnBehavior() throws Exception {
        IrisStarJTable table = new IrisStarJTable();
        IrisStarTable segTable1 = adapter.convertSegment(TestUtils.createSampleSegment());
        IrisStarTable segTable2 = adapter.convertSegment(TestUtils.createSampleSegment());
        
        List<IrisStarTable> tables = Arrays.asList(segTable1, segTable2);
        
        // Mask first row of second table
        segTable2.applyMasks(new int[] {0});
        
        table.setSelectedStarTables(tables);
        TableColumnModel columnModel = table.getColumnModel();

        // Index
        TableColumn col = columnModel.getColumn(0);
        assertEquals(col.getHeaderValue(), "Index");
        
        // 2nd index should be the masked column (after index)
        col = columnModel.getColumn(1);
        assertEquals(col.getHeaderValue(), "Masked");
        
        // Verify first index behavior
        table.selectRowIndex(0,0);
        ArrayUtils.assertEquals(new int[] {0}, table.getSelectedRows());
        
        // Select 0th index in second star table
        table.clearSelection();
        table.selectRowIndex(1, 0);
        ArrayUtils.assertEquals(new int[] {3}, table.getSelectedRows());
        
        // Mask all but first rows in both tables
        table.clearSelection();
        IrisStarTable.clearAllMasks(tables);
        segTable1.applyMasks(new int[] {1,2});
        segTable2.applyMasks(new int[] {1,2});
        
        // Only one row in the two tables, try selected both of them
        table.selectRowIndex(0, 0);
        table.selectRowIndex(1, 0);
        ArrayUtils.assertEquals(new int[] {0,3}, table.getSelectedRows());
    }
    
    @Test
    public void testRowSelection() throws Exception {
        IrisStarJTable table = new IrisStarJTable();
        IrisStarTable segTable1 = adapter.convertSegment(TestUtils.createSampleSegment());
        IrisStarTable segTable2 = adapter.convertSegment(TestUtils.createSampleSegment());
        
        List<IrisStarTable> tables = Arrays.asList(segTable1, segTable2);
        
        table.setSelectedStarTables(tables);
        
        // Should be two empty lists
        RowSelection sel = table.getRowSelection();
        assertEquals(2, sel.selectedRows.length);
        assertEquals(0, sel.selectedRows[0].length);
        assertEquals(0, sel.selectedRows[1].length);
        
        // Select all rows
        table.addRowSelectionInterval(0, 5);
        
        // Should be two 3 element lists
        sel = table.getRowSelection();
        assertEquals(2, sel.selectedRows.length);
        ArrayUtils.assertEquals(new int[] {0,1,2}, sel.selectedRows[0]);
        ArrayUtils.assertEquals(new int[] {0,1,2}, sel.selectedRows[1]);
        
        table.clearSelection();
        
        // Select only last row
        table.addRowSelectionInterval(5, 5);
        
        // One empty one not empty list
        sel = table.getRowSelection();
        assertEquals(2, sel.selectedRows.length);
        ArrayUtils.assertEquals(new int[] {}, sel.selectedRows[0]);
        ArrayUtils.assertEquals(new int[] {2}, sel.selectedRows[1]);
    }
    
    @Test
    public void testSorting() throws Exception {
        IrisStarJTable table = new IrisStarJTable();
        table.setSortBySpecValues(true);
        table.setUsePlotterDataTables(true);
        
        IrisStarTable segTable = adapter.convertSegment(TestUtils.createSampleSegment(
                        new double[] {5,4,3,2,1},
                        new double[] {6,7,8,9,10}));
        
        table.setSelectedStarTables(Arrays.asList(segTable));
        
        // Rows selection should map correctly back to model, so selecting last three in the
        // table should be first three in model
        table.addRowSelectionInterval(2, 4);
        ArrayUtils.assertEquals(new int[] {0,1,2}, table.getSelectedRows(true));
        
        table.clearSelection();
        
        // Values should be in ascending order
        assertEquals(1.0, table.getValueAt(0, 2));
        assertEquals(5.0, table.getValueAt(4, 2));
        
        // Try adding the 0th (model) index to the selection, should select the last row.
        table.selectRowIndex(0,0);
        ArrayUtils.assertEquals(new int[] {4}, table.getSelectedRows(false));
        
        // mask value 5, row 0
        segTable.applyMasks(new int[] {0});
        
        // Add new StarTable, small valued
        IrisStarTable segTable1 = adapter.convertSegment(TestUtils.createSampleSegment(
                new double[] {.1,.2},
                new double[] {1000,2000}));
        
        // Reset the table
        table.setSelectedStarTables(Arrays.asList(segTable, segTable1));
        
        // Values should still be in ascending order (also now a masked column)
        assertEquals(.1, table.getValueAt(0, 3));
        assertEquals(5.0, table.getValueAt(6, 3));
        assertEquals(true, table.getValueAt(6, 1));
        
        // Select 2nd row from 2nd star table
        table.selectRowIndex(1,1);
        
        // Should be row 1, or row 6 in the model
        ArrayUtils.assertEquals(new int[] {1}, table.getSelectedRows(false));
        ArrayUtils.assertEquals(new int[] {6}, table.getSelectedRows(true));
        
        // Add first row from the 1st star table (has value 4 since the 1st row is masked)
        table.selectRowIndex(0,0);
        
        // Should be row 1, or row 6 in the model
        ArrayUtils.assertEquals(new int[] {1,6}, table.getSelectedRows(false));
        ArrayUtils.assertEquals(new int[] {0,6}, table.getSelectedRows(true));
    }
    
    @Test
    public void testSortingWithMasks() throws Exception {
        IrisStarJTable table = new IrisStarJTable();
        table.setSortBySpecValues(true);
        table.setUsePlotterDataTables(true);
        
        IrisStarTable segTable1 = adapter.convertSegment(TestUtils.createSampleSegment());
        IrisStarTable segTable2 = adapter.convertSegment(TestUtils.createSampleSegment());
        
        // All rows are masked
        segTable1.applyMasks(new int[] {0,1,2});
        segTable2.applyMasks(new int[] {0,1,2});
        
        // Select first rows from each table
        table.setSelectedStarTables(Arrays.asList(segTable1, segTable2));
        table.selectRowIndex(0, 0);
        table.selectRowIndex(1, 0);
        
        // Check selection values
        ArrayUtils.assertEquals(new int[] {0, 1}, table.getSelectedRows(false));
        ArrayUtils.assertEquals(new int[] {0, 3}, table.getSelectedRows(true));
        
        // Verify RowSelection matches expected values
        RowSelection selection = table.getRowSelection();
        ArrayUtils.assertEquals(new int[] {0}, selection.selectedRows[0]);
        ArrayUtils.assertEquals(new int[] {0}, selection.selectedRows[1]);
    }
}
