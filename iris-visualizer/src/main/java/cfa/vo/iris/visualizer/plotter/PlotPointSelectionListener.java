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

/**
 * MouseListener which allows users to click on the PlotDisplay and add the selected
 * point to the selection in the metadata browser.
 *
 */
public class PlotPointSelectionListener extends StilPlotterPointSelectionListener
{
    @Override
    public void handleSelection(int starTableIndex, int irow, PointSelectionEvent evt) {
        MetadataBrowserMainView mbView = this.getPlotterView().getMetadataBrowserView();
        
        // The plotter irow does not take into account masks, so we map it to the row in the
        // base table before passing it to the metadata browser.
        irow = mbView.getDataModel().getSedStarTables().get(starTableIndex).getBaseTableRow(irow);
        mbView.addRowToSelection(starTableIndex, irow);
    }
    
    @Override
    public void activate(PlotDisplay<?,?> display) {
        display.addPointSelectionListener(this);
    }
}
