package cfa.vo.iris.visualizer.plotter;

import static cfa.vo.iris.test.unit.TestUtils.invokeWithRetry;
import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.CheckBox;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;
import org.uispec4j.Tree;
import org.uispec4j.Window;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.VisualizerComponent;
import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import cfa.vo.iris.visualizer.metadata.StarTableJTree;
import cfa.vo.iris.visualizer.plotter.PlotterView;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;

public class CoPlottingTest extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String plWindowName = comp.getName();
    private SedlibSedManager sedManager;
    
    private Window mbWindow; // metadata window
    private Window plWindow; // plotter window
    private Panel dataPanel;
    private Tree tablesTree;
    
    private MetadataBrowserMainView mbView;
    private PlotterView plView;
    StarTableJTree starTableJTree;
    
    @Before
    public void setupMbTest() throws Exception {
        sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        
        // Get plot window
        window.getMenuBar().getMenu("Tools").getSubMenu(plWindowName).getSubMenu(plWindowName).click();

        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                desktop.containsWindow(plWindowName).check();
            }
        });
        
        plWindow = desktop.getWindow(plWindowName);
        org.uispec4j.Button mbButton = plWindow.getButton("Metadata");
        mbButton.click();
        
        plView = comp.getDefaultPlotterView();
        mbView = plView.getMetadataBrowserView();
        
        desktop.containsWindow(mbView.getTitle()).check();
        mbWindow = desktop.getWindow(mbView.getTitle());
        dataPanel = mbWindow.getPanel("contentPane");
        tablesTree = dataPanel.getTree();
    }
    
    @Override
    protected IrisComponent getComponent() {
        return comp;
    }
    
    @Test
    public void TestCoplotting() throws Exception {
        
        final VisualizerComponentPreferences prefs = comp.getPreferences();
        
        final ExtSed sed1 = new ExtSed("test1");
        sed1.addSegment(TestUtils.createSampleSegment(new double[] {1}, new double[] {1}));
        sed1.addSegment(TestUtils.createSampleSegment(new double[] {2}, new double[] {2}));

        final ExtSed sed2 = new ExtSed("test2");
        sed2.addSegment(TestUtils.createSampleSegment(new double[] {3}, new double[] {3}));
        sed2.addSegment(TestUtils.createSampleSegment(new double[] {4}, new double[] {4}));
        
        sedManager.add(sed1);
        sedManager.add(sed2);
        
        // verify selected sed
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(mbView.getDataModel().getSelectedSeds().contains(sed2));
                assertEquals(2, mbView.getDataModel().getSedStarTables().size());
                assertEquals(2, prefs.getAvailableSeds().size());
            }
        });
        
        // Open the Co-Plotting window
        plWindow.getMenuBar().getMenu("View").getSubMenu("CoPlot...").click();
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                desktop.containsWindow("Select Seds").check();
            }
        });
        Window cpWindow = desktop.getWindow("Select Seds");
        
        // Should be bound to workspace at start
        CheckBox box = cpWindow.getCheckBox("Enable Co-Plotting");
        assertFalse(box.isSelected().isTrue());
        
        // list box should not be enabled
        ListBox sedList = cpWindow.getPanel("contentPane").getListBox();
        assertFalse(sedList.isEnabled().isTrue());
        
        // Enable box
        box.select();
        assertTrue(cpWindow.getListBox().isEnabled().isTrue());
        
        // Applying changes without selected SEDs will clear plotter
        cpWindow.getButton("Apply Changes").click();

        // verify selected sed
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(mbView.getDataModel().getSelectedSeds().isEmpty());
                assertEquals(0, mbView.getDataModel().getSedStarTables().size());
                assertEquals(2, prefs.getAvailableSeds().size());
            }
        });
        
        // Reopen the Co-Plotting window
        plWindow.getMenuBar().getMenu("View").getSubMenu("CoPlot...").click();
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                desktop.containsWindow("Select Seds").check();
            }
        });
        cpWindow = desktop.getWindow("Select Seds");
        box = cpWindow.getCheckBox("Enable Co-Plotting");
        sedList = cpWindow.getPanel("contentPane").getListBox();
        
        // Checkbox should now be selected
        assertTrue(box.isSelected().isTrue());
        assertTrue(sedList.isEnabled().isTrue());
        
        // Verify list contents
        sedList.contains(sed1.getId()).check();
        sedList.contains(sed2.getId()).check();
        assertEquals(2, sedList.getSize());
        
        // Select both SEDs
        sedList.selectIndices(0, 1);
        
        // Plot 2 seds
        cpWindow.getButton("Apply Changes").click();

        // verify multiple seds in datamodel
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(2, prefs.getDataModel().getSelectedSeds().size());
                assertEquals(4, mbView.getDataModel().getSedStarTables().size());
                assertTrue(StringUtils.contains(mbWindow.getTitle(), sed1.getId()));
                assertTrue(StringUtils.contains(mbWindow.getTitle(), sed2.getId()));
                tablesTree.contains(sed1.getId());
                tablesTree.contains(sed2.getId());
            }
        });
    }
}
