/**
 * This code was originally included in Specview:
 * www.stsci.edu/institute/software_hardware/specview
 *
 * and distributed under a BSD license,
 * as described in the LICENSE.STScI. file.
 *
 * Copyright goes to the original authors.
 *
 * This code has then been embedded into Iris, modified
 * and redistributed as
 * described in the LICENSE.IRIS file.
 * Copyright 2015 Smithsonian Astrophysical Observatory
 *
 **/
package cfa.vo.iris.units.spv;


/*
 *  Revision history:
 *  ----------------
 *
 *
 *  12 Mar 98  -  Implemented (IB)
 *  23 Oct 98  -  Add BIN_NUMBER constant (IB)
 *  20 Sep 99  -  Standard units, conversion algorithms (IB)
 *  25 Oct 99  -  Ordered wavelengths flag (IB)
 *  15 Dec 99  -  Moved to 'spv.util' package (IB)
 *  18 Jul 00  -  New converter methods (IB)
 *  31 Jul 00  -  Abstract class (IB)
 *  23 Oct 02  -  Axis labels (IB)
 *  20 Aug 04  -  isValid() instance method (IB)
 *  31 Jan 05  -  SED spelling (IB)
 */

        import java.io.Serializable;

/**
 *  Class that handles physical units.
 *  <p>
 *  Support for units conversion exist inside each <code>Units</code>
 *  object itself, by defining one of the possible units types to be
 *  "standard".
 *  <p>
 *  Static methods support conversion of entire arrays
 *  of <code>double</code> data (for the sake of efficiency).
 *
 *
 *
 *
 *  @version  1.2 - 31Jul00
 *  @version  1.1 - 20Sep99
 *  @version  1.0 - 12Mar98
 *  @author   Ivo Busko (Space Telescope Science Institute)
 */

public abstract class Units implements Serializable {

    static final long serialVersionUID = 12L;
    static final String ERROR_MSG = " Invalid units conversion ";

    protected String unitsString;
    protected String originalSpelling;
    protected String ucd = null;

    /**
     *  Compares two <code>Units</code> object for equality.
     *
     *  @param  units  the <code>Units</code> object to be compared
     *                 with this
     */
    @Override
    public boolean equals (Object units) {
        if (units == null || this.unitsString == null) {
            return false;
        } else {
            return units.toString().equalsIgnoreCase(this.unitsString);
        }
    }

    /**
     *  Return the hashcode of the string representing this instance (i.e. the return value of toString()).
     *  Note that since the @equals method ignores case, the hashcode is computed on the lower case string.
     *
     *  @return   the hashcode of the string representing this instance, after being transformed to lower case.
     */
    @Override
    public int hashCode() {
        return toString().toLowerCase().hashCode();
    }

    /**
     *  Gets the original spelling for this units instance.
     *
     *  @return    the original spelling for this units instance
     */
    public String getOriginalSpelling () {
        return originalSpelling;
    }

    /**
     *  Returns a <code>String</code> representation of the units.
     *
     *  @return  the string that represents this, or an empty string ("")
     *           if no units are defined.
     */
    @Override
    public String toString() {
        return unitsString == null ? "" : unitsString;
    }

    /**
     *  Sets a UCD string.
     *
     *  @param   ucd   the UCD string
     */
    public void setUCD(String ucd) {
        this.ucd = ucd;
    }

}