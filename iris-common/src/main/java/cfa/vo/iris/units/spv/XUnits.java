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
 *  31 Jul 00  -  Implemented (IB)
 *  21 Oct 02  -  Axis labels (IB)
 *  23 Oct 02  -  Add micron and GHz types, refactor strings (IB)
 *  07 Jul 03  -  Wavenumber (IB)
 *  14 Nov 03  -  Correct spelling (IB)
 *  05 Apr 04  -  Prefered units (IB)
 *  20 Aug 04  -  isValid() method instance (IB)
 *  21 Dec 05  -  Array with units strings (IB)
 *  15 Nov 06  -  Major refactoring (IB)
 */

import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.XUnit;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 *  This class supports physical units associated with the independent
 *  variable.
 *  <p>
 *  The standard units associated with the independent variable
 *  is "Angstrom".
 *  <p>
 *  Static methods in this class support conversion of entire arrays
 *  of <code>double</code> data (for the sake of efficiency).
 *  <p>
 *  Currently supported units are:
 *  <p>
 *  "Angstrom", "micron", "nm", "cm", "m", "Hz", "kHz", "MHz", "GHz", "THz",
 *  "eV", "keV", "MeV", "1/micron".
 *  <p>
 *  Note that the "micron" value is specified as the string
 *  <code>'\u03BC' + "m"</code> since a unicode value is present.
 *  Alternate forms are "micron", "microns" and "um", with any
 *  capitalization.
 *
 *
 *
 *
 *  @version  1.0 - 31Jul00
 *  @author   Ivo Busko (Space Telescope Science Institute)
 */

public class XUnits extends Units implements XUnit, Serializable {

    static final long serialVersionUID = 1L;

    // The conversion object. These objects are stored inside each
    // Units instance, and used by methods that convertFrom the given
    // units to/from standard units.

    protected Converter converter = null;

    /**
     *  If units is set to this value, all returned arrays
     *  contain bin numbers.
     */
    public static final String BIN_NUMBER = "Bin number";

    // These define the supported units strings.

    private static final String ANGSTROM_STRING   = "Angstrom";
    private static final String NANOMETER_STRING  = "nm";
    private static final String MICRON_STRING     = '\u03BC' + "m";
    private static final String MILLIMETER_STRING = "mm";
    private static final String CENTIMETER_STRING = "cm";
    private static final String METER_STRING      = "m";
    private static final String EV_STRING         = "eV";
    private static final String KEV_STRING        = "keV";
    private static final String MEV_STRING        = "MeV";
    private static final String GEV_STRING        = "GeV";
    private static final String HZ_STRING         = "Hz";
    private static final String KHZ_STRING        = "kHz";
    private static final String MHZ_STRING        = "MHz";
    private static final String GHZ_STRING        = "GHz";
    private static final String THZ_STRING        = "THz";
    //    private static final String WAVENUMBER_STRING = '\u03BC' + "m" + '\u207B' + '\u2071';
    public static final String WAVENUMBER_STRING  = "1/" + '\u03BC' + "m";

    // Axis labels most appropriate for each units type.

    private static final String WAVELENGTH_LABEL = "Wavelength";
    private static final String ENERGY_LABEL     = "Energy";
    private static final String FREQUENCY_LABEL  = "Frequency";
    private static final String WAVENUMBER_LABEL = "Wavenumber";

    // UCDs most appropriate for each units type.

    private static final String WAVELENGTH_UCD = "em.wl";
    private static final String WAVENUMBER_UCD = "em.wavenumber";
    private static final String FREQUENCY_UCD  = "em.freq";
    private static final String ENERGY_UCD     = "em.energy";

    // This map is used to fix spelling errors.

    public static Map<String,String> correct = new HashMap<String, String>();

    // Map with the SED-specific spellings.

    private static Map<String,String> sed = new HashMap<String, String>();

