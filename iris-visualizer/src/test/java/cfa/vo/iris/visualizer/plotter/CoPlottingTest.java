/*
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
package cfa.vo.iris.visualizer.plotter;

import static cfa.vo.iris.test.unit.TestUtils.invokeWithRetry;
import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;
import org.uispec4j.Tree;
import org.uispec4j.Window;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import cfa.vo.iris.test.unit.StubWorkspace;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import cfa.vo.iris.visualizer.metadata.StarTableJTree;
import cfa.vo.iris.visualizer.plotter.PlotterView;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import cfa.vo.iris.visualizer.preferences.VisualizerDataStore;

public class CoPlottingTest extends AbstractUISpecTest {

    private IWorkspace ws;
    private VisualizerComponentPreferences prefs;
    private VisualizerDataStore dataStore;
    private VisualizerDataModel dataModel;
    
    private Window mbWindow; // metadata window
    private Window plWindow; // plotter window
    private Panel dataPanel;
    private Tree tablesTree;
    
    private MetadataBrowserMainView mbView;
    private PlotterView plView;
    StarTableJTree starTableJTree;
    
    @Before
    public void setupMbTest() throws Exception {
        ws = new StubWorkspace();
        prefs = new VisualizerComponentPreferences(ws);
        dataStore = prefs.getDataStore();
        dataModel = prefs.getDataModel();
        
        // Create windows
        plView = new PlotterView(null, ws, prefs);
        mbView = plView.getMetadataBrowserView();

        plWindow = new Window(plView);
        
        org.uispec4j.Button mbButton = plWindow.getButton("Metadata");
        mbButton.click();
        mbWindow = new Window(mbView);
        
        dataPanel = mbWindow.getPanel("contentPane");
        tablesTree = dataPanel.getTree();
    }
    
    @Test
    public void TestCoplotting() throws Exception {
        
        final ExtSed sed1 = (ExtSed) ws.getSedManager().newSed("test1");
        sed1.addSegment(TestUtils.createSampleSegment(new double[] {1}, new double[] {1}));
        sed1.addSegment(TestUtils.createSampleSegment(new double[] {2}, new double[] {2}));

        final ExtSed sed2 = (ExtSed) ws.getSedManager().newSed("test2");
        sed2.addSegment(TestUtils.createSampleSegment(new double[] {3}, new double[] {3}));
        sed2.addSegment(TestUtils.createSampleSegment(new double[] {4}, new double[] {4}));
        
        dataStore.update(sed1);
        dataStore.update(sed2);
        dataModel.setSelectedSed(sed2);
        
        // verify selected sed
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(mbView.getDataModel().getSelectedSeds().contains(sed2));
                assertEquals(2, mbView.getDataModel().getSedStarTables().size());
                assertEquals(2, dataStore.getSedModels().size());
            }
        });
        
        // Open the Co-Plotting window
        plWindow.getMenuBar().getMenu("View").getSubMenu("CoPlot...").click();
        Window cpWindow = new Window(plView.coplotWindow);
        
        // list box should be enabled
        ListBox sedList = cpWindow.getPanel("contentPane").getListBox();
        assertTrue(sedList.isEnabled().isTrue());
        
        // Applying changes without selected SEDs will clear plotter
        cpWindow.getButton("Plot Seds").click();

        // verify selected sed
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(mbView.getDataModel().getSelectedSeds().isEmpty());
                assertEquals(0, mbView.getDataModel().getSedStarTables().size());
                assertEquals(2, dataStore.getSedModels().size());
            }
        });
        
        // Reopen the Co-Plotting window
        plWindow.getMenuBar().getMenu("View").getSubMenu("CoPlot...").click();
        cpWindow = new Window(plView.coplotWindow);
        sedList = cpWindow.getPanel("contentPane").getListBox();
        
        // Verify list contents
        sedList.contains(sed1.getId()).check();
        sedList.contains(sed2.getId()).check();
        assertEquals(2, sedList.getSize());
        
        // Select both SEDs
        sedList.selectIndices(0, 1);
        
        // Plot 2 seds
        cpWindow.getButton("Plot Seds").click();

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
