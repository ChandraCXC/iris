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
import cfa.vo.iris.sed.quantities.SPVYUnit;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.sedlib.Segment;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static cfa.vo.iris.test.unit.TestUtils.*;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.plotter.StilPlotter;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import org.uispec4j.Window;
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;

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
        final VisualizerComponentPreferences prefs = comp.getPreferences();
        final VisualizerDataModel dataModel = prefs.getDataModel();

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
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed));
            }
        });


        // Test the plotter reacts to a segment event (although this only requires subscribing to SedEvents)
        final Segment segment = createSampleSegment();
        sed.addSegment(segment);

        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertNotNull(dataModel.getSedModel(sed).getSegmentModel(segment));
            }
        });

        // Just double checking this works more than once.
        final ExtSed sed2 = sedManager.newSed("oneMoreSed");
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed2));
            }
        });

        // Just double checking we can go back through select
        sedManager.select(sed);
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed));
            }
        });

    }
    
    @Test
    public void testPlotPreferencesGridAndPlotType() throws Exception {
        SedlibSedManager sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        final VisualizerComponentPreferences prefs = comp.getPreferences();
        final VisualizerDataModel dataModel = prefs.getDataModel();
        
        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(windowName)
                .getSubMenu(windowName)
                .click();
        
        Window viewer = desktop.getWindow(windowName);
        StilPlotter plotter = viewer.findSwingComponent(StilPlotter.class);
        
        final ExtSed sed1 = sedManager.newSed("sampleSed1");
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed1));
            }
        });
        
        // check the grid is on by default
        JMenuBar menu = viewer.findSwingComponent(JMenuBar.class, "menuBar");
        JCheckBoxMenuItem gridMenuItem = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(3);
        assertTrue(gridMenuItem.isSelected());
        
        // on by default. switch grids off
        viewer.getMenuBar()
                .getMenu("View")
                .getSubMenu("Grid on/off")
                .click();
        
        PlotPreferences preferences = plotter.getPlotPreferences();
        
        // check that box is unchecked
        assertTrue(!gridMenuItem.isSelected()); 
        assertFalse(preferences.getShowGrid());
        
        
        // add another SED
        final ExtSed sed2 = sedManager.newSed("sampleSed2");
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed2));
            }
        });
        
        // when a new SED is added, it should have the default plot preferences.
        // check that the Grid on/off checkbox is selected.
        gridMenuItem = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(3);
        assertTrue(gridMenuItem.isSelected());
        
        // switch sed2's plot type to xlog
        viewer.getMenuBar()
                .getMenu("View")
                .getSubMenu("Plot Type")
                .getSubMenu("X Log")
                .click();
        JMenu plotTypeMenu = (JMenu) menu.getMenu(2).getMenuComponent(0);
        JRadioButtonMenuItem xlog = (JRadioButtonMenuItem) plotTypeMenu.getItem(2);
        assertTrue(xlog.isSelected());
        
        // switch to sed1.
        sedManager.select(sed1);
        
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed1));
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
        
        plotTypeMenu = (JMenu) menu.getMenu(2).getMenuComponent(0);
        JRadioButtonMenuItem linear = (JRadioButtonMenuItem) plotTypeMenu.getItem(1);
        assertTrue(linear.isSelected());
        
        // go back to sed2
        sedManager.select(sed2);
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed2));
            }
        });
        
        // make sure it's still in xlog space
        xlog = (JRadioButtonMenuItem) plotTypeMenu.getItem(2);
        assertTrue(xlog.isSelected());
        assertTrue(!linear.isSelected());
        
    }
    
    @Test
    public void testPlotPreferencesFixed() throws Exception {
        SedlibSedManager sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        final VisualizerComponentPreferences prefs = comp.getPreferences();
        final VisualizerDataModel dataModel = prefs.getDataModel();
        
        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(windowName)
                .getSubMenu(windowName)
                .click();
        
        Window viewer = desktop.getWindow(windowName);
        StilPlotter plotter = viewer.findSwingComponent(StilPlotter.class);
        
        // create a new sed with a segment
        final Segment seg1 = createSampleSegment();
        final ExtSed sed1 = sedManager.newSed("sampleSed1");
        sed1.addSegment(seg1);
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed1));
            }
        });
        
        // get components: PlotPreferences and autoFixed menuCheckBox
        PlotPreferences preferences = plotter.getPlotPreferences();
        JMenuBar menu = viewer.findSwingComponent(JMenuBar.class, "menuBar");
        JCheckBoxMenuItem autoFixed = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(2);
        
        // check that box is unchecked (auto range viewport by default)
        assertFalse(autoFixed.isSelected());
        assertFalse(preferences.getFixed());
        
        // now, make it so the viewport is fixed
        viewer.getMenuBar()
                .getMenu("View")
                .getSubMenu("Fixed")
                .click();
        
        // get original bounds
        viewer.getButton("Reset").click();
        PlaneAspect aspect = plotter.getPlotDisplay().getAspect();
        double origXmin = aspect.getXMin();
        double origXmax = aspect.getXMax();
        double origYmin = aspect.getYMin();
        double origYmax = aspect.getYMax();
        
        // zoom in on the viewport
        double[] ylimits = new double[] {1.2, 2.8};
        double[] xlimits = new double[] {1.2, 2.8};
        PlaneAspect newAspect = new PlaneAspect(xlimits, ylimits);
        plotter.getPlotDisplay().setAspect(newAspect);
        aspect = plotter.getPlotDisplay().getAspect();
        double xmin = aspect.getXMin();
        double xmax = aspect.getXMax();
        double ymin = aspect.getYMin();
        double ymax = aspect.getYMax();
        
        // add another segment.
        sed1.addSegment(createSampleSegment());
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertEquals(2, dataModel.getLayerModels().size());
            }
        });
        
        // because the plot is fixed, 
        // the X and Y ranges should be the same as they were before
        newAspect = plotter.getPlotDisplay().getAspect();
        assertEquals(xmin, newAspect.getXMin(), 0.000001);
        assertEquals(xmax, newAspect.getXMax(), 0.000001);
        assertEquals(ymin, newAspect.getYMin(), 0.000001);
        assertEquals(ymax, newAspect.getYMax(), 0.000001);
        
        // check that clicking "Reset" resets the plot to the full plot range
        viewer.getButton("Reset").click();
        
        newAspect = plotter.getPlotDisplay().getAspect();
        assertEquals(origXmin, newAspect.getXMin(), 0.000001);
        assertEquals(origYmin, newAspect.getYMin(), 0.000001);
        assertEquals(origXmax, newAspect.getXMax(), 0.000001);
        assertEquals(origYmax, newAspect.getYMax(), 0.000001);
        
        // zoom in to another range
        ylimits = new double[] {1.2, 2.0};
        xlimits = new double[] {1.2, 2.0};
        newAspect = new PlaneAspect(xlimits, ylimits);
        plotter.getPlotDisplay().setAspect(newAspect);
        xmin = newAspect.getXMin();
        xmax = newAspect.getXMax();
        ymin = newAspect.getYMin();
        ymax = newAspect.getYMax();
        
        // create a new sed and switch to it
        final Segment seg2 = createSampleSegment();
        final ExtSed sed2 = sedManager.newSed("sampleSed2");
        sed2.addSegment(seg2);
        
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed2));
                assertNotNull(dataModel.getSedModel(sed2).getSegmentModel(seg2));
            }
        });
        
        // check that the fixed check box is unmarked (default value)
        autoFixed = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(2);
        assertFalse(autoFixed.isSelected());
        
        // assert that the plot range is for sed2, which should be the default
        // range of sed1 since it's the same data.
        newAspect = plotter.getPlotDisplay().getAspect();
        assertEquals(origXmin, newAspect.getXMin(), 0.000001);
        assertEquals(origYmin, newAspect.getYMin(), 0.000001);
        assertEquals(origXmax, newAspect.getXMax(), 0.000001);
        assertEquals(origYmax, newAspect.getYMax(), 0.000001);
        
        // now switch back to sed1
        sedManager.select(sed1);
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed1));
            }
        });
        
        // check that the fixed check box is marked since we're on sed1 now
        autoFixed = (JCheckBoxMenuItem) menu.getMenu(2).getMenuComponent(2);
        assertTrue(autoFixed.isSelected());
        
        // make sure the view port is the same as it was before switching
        // to sed2
        // TODO: get these tests to pass. Right now, newAspect does not
        // update correctly. Its values are as if one clicked the "Reset" button
        // However, in building and testing Iris by hand, I get the expected
        // behavior.
        newAspect = plotter.getPlotDisplay().getAspect();
        assertEquals(xlimits[0], newAspect.getXMin(), 0.000001);
        assertEquals(xlimits[1], newAspect.getXMax(), 0.000001);
        assertEquals(ylimits[0], newAspect.getYMin(), 0.000001);
        assertEquals(ylimits[1], newAspect.getYMax(), 0.000001);
        
    }
    
    @Test
    public void testChangeUnits() throws Exception {
        SedlibSedManager sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        final VisualizerComponentPreferences prefs = comp.getPreferences();
        final VisualizerDataModel dataModel = prefs.getDataModel();
        
        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(windowName)
                .getSubMenu(windowName)
                .click();
        
        Window viewer = desktop.getWindow(windowName);
        final StilPlotter plotter = viewer.findSwingComponent(StilPlotter.class);
        
        // test that no exceptions occur if the user tries to 
        // change the units of the plotter with no SED loaded in yet.
        viewer.getButton("unitsButton").click();
        assertTrue(desktop.containsWindow("Select Units").isTrue());
        Window unitsChooser = desktop.getWindow("Select Units");
        
        unitsChooser.getListBox("xunitsList").select(XUnit.ANGSTROM.getString());
        unitsChooser.getListBox("yunitsList").select(SPVYUnit.FLUX0.getString());
        unitsChooser.getButton("Update").click();
        
        // create an SED and add it to Swing EDT
        final ExtSed sed0 = sedManager.newSed("sampleSed1");
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed0));
            }
        });
        
        // test that changing the units on an empty SED doesn't throw an
        // exception.
        unitsChooser.getButton("Update").click();

        // Reload units chooser
        assertFalse(desktop.containsWindow("Select Units").isTrue());
        viewer.getButton("unitsButton").click();
        assertTrue(desktop.containsWindow("Select Units").isTrue());
        unitsChooser = desktop.getWindow("Select Units");
        
        // create 2 segments with different units
        final Segment seg1 = createSampleSegment();
        seg1.setSpectralAxisUnits("Hz");
        seg1.setFluxAxisUnits("Jy");
        
        final Segment seg2 = createSampleSegment();
        seg2.setSpectralAxisUnits("Angstrom");
        seg2.setFluxAxisUnits("erg/s/cm^2/A");
        
        // add the segments to a new SED
        final ExtSed sed1 = sedManager.newSed("sampleSed1");
        sed1.addSegment(seg1);
        sed1.addSegment(seg2);
        
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed1));
            }
        });
        
        // check that the plot window has the right axes labels
        // should be the same units as the first loaded SED
        assertEquals("Frequency (Hz)", plotter.getPlotPreferences().getXLabel());
        assertEquals("Flux density (Jy)", plotter.getPlotPreferences().getYLabel());
        
        // change the units of the SEDs with the UnitsManagerFrame
        unitsChooser.getListBox("xunitsList").select(XUnit.ANGSTROM.getString());
        unitsChooser.getListBox("yunitsList").select(SPVYUnit.FLUX0.getString());
        unitsChooser.getButton("Update").click();
        
        // check that the X and Y units on the plot window updated
        assertEquals("Wavelength (Angstrom)", plotter.getPlotPreferences().getXLabel());
        assertEquals('\u03BD' + "F(" + '\u03BD' + ")"+" (Jy-Hz)", 
                plotter.getPlotPreferences().getYLabel());
        
        // check that the MB data updated
        
        final ExtSed sed2 = sedManager.newSed("sed2");
        sed2.addSegment(seg1);
        
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed2));
            }
        });
        
        // test velocity units
        unitsChooser.getListBox("xunitsList").select(XUnit.KMPSCO.getString());
        unitsChooser.getListBox("yunitsList").select(SPVYUnit.FLUXDENSITYFREQ3.getString());
        unitsChooser.getButton("Update").click();
        
        // check that the velocity unit label set properly
        assertEquals("Velocity (km/s @ 12 CO (11.5GHz))", 
                plotter.getPlotPreferences().getXLabel());
        
        // open units window, switch SEDs, choose new units, apply them, and
        // assert that the selected SED units are changed, and not the SED
        // on which the units window was opened. The units "Update" button
        // should always affect the currently selected SED.
        unitsChooser.getListBox("xunitsList").select(XUnit.EV.getString());
        unitsChooser.getListBox("yunitsList").select(SPVYUnit.FLUXDENSITYFREQ1.getString());
        
        sedManager.select(sed1);
        
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed1));
            }
        });
        
        // sed1 units should still be the same as they were before changing sed2
        // units
        assertEquals("Wavelength (Angstrom)", plotter.getPlotPreferences().getXLabel());
        assertEquals('\u03BD' + "F(" + '\u03BD' + ")"+" (Jy-Hz)", 
                plotter.getPlotPreferences().getYLabel());
        
        // update the units for sed1
        unitsChooser.getButton("Update").click();
        
        // sed1 units should update
        assertEquals("Energy (eV)", plotter.getPlotPreferences().getXLabel());
        assertEquals("Flux density (Jy)",
                plotter.getPlotPreferences().getYLabel());

        sedManager.select(sed2);
        
        // Make sure this is enqueued in the Swing EDT
        invokeWithRetry(20, 100, new Runnable() {
            @Override
            public void run() {
                assertTrue(dataModel.getSelectedSeds().contains(sed2));
            }
        });
        
        // check that sed2 units remain the same
        assertEquals("Velocity (km/s @ 12 CO (11.5GHz))",
                plotter.getPlotPreferences().getXLabel());
        assertEquals("Flux density (mJy)",
                plotter.getPlotPreferences().getYLabel());
        
        // TODO: how to test for magnitudes? The Y-axis should be flipped so 
        // that larger values are on the bottom and lower values on the top
        
    }
}
