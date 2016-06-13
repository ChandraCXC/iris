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
import cfa.vo.iris.visualizer.preferences.LayerModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurfaceFactory;
import uk.ac.starlink.ttools.plot2.task.PlanePlot2Task;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;

import java.awt.GridBagConstraints;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

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
    
    // Standard PlotPreferences for an empty plot
    private static final PlotPreferences DEFAULT_PLOT_PREFERENCES = PlotPreferences.getDefaultPlotPreferences();

    private PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> display;

    // List of SEDs plotted in Plotter
    private List<ExtSed> seds = new ArrayList<>();
    
    // For data model preferences
    private VisualizerComponentPreferences preferences;
    private VisualizerDataModel dataModel;
    
    // Plot preferences for the currently plotted selection of SEDs
    private PlotPreferences plotPreferences = DEFAULT_PLOT_PREFERENCES;
    
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

    /**
     * For binding to the datamodel. This function SHOULD NOT be called otherwise.
     */
    public void setSeds(List<ExtSed> newSeds) {
        this.seds = newSeds;
        
        // Update plot preferences for new seds.
        if (CollectionUtils.isEmpty(newSeds)) {
            this.setPlotPreferences(DEFAULT_PLOT_PREFERENCES);
        } else {
            this.setPlotPreferences(preferences.getPlotPreferences(newSeds));
        }
        
        resetPlot(false, true);
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
    
    void setDataModel(VisualizerDataModel dataModel) {
        this.dataModel = dataModel;
        this.setSeds(dataModel.getSelectedSeds());
    }
    
    public List<LayerModel> getLayerModels() {
        return dataModel.getLayerModels();
    }
    
    /**
     * For binding to the dataModel, this SHOULD NOT be called otherwise.
     */
    public void setLayerModels(List<LayerModel> models) {
        this.resetPlot(false, false);
    }

    /**
     * @return The stil plot display.
     */
    public PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> getPlotDisplay() {
        return display;
    }

    /**
     * @return MapEnvironment configuration for the Stil plot task.
     */
    protected MapEnvironment getEnv() {
        return env;
    }
    
    /**
     * @return Current set of PlotPreferences used by the plotter.
     */
    public PlotPreferences getPlotPreferences() {
        return plotPreferences;
    }
    
    /**
     * Used internally for updating the plot preferences on SED changes.
     */
    public static final String PROP_PLOT_PREFERENCES = "plotPreferences";
    public void setPlotPreferences(PlotPreferences pp) {
        // If the preferences are changing we want to save the last known display's
        // aspect in the old preferences.
        if (display != null) {
            getPlotPreferences().setAspect(display.getAspect());
        }
        
        PlotPreferences old = this.plotPreferences;
        this.plotPreferences = pp;
        this.firePropertyChange(PROP_PLOT_PREFERENCES, old, pp);
    }
    
    /**
     * Change the plotting space between logarithmic and linear. One of the axes
     * can be logarithmic, while the other is linear.
     * 
     * @param plotType the plot type to use.
     */
    public void setPlotType(PlotPreferences.PlotType plotType) {
        getPlotPreferences().setPlotType(plotType);
        resetPlot(false, false);
    }
    
    public boolean getGridOn() {
        return getPlotPreferences().getShowGrid();
    }
    
    public void setGridOn(boolean on) {
        getPlotPreferences().setShowGrid(on);
        resetPlot(false, false);
    }
    
    /**
     * Resets the plot.
     * @param forceReset - forces the plot to reset its bounds
     * @param newPlot - If we are plotting a new plot or re-plotting an existing plot.
     */
    void resetPlot(boolean forceReset, boolean newPlot)
    {
        // forceReset can override this class's internal usage of preferences
        boolean fixed = getPlotPreferences().getFixed();
        
        // Clear the display and save all necessary information before we
        // throw it away on a model change.
        setupForPlotDisplayChange(newPlot);
        
        // Setup new stil plot component
        display = createPlotComponent();
        
        // Set the bounds using the current SED's aspect if the plot is fixed and if we're not
        // forcing a redraw
        if (fixed && !forceReset) {
            PlaneAspect existingAspect = getPlotPreferences().getAspect();
            display.setAspect(existingAspect);
        }
        
        // Add the display to the plot view
        addPlotToDisplay();
    }
    
    /**
     * Resets boundaries on the zoom to their original settings.
     */
    public void resetZoom() {
        resetPlot(true, false);
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
     * @param env Plot display environment to use
     * @return PlotDisplay
     * @throws Exception
     */
    protected PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> createPlotComponent(
            MapEnvironment env) throws Exception {

        logger.log(Level.FINE, "plot environment:");
        logger.log(Level.FINE, ReflectionToStringBuilder.toString(env));
        
        // Don't use STIL caching http://tinyurl.com/h7dml6d, our data is already 
        // cached so it doesn't necessarily buy us any performance gains. Moreover,
        // it can cause problems in testing with repeating segments, and potentially
        // production with many changing segments with the same data.
        @SuppressWarnings("unchecked")
        PlotDisplay<PlaneSurfaceFactory.Profile,PlaneAspect> display =
                new PlanePlot2Task().createPlotComponent(env, false);
        
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
        for (LayerModel layer : dataModel.getLayerModels()) {
            for (String key : layer.getPreferences().keySet()) {
                env.setValue(key, layer.getPreferences().get(key));
            }
        }
    }
    
    private void setupForPlotDisplayChange(boolean newPlot) {
        if (display == null) {
            return;
        }
        
        display.removeAll();
        remove(display);
        
        if (!newPlot) {
            getPlotPreferences().setAspect(display.getAspect());
        }
    }
    
    /**
     * Add and update the PlotDisplay.
     */
    private void addPlotToDisplay() {
        
        // Ensure it fills the entire display
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = gbc.weighty = 1.0;
        gbc.gridx = gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        
        add(display, gbc);
        
        display.revalidate();
        display.repaint();
    }
}