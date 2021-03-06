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

public class Constant {
    // [OL] Updating these values to have more precision. The values come from astropy
    public static final double H = 6.62606957E-27;  // Planck's constant (erg s)
    public static final double C = 2.99792458E+18; // speed of light (Angstrom/s)
    public static final double MEV2ERG = 1.602176565E-6; // MeV to erg conversion constant
    public static final double E = H * C / MEV2ERG; // Angstrom * MeV

    // Constant used for Rayleigh conversions.
    public static final double RAYLEIGH_FACTOR = (Math.pow((180./Math.PI*3600.),2) * 4. * Math.PI )  / (H * C);

    private Constant() {
        /* Only constant fields */
    }
}
