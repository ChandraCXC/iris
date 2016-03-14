/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.visualizer;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.IrisComponent;
import cfa.vo.sedlib.Segment;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static cfa.vo.iris.test.unit.TestUtils.*;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.stil.StilPlotter;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.uispec4j.MenuItem;
import org.uispec4j.Window;

public class VisualizerComponentTest extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String windowName;

    @Before
    public void setUp() throws Exception {
        windowName = comp.getName();
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    @Test
    public void testVisualizerStub() throws Exception {
        
        window.getMenuBar()
            .getMenu("Tools")
            .getSubMenu(windowName)
            .getSubMenu(windowName)
            .click();
        
        assertTrue(desktop.containsWindow(windowName).isTrue());
    }

    @Test
    public void testEventSubscription() throws Exception {
        SedlibSedManager sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();

        // No view when starting application up
        assertNull(comp.getDefaultPlotterView());

        // When the viewer button is clicked, the displayed SED should be the one selected in the SedManager
        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(windowName)
                .getSubMenu(windowName)
                .click();
        ExtSed initialSed = sedManager.getSelected();
        assertNull(initialSed);

        // Test the plotter reacts to a sed event
        final ExtSed sed = sedManager.newSed("sampleSed");
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertSame(sed, comp.getDefaultPlotterView().getSed());
            }
        });


        // Test the plotter reacts to a segment event (although this only requires subscribing to SedEvents)
        final Segment segment = createSampleSegment();
        sed.addSegment(segment);

        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(comp.getDefaultPlotterView().getSegmentsMap().containsKey(segment));
            }
        });

        // Just double checking this works more than once.
        final ExtSed sed2 = sedManager.newSed("oneMoreSed");
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertSame(sed2, comp.getDefaultPlotterView().getSed());
            }
        });

        // Just double checking we can go back through select
        sedManager.select(sed);
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertSame(sed, comp.getDefaultPlotterView().getSed());
            }
        });


//        assertEquals("sampleSed", comp.getDefaultPlotterView().getLegend().getTitle());
    }
    
    @Test
    public void testPlotPreferences() throws Exception {
        SedlibSedManager sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        
        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(windowName)
                .getSubMenu(windowName)
                .click();
        
        Window viewer = desktop.getWindow(windowName);
        StilPlotter plotter = viewer.findSwingComponent(StilPlotter.class);
        
        final ExtSed sed1 = sedManager.newSed("sampleSed1");
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertSame(sed1, comp.getDefaultPlotterView().getSed());
            }
        });
        
        // on by default. switch grids off
        viewer.getMenuBar()
                .getMenu("View")
                .getSubMenu("Grid on/off")
                .click();
        
        PlotPreferences prefs = plotter.getVisualizerPreferences().getSelectedSedPreferences().getPlotPreferences();
        JMenuBar menu = viewer.findSwingComponent(JMenuBar.class, "menuBar");
        JCheckBoxMenuItem gridMenuItem = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(3);
        
        // check that box is unchecked
        assertTrue(!gridMenuItem.isSelected()); 
        assertFalse(prefs.getShowGrid());
        
        
        // add another SED
        final ExtSed sed2 = sedManager.newSed("sampleSed2");
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertSame(sed2, comp.getDefaultPlotterView().getSed());
            }
        });
        
        // when a new SED is added, it should have the default plot preferences.
        // check that the Grid on/off checkbox is selected.
        gridMenuItem = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(3);
        assertTrue(gridMenuItem.isSelected());
        
        // switch to sed1.
        sedManager.select(sed1);
        
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertSame(sed1, comp.getDefaultPlotterView().getSed());
            }
        });
        
        // grid check box should still be unselected.
        gridMenuItem = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(3);
        assertTrue(!gridMenuItem.isSelected());
        
        // now, switch sed1 to linear space
        viewer.getMenuBar()
                .getMenu("View")
                .getSubMenu("Plot Type")
                .getSubMenu("Linear")
                .click();
        
        JMenu plotTypeMenu = (JMenu) menu.getMenu(2).getMenuComponent(0);
        JRadioButtonMenuItem linear = (JRadioButtonMenuItem) plotTypeMenu.getItem(1);
        assertTrue(linear.isSelected());
        
        // go back to sed2
        sedManager.select(sed2);
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertSame(sed2, comp.getDefaultPlotterView().getSed());
            }
        });
        
        // make sure it's still in log space
        JRadioButtonMenuItem log = (JRadioButtonMenuItem) plotTypeMenu.getItem(0);
        assertTrue(log.isSelected());
        assertTrue(!linear.isSelected());
        
    }
}
