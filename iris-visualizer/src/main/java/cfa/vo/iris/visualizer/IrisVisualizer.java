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
package cfa.vo.iris.visualizer;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.fitting.FitController;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;

/**
 * Singleton class designed to give the Visualizer's components access to shared
 * data storage/access objects.
 *
 */
public class IrisVisualizer {
    
    // Active visualizer component preferences
    private VisualizerComponentPreferences preferences;
    
    // Active FitController
    private FitController controller;
    
    private IrisVisualizer() {};
    
    private static class Holder {
        private static final IrisVisualizer INSTANCE = new IrisVisualizer();
    }
    
    public static IrisVisualizer getInstance() {
        return Holder.INSTANCE;
    }
    
    /**
     * Calls to construct or fetch the current active preferences for the visualizer
     * tools. As it is currently implemented, the preferences can only be created 
     * once. In the future we may want to allow simultaneous opening of multiple visualizers,
     * in which case this may be adjusted to point to the current visualizer prefereces
     * currently in focus.
     * @param ws IWorkspace for the preferences
     * @return Active VisualizerComponentPreferences
     */
    public VisualizerComponentPreferences createPreferences(IWorkspace ws) {
        if (preferences == null) {
            preferences = new VisualizerComponentPreferences(ws);
        }
        
        return preferences;
    }
    
    /**
     * @return The active VisualizerComponentPreferences
     */
    public VisualizerComponentPreferences getActivePreferences() {
        return preferences;
    }

    public void setFitController(FitController controller) {
        this.controller = controller;
    }
    
    public FitController getController() {
        return this.controller;
    }
}
