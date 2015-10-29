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
 *  05 Apr 01  -  Magnitudes (IB)
 *  21 Oct 02  -  Axis labels (IB)
 *  23 Oct 02  -  Add nuFnu, refactor strings (IB)
 *  14 Nov 03  -  Add Watt units, correct spelling method (IB)
 *  05 Apr 04  -  Prefered units (IB)
 *  22 Jun 04  -  Change standard spelling (IB)
 *  20 Aug 04  -  isValid() instance method (IB)
 *  18 Feb 05  -  Normalization factor (IB)
 *  21 Dec 05  -  Array with units strings (IB)
 *  17 Nov 06  -  Major refactoring (IB)
 *   7 Jul 11  -  Add method convertErrors to deal with magnitudes (IB)
 *  12 Aug 11  -  Disable OBMAG (IB)Split between flux density and flux (IB)
 *  26 Jul 13  -  Add Rayleigh/Angstrom (IB)
 */

import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.YUnit;

import java.io.Serializable;

import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

/**
 *  This class supports physical units associated with the dependent
 *  variable.
 *  <p>
 *  The standard units associated with the dependent variable
 *  is "photon/s/cm2/Angstrom".
 *  <p>
 *  Static methods in this class support conversion of entire arrays
 *  of <code>double</code> data (for the sake of efficiency).
 *  <p>
 *  Currently supported units are:
 *  <p>
 *  "photon/s/cm2/Angstrom", "photon/cm2/s/pixel" (requires
 *  monotonic wavelength scale), "photon/cm2/s/Hz",
 *  "erg/s/cm2/Angstrom", "erg/s/cm2/Hz", "Jy", "mJy", "uJy", "erg/s/cm2",
 *  "Watt/cm2/micron", "Watt/m2/micron", "Watt/m2/nm", "Watt/m2/Hz",
 *  "Jy-Hz", "ABMAG", "STMAG", "OBMAG" (from STScI's Synphot).
 *  <p>
 *  Spelling tends to follow the specs at "Standards for Astronomical
 *  Catalogues: Units, CDS Strasbourg" (http://vizier.u-strasbg.fr/doc/catstd-3.2.htx)
 *  but may differ in some specific cases due to issues with Unicode
 *  font rendering on various objects.
 *
 *
 *
 *  @version  1.0 - 31Jul00
 *  @author   Ivo Busko (Space Telescope Science Institute)
 */

public class YUnits extends Units implements YUnit, Serializable {

    static final long serialVersionUID = 1L;

    static final double ABZERO = -48.60; // zero point of the AB mag system
    static final double STZERO = -21.10; // zero point of the ST mag system

    // The conversion object that is stored inside each Units object, and
    // used by methods that convert the given units to/from standard units.

    private Converter converter = null;

    // The optional multiplicative factor.

    private double factor = 1.0;

    // These define the supported units strings.

    private static String PHOTLAM_STRING  = "photon/s/cm2/Angstrom";
    private static String PHOTPIX_STRING  = "photon/s/cm2/pixel";
    private static String FLAM_STRING     = "erg/s/cm2/Angstrom";
    private static String FNU_STRING      = "erg/s/cm2/Hz";
    private static String PHOTNU_STRING   = "photon/s/cm2/Hz";
    private static String WATT_STRING     = "Watt/cm2/" + '\u03BC' + "m";
    private static String WATTM_STRING    = "Watt/m2/"  + '\u03BC' + "m";
    private static String WATTNM_STRING   = "Watt/m2/nm";
    private static String WATTHZ_STRING   = "Watt/m2/Hz";
    private static String JY_STRING       = "Jy";
    private static String MJY_STRING      = "mJy";
    private static String NUJY_STRING     = '\u03BC' + "Jy";
    private static String RAYLEIGH_STRING = "Rayleigh/Angstrom";

    // these are flux units.....they are convertible to flux density though
    private static String JYHZ_STRING    = "Jy-Hz";
    private static String NUFNU_STRING   = "erg/s/cm2";

    private static String ABMAG_STRING   = "ABMAG";
    private static String STMAG_STRING   = "STMAG";
    private static String OBMAG_STRING   = "OBMAG";

