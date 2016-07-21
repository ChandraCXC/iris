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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.quantities.SPVYQuantity;
import cfa.vo.iris.visualizer.preferences.FittingRangeModel;
import cfa.vo.iris.visualizer.preferences.FunctionModel;
import cfa.vo.iris.visualizer.preferences.LayerModel;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;

import java.awt.Dimension;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.ttools.plot2.Axis;

public class StilPlotter extends JPanel {

    private static final Logger logger = Logger
            .getLogger(StilPlotter.class.getName());

    private static final long serialVersionUID = 1L;
    
    // Primary plot display
    private PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> display;
    
    // Residuals
    private PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> residuals;
    private boolean showResiduals = false;
    private String residualsOrRatios = "Residuals";

    // List of SEDs plotted in Plotter
    private List<ExtSed> seds = new ArrayList<>();
    
    // For data model preferences
    private VisualizerComponentPreferences preferences;
    private VisualizerDataModel dataModel;
    
    // Plot preferences for the currently plotted selection of SEDs
    private PlotPreferences plotPreferences;
    
    // MapEnvironment for the primary plotter
    private MapEnvironment env;
    
    // MapEnvironment for the residuals plotter
    private MapEnvironment resEnv;
    
    // Fitting ranges currently plotted
    FittingRangeModel fittingRanges;
    
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
        setPlotPreferences(preferences.getPlotPreferences(newSeds));
        
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
     * @return The residuals plot display.
     */
    public PlotDisplay<PlaneSurfaceFactory.Profile, PlaneAspect> getResidualsPlotDisplay() {
        return residuals;
    }

    /**
     * @return MapEnvironment configuration for the Stil plot task.
     */
    protected MapEnvironment getEnv() {
        return env;
    }
    
