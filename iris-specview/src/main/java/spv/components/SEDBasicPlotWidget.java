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

import spv.view.BasicPlotWidget;
import spv.view.Plottable;

/**
The sole purpose of this class is to override the basic behavior of
a plot widget that inherits a plot status object from its predecessor.
Because we are re-purposing the pan canvas as a residuals plot area when
fitting, the pan canvas must be forcibly turned off when displaying a
non-fitted SED.
 */

public class SEDBasicPlotWidget extends BasicPlotWidget {

    public SEDBasicPlotWidget(Plottable pl, boolean no_access) {
        super(pl, no_access);
    }

    public boolean getFromPlotStatus() {
        boolean use_status = super.getFromPlotStatus();
        if (use_status) {
            show_pan = false;
        }
        return use_status;
    }
}
