package cfa.vo.iris.visualizer.plotter;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;

import cfa.vo.iris.visualizer.preferences.LayerModel;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import cfa.vo.iris.visualizer.stil.tables.StackedStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;
import uk.ac.starlink.ttools.plot2.task.PointSelectionListener;

public abstract class StilPlotterPointSelectionListener
        extends StilPlotterMouseListener
        implements PointSelectionListener
{
    
    private static Logger logger = Logger.getLogger(StilPlotterPointSelectionListener.class.getName());
    VisualizerDataModel dataModel;
    
    @Override
    public void activate(PlotDisplay<?,?> display, VisualizerDataModel dataModel) {
        this.dataModel = dataModel;
        display.addPointSelectionListener(this);
    }
    
    @Override
    public void pointSelected(PointSelectionEvent evt) {
        
        // Do nothing if there are no layers
        if (CollectionUtils.isEmpty(dataModel.getLayerModels())) {
            return;
        }
        
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
        
        // Find the PlotLayer closest to the selected row
        long[] rows = evt.getClosestRows();
        int index = 0;
        Iterator<LayerModel> it = dataModel.getLayerModels().iterator();
        
        // For coplotting, either layers.size is either equal to the number of SedModels in the
        // dataModel or the number of IrisStarTables in the DataModel. For the former, we need
        // to identify the correct segment identified by the row. For the latter we need to identify
        // the correct startable identified by the layer index.
        LayerModel model = it.next();
        while (index < rows.length) {
            if (rows[index] >= 0) {
                if (dataModel.isCoplotted()) {
                    handleCoplotting(model, rows[index], evt);
                } else {
                    int tableIndex = dataModel.getSedStarTables().indexOf(model.getInSource());
                    handleSelection(tableIndex, (int) rows[index], evt);
                }
                
                return;
            }
            
            // Skip to the next layer
            index += model.getNumberOfLayers();
            model = it.next();
        }
    }
    
    private void handleCoplotting(LayerModel model, long row, PointSelectionEvent evt) {
        List<StarTable> sedTables = ((StackedStarTable) model.getInSource())
                .getBaseTables();
        
        // Do nothing if the table is empty
        if (CollectionUtils.isEmpty(sedTables)) return;
        
        // Otherwise find the correct StarTable
        Iterator<StarTable> it = sedTables.iterator();
        StarTable t = it.next();
        while (row > t.getRowCount()) {
            row -= t.getRowCount();
            t = it.next();
        }
        int tableIndex = dataModel.getSedStarTables().indexOf(t);
        handleSelection(tableIndex, (int) row, evt);
    }
    
    public abstract void handleSelection(int starTableIndex, int irow, PointSelectionEvent evt);
}
