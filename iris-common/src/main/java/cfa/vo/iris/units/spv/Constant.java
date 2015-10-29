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
 *  13 Mar 98  -  Implemented (IB)
 */

/**
 *  This interface defines general-purpose constants.
 *
 *
 *
 *  @version  1.0 - 13Mar98
 *  @author   Ivo Busko (Space Telescope Science Institute)
 */

public interface Constant {

    /** Double maximum */
//    public static final double DMAX = 1.0000000000000000E70;
    public static final double DMAX = Double.MAX_VALUE;

    /** Double minimum */
    public static final double DMIN = -DMAX;

    /** Double INDEF */
    public static final double DATA_MARKER = -1.1000000000000000E70;

    /** Int maximum */
    public static final int IMAX = 32766;

    /** Int minimum */
    public static final int IMIN = -IMAX;

    /** Int INDEF */
    public static final int IINDEF = 32767;

    /** Used for model selection/identification in the fitting code */
    public final String MODEL_ID      = new String ("Model");
    public final String RESIDUALS_ID  = new String ("Residuals");
    public final String NORMALIZED_ID = new String ("Ratio");
}
