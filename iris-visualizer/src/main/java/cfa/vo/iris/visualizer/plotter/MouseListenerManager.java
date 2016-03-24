package cfa.vo.iris.visualizer.plotter;

import java.util.LinkedHashSet;
import java.util.Set;

import cfa.vo.iris.visualizer.plotter.MouseCoordinateMotionListener;
import uk.ac.starlink.ttools.plot2.geom.PlaneAspect;
import uk.ac.starlink.ttools.plot2.geom.PlaneSurfaceFactory.Profile;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;

public class MouseListenerManager {
    
    private Set<StilPlotterMouseListener> listeners;
    private PlotterView view;
    
    // Collection of all mouse listeners the plotter
    MouseCoordinateMotionListener mouseCoordinateMotionListener;
    PlotPointSelectionListener pointSelectionListener;
    
    public MouseListenerManager() {
        this.listeners = new LinkedHashSet<>();
        
        // Shows mouse coordinates in the plot view window
        mouseCoordinateMotionListener = new MouseCoordinateMotionListener();
        listeners.add(mouseCoordinateMotionListener);
        
        // Prints point selection values to stdout
        pointSelectionListener = new PlotPointSelectionListener();
        listeners.add(pointSelectionListener);
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
                listener.activate(plotDisplay);
            }
        }
    }
}
