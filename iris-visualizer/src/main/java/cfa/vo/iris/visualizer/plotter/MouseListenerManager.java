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

import java.util.LinkedHashSet;
import java.util.Set;

import cfa.vo.iris.visualizer.plotter.MouseCoordinateMotionListener;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurfaceFactory.Profile;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;

/**
 * Manages all mouse listeners that are applied to the StilPlotter. 
 * Listeners will always be added whenever a PlotDisplay object is created
 * in the StilPlotter by way of the @activateListeners method.
 *
 */
public class MouseListenerManager {
    
    private Set<StilPlotterMouseListener> listeners;
    private VisualizerComponentPreferences preferences;
    private PlotterView view;
    
    // Collection of all mouse listeners the plotter
    MouseCoordinateMotionListener mouseCoordinateMotionListener;
    PlotPointSelectionListener pointSelectionListener;
    PlotPointSelectionDetailsListener pointDetailsListener;
    
    public MouseListenerManager(VisualizerComponentPreferences preferences) {
        this.listeners = new LinkedHashSet<>();
        this.preferences = preferences;
        
        // Shows mouse coordinates in the plot view window
        mouseCoordinateMotionListener = new MouseCoordinateMotionListener();
        listeners.add(mouseCoordinateMotionListener);
        
        // Selects points in the MB from the plotter
        pointSelectionListener = new PlotPointSelectionListener();
        listeners.add(pointSelectionListener);
        
        // Shows a tooltip when clicking on a point
        pointDetailsListener = new PlotPointSelectionDetailsListener();
        listeners.add(pointDetailsListener);
    }
    
    public void setPlotterView(PlotterView view) {
        this.view = view;
        for (StilPlotterMouseListener listener : listeners) {
            listener.setPlotterView(view);
        }
    }
    
    public Set<StilPlotterMouseListener> getListeners() {
        return listeners;
    }
    
    public void activateListeners(PlotDisplay<Profile, PlaneAspect> plotDisplay) {
        for (StilPlotterMouseListener listener : listeners) {
            if (listener.isActive()) {
                listener.activate(plotDisplay, preferences.getDataModel());
            }
        }
    }
}