    // Axis labels most appropriate for each units type.

    private static final String FLUXDENSITY_LABEL = "Flux density";
    private static final String FLUX_LABEL        = '\u03BD' + "F(" + '\u03BD' + ")";
    private static final String MAGNITUDE_LABEL   = "Magnitude";
    private static final String COUNTS_LABEL      = "Counts";

    // UCDs most appropriate for each units type.

    private static final String FLUXD_WAV_UCD = "phot.fluxDens;em.wl";
    private static final String FLUXD_FRE_UCD = "phot.fluxDens;em.freq";
    private static final String FLUXD_ENE_UCD = "phot.fluxDens;em.energy";
    private static final String FLUX_UCD      = "phot.flux.beam";
    private static final String MAGNITUDE_UCD = "phot.mag";
    private static final String COUNTS_UCD    = "phot.count";

    // Alternate spellings.

    public static Map<String,String> correct = new HashMap<String, String>();

    // SED spellings.

    private static Map<String,String> sed = new HashMap<String, String>();

    static {
        // keys must be all lower case !

        // This mess can only be corrected by a VO initiative!

        correct.put("ergs/cm**2/s/a",             FLAM_STRING);
        correct.put("ergs/s/cm**2/a",             FLAM_STRING);
        correct.put("erg/cm**2/s/a",              FLAM_STRING);
        correct.put("erg/s/cm**2/a",              FLAM_STRING);
        correct.put("ergs/cm2/s/a",               FLAM_STRING);
        correct.put("ergs/s/cm2/a",               FLAM_STRING);
        correct.put("erg/cm2/s/a",                FLAM_STRING);
        correct.put("erg/s/cm2/a",                FLAM_STRING);
        correct.put("ergs/cm^2/s/a",              FLAM_STRING);
        correct.put("ergs/s/cm^2/a",              FLAM_STRING);
        correct.put("erg/cm^2/s/a",               FLAM_STRING);
        correct.put("erg/cm^2/a/sec",             FLAM_STRING);
        correct.put("erg/s/cm^2/a",               FLAM_STRING);
        correct.put("erg/sec/cm2/angstrom",       FLAM_STRING);
        correct.put("erg/sec/cm**2/angstrom",     FLAM_STRING);
        correct.put("erg/cm2/sec/angstrom",       FLAM_STRING);
        correct.put("erg/cm**2/sec/angstrom",     FLAM_STRING);
        correct.put("ergs/cm**2/s/angstrom",      FLAM_STRING);
        correct.put("ergs/s/cm**2/angstrom",      FLAM_STRING);
        correct.put("erg/cm**2/s/angstrom",       FLAM_STRING);
        correct.put("erg/s/cm**2/angstrom",       FLAM_STRING);
        correct.put("ergs/cm2/s/angstrom",        FLAM_STRING);
        correct.put("ergs/s/cm2/angstrom",        FLAM_STRING);
        correct.put("erg/cm2/s/angstrom",         FLAM_STRING);
        correct.put("erg/s/cm2/angstrom",         FLAM_STRING);
        correct.put("ergs/cm^2/s/angstrom",       FLAM_STRING);
        correct.put("ergs/s/cm^2/angstrom",       FLAM_STRING);
        correct.put("erg/cm^2/s/angstrom",        FLAM_STRING);
        correct.put("erg/s/cm^2/angstrom",        FLAM_STRING);
        correct.put("ergs/cm**2/s/angstroms",     FLAM_STRING);
        correct.put("ergs/s/cm**2/angstroms",     FLAM_STRING);
        correct.put("ergs/cm**2/sec/angstrom",    FLAM_STRING);
        correct.put("erg/cm**2/s/angstroms",      FLAM_STRING);
        correct.put("erg/s/cm**2/angstroms",      FLAM_STRING);
        correct.put("ergs/cm2/s/angstroms",       FLAM_STRING);
        correct.put("ergs/s/cm2/angstroms",       FLAM_STRING);
        correct.put("erg/cm2/s/angstroms",        FLAM_STRING);
        correct.put("erg/s/cm2/angstroms",        FLAM_STRING);
        correct.put("ergs/cm^2/s/angstroms",      FLAM_STRING);
        correct.put("ergs/s/cm^2/angstroms",      FLAM_STRING);
        correct.put("erg/cm^2/s/angstroms",       FLAM_STRING);
        correct.put("erg/cm^2/sec/angstrom",      FLAM_STRING);
        correct.put("erg/s/cm^2/angstroms",       FLAM_STRING);
        correct.put("erg cm^-2 s^-1 angstrom^-1", FLAM_STRING);
        correct.put("erg cm^-2 s^-1 ang^-1",      FLAM_STRING);
        correct.put("erg/ s/ cm**2/ angstrom",    FLAM_STRING);
        correct.put("flam",                       FLAM_STRING);

        correct.put("erg cm**(-2) s**(-1) angstrom**(-1)", FLAM_STRING);
        correct.put("ergcm**(-2)s**(-1)angstrom**(-1)",    FLAM_STRING);

        correct.put("photon/ s/ cm**2/ angstrom", PHOTLAM_STRING);
        correct.put("photons/cm^2/sec/angstrom",  PHOTLAM_STRING);
        correct.put("photlam",                    PHOTLAM_STRING);

        correct.put("photon/ s/ cm**2/ hz",       PHOTNU_STRING);
        correct.put("photon/cm**2/s/Hz",          PHOTNU_STRING);
        correct.put("photon/cm^2/s/Hz",           PHOTNU_STRING);
        correct.put("photons/cm**2/s/Hz",         PHOTNU_STRING);
        correct.put("photons/cm^2/s/Hz",          PHOTNU_STRING);
        correct.put("photnu",                     PHOTNU_STRING);

        correct.put("photon/ s/ cm**2/ pixel",    PHOTPIX_STRING);

        correct.put("erg/ s/ cm**2/ hz",             FNU_STRING);
        correct.put("erg/s/cm**2/hz",                FNU_STRING);
        correct.put("erg cm**(-2) s**(-1) hz**(-1)", FNU_STRING);
        correct.put("fnu",                           FNU_STRING);

        correct.put("watts/cm^2/micron",  WATT_STRING);
        correct.put("watt/cm^2/micron",   WATT_STRING);
        correct.put("watts/cm**2/micron", WATT_STRING);
        correct.put("watt/cm**2/micron",  WATT_STRING);
        correct.put("watt/cm**2/?m",      WATT_STRING);
        correct.put("watt/cm^2/?m",       WATT_STRING);
        correct.put("watt/cm**2/um",      WATT_STRING);
        correct.put("watt/cm^2/um",       WATT_STRING);
        correct.put("watts/cm**2/um",     WATT_STRING);
        correct.put("watts/cm^2/um",      WATT_STRING);
        correct.put("watts/cm**2/?m",     WATT_STRING);
        correct.put("watts/cm^2/?m",      WATT_STRING);
        correct.put("w/cm^2/um",          WATT_STRING);
        correct.put("w/cm^2/?m",          WATT_STRING);
        correct.put("watts/cm2/micron",   WATT_STRING);
        correct.put("watt/cm2/micron",    WATT_STRING);
        correct.put("watts/cm2/?m",       WATT_STRING);
        correct.put("watt/cm2/?m",        WATT_STRING);
        correct.put("watts/cm2/um",       WATT_STRING);
        correct.put("watt/cm2/um",        WATT_STRING);
        correct.put("watts/cm2/um",       WATT_STRING);
        correct.put("watts/cm2/?m",       WATT_STRING);
        correct.put("watt/cm2/?m",        WATT_STRING);
        correct.put("w/cm2/um",           WATT_STRING);
        correct.put("w/cm2/?m",           WATT_STRING);
        correct.put("watt/ cm**2/ um",    WATT_STRING);
        correct.put("watt/ cm**2/ ?m",    WATT_STRING);

        correct.put("w/m^2/um",           WATTM_STRING);
        correct.put("w/m^2/?m",           WATTM_STRING);
        correct.put("watt/m^2/um",        WATTM_STRING);
        correct.put("watt/m^2/?m",        WATTM_STRING);
        correct.put("watt/m2/um",         WATTM_STRING);
        correct.put("watt/m2/?m",         WATTM_STRING);
        correct.put("watt/m**2/um",       WATTM_STRING);
        correct.put("watt/m**2/?m",       WATTM_STRING);
        correct.put("watt/ m**2/ um",     WATTM_STRING);
        correct.put("watt/ m**2/ ?m",     WATTM_STRING);

        correct.put("w m^-2 nm^-1",       WATTNM_STRING);
        correct.put("watt/ m**2/ nm",     WATTNM_STRING);

        correct.put("watt/ m**2/ hz",     WATTHZ_STRING);
        correct.put("w/ m**2/ hz",        WATTHZ_STRING);
        correct.put("watt/m**2/hz",       WATTHZ_STRING);
        correct.put("w/m**2/hz",          WATTHZ_STRING);
        correct.put("w/m^2/hz",           WATTHZ_STRING);

        correct.put("nujy",               NUJY_STRING);
        correct.put("ujy",                NUJY_STRING);
        correct.put("?jy",                NUJY_STRING);
        correct.put("microjy",            NUJY_STRING);
        correct.put("micro jy",           NUJY_STRING);

        correct.put("erg/ s/ cm**2",        NUFNU_STRING);
        correct.put("erg cm**(-2) s**(-1)", NUFNU_STRING);

        correct.put("jy",                 JY_STRING);
        correct.put("mjy",                MJY_STRING);
        correct.put("ujy",                NUJY_STRING);
        correct.put("jy-hz",              JYHZ_STRING);
        correct.put("jy hz",              JYHZ_STRING);
        correct.put("abmag",              ABMAG_STRING);
        correct.put("stmag",              STMAG_STRING);
        correct.put("obmag",              OBMAG_STRING);

        // SED spellings.

        sed.put(PHOTLAM_STRING, "photon/ s/ cm**2/ Angstrom");
        sed.put(PHOTPIX_STRING, "photon/ s/ cm**2/ pixel");
        sed.put(FLAM_STRING,    "erg/ s/ cm**2/ Angstrom");
        sed.put(FNU_STRING,     "erg/ s/ cm**2/ Hz");
        sed.put(PHOTNU_STRING,  "photon/ s/ cm**2/ Hz");
        sed.put(WATT_STRING,    "Watt/ cm**2/ um");
        sed.put(WATTNM_STRING,  "Watt/ m**2/ nm");
        sed.put(WATTM_STRING,   "Watt/ m**2/ um");
        sed.put(WATTHZ_STRING,  "Watt/ m**2/ Hz");
        sed.put(NUJY_STRING,    "uJy");
        sed.put(JYHZ_STRING,    "Jy Hz");
        sed.put(NUFNU_STRING,   "erg/ s/ cm**2");
    }

