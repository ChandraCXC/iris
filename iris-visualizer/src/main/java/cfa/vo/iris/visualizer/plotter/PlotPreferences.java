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

import java.util.HashMap;
import java.util.Map;

public class PlotPreferences {
    
    // This is only a small sample of the preferences available in STILTS, for
    // the full list, see
    // http://www.star.bris.ac.uk/~mbt/stilts/sun256/sun256.html#TypedPlot2Task
    // Global Settings
    public static final String GRID = "grid";
    public static final String X_LABEL = "xlabel";
    public static final String Y_LABEL = "ylabel";
    public static final String X_LOG = "xlog";
    public static final String Y_LOG = "ylog";
    public static final String AUTO_FIX = "auto_fix"; // not STILTS
    public static final String X_MAX = "xmax";
    public static final String X_MIN = "xmin";
    public static final String Y_MAX = "ymax";
    public static final String Y_MIN = "ymin";
    public static final String PLOT_TYPE = "plot_type"; // not STILTS
    //public static final String SHOW_ERRORS = "show_errors"; // not STILTS
    
    // for the plot legend
    public static final String SHOW_LEGEND = "legend";
    public static final String LEGEND_BORDER = "legborder";
    public static final String LEGEND_OPAQUE = "legopaque";
    public static final String LEGEND_POSITION = "legpos";
    
    // Plot Types - Iris-specific, not STILTS.
    public enum PlotType {
        LOG("log", true, true),
        LINEAR("linear", false, false),
        X_LOG("xlog", true, false),
        Y_LOG("ylog", false, true);
        
        public String name;
        public boolean xlog;
        public boolean ylog;
    
        private PlotType(String name, boolean x, boolean y) {
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
        return new PlotPreferences()
                .setXlog(true)
                .setYlog(true)
                .setShowGrid(true)
                .setFixed(false)
//                .setShowErrors(true)
                .setPlotType(PlotType.LOG)
                .setShowLegend(true)
                .setLegendPosition(1.0, 1.0)
                .setLegendOpaque(false)
                .setLegendBorder(true);
    }
    
    private Map<String, Object> preferences;

    public PlotPreferences() {
        this.preferences = new HashMap<String, Object>();
    }
    
    public Map<String, Object> getPreferences() {
        return preferences;
    }
    
    public PlotPreferences setShowGrid(boolean arg1) {
        this.preferences.put(GRID, arg1);
        return this;
    }
    
    public boolean getShowGrid() {
        return (boolean) this.preferences.get(GRID);
    }
    
    public PlotPreferences setYlabel(String arg1) {
        this.preferences.put(Y_LABEL, arg1);
        return this;
    }
    
    public String getYlabel() {
        return (String) this.preferences.get(Y_LABEL);
    }
    
    public PlotPreferences setXlabel(String arg1) {
        this.preferences.put(X_LABEL, arg1);
        return this;
    }
    
    public String getXlabel() {
        return (String) this.preferences.get(X_LABEL);
    }
    
    public PlotPreferences setXlog(boolean arg1) {
        this.preferences.put(X_LOG, arg1);
        return this;
    }
    
    public boolean getXlog() {
        return (boolean) this.preferences.get(X_LOG);
    }
    
    public PlotPreferences setYlog(boolean arg1) {
        this.preferences.put(Y_LOG, arg1);
        return this;
    }
    
    public boolean getYlog() {
        return (boolean) this.preferences.get(Y_LOG);
    }
    
    public PlotPreferences setFixed(boolean arg1) {
        this.preferences.put(AUTO_FIX, arg1);
        return this;
    }
    
    public boolean getFixed() {
        return (boolean) this.preferences.get(AUTO_FIX);
    }
    
    public PlotPreferences setXmax(double arg1) {
        this.preferences.put(X_MAX, arg1);
        return this;
    }
    
    public double getXmax() {
        return (double) this.preferences.get(X_MAX);
    }
    
    public PlotPreferences setXmin(double arg1) {
        this.preferences.put(X_MIN, arg1);
        return this;
    }
    
    public double getXmin() {
        return (double) this.preferences.get(X_MIN);
    }
    
    public PlotPreferences setYmax(double arg1) {
        this.preferences.put(Y_MAX, arg1);
        return this;
    }
    
    public double getYmax() {
        return (double) this.preferences.get(Y_MAX);
    }
    
    public PlotPreferences setYmin(double arg1) {
        this.preferences.put(Y_MIN, arg1);
        return this;
    }
    
    public double getYmin() {
        return (double) this.preferences.get(Y_MIN);
    }
    
    public PlotPreferences setPlotType(PlotType arg1) {
        this.preferences.put(PLOT_TYPE, arg1);
        setXlog(arg1.xlog);
        setYlog(arg1.ylog);
        return this;
    }
    
    public PlotType getPlotType() {
        return (PlotType) preferences.get(PLOT_TYPE);
    }
    
//    public PlotPreferences setShowErrors(boolean arg1) {
//        this.preferences.put(SHOW_ERRORS, arg1);
//        return this;
//    }
    
    public PlotPreferences setShowLegend(boolean arg1) {
        this.preferences.put(SHOW_LEGEND, arg1);
        return this;
    }
    
    public boolean getShowLegend() {
        return (boolean) this.preferences.get(SHOW_LEGEND);
    }
    
    public PlotPreferences setLegendOpaque(boolean arg1) {
        this.preferences.put(LEGEND_OPAQUE, arg1);
        return this;
    }
    
    public boolean getLegendOpaque() {
        return (boolean) this.preferences.get(LEGEND_OPAQUE);
    }
    
    public PlotPreferences setLegendPosition(double xratio, double yratio) {
        this.preferences.put(LEGEND_POSITION, new double[] {xratio, yratio});
        return this;
    }
    
    public double[] getLegendPostion() {
        return (double[]) this.preferences.get(LEGEND_POSITION);
    }
    
    public PlotPreferences setLegendBorder(boolean arg1) {
        this.preferences.put(LEGEND_BORDER, arg1);
        return this;
    }
    
    public boolean getLegendBorder() {
        return (boolean) this.preferences.get(LEGEND_BORDER);
    }
}

