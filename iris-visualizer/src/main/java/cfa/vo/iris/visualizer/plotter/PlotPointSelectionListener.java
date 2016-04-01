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

import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;
import uk.ac.starlink.ttools.plot2.task.PointSelectionListener;

public class PlotPointSelectionListener extends StilPlotterMouseListener
    implements PointSelectionListener 
{
    
    @Override
    public void pointSelected(PointSelectionEvent evt) {
        MetadataBrowserMainView mbView = this.getPlotterView().getMetadataBrowserView();
        
        long[] rows = evt.getClosestRows();
        for (int i=0; i*2<rows.length; i++) {
            if (rows[2*i] >= 0) {
                mbView.addRowToSelection(i, (int) rows[2*i]);
                return;
            }
        }
    }
    
    @Override
    public void activate(PlotDisplay<?,?> display) {
        display.addPointSelectionListener(this);
    }
}
