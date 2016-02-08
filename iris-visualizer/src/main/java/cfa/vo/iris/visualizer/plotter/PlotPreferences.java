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
    
    /**
     * 
     * @return default plot preferences
     */
    public static PlotPreferences getDefaultPlotPreferences() {
        return new PlotPreferences()
                .setXlog(true)
                .setYlog(true)
                .setShowGrid(true);
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
}

