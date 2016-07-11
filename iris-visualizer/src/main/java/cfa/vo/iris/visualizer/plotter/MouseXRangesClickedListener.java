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
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import uk.ac.starlink.ttools.plot2.Surface;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;

/**
 *
 * Mouse Listener for getting fit ranges from a PlotDisplay.
 */
public class MouseXRangesClickedListener extends StilPlotterMouseListener implements MouseListener {
    private PlotDisplay<?,?> display;
    private FittingRange fittingRange = new FittingRange();
    private boolean isStartPoint; // flag for if it's a first (start) or second (end) click on the plot
    private boolean pickingRanges; // flag for if fit ranges are currently being choosen. This mouse listener only reacts if this flag is set to true.

    @Override
    public void setPlotterView(PlotterView plotterView) {
        super.setPlotterView(plotterView);
    }

    @Override
    public void activate(PlotDisplay<?,?> display, VisualizerDataModel dataModel) {
        this.display = display;
        isStartPoint = true;   // the first click on the plotter will always be the starting point.
        pickingRanges = false; // set picking ranges off initially
        display.addMouseListener(this);
    }
    
    // mouseClicked() only works when pickingRanges flag == true
    public boolean isPickingRanges() {
        return pickingRanges;
    }
    
    public void setPickingRanges(boolean pickingRanges) {
        this.pickingRanges = pickingRanges;
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
                fittingRange.setEndPoint(x);
                
                // add fitting range to SED fit configuration
                for (ExtSed sed : plotterView.getDataModel().getSelectedSeds()) {
                    sed.getFit().addFittingRange(fittingRange);
                }
                
                // reset fitting range flags
                isStartPoint = true;
                pickingRanges = false;
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
