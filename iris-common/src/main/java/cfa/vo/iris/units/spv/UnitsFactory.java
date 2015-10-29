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

    public static final int X = 0;
    public static final int Y = 1;

    /**
     *  Creates a <code>Units</code> object of appropriate sub-type,
     *  based on a string representation of the units. In order to build
     *  a valid <code>Units</code> object, the units must be supported
     *  by the units conversion methods in the subclasses. If not supported,
     *  a <code>null</code> object is returned instead.
     *
     *  @param  units  the string that defines the units
     *  @return        the <code>Units</code> object of appropriate subclass
     */
    public static Units MakeUnits (String units) {

        String test_xunits = XUnits.GetCorrectSpelling(units);
        String test_yunits = YUnits.GetCorrectSpelling(units);

        if (XUnits.converters.get (test_xunits) != null ||
                XUnits.correct.containsKey(test_xunits)) {
            return new XUnits (units);
        } else if (YUnits.converters.get (test_yunits) != null ||
                YUnits.correct.containsKey(test_yunits)) {
            return new YUnits (units);
        } else {
            return null;
        }
    }

    /**
     *  Creates a <code>Units</code> object of appropriate sub-type,
     *  based on a string representation of the units. In order to build
     *  a valid <code>Units</code> object, the units must be supported
     *  by the units conversion methods in the subclasses. If not supported,
     *  a <code>UnknownUnits</code> subtype object is returned instead.
     *  The subtype is based on the "type" value.
     *
     *  @param  units  the string that defines the units
     *  @param  type   the subtype
     *  @return        the <code>Units</code> object of appropriate subclass
     */
    public static Units MakeUnits(String units, int type) {

        Units result = MakeUnits (units);

        if (! isConsistent(result, type)) {
            result = null;
        }

        if (result == null) {
            switch (type) {
                case X:
                    return new UnknownXUnits(units.toString());
                case Y:
                    return new UnknownYUnits(units.toString());
            }
        }
        return result;
    }

    /**
     *  Checks if the units built by the factory method is consistent
     *  with the requested type. This complicated logic results from the
     *  fact that in some circunstances a valid X units may be used to
     *  build an instance of Y units, or vice versa.
     *
     *  @param  units   the units
     *  @param  type    the type
     *  @return         <code>true</code> only if they match
     */
    private static boolean isConsistent(Units units, int type) {
        if (units instanceof YUnits) {
            if (YUnits.IsValidUnits(units) && type == Y) {
                return true;
            }
            return false;
        }

        if (units instanceof XUnits) {
            if (XUnits.IsValidUnits(units) && type == X) {
                return true;
            }
            return false;
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////
    //
    //                       SELF-TEST
    //
    /////////////////////////////////////////////////////////////////


    /**
     *  Self-test.
     *
     *  To run, type this command after running make:
     *  <p>
     *  <code>% java spv.util.UnitsFactory </code>
     */
//    public static void main (String[] args) {
//
//        System.out.println ((UnitsFactory.MakeUnits ("Angstrom")).toString());
//        System.out.println ((UnitsFactory.MakeUnits ("mJy")).toString());
//        System.out.println ((UnitsFactory.MakeUnits ("blah")).toString());
//
//        System.out.println ("Done !");
//    }
}
