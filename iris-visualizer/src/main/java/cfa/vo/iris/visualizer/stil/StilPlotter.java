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
package cfa.vo.iris.visualizer.stil;

import javax.swing.JPanel;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.preferences.SedPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.sedlib.Segment;
import uk.ac.starlink.ttools.plot2.task.PlanePlot2Task;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.ttools.plot.PlotState;
import uk.ac.starlink.ttools.plot.SurfacePlot;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurface;

public class StilPlotter extends JPanel {
    
    private static final Logger logger = Logger.getLogger(StilPlotter.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    private PlotDisplay display;
    
    private IWorkspace ws;
    private SedlibSedManager sedManager;
    private ExtSed currentSed;
    private final VisualizerComponentPreferences preferences;
    
    private MapEnvironment env;
    
    public StilPlotter(IWorkspace ws, VisualizerComponentPreferences preferences) {
        this.ws = ws;
        this.sedManager = (SedlibSedManager) ws.getSedManager();
        this.preferences = preferences;
        
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(1, 0, 0, 0));
        
        reset(null, true);
    }
    
    /**
     * 
     * @param sed Sed to reset plot to
     * @param dataMayChange indicates if the data on the plot may change while
     * while the current display is active.
     */
    public void reset(ExtSed sed, boolean dataMayChange) {
        setupForPlotDisplayChange();
        
        try {
            // https://github.com/Starlink/starjava/blob/master/ttools/src/main/
            // uk/ac/starlink/ttools/example/EnvPlanePlotter.java
            boolean cached =! dataMayChange;
            display = createPlotComponent(sed, cached);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        updatePlotDisplay();
    }
    
    /**
     * Sets both axes to log or linear space.
     * @param arg set axes to logarithmic space if "true," and linear space
     * if "false."
     */
    public void setLogAxes(boolean arg) {
        setupForPlotDisplayChange();
        
        try {
            preferences.getPlotPreferences().setXlog(arg);
            preferences.getPlotPreferences().setYlog(arg);
            env.setValue(PlotPreferences.X_LOG, arg);
            env.setValue(PlotPreferences.Y_LOG, arg);
            display = createPlotComponent(env, false);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        updatePlotDisplay();
        
        // what I would like to do is get the SurfacePlot from the display
        // so that we have a "live" plot with a state.
        // PlaneSurface surface = (PlaneSurface) display.getSurface();
//        SurfacePlot surface = (SurfacePlot) display.getSurface();
//        surface.getSurface()..setLogFlags(new boolean[] {x, y});
//        plott.setState(state);
//        display.revalidate();
//        display.repaint();
    }
    
    /**
     * Sets the specified axis to logarithmic space. This will switch the
     * unspecified axis to linear space. If you want to make the X and Y axes'
     * spacing independent of each other, use 
     * setLogAxis(String axis, boolean log).
     * @param axis the specified axis to convert to logarithmic space. 
     */
    public void setLogAxis(String axis) {
        
        try {
            switch (axis) {
                
                case PlotPreferences.X_LOG:
                    
                    // if plot axes are in the right spacing, return.
                    if (preferences.getPlotPreferences().getXlog() &&
                            !preferences.getPlotPreferences().getYlog()) {
                        return;
                    }
                    
                    preferences.getPlotPreferences().setXlog(true);
                    env.setValue(PlotPreferences.X_LOG, true);
                    preferences.getPlotPreferences().setYlog(false);
                    env.setValue(PlotPreferences.Y_LOG, false);
                    break;
                
                case PlotPreferences.Y_LOG:
                    
                    // if plot axes are in the right spacing, return.
                    if (preferences.getPlotPreferences().getYlog() &&
                            !preferences.getPlotPreferences().getXlog()) {
                        return;
                    }
                    
                    preferences.getPlotPreferences().setYlog(true);
                    env.setValue(PlotPreferences.Y_LOG, true);
                    preferences.getPlotPreferences().setXlog(false);
                    env.setValue(PlotPreferences.X_LOG, false);
                    break;
                
                default:
                    throw new IOException("Invalid axis specified. "
                            + "Must be a PlotPreferences enum.");
            }
            
            // don't move this. If the function returns before the preferences 
            // are updated, then the display will remain null.
            setupForPlotDisplayChange();
            
            display = createPlotComponent(env, false);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        updatePlotDisplay();
    }
    
    /**
     * Sets the specified axis to logarithmic or linear space. Good to use if 
     * you make the X and Y axis spacing independent from each other. If you 
     * want to make only the X or Y axis logarithmic, use 
     * setLogAxes(String axis, boolean log).
     * @param axis the specified axis. 
     * @param log set axis to logarithmic space if "true," and linear space
     * if "false."
     */
    public void setLogAxis(String axis, boolean log) {
        
        try {
            switch (axis) {
                
                case PlotPreferences.X_LOG:
                    preferences.getPlotPreferences().setXlog(log);
                    env.setValue(PlotPreferences.X_LOG, log);
                    break;
                
                case PlotPreferences.Y_LOG:
                    preferences.getPlotPreferences().setYlog(log);
                    env.setValue(PlotPreferences.Y_LOG, log);
                    break;
                
                default:
                    throw new IOException("Invalid axis specified. "
                            + "Must be a PlotPreferences enum.");
            }
            
            // don't move this. If the exception is thrown before the 
            // preferences are updated, then the display will remain null.
            setupForPlotDisplayChange();
            
            display = createPlotComponent(env, false);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        updatePlotDisplay();
    }
    
    public void setGridOn(boolean on) {
        setupForPlotDisplayChange();
        
        try {
            preferences.getPlotPreferences().setShowGrid(on);
            env.setValue(PlotPreferences.GRID, on);
            display = createPlotComponent(env, false);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        updatePlotDisplay();
    }
    
    /**
     * Fix the plot viewport, or let the viewport automatically resize itself
     * when updated. Zooming and panning are disabled if the viewport is fixed.
     * @param arg set to "true" to fix the viewport, "false" to let it resize
     * with updates.
     */
    public void setFixed(boolean arg) {
        
        throw new UnsupportedOperationException("Fixed viewport not yet implemented.");
        
//        try {
//            double xmin;
//            double xmax;
//            double ymin;
//            double ymax;
//            if (arg) {
//                xmin = display.get;
//                xmax = display.getSurface().getPlotBounds().getMaxX();
//                ymin = display.getSurface().getPlotBounds().getMinY();
//                ymax = display.getSurface().getPlotBounds().getMaxY();
//            } else {
//                xmin = 0;
//                xmax = 1;
//                ymin = 0;
//                ymax = 1;
//            }
//            preferences.getPlotPreferences().setFixed(arg);
//            preferences.getPlotPreferences().setXmax(xmax);
//            preferences.getPlotPreferences().setYmax(ymax);
//            preferences.getPlotPreferences().setXmin(xmin);
//            preferences.getPlotPreferences().setYmin(ymin);
//            env.setValue(PlotPreferences.NAV_AXES_XY, arg);
//            
//            setupForPlotDisplayChange();
//            
//            display = createPlotComponent(env, false);
//        } catch (RuntimeException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        
//        updatePlotDisplay();
    }
    
    public ExtSed getSed() {
        return currentSed;
    }

    public Map<Segment, SegmentLayer> getSegmentsMap() {
        return Collections.unmodifiableMap(preferences.getSelectedSedPreferences().getAllSegmentPreferences());
    }
    
    public PlotDisplay getPlotDisplay() {
        return display;
    }

    /**
     * Get the value of env
     *
     * @return the value of env
     */
    protected MapEnvironment getEnv() {
        return env;
    }

    /**
     * 
     * @param env Plot display environment to use 
     * @param cached If true, cache the environment. Should be false if the data
     * might change.
     * @return PlotDisplay
     * @throws Exception 
     */
    protected PlotDisplay createPlotComponent(MapEnvironment env, boolean cached) throws Exception {
        
        logger.log(Level.FINE, "plot environment:");
        logger.log(Level.FINE, ReflectionToStringBuilder.toString(env));
        
        return new PlanePlot2Task().createPlotComponent(env, cached);
    }
    
    /**
     * 
     * @param sed the SED to plot
     * @param cached If true, cache the environment. Should be false if the data
     * might change.
     * @return
     * @throws Exception 
     */
    protected PlotDisplay createPlotComponent(ExtSed sed, boolean cached) throws Exception {
        logger.info("Creating new plot from selected SED");

        if (sed == null) {
            sed = sedManager.getSelected();
        }
        this.currentSed = sed;
        
        env = new MapEnvironment();
        env.setValue("type", "plot2plane");
        env.setValue("insets", new Insets(50, 80, 50, 50)); 
        // TODO: force numbers on Y axis to only be 3-5 digits long. Keeps
        // Y-label from falling off the jpanel. Conversely, don't set "insets"
        // and let the plotter dynamically change size to keep axes labels
        // on the plot.
        
        // Add high level plot preferences
        PlotPreferences pp = preferences.getPlotPreferences();
        for (String key : pp.getPreferences().keySet()) {
            env.setValue(key, pp.getPreferences().get(key));
        }
        
        if (sed != null) {
            // set title of plot if available
            env.setValue("title", sed.getId());
            
            // Add segments and segment preferences
            addSegmentLayers(sed, env);
        }
        
        return createPlotComponent(env, cached);
    }
    
    private void addSegmentLayers(ExtSed sed, MapEnvironment env) throws IOException {
        if (sed == null) {
            logger.info("No SED selected, returning empty plot");
            return;
        }
        
        logger.info(String.format("Plotting SED with %s segments...", sed.getNamespace()));
        
        SedPreferences prefs = preferences.getSedPreferences(sed);
        for (int i=0; i<sed.getNumberOfSegments(); i++) {
            SegmentLayer layer = prefs.getSegmentPreferences(sed.getSegment(i));
            for (String key : layer.getPreferences().keySet()) {
                env.setValue(key, layer.getPreferences().get(key));
            }
        }
    }
    
    /**
     * Sets PlotDisplay up for new changes, like changing a SED color,
     * switching from linear to logarithmic plotting, etc.
     */
    private void setupForPlotDisplayChange() {
        if (display != null) {
            display.removeAll();
            remove(display);
            display = null; // just to be safe
        }
    }
    
    /**
     * Add and update the PlotDisplay.
     */
    private void updatePlotDisplay() {
        add(display, BorderLayout.CENTER);
        display.revalidate();
        display.repaint();
    }
}