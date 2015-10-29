/*
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top source directory.
 */
package cfa.vo.iris.units.spv;

/*
 *  Revision history:
 *  ----------------
 *
 *
 *  31 Jul 00  -  Implemented (IB)
 */


import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.YUnit;

/**
 *  This class provides a static method that delivers sub-classes of
 *  the <code>Units</code> class.
 *
 *
 *
 *
 *  @version  1.0 - 31Jul00
 *  @author   Ivo Busko (Space Telescope Science Institute)
 */

public class UnitsFactory {

    // All methods are static
    private UnitsFactory() {
    }

    /**
     *  Creates an <code>XUnit</code> object,
     *  based on a string representation of the units. In order to build
     *  a valid <code>Units</code> object, the units must be supported
     *  by the units conversion methods in the subclasses. If not supported,
     *  a <code>UnknownXUnits</code> subtype object is returned instead.
     *  The subtype is based on the "type" value.
     *
     *  @param  units  the string that defines the units
     *  @return        the <code>Units</code> object of appropriate subclass
     */
    public static XUnit makeXUnits(String units) {

        XUnit result = new XUnits(units);

        if (!result.isValid()) {
            result = new UnknownUnits();
        }

        return result;
    }

    /**
     *  Creates a <code>YUnit</code> object,
     *  based on a string representation of the units. In order to build
     *  a valid <code>Units</code> object, the units must be supported
     *  by the units conversion methods in the subclasses. If not supported,
     *  a <code>UnknownYUnits</code> subtype object is returned instead.
     *  The subtype is based on the "type" value.
     *
     *  @param  units  the string that defines the units
     *  @return        the <code>Units</code> object of appropriate subclass
     */
    public static YUnit makeYUnits(String units) {

        YUnit result = new YUnits(units);

        if (!result.isValid()) {
            result = new UnknownUnits();
        }

        return result;
    }

}
