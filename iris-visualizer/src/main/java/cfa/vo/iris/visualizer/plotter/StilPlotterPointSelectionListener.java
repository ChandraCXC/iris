package cfa.vo.iris.visualizer.plotter;

import java.util.logging.Logger;

import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;
import uk.ac.starlink.ttools.plot2.task.PointSelectionListener;

public abstract class StilPlotterPointSelectionListener
        extends StilPlotterMouseListener
        implements PointSelectionListener
{
    
    private static Logger logger = Logger.getLogger(StilPlotterPointSelectionListener.class.getName());
    
    @Override
    public void pointSelected(PointSelectionEvent evt) {
        try {
            processEvent(evt);
        } catch (Exception e) {
            // Mouse events are handled asynchronously by the swing EDT, so just log a warning.
            logger.warning(e.getMessage());
        }
    }
    
    /**
     * Convert a PointSelectionEvent to a StarTable index in selectedTables and a row
     * coordinate within that table.
     * @param evt
     */
    private void processEvent(PointSelectionEvent evt) {
        
        // TODO: Change this to increment by the number of layers in a given segment layer, 
        // rather than just 2.
        long[] rows = evt.getClosestRows();
        for (int i=0; i*2<rows.length; i++) {
            // It is possible that the Event will contain multiple selected points, but
            // we will only use the first point selected. See http://tinyurl.com/gq9o7te
            if (rows[2*i] >= 0) {
                handleSelection(i, (int) rows[2*i], evt);
                return;
            }
        }
        
    }
    
    public abstract void handleSelection(int starTableIndex, int irow, PointSelectionEvent evt);
}
