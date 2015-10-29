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
        import java.util.Enumeration;

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

public abstract class Units implements Constant, Cloneable, Serializable {

    public static final String UNITLESS = "Unitless";

    static final long serialVersionUID = 12L;
    static final String ERROR_MSG = " Invalid units conversion ";

    protected static final double H = 6.62620E-27;  // Planck's constant
    protected static final double C = 2.997925E+18; // speed of light (Angstrom/s)
    protected static final double E = H * C / 1.6021917E-6; // Angstrom * MeV

    protected String units_string;
    protected String original_spelling;
    protected String ucd = null;

    /**
     *  Sets the string value of the units object. This works as a
     *  pseudo-constructor because units ojects are basically defined
     *  by a string.
     *
     *  @param   value   the string value
     */
    public void setFromString (String value) {
        this.units_string = new String (value);
        getConverterObject();
    }

    /**
     *  Compares two <code>Units</code> object for equality.
     *
     *  @param  units  the <code>Units</code> object to be compared
     *                 with this
     */
    public boolean equals (Object units) {
        if (units == null || this.units_string == null) {
            return false;
        } else {
            return ((units.toString()).equalsIgnoreCase(this.units_string.toString()));
        }
    }

    /**
     *  Returns an hash code. This method overrides the default
     *  implementation in <code>Object</code> to always return the same
     *  constant. In this way <code>Units</code> objects can be used
     *  as <code>Hashtable</code> keys based solely on the units
     *  string.
     *
     *  @return   a constant
     */
    public int hashCode() {
        return 1;
    }

    /**
     *  Gets the original spelling for this units instance.
     *
     *  @return    the original spelling for this units instance
     */
    public String getOriginalSpelling () {
        return original_spelling;
    }

    /**
     *  Returns a <code>String</code> representation of the units.
     *
     *  @return  the string that represents this, or an empty string ("")
     *           if no units are defined.
     */
    public String toString() {
        if (units_string == null) {
            return ("");
        } else {
            return units_string;
        }
    }

    /**
     *  Sets a UCD string.
     *
     *  @param   ucd   the UCD string
     */
    public void setUCD(String ucd) {
        this.ucd = ucd;
    }

    /**
     *  Returns the units string spelled according to SED specs.
     *  <p>
     *  This class assumes that the standard spelling is already the
     *  correct one. If not, subclasses should override this method
     *  in order to implement special cases.
     *
     *  @return    the units string spelled according to SED specs
     */
    public String getSEDSpelling() {
        return units_string;
    }

    /**
     *  Checks if this units instance is internally consistent;
     *  that is, its string representation matches one of the
     *  supported units types.
     *
     *  @return   <code>true</code> if this is a valid instance
     */
    public abstract boolean isValid();

    /**
     *  Returns a string appropriate to be used as a axis label.
     *  The string may look like "Wavelength", "Energy" or "Frequency" or
     *  "Flux density", or whatever, depending on the units type.
     */
    public abstract String getLabel();

    /**
     *  Returns a string appropriate to be used as a UCD.
     */
    public abstract String getUCD();

    /**
     *  Returns an <code>Enumeration</code> with the string designations
     *  of all supported units.
     *
     *  @return  an <code>Enumeration</code> with all supported
     *           units
     */
    public abstract Enumeration getSupportedUnits();

    /**
     *  Converts argument from this units to standard units.
     *
     *  @param   value   the value to be converted, expressed in this units
     *  @param   avalue  auxiliary value expressed in standard units,
     *                   eventually used by the conversion formula.
     *  @return          the value after conversion to standard units
     */
    public abstract double convertToStandardUnits (double value,
                                                   double avalue);

    /**
     *  Converts argument from standard units to this units.
     *
     *  @param   value   the value expressed in standard units
     *  @param   avalue  auxiliary value expressed in standard units,
     *                   eventually used by the conversion formula.
     *  @return          the value after conversion to this units
     */
    public abstract double convertFromStandardUnits (double value,
                                                     double avalue);

    /**
     *  Grabs and stores the two objects that convertFrom this units type
     *  to/from standard units. If no conversion is possible/supported,
     *  the converter references remain <code>null</code>.
     */
    protected abstract void getConverterObject();


    /////////////////////////////////////////////////////////////////
    //
    //                 Cloneable interface.
    //
    /////////////////////////////////////////////////////////////////


    /**
     *  Returns a clone copy of this object.
     *
     *  @return  the clone
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}