    /**
     *  Corrects spelling errors.
     *
     *  @param  value   the input string
     *  @return         the correct string
     */
    public static String getCorrectSpelling(String value) {
        value = getFactorAndUnit(value)[1];

        String result = null;

        if (value != null) {
            result = correct.get(value.toLowerCase());
        }

        if (result == null) {
            result = value;
        }

        return result;
    }

    /**
     *  Constructor.
     *
     *  @param  arg  string with the units
     */
    public YUnits (String arg) {
        String[] result = getFactorAndUnit(arg);

        if (result[0] != null) {
            factor = parseFactor(result[0]);
        }

        String clean_string = result[1];

        originalSpelling = clean_string;

        unitsString = getCorrectSpelling(clean_string);

        makeConverterObject();
    }

    private static String[] getFactorAndUnit(String unit) {
        String[] result = new String[2];
        result[1] = unit;
        if (!unit.isEmpty() && unit.startsWith("10")) {
            int index = unit.indexOf(' ');
            if (index < 0) {
                index = unit.indexOf('.');
            }
            if (index < 0) {
                index = unit.indexOf(')');
            }

            if (index > 0) {
                result[1] = unit.substring(index + 1);
                result[0] = unit.substring(0,index) + " ";
            }
        }

        return result;
    }

    private double parseFactor(String factor_string) {

        String[] factors = factor_string.split("\\*+");

        if (factors.length != 2) {
            return 1.0;
        }

        // Remove eventual parenthesis around the mantissa.

        if (factors[1].startsWith("(")) {
            factors[1] = factors[1].substring(1,factors[1].length()-2);
        }

        double result = 1.0;
        try {
            double charac = Double.parseDouble(factors[0]);
            double mant   = Double.parseDouble(factors[1]);

            result = Math.pow(charac, mant);

        } catch (NumberFormatException e) {
        }

        return result;
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
     *  Returns a string appropriate to be used as a label in the Y axis.
     *  The string may look like "Flux density", or "Magnitude", or whatever,
     *  depending on the units type.
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
        String spelling = sed.get(unitsString);
        if (spelling != null) {
            return spelling;
        }
        return unitsString;
    }

