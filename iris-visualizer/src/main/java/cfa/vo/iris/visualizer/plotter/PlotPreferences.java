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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import cfa.vo.iris.sed.stil.SegmentStarTable.ColumnName;

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

    // Override-able Settings
    public static final String SHAPE = "shape";
    public static final String TYPE = "type";
    public static final String LAYER = "layer";
    public static final String IN = "in";
    public static final String X_COL = "x";
    public static final String Y_COL = "y";
    public static final String X_ERR_HI = "xerrhi";
    public static final String Y_ERR_HI = "yerrhi";
    public static final String X_ERR_LO = "xerrlo";
    public static final String Y_ERR_LO = "yerrlo";
    public static final String COLOR = "color";
    public static final String ERROR_BAR = "errorbar";
    public static final String SIZE = "size";
    
    public static PlotPreferences getDefaultPlotPreferences() {
        return new PlotPreferences()
                .setColor("blue")
                .setXlog(true)
                .setYlog(true)
                .setGrid(true);
    }
    
    private Map<String, Object> preferences;

    public PlotPreferences() {
        this.preferences = new HashMap<String, Object>();
    }
    
    public Map<String, Object> getPreferences() {
        return preferences;
    }
    
    public PlotPreferences setType(String arg1) {
        this.preferences.put(TYPE, arg1);
        return this;
    }
    public PlotPreferences setColor(String arg1) {
        this.preferences.put(COLOR, arg1);
        return this;
    }
    public PlotPreferences setErrBar(String arg1) {
        this.preferences.put(ERROR_BAR, arg1);
        return this;
    }
    public PlotPreferences setShape(String arg1) {
        this.preferences.put(SHAPE, arg1);
        return this;
    }
    public PlotPreferences setGrid(boolean arg1) {
        this.preferences.put(GRID, arg1);
        return this;
    }
    public PlotPreferences setxCol(String arg1) {
        this.preferences.put(X_COL, arg1);
        return this;
    }
    public PlotPreferences setyCol(String arg1) {
        this.preferences.put(Y_COL, arg1);
        return this;
    }
    public PlotPreferences setXerrhi(String arg1) {
        this.preferences.put(X_ERR_HI, arg1);
        return this;
    }
    public PlotPreferences setXerrlo(String arg1) {
        this.preferences.put(X_ERR_LO, arg1);
        return this;
    }
    public PlotPreferences setYerrhi(String arg1) {
        this.preferences.put(Y_ERR_HI, arg1);
        return this;
    }
    public PlotPreferences setYerrlo(String arg1) {
        this.preferences.put(Y_ERR_LO, arg1);
        return this;
    }
    public PlotPreferences setYlabel(String arg1) {
        this.preferences.put(Y_LABEL, arg1);
        return this;
    }
    public PlotPreferences setXlabel(String arg1) {
        this.preferences.put(X_LABEL, arg1);
        return this;
    }
    public PlotPreferences setXlog(boolean arg1) {
        this.preferences.put(X_LOG, arg1);
        return this;
    }
    public PlotPreferences setYlog(boolean arg1) {
        this.preferences.put(Y_LOG, arg1);
        return this;
    }
}

