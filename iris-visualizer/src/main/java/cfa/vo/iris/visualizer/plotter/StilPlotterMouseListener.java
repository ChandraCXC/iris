package cfa.vo.iris.visualizer.plotter;

import uk.ac.starlink.ttools.plot2.task.PlotDisplay;

public abstract class StilPlotterMouseListener {
    
    PlotterView plotterView;
    boolean active = true;
    
    public abstract void activate(PlotDisplay display);

    public PlotterView getPlotterView() {
        return plotterView;
    }

    public void setPlotterView(PlotterView plotterView) {
        this.plotterView = plotterView;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