    /**
     *  Returns a string appropriate to be used as a UCD.
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
     *  Returns <code>true</code> if the units type require an inverted
     *  plotting axis, such as when plotting stellar magnitudes.
     *
     *  @return  <code>true</code> if the units type require an inverted
     *  plotting axis
     */
    public boolean isInverted() {
        return (converter != null) && converter.isInverted();
    }

    /**
     *  Returns <code>true</code> if it is a magnitude.
     *
     * @return  <code>true</code> if it is a magnitude
     */
    public boolean isMagnitude() {
        return (converter != null) && converter.isMagnitude();
    }

    /**
     *  Returns <code>true</code> if it is flux
     *
     * @return  <code>true</code> if it is flux
     */
    public boolean isFlux() {
        return (converter != null) && converter.isFlux();
    }

    /**
     *  Grabs and stores the two objects that convertFrom this units type
     *  to/from standard units. If no conversion is possible/supported,
     *  the converter references remain <code>null</code>.
     */
    protected void makeConverterObject() {
        converter = (Converter)converters.get (unitsString);
    }

    public double getFactor() {
        return factor;
    }


    /////////////////////////////////////////////////////////////////
    //
    //                 Conversion algorithms.
    //
    /////////////////////////////////////////////////////////////////


    // Conversion is performed in two steps: first the input value is
    // converted to PHOTLAM units, them the resulting value is converted
    // to the target units. Each individual algorithm is packaged in a
    // Converter-implementing object. These objects are indexed in a
    // KeyedVector by the string representation of the Units associated
    // with the data. A Units object itself cannot be used as key since
    // this results in infinite recursion at constructor time. The
    // wavelength array must be previously converted to Angstrom.

