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
package cfa.vo.iris.visualizer.plotter;

import javax.swing.JPanel;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.quantities.SPVYQuantity;
import cfa.vo.iris.visualizer.preferences.SegmentModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurfaceFactory;
import uk.ac.starlink.ttools.plot2.task.PlanePlot2Task;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.border.BevelBorder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.ttools.plot2.Axis;

public class StilPlotter extends JPanel {

    private static final Logger logger = Logger
            .getLogger(StilPlotter.class.getName());

    private static final long serialVersionUID = 1L;

    private PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> display;

    private List<ExtSed> seds = new ArrayList<>();
    private VisualizerComponentPreferences preferences;
    private VisualizerDataModel dataModel;
    
    private MapEnvironment env;
    
    // Needs a default constructor for Netbeans
    public StilPlotter() {
    }
    
    public StilPlotter(VisualizerComponentPreferences preferences) {
        this.setPreferences(preferences);
    }

    /*
     * 
     * Getters and Setters
     * 
     */
    
    public List<ExtSed> getSeds() {
        return seds;
    }

    public void setSeds(List<ExtSed> seds) {
        // TODO: Support more than one SED
        if (seds.size() > 1) {
            throw new IllegalArgumentException("Invalid sed list length");
        }
        this.seds = seds;
        
        resetPlot(true);
    }

    public VisualizerComponentPreferences getPreferences() {
        return preferences;
    }

    void setPreferences(VisualizerComponentPreferences prefs) {
        this.preferences = prefs;
        setDataModel(prefs.getDataModel());
    }
    
    public VisualizerDataModel getDataModel() {
        return dataModel;
    }
    
    public void setDataModel(VisualizerDataModel dataModel) {
        this.dataModel = dataModel;
        
        // Always reset on a datamodel change
        resetPlot(true);
    }

    public PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> getPlotDisplay() {
        return display;
    }

    /**
     * @return the value of env
     */
    protected MapEnvironment getEnv() {
        return env;
    }
    
    /**
     * Handles getting the plot preferences for the current selection of SEDs in the plotter.
     */
    public PlotPreferences getPlotPreferences() {
        if (CollectionUtils.isEmpty(seds)) {
            return preferences.getPlotPreferences();
        } else {
            return dataModel.getSedModel(this.seds.get(0)).getPlotPreferences();
        }
    }
    
