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

package cfa.vo.iris.visualizer.plotter;

import cfa.vo.iris.sed.SedException;
import cfa.vo.iris.sed.quantities.SPVYQuantity;
import cfa.vo.iris.sed.quantities.SPVYUnit;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.sed.quantities.YUnit;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.iris.units.spv.YUnits;
import cfa.vo.iris.visualizer.preferences.SedModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;

public class PlotPreferences {
    
    private final PropertyChangeSupport pcs;
    
    // This is only a small sample of the preferences available in STILTS, for
    // the full list, see
    // http://www.star.bris.ac.uk/~mbt/stilts/sun256/sun256.html#TypedPlot2Task
    // Global Plot Settings
    static final String GRID = "grid";
    
    // Labels are currently attached to the units for this plot, but that could
    // change in the future.
    static final String X_LABEL = "xlabel";
    static final String Y_LABEL = "ylabel";
    static final String X_LOG = "xlog";
    static final String Y_LOG = "ylog";
    static final String AUTO_FIX = "auto_fix"; // not STILTS
    static final String X_MAX = "xmax";
    static final String X_MIN = "xmin";
    static final String Y_MAX = "ymax";
    static final String Y_MIN = "ymin";
    static final String Y_FLIP = "yflip";
    static final String X_FLIP = "xflip";
    static final String PLOT_TYPE = "plot_type"; // not STILTS
    
    // These settings are set at the Sed level, but can be overridden by the underlying
    // Segments
    public static final String ERROR_BAR_TYPE = "errorbar";
    public static final String SIZE = "size";
    public static final String SHADING = "shading";
    public static final String SHAPE = "shape";
    public static final String TYPE = "layer";
    
    // for the plot legend
    static final String SHOW_LEGEND = "legend";
    static final String LEGEND_BORDER = "legborder";
    static final String LEGEND_OPAQUE = "legopaque";
    static final String LEGEND_POSITION = "legpos";
    
    private PlaneAspect aspect; // not STILTS. Do not add to the prefs map!
                                // It'll cause an error in STILTS.
    private String xUnits; // XUnits for this set of plot preferences
    private String yUnits; // YUnits for this set of plot preferences
    
    // Plot Types - Iris-specific, not STILTS.
    public enum PlotType {
        LOG("log", true, true),
        LINEAR("linear", false, false),
        X_LOG("xlog", true, false),
        Y_LOG("ylog", false, true);
        
        public String name;
        public Boolean xlog;
        public Boolean ylog;
    
        private PlotType(String name, Boolean x, Boolean y) {
            this.name = name;
            this.xlog = x;
            this.ylog = y;
        }
    }
    
    /**
     * 
     * @return default plot preferences
     */
    public static PlotPreferences getDefaultPlotPreferences() {
        
        // for the aspect
        double[] xlimits = new double[] {0, 10};
        double[] ylimits = new double[] {0, 10};
        PlaneAspect aspect = new PlaneAspect(xlimits, ylimits);
        
        PlotPreferences pp = new PlotPreferences();
        pp.setXlog(true);
        pp.setYlog(true);
        pp.setShowGrid(true);
        pp.setFixed(false);
        pp.setYflip(false);
        pp.setXflip(false);
        pp.setPlotType(PlotType.LOG);
        pp.setShowLegend(true);
        pp.setLegendPosition(new double[] {1.0, 1.0});
        pp.setLegendOpaque(false);
        pp.setLegendBorder(true);
        pp.setAspect(aspect);
        pp.setErrorBarType(ErrorBarType.capped_lines);
        pp.setMarkType(ShapeType.open_circle);
        pp.setSize(4);
        
        return pp;
    }
    
    private Map<String, Object> preferences;