    // The KeyedVector keys are used as the source for the supported
    // units enumeration. The first entry must be the default standard
    // units.

    interface Converter extends Serializable {
        // Constant used for Rayleigh conversions.
        double RAYLEIGH_FACTOR = (1e-17 * Math.pow((180./Math.PI*3600.),2) * 4. * Math.PI )  /
                (H * 1.e-7 * 299792458.);

        double convertFrom(double flux, double wave, double w1, double w2);
        double convertTo(double flux, double wave, double w1, double w2);
        boolean requireSortedWavelengths();
        boolean isInverted();
        boolean isMagnitude ();
        boolean isFlux ();
        String getAxisLabel();
        String getUCD();
    }

    private static class AConverter implements Converter {
        private boolean sorted_wavelengths = false;
        private boolean is_inverted = false;
        private boolean is_magnitude = false;
        private boolean is_flux = false;
        private String label;
        private String ucdstring;

        public AConverter(String label, String ucdstring) {
            this.label = label;
            this.ucdstring = ucdstring;
        }
        public double convertFrom(double flux, double wave, double w1, double w2) {
            return 0;
        }
        public double convertTo(double flux, double wave, double w1, double w2) {
            return 0;
        }
        public boolean requireSortedWavelengths() {
            return sorted_wavelengths;
        }
        public boolean isInverted() {
            return is_inverted;
        }
        public boolean isMagnitude() {
            return is_magnitude;
        }
        public boolean isFlux() {
            return is_flux;
        }
        public String getAxisLabel() {
            return label;
        }
        public String getUCD() {
            return ucdstring;
        }
    }

