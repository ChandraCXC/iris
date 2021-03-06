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

import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.fitting.FittingRangesFrame;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedException;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.ttools.plot2.Surface;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;

/**
 *
 * Mouse Listener for getting fit ranges from a PlotDisplay.
 */
public class MouseXRangesClickedListener extends StilPlotterMouseListener implements MouseListener {
    private PlotDisplay<?,?> display;
    private VisualizerDataModel dataModel;
    private FittingRange fittingRange;
    private boolean isStartPoint; // flag for if it's a first (start) or second (end) click on the plot
    private boolean pickingRanges; // flag for if fit ranges are currently being choosen. This mouse listener only reacts if this flag is set to true.
    private FittingRangesFrame frame; // For refreshing when we've added a range to a FitConfiguration
    
    @Override
    public void setPlotterView(PlotterView plotterView) {
        super.setPlotterView(plotterView);
    }

    @Override
    public void activate(PlotDisplay<?,?> display, VisualizerDataModel dataModel) {
        this.display = display;
        this.dataModel = dataModel;
        isStartPoint = true;   // the first click on the plotter will always be the starting point.
        pickingRanges = false; // set picking ranges off initially
        display.addMouseListener(this);
    }
    
    // mouseClicked() only works when pickingRanges flag == true
    public boolean isPickingRanges() {
        return pickingRanges;
    }
    
    public void setPickingRanges(boolean pickingRanges, FittingRangesFrame frame) {
        if (display != null) {
            display.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
        this.pickingRanges = pickingRanges;
        this.fittingRange = new FittingRange();
        this.frame = frame;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        // only do anything if pickingRanges is true.
        if (pickingRanges) {
            if (display == null || display.getSurface() == null) {
                return;
            }

            // get the x value of the cursor (in the plotter's units)
            Surface surface = display.getSurface();

            Point p = evt.getPoint();
            if (!surface.getPlotBounds().contains(p)) {
                return;
            }
            
            double x = surface.graphicsToData(p, null)[0];

            if (isStartPoint) {
                fittingRange.setStartPoint(x);
                isStartPoint = false;
            } else {
                display.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                fittingRange.setEndPoint(x);
                
                // set unit
                try {
                    fittingRange.setXUnit(XUnit.getFromUnitString(dataModel.getXunits()));
                } catch (SedException ex) {
                    Logger.getLogger(MouseXRangesClickedListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // add fitting range to SED fit configuration
                for (ExtSed sed : plotterView.getDataModel().getSelectedSeds()) {
                    sed.getFit().addFittingRange(fittingRange);
                }
                
                // reset fitting range flags
                isStartPoint = true;
                pickingRanges = false;
                
                // Reset the plotter
                dataModel.refresh();
                
                // Refresh the frame, if available
                if (frame != null) {
                    frame.updateTable();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        // do nothing
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        // do nothing
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        // do nothing
    }

    @Override
    public void mouseExited(MouseEvent me) {
        // do nothing
    }
}