    static {
        // keys must be all lower case !
        correct.put("meter",        METER_STRING);
        correct.put("metre",        METER_STRING);
        correct.put("millimeter",   MILLIMETER_STRING);
        correct.put("micron",       MICRON_STRING);
        correct.put("microns",      MICRON_STRING);
        correct.put("um",           MICRON_STRING);
        correct.put("?m",           MICRON_STRING);
        correct.put("angstroms",    ANGSTROM_STRING);
        correct.put("angstrom",     ANGSTROM_STRING);
        correct.put("a",            ANGSTROM_STRING);
        correct.put("1/micron",     WAVENUMBER_STRING);
        correct.put("1/microns",    WAVENUMBER_STRING);
        correct.put("1/um",         WAVENUMBER_STRING);
        correct.put("1/?m",         WAVENUMBER_STRING);
        correct.put("[10^-6 m]^-1", WAVENUMBER_STRING);

        // These are just to fix input in lower case,
        // but otherwise correctly spelled.

        correct.put("ev",           EV_STRING);
        correct.put("kev",          KEV_STRING);
        correct.put("mev",          MEV_STRING);
        correct.put("gev",          GEV_STRING);
        correct.put("hz",           HZ_STRING);
        correct.put("khz",          KHZ_STRING);
        correct.put("mhz",          MHZ_STRING);
        correct.put("ghz",          GHZ_STRING);
        correct.put("thz",          THZ_STRING);

        // SED spellings.

        sed.put("angstrom",        "Angstrom");
        sed.put(MICRON_STRING,     "um");
        sed.put(WAVENUMBER_STRING, "1/um");
    }

    /**
     *  Corrects spelling errors.
     *
     *  @param  value   the input string
     *  @return         the correct string
     */
    public static String getCorrectSpelling(String value) {
        String result = XUnits.correct.get(value.toLowerCase());
        if (result == null) {
            result = value;
        }
        return result;
    }

    /**
     *  Constructor.
     *  <p>
     *  This constructor is capable of fixing some common mispellings and
     *  alternate spellings.
     *
     *  @param  arg  string with the units
     */
    public XUnits (String arg) {
        this.originalSpelling = arg;
        this.unitsString = getCorrectSpelling(arg);

        makeConverterObject();
    }

    /**
     *  Checks if this units instance is internally consistent;
     *  that is, its string representation matches one of the
     *  supported units types.
     *
     *  @return   <code>true</code> if this is a valid instance
     */
    public boolean isValid() {
        return converters.containsKey(this.toString());
    }

    /**
     *  Returns a string appropriate to be used as a label in the X axis.
     *  The string may look like "Wavelength", "Energy" or "Frequency"
     *  depending on the units type.
     *
     *  @return   a string with an axis title, or <code>null</code> if the
     *            units type is not supported.
     */
    public String getLabel() {
        if (converter != null) {
            return converter.getAxisLabel();
        } else {
            return null;
        }
    }

    /**
     *  Returns the units string spelled according to SED specs.
     *
     *  @return    the units string spelled according to SED specs
     */
    public String getSEDSpelling() {
        String spelling = sed.get(this.toString());
        if (spelling != null) {
            return spelling;
        }
        return this.toString();
    }

    /**
     *  Returns a string appropriate to be used as a UCD.
     *
     *  @return   a string with an axis title, or <code>null</code> if the
     *            units type is not supported.
     */
    public String getUCD() {
        if (ucd != null) {
            return ucd;
        }
        if (converter != null) {
            return converter.getUCD();
        }
        return null;
    }

    /**
     *  Returns the standard independent variable units.
     *
     *  @return   the standard independent variable units
     */
    public static XUnits getStandardUnits() {
        return new XUnits (ANGSTROM_STRING);
    }

    /**
     *  Converts argument from this units to standard units.
     *
     *  @param   value   the value expressed in this units
     *  @return          the value after conversion to standard units
     */
    public double convertToStandardUnits (double value) {
        if (converter != null) {
            return (converter.convertFrom(value));
        } else {
            return value;
        }
    }

    /**
     *  Converts argument from standard units to this units.
     *
     *  @param   value   the value expressed in standard units
     *  @return          the value after conversion to this units
     */
    public double convertFromStandardUnits (double value) {
        if (converter != null) {
            return(converter.convertTo(value));
        } else {
            return value;
        }
    }

    /**
     *  Grabs and stores the object that converts this units type
     *  to/from standard units. If no conversion is possible/supported,
     *  the converter references remain <code>null</code>.
     */
    protected void makeConverterObject() {
        converter = (Converter)converters.get (this.toString());
    }

    /////////////////////////////////////////////////////////////////
    //
    //                 Conversion algorithms.
    //
    /////////////////////////////////////////////////////////////////


    // Conversion is performed in two steps: first the input value is
    // converted to ANGSTROM units, them the resulting value is converted
    // to the target units. Each individual algorithm is packaged in a
    // Converter-implementor object. These objects are indexed in a
    // KeyedVector by the string representation of the Units associated
    // with the data. A Units object itself cannot be used as key since
    // this results in infinite recursion at constructor time.

    // The KeyedVector keys are used as the source for the supported units
    // enumeration. The first entry must be the default standard units.

    interface Converter extends Serializable {
        double convertFrom(double arg);
        double convertTo(double arg);
        String getAxisLabel();
        String getUCD();
    }

