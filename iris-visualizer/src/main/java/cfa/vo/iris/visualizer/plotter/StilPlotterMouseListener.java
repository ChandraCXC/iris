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

import java.text.DecimalFormat;

import uk.ac.starlink.ttools.plot2.task.PlotDisplay;

public abstract class StilPlotterMouseListener {
    
    PlotterView plotterView;
    boolean active = true;
    
    protected final static DecimalFormat df = new DecimalFormat("0.######E0");
    static {
        df.setMaximumFractionDigits(6);
    }
    
    public abstract void activate(PlotDisplay<?,?> display);

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
    
    protected static String formatNumber(double d) {
        return df.format(d);
    }
}