    /**
     * @return MapEnvironment configuration for the residuals.
     */
    protected MapEnvironment getResEnv() {
        return resEnv;
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
    
    public boolean isShowResiduals() {
        return showResiduals;
    }
    
    public void setShowResiduals(boolean on) {
        this.showResiduals = on;
        resetPlot(false, false);
    }
    
    public String getResidualsOrRatios() {
        return residualsOrRatios;
    }
    
    public void setResidualsOrRatios(String residualsOrRatios) {
        this.residualsOrRatios = residualsOrRatios;
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
        
        // Setup new stil plot components
        setupPlotComponents();
        
        // Set the bounds using the current SED's aspect if the plot is fixed and if we're not
        // forcing a redraw
        if (fixed && !forceReset) {
            PlaneAspect existingAspect = getPlotPreferences().getAspect();
            display.setAspect(existingAspect);
        }
        
        // Add the display to the plot view
        addPlotToDisplay();
        
        // Check for valid model functions on redraws
        validateModelFunctions();
    }
    
    /**
     * Verify all model functions are valid and up to date with the original version
     * of the fit function. Notify the user if not.
     */
    private void validateModelFunctions() {
        StringBuilder sedIds = new StringBuilder();
        for (SedModel sedModel : dataModel.getSedModels()) {
            
            // If there is no model or the model is valid, then we're good
            if (!sedModel.getHasModelFunction() ||
               (sedModel.getVersion() == sedModel.getModelVersion())) 
            {
                continue;
            }
            
            sedIds.append(sedModel.getSed().getId() + ", ");
        }
        
        // If any models were invalid notify the user, this should only display once
        if (sedIds.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                    String.format(
                    "Warning: %smay no longer be valid! You may want to\n refit the sed or re-evaluate the model for the fit.",
                    sedIds.toString()),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            
            // We will have warned the user, so update the modelVersionNumbers for all SedModels so
            // this popup doesn't show twice for the same sed model
            dataModel.updateFittingVersionNumbers();
        }
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
     * Pan the plotter by a set amount in the direction specified.
     * @param direction SwingConstant North, South, East, or West.
     */
    public void dataPan(int direction) {
        
        PlaneAspect aspect = getPlotDisplay().getAspect();
        
        double[] newX = new double[] { aspect.getXMin(), aspect.getXMax() };
        double[] newY = new double[] { aspect.getYMin(), aspect.getYMax() };
        
        switch (direction) {
        case SwingConstants.NORTH:
            newY = panAxis(newY, getPlotPreferences().getYlog(), true);
            break;
        case SwingConstants.SOUTH:
            newY = panAxis(newY, getPlotPreferences().getYlog(), false);
            break;
        case SwingConstants.EAST:
            newX = panAxis(newX, getPlotPreferences().getXlog(), true);
            break;
        case SwingConstants.WEST:
            newX = panAxis(newX, getPlotPreferences().getXlog(), false);
            break;
        default:
            // ignore it
            return;
        }
        
        PlaneAspect newAspect = new PlaneAspect(newX, newY);
        getPlotDisplay().setAspect(newAspect);
    }
    
    private double[] panAxis(double[] cur, boolean isLog, boolean positive) {
        double d0;
        double d1;
        
        // For log move by a factor of 1/3 or 3
        if (isLog) {
            d0 = 1;
            d1 = 3;
        } 
        // For linear move by 1/4 of the width
        else {
            d0 = cur[0];
            d1 = d0 + Math.abs(cur[1] - cur[0])/4;
        }
        
        double[] zoomed;
        if (positive) {
            zoomed = Axis.pan(cur[0], cur[1], d1, d0, isLog);
        } else {
            zoomed = Axis.pan(cur[0], cur[1], d0, d1, isLog);
        }
        
        return zoomed;
    }

    /**
     * Create the stil plot component
     * @return
     */
    private void setupPlotComponents()
    {
        logger.info("Creating new plot from selected SED(s)");

        try {
            // Setup each MapEnvironment
            setupMapEnvironment();
            setupResidualMapEnvironment();
            
            // Create the plot display
            display = createPlotComponent(env, true);
            
            // Create the residuals if specified
            residuals = showResiduals ? createPlotComponent(resEnv, false) : null;
            
            // TODO: Handle mouse listeners and zooming for the residuals!
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
            MapEnvironment env, boolean enableMouseListeners) throws Exception {

        logger.log(Level.FINE, "plot environment:");
        logger.log(Level.FINE, ReflectionToStringBuilder.toString(env));
        
        // Don't use STIL caching http://tinyurl.com/h7dml6d, our data is already 
        // cached so it doesn't necessarily buy us any performance gains. Moreover,
        // it can cause problems in testing with repeating segments, and potentially
        // production with many changing segments with the same data.
        @SuppressWarnings("unchecked")
        PlotDisplay<PlaneSurfaceFactory.Profile,PlaneAspect> display =
                new PlanePlot2Task().createPlotComponent(env, false);
        
        if (enableMouseListeners) {
            // Always update mouse listeners with the new display
            preferences.getMouseListenerManager().activateListeners(display);
        }
        
        return display;
    }

    protected void setupMapEnvironment() throws IOException {

        env = new MapEnvironment(new LinkedHashMap<String, Object>());
        env.setValue("type", "plot2plane");
        env.setValue("insets", new Insets(30, 80, 40, 50));
        
        // TODO: force numbers on Y axis to only be 3-5 digits long. Keeps
        // Y-label from falling off the jpanel. Conversely, don't set "insets"
        // and let the plotter dynamically change size to keep axes labels
        // on the plot.
        
        PlotPreferences pp = getPlotPreferences();
        
        // Add high level plot preferences
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
            Map<String, Object> prefs = layer.getPreferences();
            for (String key : prefs.keySet()) {
                env.setValue(key, prefs.get(key));
            }
        }

        // Add model functions
        addFunctionModels(env);
        
        // Add Fitting ranges
        addFittingRanges(env);
    }

    private void addFunctionModels(MapEnvironment env) {
        
        // Override color settings so that each model function is a different color,
        // starting with dark red
        ColorPalette palette = new HSVColorPalette(360, 100, 75);
        
        for (FunctionModel model : dataModel.getFunctionModels()) {
            // If no model available (e.g. no fit) skip it
            if (!model.hasModelValues()) {
                continue;
            }
            
            // Update color
            LayerModel layer = model.getFunctionLayerModel();
            layer.setLineColor(ColorPalette.colorToHex(palette.getNextColor()));
            
            // Add layer prefereces for function models
            Map<String, Object> prefs = layer.getPreferences();
            for (String key : prefs.keySet()) {
                env.setValue(key, prefs.get(key));
            }
        }
    }
    
    private void setupResidualMapEnvironment() throws Exception {
        if (!showResiduals) {
            resEnv = null;
            return;
        }
        
        PlotPreferences pp = getPlotPreferences();
        
        resEnv = new MapEnvironment(new LinkedHashMap<String, Object>());
        resEnv.setValue("type", "plot2plane");
        resEnv.setValue("insets", new Insets(20, 80, 20, 50));
        
        // Get settings from the overall plot preferences and manually add them here.
        resEnv.setValue(PlotPreferences.SIZE, pp.getSize());
        resEnv.setValue(PlotPreferences.SHAPE, pp.getMarkType().name());
        resEnv.setValue(PlotPreferences.X_LOG, pp.getPlotType().xlog);
        resEnv.setValue(PlotPreferences.GRID, pp.getShowGrid());
        
        resEnv.setValue("ylabel", residualsOrRatios);
        resEnv.setValue("xlabel", null);
        resEnv.setValue("legend", false);
        
        // add model functions
        for (FunctionModel model : dataModel.getFunctionModels()) {
            // If no model available (e.g. no fit) skip it
            if (!model.hasModelValues()) {
                continue;
            }
            
            LayerModel layer = model.getResidualsLayerModel(residualsOrRatios);
            Map<String, Object> prefs = layer.getPreferences();
            for (String key : prefs.keySet()) {
                resEnv.setValue(key, prefs.get(key));
            }
        }
    }
    
    private void addFittingRanges(MapEnvironment env) {
        List<FittingRange> ranges = dataModel.getFittingRanges();

        // Do nothing if none are available
        if (CollectionUtils.isEmpty(ranges)) return;
        
        // If there is no aspect then do nothing
        PlaneAspect aspect = getPlotPreferences().getAspect();
        if (aspect == null) return;
        
        // Otherwise add the layer at 5% from the bottom of the aspect
        double y = aspect.getYMin() + ((aspect.getYMax() - aspect.getYMin()) * .05);
        fittingRanges = new FittingRangeModel(ranges, dataModel.getXunits(), y);
        
        Map<String, Object> prefs = fittingRanges.getPreferences();
        for (String key : prefs.keySet()) {
            env.setValue(key, prefs.get(key));
        }
    }
    
    private void setupForPlotDisplayChange(boolean newPlot) {
        if (display == null) {
            return;
        }
        
        if (residuals != null) {
            remove(residuals);
        }
        
        remove(display);
        
        if (!newPlot) {
            getPlotPreferences().setAspect(display.getAspect());
        }
    }
    
    /**
     * Add and update the PlotDisplay.
     */
    private void addPlotToDisplay() {
        
        GridBagConstraints displayGBC = new GridBagConstraints();
        displayGBC.anchor = GridBagConstraints.NORTHWEST;
        displayGBC.fill = GridBagConstraints.BOTH;
        displayGBC.weightx = 1;
        displayGBC.weighty = .75;
        displayGBC.gridx = 0;
        displayGBC.gridy = 0;
        displayGBC.gridheight = 1;
        displayGBC.gridwidth = 1;

        display.setPreferredSize(new Dimension(600, 500));
        add(display, displayGBC);
        
        // Ad the residuals to the jpanel if specified
        if (showResiduals) {
            // Ensure it fills the entire display
            GridBagConstraints residualsGBC = new GridBagConstraints();
            residualsGBC.anchor = GridBagConstraints.NORTHWEST;
            residualsGBC.fill = GridBagConstraints.BOTH;
            residualsGBC.weightx = 1;
            residualsGBC.weighty = .25;
            residualsGBC.gridx = 0;
            residualsGBC.gridy = 1;
            residualsGBC.gridheight = 1;
            residualsGBC.gridwidth = 1;
            
            residuals.setPreferredSize(new Dimension(600,100));
            residuals.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            residuals.setMinimumSize(new Dimension(0, 50));
            add(residuals, residualsGBC);
            
            residuals.revalidate();
            residuals.repaint();
        }
        
        display.revalidate();
        display.repaint();
    }
}