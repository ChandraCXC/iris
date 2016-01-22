package cfa.vo.iris.visualizer.metadata;

import org.junit.Before;
import org.junit.Test;
import org.uispec4j.ListBox;
import org.uispec4j.Table;
import org.uispec4j.Window;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.visualizer.VisualizerComponent;
import cfa.vo.sedlib.Segment;
import static org.junit.Assert.*;

import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class MetadataBrowserViewTest extends AbstractComponentGUITest {

    private VisualizerComponent comp = new VisualizerComponent();
    private String plWindowName = comp.getName();
    private String mbWindowName = MetadataBrowserView.MB_WINDOW_TITLE;

    private MetadataBrowserView mbView;
    private SedlibSedManager sedManager;
    private Window mbWindow;

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    @Before
    public void setupMbTest() {
        sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        
        window.getMenuBar().getMenu("Tools").getSubMenu(plWindowName).getSubMenu(plWindowName).click();
        
        org.uispec4j.Button mbButton = desktop.getWindow(plWindowName).getButton("Metadata");
        mbButton.click();
        
        desktop.containsWindow(mbWindowName).check();
        mbWindow = desktop.getWindow(mbWindowName);
        
        mbView = comp.getDefaultPlotterView().getMetadataBrowserView();
    }

    @Test
    public void testMetadataBrowser() throws Exception {
        
        // Load an sed into the workspace and ensure it shows up in the MB
        final ExtSed sed = sedManager.newSed("test1");
        sedManager.select(sed);
        mbView.reset();
        
        // Verify the window title matches the selected SED id
        TitledBorder sedTitle = (TitledBorder) mbView.segmentListScrollPane.getBorder();
        assertEquals(sed.getId(), sedTitle.getTitle());
        
        // Ensure these are initialized prior to grabbing the two tables
        final Object[] tables = new Object[2];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                tables[0] = mbWindow.getPanel("content").getPanel("segmentListScrollPane").getListBox("selectedTables");
                tables[1] = mbWindow.getPanel("content").getPanel("segmentDataScrollPane").getTable("metadataTable");
            }
        });
        final ListBox segmentTable = (ListBox) tables[0];
        final Table metadataTable = (Table) tables[1];

        // Nothing should be selected
        assertEquals(0, metadataTable.getRowCount());
        assertEquals(0, mbView.selectedTables.getModel().getSize());
        assertEquals(0, segmentTable.getSize());

        // Add a segment to the selected sed
        final Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);
        mbView.reset();
        
        // 1 segment should have been added to table
        assertEquals(1, mbView.selectedTables.getModel().getSize());
        assertEquals(3, metadataTable.getRowCount());
        assertEquals(1, Double.parseDouble((String) metadataTable.getContentAt(0, 0)), .1);

        // Add another segment
        double x[] = new double[] { 100, 200 };
        double y[] = new double[] { 300, 400 };
        final Segment seg2 = createSampleSegment(x, y);
        sed.addSegment(seg2);
        mbView.reset();
        
        // Verify there are two tables
        assertEquals(2, mbView.selectedTables.getModel().getSize());
        
        // First segment should still be selected 
        assertEquals(3, metadataTable.getRowCount());
        assertEquals(1.0, mbView.selectedStarTable.getCell(0, 0));
        assertEquals(1, Double.parseDouble((String) metadataTable.getContentAt(0, 0)), .1);
        
        // Set selected segment to seg2 by clicking on segment list
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                segmentTable.clearSelection();
                segmentTable.click(1);
            }
        });
        
        // Second segment should be selected, verify table and value
        assertEquals(100.0, mbView.selectedStarTable.getCell(0, 0));
        assertEquals(2, metadataTable.getRowCount());
        
        // Add a new SED
        final ExtSed sed2 = sedManager.newSed("test2");
        sedManager.select(sed2);
        mbView.reset();
        
        // Verify the title has changed
        sedTitle = (TitledBorder) mbView.segmentListScrollPane.getBorder();
        assertEquals(sed2.getId(), sedTitle.getTitle());
        
        // Verify the metadata and segment tables have cleared
        assertEquals(0, metadataTable.getRowCount());
        assertEquals(0, segmentTable.getSize());
    }
}