    /**
     * Change the plotting space between logarithmic and linear. One of the axes
     * can be logarithmic, while the other is linear.
     * @param plotType the plot type to use. Must be one of the 
     * PlotPreferences.PlotType enums. Choices are LOG, LINEAR, XLOG, and YLOG.
     */
    public void changePlotType(PlotPreferences.PlotType plotType) {
                
        try {
            getPlotPreferences().setPlotType(plotType);
            resetPlot(true);
        } catch (EnumConstantNotPresentException ex) {
            logger.log(Level.WARNING, ex.getMessage());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }
    
    public void setGridOn(boolean on) {
        try {
            getPlotPreferences().setShowGrid(on);
            resetPlot(true);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Resets boundaries on the zoom to their original settings.
     */
    public void resetZoom() {
        resetPlot(false);
    }
    
    /**
     * Zoom in or out of plot by a given scale factor.
     * @param zoomFactor 
     */
    public void zoom(double zoomFactor) {
        
        double xmax = this.getPlotDisplay().getAspect().getXMax();
        double xmin = this.getPlotDisplay().getAspect().getXMin();
        double ymax = this.getPlotDisplay().getAspect().getYMax();
        double ymin = this.getPlotDisplay().getAspect().getYMin();
        
        double [] xlimits = zoomAxis(zoomFactor, xmin, xmax, 
                getPlotPreferences().getXlog());
        double [] ylimits = zoomAxis(zoomFactor, ymin, ymax, 
                getPlotPreferences().getYlog());
                
        // create new aspect for zoomed view
        PlaneAspect zoomedAspect = new PlaneAspect(xlimits, ylimits);
        this.getPlotDisplay().setAspect(zoomedAspect);
    }
    
    /**
     * Resets the plot.
     */
    private void resetPlot(boolean fixed)
    {
        // Preferences can override this class's internal usage of fixed
        if (fixed) {
            fixed = getPlotPreferences().getFixed();
        }
        
        // Clear the display if it's available
        setupForPlotDisplayChange();
        
        // Setup new plot component
        display = createPlotComponent();
        
        // Set the bounds using the current SED's aspect if the plot is fixed
        if (fixed) {
            PlaneAspect existingAspect = this.getPlotPreferences().getAspect();
            display.setAspect(existingAspect);
        }
        
        // Add the display to the plot view
        updatePlotDisplay();
    }
    
    /**
     * Calculate new zoomed axis range.
     * @param zoomFactor - scale factor to zoom in/out by
     * @param min - min axis value
     * @param max - max axis value
     * @param isLog - flag if the axis is in log-space (true) or not (false)
     * @return a double array of the zoomed min and max range: [min, max]
     */
    private double[] zoomAxis(double zoomFactor, double min, double max, boolean isLog) {
        
        if (isLog) {
            
            // calculate central axis value
            double centerFactor = (Math.log10(max) - Math.log10(min))/2;
            double center = centerFactor + Math.log10(min);
            center = Math.pow(10, center);
            
            // calculate zoomed min and max values
            return Axis.zoom(min, max, center, zoomFactor, isLog);
            
        } else {
            
            // calculate the central axis value
            double center = (Math.abs(max) - Math.abs(min))/2;
            if (min < 0 && max > 0) {
                // pass. leave xcenter as it is
            } else {
                center = min + Math.abs(center);
            }
            
            // calculate zoomed min and max values
            return Axis.zoom(min, max, center, zoomFactor, isLog);
        }
    }

    /**
     * Create the stil plot component
     * @return
     */
    protected PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> 
        createPlotComponent()
    {
        logger.info("Creating new plot from selected SED(s)");

        try {
            setupMapEnvironment();
            return createPlotComponent(env);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param env
     *            Plot display environment to use
     * @return PlotDisplay
     * @throws Exception
     */
    protected PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> createPlotComponent(
            MapEnvironment env) throws Exception {

        logger.log(Level.FINE, "plot environment:");
        logger.log(Level.FINE, ReflectionToStringBuilder.toString(env));
        
        
        // Always cache between repaints
        @SuppressWarnings("unchecked")
        PlotDisplay<PlaneSurfaceFactory.Profile,PlaneAspect> display =
                new PlanePlot2Task().createPlotComponent(env, true);
        
        // Always update mouse listeners with the new display
        preferences.getMouseListenerManager().activateListeners(display);
        
        return display;
    }

    protected void setupMapEnvironment() throws IOException {

        env = new MapEnvironment();
        env.setValue("type", "plot2plane");
        env.setValue("insets", new Insets(50, 80, 50, 50));
        
        // TODO: force numbers on Y axis to only be 3-5 digits long. Keeps
        // Y-label from falling off the jpanel. Conversely, don't set "insets"
        // and let the plotter dynamically change size to keep axes labels
        // on the plot.

        // Add high level plot preferences
        PlotPreferences pp = getPlotPreferences();
        for (String key : pp.getPreferences().keySet()) {
            
            // if in magnitudes, flip the direction of the Y-axis
            // (lower magnitude = brighter source -> higher on Y-axis)
            if (SPVYQuantity.MAGNITUDE.getPossibleUnits().contains(pp.getPreferences().get(key))) {
                env.setValue("yflip", true);
            }
            
            env.setValue(key, pp.getPreferences().get(key));
        }

        // set title of plot
        env.setValue("title", dataModel.getDataModelTitle());

        // Add segments and segment preferences
        addSegmentLayers(env);
    }

    private void addSegmentLayers(MapEnvironment env)
            throws IOException {
        if (CollectionUtils.isEmpty(seds)) {
            logger.info("No SED selected, returning empty plot");
            return;
        }

        for (ExtSed sed : this.seds) {
            List<SegmentModel> models = dataModel.getModelsForSed(sed);
            for (SegmentModel layer : models) {
                for (String key : layer.getPreferences().keySet()) {
                    env.setValue(key, layer.getPreferences().get(key));
                }
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
            
            getPlotPreferences().setAspect(display.getAspect());
        }
    }
    
    /**
     * Add and update the PlotDisplay.
     */
    private void updatePlotDisplay() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = gbc.weighty = 1.0;
        gbc.gridx = gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        
        add(display, gbc);
        
        display.revalidate();
        display.repaint();
    }
}