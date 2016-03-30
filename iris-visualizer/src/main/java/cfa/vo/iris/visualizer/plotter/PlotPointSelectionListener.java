package cfa.vo.iris.visualizer.plotter;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;
import uk.ac.starlink.ttools.plot2.task.PointSelectionListener;

public class PlotPointSelectionListener extends StilPlotterMouseListener
    implements PointSelectionListener 
{

    @Override
    public void pointSelected(PointSelectionEvent evt) {
        // TODO: Something better than this
        System.out.println(ReflectionToStringBuilder.toString(evt));
    }

    @Override
    public void activate(PlotDisplay display) {
        display.addPointSelectionListener(this);
    }
}
