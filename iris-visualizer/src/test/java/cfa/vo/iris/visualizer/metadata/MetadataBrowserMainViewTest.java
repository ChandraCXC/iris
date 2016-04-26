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

package cfa.vo.iris.visualizer.metadata;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;
import org.uispec4j.Table;
import org.uispec4j.Window;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.VisualizerComponent;
import cfa.vo.iris.visualizer.plotter.PlotterView;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.sedlib.Segment;
import static org.junit.Assert.*;

import java.util.BitSet;

import static cfa.vo.iris.test.unit.TestUtils.*;

public class MetadataBrowserMainViewTest extends AbstractComponentGUITest {

    private VisualizerComponent comp = new VisualizerComponent();
    private String plWindowName = comp.getName();
    private SedlibSedManager sedManager;

    private Window mbWindow; // metadata window
    private Window plWindow; // plotter window
    
    private MetadataBrowserMainView mbView;
    private PlotterView plView;

    private Panel plotter;
    private Panel dataPanel;
    private ListBox starTableList;
    
    private Table plotterTable;
    private Table dataTable;
    private Table segmentTable;
    
    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    @Before
    public void setupMbTest() throws Exception {
        sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        
        // Get plot window
        window.getMenuBar().getMenu("Tools").getSubMenu(plWindowName).getSubMenu(plWindowName).click();
        desktop.containsWindow(plWindowName).check();
        plWindow = desktop.getWindow(plWindowName);
        
        org.uispec4j.Button mbButton = desktop.getWindow(plWindowName).getButton("Metadata");
        mbButton.click();
        
        plotter = plWindow.getPanel("plotter");
        
        plView = comp.getDefaultPlotterView();
        mbView = plView.getMetadataBrowserView();
        
        desktop.containsWindow(mbView.getTitle()).check();
        mbWindow = desktop.getWindow(mbView.getTitle());
        dataPanel = mbWindow.getPanel("contentPane");
        
        // Segment list
        starTableList = dataPanel.getListBox();
        
        // Data table with plotter info
        dataPanel.getTabGroup().selectTab("Data");
        plotterTable = dataPanel.getTabGroup().getSelectedTab().getTable();
        
        // Data table with all info
        dataPanel.getTabGroup().selectTab("Point Metadata");
        dataTable = dataPanel.getTabGroup().getSelectedTab().getTable();
        
        // Data table with segment info
        dataPanel.getTabGroup().selectTab("Segment Metadata");
        segmentTable = dataPanel.getTabGroup().getSelectedTab().getTable();
        
        dataPanel.getTabGroup().selectTab("Data");
    }

