/*
 * Copyright 2015 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

/**
 *
 * @author jbudynk
 */
public class StarTablePreferences {

        // This is only a small sample of the preferences available in STILTS, for
    // the full list, see
    // http://www.star.bris.ac.uk/~mbt/stilts/sun256/sun256.html#TypedPlot2Task
    // Global Settings
    public static final String SHAPE = "shape";
    public static final String GRID = "grid";
    public static final String X_LABEL = "xlabel";
    public static final String Y_LABEL = "ylabel";
    public static final String X_LOG = "xlog";
    public static final String Y_LOG = "ylog";

    // Override-able Settings
    public static final String TYPE = "layer";
    public static final String IN = "in";
    public static final String X_COL = "x";
    public static final String Y_COL = "y";
    public static final String X_ERR_HI = "xerrhi";
    public static final String Y_ERR_HI = "yerrhi";
    public static final String X_ERR_LO = "xerrlo";
    public static final String Y_ERR_LO = "yerrlo";
    public static final String COLOR = "color";
    public static final String ERROR_BAR = "errorbar";
    
    
    public Map<String, Object> preferences;

    public StarTablePreferences() {
        this.preferences = new HashMap<>();
    }
    
    public StarTablePreferences setType(String arg1) {
        this.preferences.put(TYPE, arg1);
        return this;
    }
    public StarTablePreferences setColor(String arg1) {
        this.preferences.put(COLOR, arg1);
        return this;
    }
    public StarTablePreferences setErrBar(String arg1) {
        this.preferences.put(ERROR_BAR, arg1);
        return this;
    }
    public StarTablePreferences setShape(String arg1) {
        this.preferences.put(SHAPE, arg1);
        return this;
    }
    public StarTablePreferences setGrid(boolean arg1) {
        this.preferences.put(GRID, arg1);
        return this;
    }
    public StarTablePreferences setxCol(String arg1) {
        this.preferences.put(X_COL, arg1);
        return this;
    }
    public StarTablePreferences setyCol(String arg1) {
        this.preferences.put(Y_COL, arg1);
        return this;
    }
    public StarTablePreferences setXerrhi(String arg1) {
        this.preferences.put(X_ERR_HI, arg1);
        return this;
    }
    public StarTablePreferences setXerrlo(String arg1) {
        this.preferences.put(X_ERR_LO, arg1);
        return this;
    }
    public StarTablePreferences setYerrhi(String arg1) {
        this.preferences.put(Y_ERR_HI, arg1);
        return this;
    }
    public StarTablePreferences setYerrlo(String arg1) {
        this.preferences.put(Y_ERR_LO, arg1);
        return this;
    }
    public StarTablePreferences setYlabel(String arg1) {
        this.preferences.put(Y_LABEL, arg1);
        return this;
    }
    public StarTablePreferences setXlabel(String arg1) {
        this.preferences.put(X_LABEL, arg1);
        return this;
    }
    public StarTablePreferences setXlog(boolean arg1) {
        this.preferences.put(X_LOG, arg1);
        return this;
    }
    public StarTablePreferences setYlog(boolean arg1) {
        this.preferences.put(Y_LOG, arg1);
        return this;
    }
    
    // Overrideable preferences
    public static class LayerPreferences {
        
        public Map<String, Object> preferences;
        private String suffix;

        public LayerPreferences(String layerName) {
            this.preferences = new HashMap<>();
            this.suffix = '_' + layerName;
        }

        public LayerPreferences setType(String arg1) {
            this.preferences.put(TYPE + suffix, arg1);
            return this;
        }
        public LayerPreferences setInSource(Object arg1) {
            this.preferences.put(IN + suffix, arg1);
            return this;
        }
        public LayerPreferences setColor(String arg1) {
            this.preferences.put(COLOR + suffix, arg1);
            return this;
        }
        public LayerPreferences setShape(String arg1) {
            this.preferences.put(SHAPE + suffix, arg1);
            return this;
        }
        public LayerPreferences setxCol(String arg1) {
            this.preferences.put(X_COL + suffix, arg1);
            return this;
        }
        public LayerPreferences setyCol(String arg1) {
            this.preferences.put(Y_COL + suffix, arg1);
            return this;
        }
        public LayerPreferences setXerrhi(String arg1) {
            this.preferences.put(X_ERR_HI + suffix, arg1);
            return this;
        }
        public LayerPreferences setXerrlo(String arg1) {
            this.preferences.put(X_ERR_LO + suffix, arg1);
            return this;
        }
        public LayerPreferences setYerrhi(String arg1) {
            this.preferences.put(Y_ERR_HI + suffix, arg1);
            return this;
        }
        public LayerPreferences setYerrlo(String arg1) {
            this.preferences.put(Y_ERR_LO + suffix, arg1);
            return this;
        }
    }
    
}
