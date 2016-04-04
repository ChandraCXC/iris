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

import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cfa.vo.iris.sed.stil.SegmentStarTable.Column;
import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;
import uk.ac.starlink.ttools.plot2.task.PointSelectionListener;

/**
 * Shows a tooltip with point name and details when selecting a point
 * in the plotter.
 *
 * TODO: This functionality should ideally be done by a mouse hover function - but
 *  the current implemenation of a PlotDisplay in Stil limits our access to point
 *  values to point selection events.
 */
public class PlotPointSelectionDetailsListener extends StilPlotterMouseListener
        implements PointSelectionListener 
{
    
    private static Logger logger = Logger.getLogger(PlotPointSelectionDetailsListener.class.getName());
    
    private PlotDisplay<?, ?> display;

    @Override
    public void pointSelected(PointSelectionEvent evt) {
        try {
            processEvent(evt);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
    
    private void processEvent(PointSelectionEvent evt) throws Exception {
        MetadataBrowserMainView mbView = this.getPlotterView().getMetadataBrowserView();
        
        long[] rows = evt.getClosestRows();
        for (int i=0; i*2<rows.length; i++) {
            if (rows[2*i] < 0) {
                continue;
            }
            
            // Get selected star table based on PointSelectionEvent
            IrisStarTable table = mbView.getSelectedTables().get(i);
            ColumnIdentifier id = new ColumnIdentifier(table);
            
            // Grab the selected row, format the info String
            Object[] row = table.getRow(rows[2*i]);
            double x = (double) row[id.getColumnIndex(Column.Spectral_Value.name())];
            double y = (double) row[id.getColumnIndex(Column.Flux_Value.name())];
            String tt = String.format("%s (%s, %s)", table.getName(), formatNumber(x), formatNumber(y));
            
            // Display the row in a popup menu
            JPopupMenu popup = new JPopupMenu();
            popup.add(new JMenuItem(tt));
            popup.show(display, evt.getPoint().x, evt.getPoint().y);
            
            return;
        }
    }

    @Override
    public void activate(PlotDisplay<?, ?> display) {
        display.addPointSelectionListener(this);
        this.display = display;
    }
}