    @Test
    public void testMetadataBrowser() throws Exception {
        
        // Nothing should be selected
        assertEquals("Metadata Browser (Select SED)", mbWindow.getTitle());
        
        // Load an sed into the workspace and ensure it shows up in the MB
        final ExtSed sed = sedManager.newSed("test1");
        sedManager.select(sed);

        // verify selected sed
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(mbView.getTitle(), mbWindow.getTitle());
                assertEquals(sed, mbView.selectedSed);
                assertEquals(0, mbView.sedStarTables.size());
                assertEquals(0, starTableList.getSize());
            }
        });
        
        // Add a segment to the selected sed
        final Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);

        // 1 segment should have been added to table and selected
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(1, mbView.sedStarTables.size());
                assertEquals(1, starTableList.getSize());
                assertEquals(1, segmentTable.getRowCount());

                assertEquals(1, Double.parseDouble((String) plotterTable.getContentAt(0, 2)), 0.1);
                assertEquals(2, Double.parseDouble((String) plotterTable.getContentAt(1, 2)), 0.1);
                assertEquals(3, Double.parseDouble((String) plotterTable.getContentAt(2, 2)), 0.1);
            }
        });
        
        // Add another segment
        final Segment seg2 = createSampleSegment(new double[] {100, 200}, new double[] {300,400});
        sed.addSegment(seg2);
        
        // 2 segments should have been added to table, first segment still selected
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(2, mbView.sedStarTables.size());
                assertEquals(2, starTableList.getSize());
                assertEquals(2, segmentTable.getRowCount());

                assertEquals(1, Double.parseDouble((String) plotterTable.getContentAt(0, 2)), 0.1);
                assertEquals(2, Double.parseDouble((String) plotterTable.getContentAt(1, 2)), 0.1);
                assertEquals(3, Double.parseDouble((String) plotterTable.getContentAt(2, 2)), 0.1);
                
                assertEquals(1, Double.parseDouble((String) dataTable.getContentAt(0, 1)), 0.1);
                assertEquals(2, Double.parseDouble((String) dataTable.getContentAt(1, 1)), 0.1);
                assertEquals(3, Double.parseDouble((String) dataTable.getContentAt(2, 1)), 0.1);
            }
        });
        
        // Select the second segment
        starTableList.clearSelection();
        starTableList.selectIndex(1);
        
        // 2 segments should have been added to table, first segment still selected
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(100, Double.parseDouble((String) plotterTable.getContentAt(0, 2)), 0.1);
                assertEquals(200, Double.parseDouble((String) plotterTable.getContentAt(1, 2)), 0.1);
                
                assertEquals(100, Double.parseDouble((String) dataTable.getContentAt(0, 1)), 0.1);
                assertEquals(200, Double.parseDouble((String) dataTable.getContentAt(1, 1)), 0.1);
            }
        });
        
        // Add a new sed to the workspace
        final ExtSed sed2 = sedManager.newSed("test2");
        sedManager.select(sed2);
        
        // Verify changes in the browser
        // 2 segments should have been added to table, first segment still selected
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(StringUtils.contains(mbWindow.getTitle(), sed2.getId()));
                assertEquals(0, plotterTable.getRowCount());
                assertEquals(0, dataTable.getRowCount());
                assertEquals(0, segmentTable.getRowCount());
            }
        });
        
        // Window changes on rename
        sedManager.rename(sed2, "purplemonkeydishwasher");
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(StringUtils.contains(mbWindow.getTitle(), sed2.getId()));
            }
        });
        
        // Delete SED, verify first SED selected
        sedManager.remove(sed2.getId());
        
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(StringUtils.contains(mbWindow.getTitle(), sed.getId()));
                assertEquals(2, mbView.sedStarTables.size());
                assertEquals(2, starTableList.getSize());
                assertEquals(2, segmentTable.getRowCount());

                assertEquals(1, Double.parseDouble((String) plotterTable.getContentAt(0, 2)), 0.1);
                assertEquals(2, Double.parseDouble((String) plotterTable.getContentAt(1, 2)), 0.1);
                assertEquals(3, Double.parseDouble((String) plotterTable.getContentAt(2, 2)), 0.1);
            }
        });
    }
    
    @Test
    public void testDataSelectionActions() throws Exception {
        
        final ExtSed sed = new ExtSed("sed");
        sed.addSegment(createSampleSegment());
        sed.addSegment(createSampleSegment(new double[] {1}, new double[] {2}));
        sedManager.add(sed);
        
        // verify selected sed
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(mbView.getTitle(), mbWindow.getTitle());
                assertEquals(sed, mbView.selectedSed);
                assertEquals(2, mbView.sedStarTables.size());
                assertEquals(2, starTableList.getSize());
                assertEquals(3, plotterTable.getRowCount());
                assertEquals(3, dataTable.getRowCount());
                assertEquals(2, segmentTable.getRowCount());
            }
        });
        
        // Select some rows in the plotter data table
        plotterTable.addRowToSelection(0);
        plotterTable.addRowToSelection(1);
        
        // dataTable row selection is independent
        dataTable.rowsAreSelected().check();
        
        // Select some rows the the dataTable
        dataTable.selectRow(2);
        dataTable.addRowToSelection(0);
        
        // Plotter data table should still have same rows selected
        plotterTable.rowsAreSelected(0,1).check();
        
        // Click select all button
        mbWindow.getButton("Select All").click();
        
        // Everything should be selected, nothing in the segment tab
        plotterTable.rowsAreSelected(0,1,2).check();
        segmentTable.rowsAreSelected(0).check();
        
        // Clear selections
        mbWindow.getButton("Clear Selection").click();
        
        // Verify selections are empty
        plotterTable.selectionIsEmpty().check();
        
        // select 0th index in tables
        plotterTable.selectRow(0);
        
        // Invert selection
        mbWindow.getButton("Invert Selection").click();
        
        // verify inversion
        plotterTable.rowsAreSelected(1,2).check();
        assertFalse(plotterTable.rowIsSelected(0).isTrue());
        
        // Set to segment tab
        mbWindow.getButton("Clear Selection").click();
        dataPanel.getTabGroup().selectTab("Segment Metadata");
        mbWindow.getButton("Clear Selection").click();
        segmentTable.selectionIsEmpty().check();
        
        mbWindow.getButton("Select All").click();
        segmentTable.rowsAreSelected(0,1).check();
        
        // Verify other tables still empty
        plotterTable.selectionIsEmpty().check();
        dataTable.selectionIsEmpty().check();
    }
    
    @Test
    public void testMetadataBrowserMasking() throws Exception {
        final ExtSed sed = new ExtSed("");//ExtSed.read(TestData.class.getResource("3c273.vot").openStream(), SedFormat.VOT);
        sed.addSegment(TestUtils.createSampleSegment());
        sedManager.add(sed);
        
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(mbView.getTitle(), mbWindow.getTitle());
                assertEquals(sed, mbView.selectedSed);
                assertEquals(1, mbView.sedStarTables.size());
                assertEquals(1, starTableList.getSize());
                assertEquals(3, plotterTable.getRowCount());
            }
        });
        
        // Apply a mask on the first and last rows
        final BitSet masked = new BitSet();
        masked.set(0);
        masked.set(2);
        
        dataPanel.getTabGroup().selectTab("Data");
        plotterTable.selectRows(0, 2);
        mbWindow.getButton("Apply Mask").click();
        
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                IrisStarTable table = mbView.selectedStarTables.get(0);
                assertEquals(masked, table.getMasked());
            }
        });
        
        // Apply a mask to all rows
        plotterTable.selectAllRows();
        masked.set(0, plotterTable.getRowCount());
        mbWindow.getButton("Apply Mask").click();
        
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                IrisStarTable table = mbView.selectedStarTables.get(0);
                assertEquals(masked, table.getMasked());
            }
        });
    }
    
    @Test
    public void testPlotterMetadataBrowserPointSelection() throws Exception {
        assertEquals("0E0", plView.getXcoord());
        assertEquals("0E0", plView.getYcoord());
        
        final ExtSed sed = new ExtSed("test1");
        final Segment seg1 = createSampleSegment(new double[] {100}, new double[] {1});
        sed.addSegment(seg1);
        final Segment seg2 = createSampleSegment(new double[] {200}, new double[] {2});
        sed.addSegment(seg2);
        
        sedManager.add(sed);
        
        invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                // two tables in selection, 1 selected, no rows selected
                assertEquals(2, mbView.getSelectedTables().size());
                assertEquals(1, mbView.getSelectedStarTables().size());
                assertEquals(1, dataTable.getRowCount());
                dataTable.selectionIsEmpty().check();
            }
        });
        
        // Selecting a point should add the first row to selection
        mbView.addRowToSelection(0, 0);
        
        invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                // first row should be selected, still just one star table
                plotterTable.rowsAreSelected(0).check();
                assertEquals(1, mbView.getSelectedStarTables().size());
            }
        });
        
        // Select the only point in the second star table
        mbView.addRowToSelection(1, 0);
        
        invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                plotterTable.rowsAreSelected(1).check();
                assertEquals(2, mbView.getSelectedStarTables().size());
            }
        });
    }
}
