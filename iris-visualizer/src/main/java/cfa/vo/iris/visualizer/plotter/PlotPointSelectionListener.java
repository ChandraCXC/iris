package cfa.vo.iris.visualizer.plotter;

import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;
import uk.ac.starlink.ttools.plot2.task.PointSelectionListener;

public class PlotPointSelectionListener extends StilPlotterMouseListener
    implements PointSelectionListener 
{
    
    @Override
    public void pointSelected(PointSelectionEvent evt) {
        
        // Get selected star tables out of the Metadata Browser
        MetadataBrowserMainView mbView = this.getPlotterView().getMetadataBrowserView();
        
        long[] rows = evt.getClosestRows();
        for (int i=0; i*2<rows.length; i++) {
            if (rows[2*i] >= 0) {
                mbView.addRowToSelection(i, (int) rows[2*i]);
            }
        }
    }
    
    @Override
    public void activate(PlotDisplay<?,?> display) {
        display.addPointSelectionListener(this);
    }
}