    protected static Map<String, Converter> converters = new HashMap<String, Converter>();

    static {
        // From PHOTLAM to PHOTLAM
        converters.put (PHOTLAM_STRING, new Converter() {
            public double convertFrom (double f, double d1, double d2, double d3) {
                return f;
            }
            public double convertTo(double f, double d, double d1, double d2) {
                return f;
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_WAV_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From COUNTS to PHOTLAM (requires monotonic wavelengths)
//        converters.put (PHOTPIX_STRING, new Converter() {
//            public double convertFrom (double f, double d, double w1, double w2) {
//                if (w1 != DATA_MARKER && w2 != DATA_MARKER) {
//                    return (f / (Math.abs(w1 - w2) * 0.5));
//                } else {
//                        return DATA_MARKER;
//                }
//            }
//            public double convertTo(double f, double d, double w1, double w2) {
//                if (w1 != DATA_MARKER && w2 != DATA_MARKER) {
//                    return (f * (Math.abs(w2 - w1) * 0.5));
//                } else {
//                        return DATA_MARKER;
//                }
//            }
//            public boolean requireSortedWavelengths() {
//                return true;
//            }
//            public String getAxisLabel() {
//                return COUNTS_LABEL;
//            }
//            public String getUCD() {
//                return COUNTS_UCD;
//            }
//            public boolean isInverted() {
//                return false;
//            }
//            public boolean isMagnitude() {
//                return false;
//            }
//            public boolean isFlux() {
//                return false;
//            }
//        });
        // From FLAM to PHOTLAM
        converters.put (FLAM_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (f * w / (H * C));
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (H * C * f / w);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_WAV_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From FNU to PHOTLAM
        converters.put (FNU_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (f / w / H);
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (H * f * w);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_FRE_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From PHOTNU to PHOTLAM
        converters.put (PHOTNU_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (C * f / (w * w));
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (f * w * w / C);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_FRE_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From Watt/cm**2/micron to PHOTLAM
        // 1 erg/s     =  1.E-7 Watt
        // 1 Angstrom  =  1.E-4 micron
        //   flam   =   erg/s     / cm**2  / Angstrom
        //              Watt      / cm**2  / micron
        //            1.E7 erg/s  / cm**2  / (1.E4 Angstrom)
        //            1.E3 erg/s  / cm**2  / Angstrom
        //   1 (Watt/cm**2/micron) = 1.E3 flam
        converters.put (WATT_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (1.E+3 * f * w / (H * C));
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (H * C * f / 1.E+3 / w);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_WAV_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From Watt/m**2/micron to PHOTLAM
        converters.put (WATTM_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (1.E-1 * f * w / (H * C));
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (H * C * f / 1.E-1 / w);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_WAV_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From Watt/m**2/nm to PHOTLAM
        converters.put (WATTNM_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (1.E+2 * f * w / (H * C));
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (H * C * f / 1.E+2 / w);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_WAV_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From RAYLEIGH/ANGSTROM to PHOTLAM
        converters.put (RAYLEIGH_STRING, new Converter() {

            //
            // Original spec (by Laurent Lamy):
            //
            // correction_factor = (1e-7*(x*1e-10)/(6.626e-34*299792458.)*(180./!pi*3600.)^2*4.*!pi)/1e6
            //

            public double convertFrom (double f, double w, double d1, double d2) {
                return f *  (w * RAYLEIGH_FACTOR) / 1e6;
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return f / ((w * RAYLEIGH_FACTOR) / 1e6);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_WAV_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        });

        class JyConverter implements Converter {
            private double factor;
            JyConverter(double factor) {
                this.factor = factor;
            }
            public double convertFrom (double f, double w, double d1, double d2) {
                return (factor * f / w / H);
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (1./factor * f * w * H);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUXDENSITY_LABEL;
            }
            public String getUCD() {
                return FLUXD_FRE_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return false;
            }
        }

        // From Watt/m**2/Hz to PHOTLAM
        converters.put(WATTHZ_STRING, new JyConverter(1.E+3));
        // From JY to PHOTLAM
        converters.put(JY_STRING, new JyConverter(1.E-23));
        // From mJY to PHOTLAM
        converters.put(MJY_STRING, new JyConverter(1.E-26));
        // From nuJY to PHOTLAM
        converters.put(NUJY_STRING, new JyConverter(1.E-29));

        // From Jy-Hz to PHOTLAM
        converters.put (JYHZ_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (1.E-23 / H / C * f);
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (1.E+23 * H * C * f);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUX_LABEL;
            }
            public String getUCD() {
                return FLUXD_FRE_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return true;
            }
        });
        // From NUFNU to PHOTLAM
        converters.put (NUFNU_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (f / C / H);
            }
            public double convertTo(double f, double w, double d1, double d2) {
                return (H * f * C);
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return FLUX_LABEL;
            }
            public String getUCD() {
                return FLUXD_FRE_UCD;
            }
            public boolean isInverted() {
                return false;
            }
            public boolean isMagnitude() {
                return false;
            }
            public boolean isFlux() {
                return true;
            }
        });
        // From ABMAG to PHOTLAM
        converters.put (ABMAG_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (1.0 / (H * w) * Math.pow (10.0, (-0.4 * (f - ABZERO))));
            }
            public double convertTo(double f, double w, double d1, double d2) {
                double arg = H * f * w;
                if (arg > 0.0) {
                    return (-1.085736 * Math.log (arg) + ABZERO);
                } else {
                    return Double.NaN;
                }
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return MAGNITUDE_LABEL;
            }
            public String getUCD() {
                return MAGNITUDE_UCD;
            }
            public boolean isInverted() {
                return true;
            }
            public boolean isMagnitude() {
                return true;
            }
            public boolean isFlux() {
                return false;
            }
        });
        // From STMAG to PHOTLAM
        converters.put (STMAG_STRING, new Converter() {
            public double convertFrom (double f, double w, double d1, double d2) {
                return (w / H / C * Math.pow (10.0, (-0.4 * (f - STZERO))));
            }
            public double convertTo(double f, double w, double d1, double d2) {
                double arg = H * C * f / w;
                if (arg > 0.0)
                    return (-1.085736 * Math.log (arg) + STZERO);
                else
                    return Double.NaN;
            }
            public boolean requireSortedWavelengths() {
                return false;
            }
            public String getAxisLabel() {
                return MAGNITUDE_LABEL;
            }
            public String getUCD() {
                return MAGNITUDE_UCD;
            }
            public boolean isInverted() {
                return true;
            }
            public boolean isMagnitude() {
                return true;
            }
            public boolean isFlux() {
                return false;
            }
        });

//  Take down per VAO tester request.

        // From OBMAG to PHOTLAM (requires monotonic wavelengths)
//        converters.put (OBMAG_STRING, new Converter() {
//            public double convertFrom (double f, double w, double w1, double w2) {
//                if (w1 != DATA_MARKER && w2 != DATA_MARKER) {
//                    return ((Math.pow (10.0, (-0.4 * f))) /
//                            (Math.abs (w1 - w2) * 0.5));
//                } else {
//                    return DATA_MARKER;
//                }
//            }
//            public double convertTo(double f, double w, double w1, double w2) {
//                if (f > 0.0)
//                    return (-1.085736 * Math.log (f  *
//                            Math.abs (w1 - w2) * 0.5));
//                else
//                    return Double.NaN;
//            }
//            public boolean requireSortedWavelengths() {
//                return true;
//            }
//            public String getAxisLabel() {
//                return MAGNITUDE_LABEL;
//            }
//            public String getUCD() {
//                return MAGNITUDE_UCD;
//            }
//            public boolean isInverted() {
//                return true;
//            }
//            public boolean isMagnitude() {
//                return true;
//            }
//        });
    }

    /**
     *  Converts dependent variable array from one unit to another.
     *  <p>
     *  Conversion in between PHOTLAM and COUNTS requires that the
     *  data be monotonicaly arranged in wavelength.
     *
     *  @param   y       the dependent variable array, usually flux.
     *  @param   x       the independent variable array, usually wavelength.
     *  @param   yunit   dependent variable current units
     *  @param   xunit   independent variable units
     *  @param   nunit   dependent variable new (output) units
     *  @param   raise   if <code>true</code>, raise an exception on wrong Y units
     *                   (an exception will always be raised on wrong X units).
     *  @return          the converted dependent variable values
     *  @throws  UnitsException if the units aren't of the appropriate type
     */
    static public double[] convert (double[] y, double[] x,
                                    YUnit yunit, XUnit xunit, YUnit nunit,
                                    boolean raise) throws UnitsException {

        if (yunit == null || xunit == null || nunit == null) {
            return y;
        }

        double[] out = new double[x.length];

        // Generate a wavelength array in standard units.
        double[] wave = XUnits.convert (x, xunit, XUnits.getStandardUnits());

        // Get the "from" and "to" conversion objects.

        Converter from = (Converter)converters.get (YUnits.getCorrectSpelling(yunit.toString()));
        Converter to   = (Converter)converters.get (YUnits.getCorrectSpelling(nunit.toString()));

        if (from == null || to == null) {
            if (raise) {
                throw new UnitsException("Invalid flux density units");
            } else {
                System.arraycopy (y, 0, out, 0, y.length);
                return out;
            }
        }

        // Do the conversion.
        double hold, w1, w2;
        double factor = yunit.getFactor();
        int lim = wave.length - 1;

        for (int i = 0; i < out.length; i++) {
            w1 = wave[Math.max(i-1,0)];
            w2 = wave[Math.min(i+1,lim)];
            hold   = factor * from.convertFrom(y[i], wave[i], w1, w2);
            out[i] = to.convertTo(hold, wave[i], w1, w2);
        }

        return out;
    }

    /**
     *  This method takes care of converting errors when data and errors are
     *  expressed in a mmagnitude scale.
     *  <p>
     *  For non-magnitude units, it falls back to the regular convert() method.
     *
     *
     *  @param   y       the dependent variable array, usually flux
     *  @param   x       the independent variable array, usually wavelength.
     *  @param   yunit   dependent variable current units
     *  @param   xunit   independent variable units
     *  @param   nunit   dependent variable new (output) units
     *  @param   raise   if <code>true</code>, raise an exception on wrong Y units
     *                   (an exception will always be raised on wrong X units).
     *  @return          the converted dependent variable values
     *  @throws  UnitsException if the units aren't of the appropriate type
     */
    public static double[] convertErrors(double[] e, double[] y, double[] x, YUnit yunit,
                                         XUnit xunit, YUnit nunit, boolean raise)
            throws UnitsException {
        if (!yunit.isMagnitude() && !nunit.isMagnitude()) {
            return convert(e, x, yunit, xunit, nunit, raise);
        }

        // Errors in magnitude must be added to the data magnitude to get
        // the lower error bar in flux, and subtracted to get the upper. We do only
        // one here.

        // we first compute where are the end points of the error bars.
        double[] errorBarEndValue = new double[e.length];
        for (int i = 0; i < errorBarEndValue.length; i++) {
            errorBarEndValue[i] = y[i] + e[i];
        }
        // now we convert both the data values and the error bar end values.
        double[] y_converted = convert(y, x, yunit, xunit, nunit, raise);
        double[] result = convert(errorBarEndValue, x, yunit, xunit, nunit, raise);
        // the error bar in converted units is the difference.
        for (int i = 0; i < result.length; i++) {
            result[i] = y_converted[i] - result[i];
        }
        return result;
    }

    @Override
    public double[] convert(double[] y, double[] x, XUnit xUnit, YUnit toUnit) throws UnitsException {
        return convert(y, x, this, xUnit, toUnit, true);
    }

    @Override
    public double[] convertErrors(double[] e, double[] y, double[] x, XUnit xUnit, YUnit toUnit) throws UnitsException {
        return convertErrors(e, y, x, this, xUnit, toUnit, true);
    }

}



