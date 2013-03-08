/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top source directory.
 */

package spv.components;

/**
 * Created by IntelliJ IDEA.
 * User: busko
 * Date: 4/2/12
 * Time: 9:54 AM
 */


import spv.glue.PlottableSEDSegmentedSpectrum;
import spv.glue.PlottableSegmentedSpectrum;
import spv.graphics.LegendCanvas;
import spv.spectrum.SEDMultiSegmentSpectrum;
import spv.util.Units;
import spv.view.BasicPlotWidget;
import spv.view.Plottable;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
This class overrides the behavior of a basic plot widget in two ways:

  - It add a legend canvas to the canvas stack.

  - Because we are re-purposing the pan canvas as a residuals plot area when
    fitting, the pan canvas must be forcibly turned off when displaying a
    non-fitted SED.
*/

public class SEDBasicPlotWidget extends BasicPlotWidget {

    private boolean coplot;

    public SEDBasicPlotWidget(Plottable pl, boolean no_access) {
        super(pl, no_access);

        hideshow.setVisible(false);

        SEDMultiSegmentSpectrum rootObject = (SEDMultiSegmentSpectrum) pl.getRootObject();
        coplot = rootObject.getName().startsWith(PlottableSEDSegmentedSpectrum.COPLOT_IDENT);
    }

    public boolean getFromPlotStatus() {
        boolean use_status = super.getFromPlotStatus();
        if (use_status) {
            show_pan = false;
        }
        return use_status;
    }

    protected void assembleCanvases(String xtitle, String ytitle,
                                    Units x_units, Units y_units,
                                    boolean use_status) {

        super.assembleCanvases(xtitle, ytitle, x_units, y_units, use_status);

        canvas = new LegendCanvas(canvas);

        ((LegendCanvas)canvas).setCoplotMode(coplot);

        // at this point, plottable.spColors contains the colors used to
        // draw segments. It's a map, one entry per point, with keys
        // being the point IDs and the values being instances of Color.
        //
        // we should build from that map another map where the key is
        // the object ID string and the value is the color instance.
        // That way we will have just one entry per target. That map
        // should be passed to the canvas so it can build the legend.
        //
        // The object name is the full key of the spColors map, stripped
        // of the file, segment, and point number digits. A typical entry
        // in spColors looks like:
        //
        //      MESSIER 087 -1_2_80  ->  java.awt.COLOR[r=0,g=0,b=0]
        //
        // Note that all this must be activated only if in co-plot mode.

        if (coplot) {
            Map targetIDColors = buildTargetIDMap();

            ((LegendCanvas)canvas).setLegendColorMap(targetIDColors);
        }
    }

    private Map buildTargetIDMap() {

        Map<String,Color> result = new HashMap<String,Color>();
        Map pointColors = ((PlottableSegmentedSpectrum) plottable).getColors();

        Set keys = pointColors.keySet();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            int i1 = key.indexOf("-");
            String targetID = key.substring(0, i1);

            result.put(targetID, (Color) pointColors.get(key));
        }
        return result;
    }
}
