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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import uk.ac.starlink.ttools.plot2.Surface;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;

/**
 * MouseListener that displays the mouse's current coordinates (in the PlotDisplay's
 * coordinate system) in the relevant PlotterView text objects.
 *
 */
public class MouseCoordinateMotionListener extends StilPlotterMouseListener 
    implements MouseMotionListener {
    
    private PlotDisplay<?,?> display;

    @Override
    public void setPlotterView(PlotterView plotterView) {
        super.setPlotterView(plotterView);
    }

    @Override
    public void activate(PlotDisplay<?,?> display) {
        this.display = display;
        display.addMouseMotionListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        if (display == null || display.getSurface() == null) {
            return;
        }

        Surface surface = display.getSurface();

        Point p = evt.getPoint();
        if (!surface.getPlotBounds().contains(p)) {
            return;
        }

        double[] loc = surface.graphicsToData(p, null);
        plotterView.setXcoord(formatNumber(loc[0]));
        plotterView.setYcoord(formatNumber(loc[1]));
    }
}