    private static class WavelengthConverter implements Converter {
        private double factor;
        public WavelengthConverter(double factor) {
            this.factor = factor;
        }
        public double convertFrom(double arg) {return arg * factor;}
        public double convertTo(double arg)   {return arg / factor;}
        public String getAxisLabel()          {return WAVELENGTH_LABEL;}
        public String getUCD()                {return WAVELENGTH_UCD;}
    }

    private static class SymmetricalConverter implements Converter {
        private double factor;
        private String label;
        private String ucdstring;
        public SymmetricalConverter(double factor, String label, String ucdstring) {
            this.factor = factor;
            this.label = label;
            this.ucdstring = ucdstring;
        }
        public double convertFrom(double arg) {return factor / arg;}
        public double convertTo(double arg)   {return convertFrom(arg);}
        public String getAxisLabel()          {return label;}
        public String getUCD()                {return ucdstring;}
    }

    protected static Map<String, Converter> converters = new HashMap<String, Converter>();

    static {
        converters.put(ANGSTROM_STRING,   new WavelengthConverter(1.0));
        converters.put(NANOMETER_STRING,  new WavelengthConverter(1.E+1));
        converters.put(MICRON_STRING,     new WavelengthConverter(1.E+4));
        converters.put(MILLIMETER_STRING, new WavelengthConverter(1.E+7));
        converters.put(CENTIMETER_STRING, new WavelengthConverter(1.E+8));
        converters.put(METER_STRING,      new WavelengthConverter(1.E+10));

        converters.put(EV_STRING,         new SymmetricalConverter(Constant.E * 1.E6,  ENERGY_LABEL, ENERGY_UCD));
        converters.put(KEV_STRING,        new SymmetricalConverter(Constant.E * 1.E3,  ENERGY_LABEL, ENERGY_UCD));
        converters.put(MEV_STRING,        new SymmetricalConverter(Constant.E,         ENERGY_LABEL, ENERGY_UCD));
        converters.put(GEV_STRING,        new SymmetricalConverter(Constant.E * 1.E-3, ENERGY_LABEL, ENERGY_UCD));
        converters.put(HZ_STRING,         new SymmetricalConverter(Constant.C,         FREQUENCY_LABEL, FREQUENCY_UCD));
        converters.put(KHZ_STRING,        new SymmetricalConverter(Constant.C / 1.E3,  FREQUENCY_LABEL, FREQUENCY_UCD));
        converters.put(MHZ_STRING,        new SymmetricalConverter(Constant.C / 1.E6,  FREQUENCY_LABEL, FREQUENCY_UCD));
        converters.put(GHZ_STRING,        new SymmetricalConverter(Constant.C / 1.E9,  FREQUENCY_LABEL, FREQUENCY_UCD));
        converters.put(THZ_STRING,        new SymmetricalConverter(Constant.C / 1.E12, FREQUENCY_LABEL, FREQUENCY_UCD));
        converters.put(WAVENUMBER_STRING, new SymmetricalConverter(10000.0,   WAVENUMBER_LABEL, WAVENUMBER_UCD));
    }

    /**
     *  Converts independent variable array from one unit to another.
     *
     *  @param   x       the independent variable array, usually wavelength.
     *  @param   xunit   independent variable units
     *  @param   nunit   independent variable new (output) units
     *  @return          new array with the converted values
     *  @throws  UnitsException if the units aren't of the appropriate type
     */
    public static double[] convert(double[] x, XUnit xunit, XUnit nunit) throws UnitsException {

        double[] out = new double[x.length];

        if (xunit.equals (new XUnits (BIN_NUMBER)) ||
                nunit.equals (new XUnits (BIN_NUMBER))) {
            System.arraycopy (x, 0, out, 0, x.length);
            return (out);
        }

        //noinspection ConstantConditions
        if (xunit == null || nunit == null) {
            throw new UnitsException (ERROR_MSG);
        }

        String fromUnits = XUnits.getCorrectSpelling(xunit.toString());
        String toUnits = XUnits.getCorrectSpelling(nunit.toString());

        Converter converterFrom = (Converter)converters.get(fromUnits);
        Converter converterTo   = (Converter)converters.get(toUnits);

        if (converterFrom == null || converterTo == null) {
            throw new UnitsException (ERROR_MSG);
        }

        for (int i = 0; i < out.length; i++) {
            out[i] = converterTo.convertTo(converterFrom.convertFrom(x[i]));
        }

        return out;
    }

    @Override
    public double[] convert(double[] x, XUnit toUnit) throws UnitsException {
        return convert(x, this, toUnit);
    }
}



