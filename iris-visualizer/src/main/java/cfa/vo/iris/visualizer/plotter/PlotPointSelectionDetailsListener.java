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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cfa.vo.iris.sed.stil.SegmentStarTable.Column;
import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;

/**
 * Shows a tooltip with point name and details when selecting a point
 * in the plotter.
 *
 * TODO: This functionality should ideally be done by a mouse hover function - but
 *  the current implemenation of a PlotDisplay in Stil limits our access to point
 *  values to point selection events - so we simulate a tooltip using a custom JPopup
 *  Object.
 */
public class PlotPointSelectionDetailsListener extends StilPlotterPointSelectionListener
{
    private PlotDisplay<?, ?> display;
    PointDataPopup popup = new PointDataPopup();

    @Override
    public void handleSelection(int starTableIndex, int irow, PointSelectionEvent evt) {
        MetadataBrowserMainView mbView = this.getPlotterView().getMetadataBrowserView();
        
        // Get selected star table based on PointSelectionEvent
        IrisStarTable table = mbView.getSelectedTables().get(starTableIndex);
        ColumnIdentifier id = new ColumnIdentifier(table);
        
        String tt;
        try {
            
            // Grab the selected row, format the info String
            Object[] row = table.getRow(irow);
            double x = (double) row[id.getColumnIndex(Column.Spectral_Value.name())];
            double y = (double) row[id.getColumnIndex(Column.Flux_Value.name())];
            tt = String.format("%s (%s, %s)", table.getName(), formatNumber(x), formatNumber(y));
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Display the row in a popup menu
        popup.setDataString(tt);
        
        // The popup shows up so that the mouse cursor is over the JPopup, so that when the mouse
        // moves off of the window it will disappear in a predictable manner.
        popup.show(display, evt.getPoint().x - 3, evt.getPoint().y - 3);
        
        return;
    }

    @Override
    public void activate(PlotDisplay<?, ?> display) {
        display.addPointSelectionListener(this);
        this.display = display;
    }
    
    private static class PointDataPopup extends JPopupMenu {
        
        private JMenuItem item;
        
        public PointDataPopup() {
            
            item = new JMenuItem();
            add(item);
            
            // Remove these to avoid highlighting the menu item
            item.removeMouseListener(item.getMouseListeners()[0]);
            item.removeMouseMotionListener(item.getMouseMotionListeners()[0]);
            
            // Get rid of the popup like a tooltip
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    setVisible(false);
                }
                @Override
                public void mouseEntered(MouseEvent arg0) {
                }
                @Override
                public void mouseExited(MouseEvent arg0) {
                    setVisible(false);
                }
                @Override
                public void mousePressed(MouseEvent arg0) {
                    setVisible(false);
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                }
            });
        }
        
        public void setDataString(String tt) {
            item.setText(tt);
        }
    }
}