    public PlotPreferences() {
        preferences = new HashMap<String, Object>();
        this.pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
    public Map<String, Object> getPreferences() {
        return preferences;
    }
    
    public Boolean getShowGrid() {
        return (Boolean) preferences.get(GRID);
    }
    
    public static final String PROP_SHOW_GRID = "showGrid";
    public void setShowGrid(Boolean arg1) {
        Boolean old = getShowGrid();
        preferences.put(GRID, arg1);
        pcs.firePropertyChange(PROP_SHOW_GRID, old, arg1);
    }

    public String getYUnits() {
        return this.yUnits;
    }

    public static final String PROP_Y_UNITS = "yUnits";
    public void setYUnits(String arg1) {
        String old = getYUnits();
        this.yUnits = arg1;
        
        String ylabel;
        try {
            YUnits yconvert = new YUnits(arg1);
            ylabel = yconvert.getLabel() + 
                    " (" + SPVYUnit.getFromUnitString(arg1).getString() + ")";
        } catch (SedException ex) {
            // if unit is not defined
            // TODO: set to default or unknown units?
            ylabel = "Flux density (" +
                    YUnit.PHOTONFLUXDENSITY0.getString() + ")";
            Logger.getLogger(PlotPreferences.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        flipYifMag(arg1);
        this.setYLabel(ylabel);
        pcs.firePropertyChange(PROP_Y_UNITS, old, yUnits);
    }
    
    public String getXUnits() {
        return this.xUnits;
    }

    public static final String PROP_X_UNITS = "xUnits";
    public void setXUnits(String arg1) {
        String old = getXUnits();
        this.xUnits = arg1;
        
        String xlabel;
        try {
            XUnits xconvert = new XUnits(arg1);
            xlabel = xconvert.getLabel() +
                    " (" + XUnit.getFromUnitString(arg1).getString() + ")";
        } catch (SedException ex) {
            // if unit is not defined
            // TODO: set to default or unknown units?
            xlabel = "Wavelength (" + XUnit.ANGSTROM.getString() + ")";
            Logger.getLogger(PlotPreferences.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        this.setXLabel(xlabel);
        pcs.firePropertyChange(PROP_X_UNITS, old, xUnits);
    }
    
    public String getYLabel() {
        return (String) preferences.get(Y_LABEL);
    }
    
    public static String PROP_Y_LABEL = "yLabel";
    public void setYLabel(String yLabel) {
        String old = getYLabel();
        preferences.put(Y_LABEL, yLabel);
        pcs.firePropertyChange(PROP_Y_LABEL, old, yLabel);
    }
    
    public String getXLabel() {
        return (String) preferences.get(X_LABEL);
    }

    public static String PROP_X_LABEL = "xLabel";
    public void setXLabel(String xLabel) {
        String old = getXLabel();
        preferences.put(X_LABEL, xLabel);
        pcs.firePropertyChange(PROP_X_LABEL, old, xLabel);
    }
    
    public Boolean getYflip() {
        return (Boolean) preferences.get(Y_FLIP);
    }

    public static final String PROP_Y_FLIP = "yflip";
    public void setYflip(Boolean arg1) {
        Boolean old = getYflip();
        preferences.put(Y_FLIP, arg1);
        pcs.firePropertyChange(PROP_Y_FLIP, old, arg1);
    }
    
    public Boolean getXflip() {
        return (Boolean) preferences.get(X_FLIP);
    }

    public static final String PROP_X_FLIP = "xflip";
    public void setXflip(Boolean arg1) {
        Boolean old = getXflip();
        preferences.put(X_FLIP, arg1);
        pcs.firePropertyChange(PROP_X_FLIP, old, arg1);
    }
    
    public Boolean getXlog() {
        return (Boolean) preferences.get(X_LOG);
    }

    public static final String PROP_X_LOG = "xlog";
    public void setXlog(Boolean arg1) {
        Boolean old = getXlog();
        preferences.put(X_LOG, arg1);
        pcs.firePropertyChange(PROP_X_LOG, old, arg1);
    }
    
    public Boolean getYlog() {
        return (Boolean) preferences.get(Y_LOG);
    }
    
    public static final String PROP_Y_LOG = "ylog";
    public void setYlog(Boolean arg1) {
        Boolean old = getYlog();
        preferences.put(Y_LOG, arg1);
        pcs.firePropertyChange(PROP_Y_LOG, old, arg1);
    }
    
    public Boolean getFixed() {
        return (Boolean) preferences.get(AUTO_FIX);
    }
    
    public static final String PROP_FIXED = "fixed";
    public void setFixed(Boolean arg1) {
        Boolean old = getFixed();
        preferences.put(AUTO_FIX, arg1);
        pcs.firePropertyChange(PROP_FIXED, old, arg1);
    }
    
    public double getXmax() {
        return (double) preferences.get(X_MAX);
    }

    public static final String PROP_X_MAX = "xmax";
    public void setXmax(double arg1) {
        double old = getXmax();
        preferences.put(X_MAX, arg1);
        pcs.firePropertyChange(PROP_X_MAX, old, arg1);
    }
    
    public double getXmin() {
        return (double) preferences.get(X_MIN);
    }

    public static final String PROP_X_MIN = "xmin";
    public void setXmin(double arg1) {
        double old = getXmin();
        preferences.put(X_MIN, arg1);
        pcs.firePropertyChange(PROP_X_MIN, old, arg1);
    }
    
    public double getYmax() {
        return (double) preferences.get(Y_MAX);
    }

    public static final String PROP_Y_MAX = "ymax";
    public void setYmax(double arg1) {
        double old = getYmax();
        preferences.put(Y_MAX, arg1);
        pcs.firePropertyChange(PROP_Y_MAX, old, arg1);
    }
    
    public double getYmin() {
        return (double) preferences.get(Y_MIN);
    }

    public static final String PROP_Y_MIN = "ymin";
    public void setYmin(double arg1) {
        double old = getYmin();
        preferences.put(Y_MIN, arg1);
        pcs.firePropertyChange(PROP_Y_MIN, old, arg1);
    }
    
    public PlotType getPlotType() {
        return (PlotType) preferences.get(PLOT_TYPE);
    }
    
    public static final String PROP_PLOT_TYPE = "plotType";
    public void setPlotType(PlotType arg1) {
        PlotType old = getPlotType();
        preferences.put(PLOT_TYPE, arg1);
        setXlog(arg1.xlog);
        setYlog(arg1.ylog);
        pcs.firePropertyChange(PROP_PLOT_TYPE, old, arg1);
    }
    
    public PlaneAspect getAspect() {
        return aspect;
    }
    
    public static final String PROP_ASPECT = "aspect";
    public void setAspect(PlaneAspect arg1) {
        // don't add it to the prefs map; it'll cause an error in STILTS
        PlaneAspect old = getAspect();
        this.aspect = arg1;
        pcs.firePropertyChange(PROP_ASPECT, old, arg1);
    }
    
    public Boolean getShowLegend() {
        return (Boolean) preferences.get(SHOW_LEGEND);
    }
    
    public static final String PROP_SHOW_LEGEND = "showLegend";
    public void setShowLegend(Boolean arg1) {
        Boolean old = getShowLegend();
        preferences.put(SHOW_LEGEND, arg1);
        pcs.firePropertyChange(PROP_SHOW_LEGEND, old, arg1);
    }
    
    public Boolean getLegendOpaque() {
        return (Boolean) preferences.get(LEGEND_OPAQUE);
    }

    public static final String PROP_LEGEND_OPAQUE = "legendOpaque";
    public void setLegendOpaque(Boolean arg1) {
        Boolean old = getLegendOpaque();
        preferences.put(LEGEND_OPAQUE, arg1);
        pcs.firePropertyChange(PROP_LEGEND_OPAQUE, old, arg1);
    }
    
    public double[] getLegendPostion() {
        return (double[]) preferences.get(LEGEND_POSITION);
    }

    public static final String PROP_LEGEND_POSITION = "legendPosition";
    public void setLegendPosition(double[] ratios) {
        double[] old = this.getLegendPostion();
        preferences.put(LEGEND_POSITION, ratios);
        pcs.firePropertyChange(PROP_LEGEND_POSITION, old, ratios);
    }
    
    public Boolean getLegendBorder() {
        return (Boolean) preferences.get(LEGEND_BORDER);
    }

    public static final String PROP_LEGEND_BORDER = "legendBorder";
    public void setLegendBorder(Boolean arg1) {
        Boolean old = getLegendBorder();
        preferences.put(LEGEND_BORDER, arg1);
        pcs.firePropertyChange(PROP_LEGEND_BORDER, old, arg1);
    }
    
    protected ErrorBarType getErrorBarType() {
        return (ErrorBarType) preferences.get(ERROR_BAR_TYPE);
    }

    public static final String PROP_ERROR_BAR_TYPE = "errorBarType";
    protected void setErrorBarType(ErrorBarType errorBarType) {
        ErrorBarType old = getErrorBarType();
        preferences.put(ERROR_BAR_TYPE, errorBarType.name());
        pcs.firePropertyChange(PROP_LEGEND_BORDER, old, errorBarType);
    }

    protected ShapeType getMarkType() {
        String name = (String) preferences.get(SHAPE);
        return StringUtils.isEmpty(name) ? null : ShapeType.valueOf(name);
    }

    public static final String PROP_MARK_TYPE = "markType";
    protected void setMarkType(ShapeType markType) {
        ShapeType old = getMarkType();
        preferences.put(SHAPE, markType.name());
        pcs.firePropertyChange(SHAPE, old, markType);
    }

    protected Integer getSize() {
        return (Integer) preferences.get(SIZE);
    }

    public static final String PROP_MARK_SIZE = "size";
    protected void setSize(Integer size) {
        Integer old = getSize();
        preferences.put(SIZE, size);
        pcs.firePropertyChange(PROP_LEGEND_BORDER, old, size);
    }
    
    /**
     * Flips the Y-axis if yunit is a magnitude. A lower magnitude = brighter 
     * source  higher on Y-axis)
     * @param yunit 
     */
    private void flipYifMag(String yunit) {
        try {
            if (SPVYQuantity.MAGNITUDE.getPossibleUnits()
                    .contains(SPVYUnit.getFromUnitString(yunit))) {
                this.setYflip(true);
            } else {
                this.setYflip(false);
            }
        } catch (SedException ex) {
            Logger.getLogger(SedModel.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}

