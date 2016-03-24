/*
 * Copyright 2016 Chandra X-Ray Observatory.
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
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;
import uk.ac.starlink.ttools.plot2.task.PlanePlot2Task;

/*
 * These are plot preferences outside the STILTS regime.
 */
public class OtherPlotPreferences {
    
    public static final String ASPECT = "aspect";
    
    private Map<String, Object> preferences;
    
    /**
     * 
     * @return default plot preferences
     */
    public static OtherPlotPreferences getDefaultPreferences() {
        double[] xlimits = new double[] {0, 10};
        double[] ylimits = new double[] {0, 10};
        PlaneAspect aspect = new PlaneAspect(xlimits, ylimits);
        return new OtherPlotPreferences()
                .setAspect(aspect);
    }

    public OtherPlotPreferences() {
        this.preferences = new HashMap<String, Object>();
    }
    
    public Map<String, Object> getPreferences() {
        return preferences;
    }
    
    public OtherPlotPreferences setAspect(PlaneAspect arg1) {
        this.preferences.put(ASPECT, arg1);
        return this;
    }
    
    public PlaneAspect getAspect() {
        return (PlaneAspect) preferences.get(ASPECT);
    }
}
