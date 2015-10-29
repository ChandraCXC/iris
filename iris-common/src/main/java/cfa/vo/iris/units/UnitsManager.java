package cfa.vo.iris.units;

/**
 * Interface defining the behavior of a Units Manager, i.e. a class that provides utility methods
 * for creating an manipulating unit instances.
 */
public interface UnitsManager {
    /**
     * <p>Create a new instance of a spectral unit.
     *
     * <p>If an invalid string is passed to the method, then an invalid unit instance of @XUnit is returned.
     *
     * @param unitString
     * @return
     */
    XUnit newXUnits(String unitString);

    /**
     * <p>Create a new instance of a flux, flux density, photon flux density, magnitude unit.</p>
     *
     * <p>If an invalid string is passed to the method, then an invalid unit instance of @XUnit is returned.
     *
     * @param unitString
     * @return
     */
    YUnit newYUnits(String unitString);

    /**
     * Convert an array from a given spectral unit to another.
     *
     * @param x The input array.
     * @param xunit The units of the values in the input array.
     * @param toUnit The target units.
     * @return An array of double converted to the target units.
     * @throws UnitsException
     */
    double[] convertX(double[] x, XUnit xunit, XUnit toUnit) throws UnitsException;

    /**
     * Convenience method for passing unit strings rather than unit instances.
     *
     * @param x The input array.
     * @param xunit The units of the values in the input array.
     * @param toUnit The target units.
     * @return An array of double converted to the target units.
     * @throws UnitsException
     */
    double[] convertX(double[] x, String xunit, String toUnit) throws UnitsException;

    /**
     *
     * Convert an array from a given flux, flux density, etc. unit to another.
     *
     * @param y The input array.
     * @param x The spectral values corresponding to the input array. This is required for e.g. converting from
     *          flux density to flux.
     * @param yunit The units in which the input array is represented.
     * @param xunit The units in which the spectral array is represented.
     * @param toUnit The target units.
     * @return An array of double converted to the target units.
     * @throws UnitsException
     */
    double[] convertY(double[] y, double[] x, YUnit yunit, XUnit xunit, YUnit toUnit)
            throws UnitsException;

    /**
     * Convenience method for passing unit strings rather than unit instances.
     *
     * @param y The input array.
     * @param x The spectral values corresponding to the input array. This is required for e.g. converting from
     *          flux density to flux.
     * @param yunit The units in which the input array is represented.
     * @param xunit The units in which the spectral array is represented.
     * @param toUnit The target units.
     * @return An array of double converted to the target units.
     * @throws UnitsException
     */
    double[] convertY(double[] y, double[] x, String yunit, String xunit, String toUnit)
            throws UnitsException;

    /**
     * Convert errors for the Y axis. The algorithm for converting errors may depend on the
     * type of quantity represented by a units instance.
     *
     * @param e The input array.
     * @param y The array of values of which the input array represents the errors.
     * @param x The spectral values corresponding to the input array. This is required for e.g. converting from
     *          flux density to flux.
     * @param yunit The units in which the input array and the y array are represented.
     * @param xunit The units in which the spectral array is represented.
     * @param toUnit The target units.
     * @return An array of double converted to the target units.
     * @throws UnitsException
     */
    double[] convertErrors(double[] e, double[] y, double[] x, YUnit yunit,
                           XUnit xunit, YUnit toUnit)
            throws UnitsException;

    /**
     * Convenience method for passing unit strings rather than unit instances.
     *
     * @param e The input array.
     * @param y The array of values of which the input array represents the errors.
     * @param x The spectral values corresponding to the input array. This is required for e.g. converting from
     *          flux density to flux.
     * @param yunit The units in which the input array and the y array are represented.
     * @param xunit The units in which the spectral array is represented.
     * @param toUnit The target units.
     * @return An array of double converted to the target units.
     * @throws UnitsException
     */
    double[] convertErrors(double[] e, double[] y, double[] x, String yunit,
                           String xunit, String toUnit)
            throws UnitsException;
}
