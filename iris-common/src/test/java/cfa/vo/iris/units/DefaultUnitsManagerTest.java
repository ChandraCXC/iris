package cfa.vo.iris.units;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultUnitsManagerTest {

    private static UnitsManager manager;
    private static List<String> xStrings;
    private static List<String> yStrings;
    private static List<String> magnitudes;

    private static final double[] X = new double[]{1, 2, 3, 4, 5};
    private static final double[] Y = new double[]{1, 2, 3, 4, 5};
    private static final double[] E = new double[]{0.1, 0.2, 0.3, 0.4, 0.5};

    private static final double PRECISION = 1e5;

    @Test
    public void buildValidXUnits() throws Exception {
        for(String s : xStrings) {
            XUnit unit = manager.newXUnits(s);
            assertTrue(s + " should be a valid x unit string",
                    unit.isValid());
        }
    }

    @Test
    public void buildValidYUnits() throws Exception {
        for(String s : yStrings) {
            YUnit unit = manager.newYUnits(s);
            assertTrue(s + " should be a valid y unit string",
                    unit.isValid());
        }

        YUnit unit = manager.newYUnits("ABMAG");
        assertTrue(unit.isMagnitude());
    }

    @Test
    public void magnitudes() throws Exception {
        for(String s: yStrings) {
            YUnit unit = manager.newYUnits(s);
            if(magnitudes.contains(s)) {
                assertTrue(s + " should be a magnitude", unit.isMagnitude());
            } else {
                assertFalse(s + " should not be a magnitude", unit.isMagnitude());
            }
        }
    }

    @Test
    public void factors() throws Exception {
        YUnit unit = manager.newYUnits("10**1 Jy");
        assertEquals(10, unit.getFactor(), 1e-20);

        unit = manager.newYUnits("10**3.Jy");
        assertEquals(1000, unit.getFactor(), 1e-20);

        unit = manager.newYUnits("10**(-3) Jy");
        assertEquals(0.001, unit.getFactor(), 1e-20);

        unit = manager.newYUnits("10**(-5).Jy");
        assertEquals(0.00001, unit.getFactor(), 1e-20);

        unit = manager.newYUnits("10**-1 Jy");
        assertEquals(0.1, unit.getFactor(), 1e-20);

        unit = manager.newYUnits("10**-2.Jy");
        assertEquals(0.01, unit.getFactor(), 1e-20);
    }

    @Test
    public void buildInvalidXUnits() throws Exception {
        for(String s : yStrings) {
            XUnit unit = manager.newXUnits(s);
            assertFalse(s + " should not be a valid x unit string",
                    unit.isValid());
        }

        XUnit unit = manager.newXUnits("blah");
        assertFalse("blah should not be a valid x unit string", unit.isValid());
    }

    @Test
    public void buildInvalidYUnits() throws Exception {
        for(String s : xStrings) {
            YUnit unit = manager.newYUnits(s);
            assertFalse(s + " should not be a valid y unit string",
                    unit.isValid());
        }

        YUnit unit = manager.newYUnits("blah");
        assertFalse("blah should not be a valid y unit string", unit.isValid());
    }

    @Test
    public void emptyStrings() throws Exception {
        XUnit unit = manager.newXUnits("");
        assertFalse("empty strings are not valid units", unit.isValid());

        YUnit yUnit = manager.newYUnits("");
        assertFalse("empty strings are not valid units", yUnit.isValid());

        assertEquals("The factor of an invalid Y unit should be 1", 1.0, yUnit.getFactor(), 1e-20);
    }

    /**
     * We used astropy as an oracle for the conversions, e.g.:
     * In [59]: (x*u.Hz).to("keV", equivalencies=u.spectral())
     * Out[59]:
     *   <Quantity [  4.13566751e-18,  8.27133503e-18,  1.24070025e-17,
     *       1.65426701e-17,  2.06783376e-17] keV>
     */
    @Test
    public void convertXWithStrings() throws Exception {
        double[] observed = manager.convertX(X, "Angstrom", "Hz");
        double[] expected = new double[]{
                2.99792458e+18, 1.49896229e+18, 9.99308193e+17,
                7.49481145e+17, 5.99584916e+17};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);
        observed = manager.convertX(X, "Hz", "Angstrom");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                4.13566751e-18,  8.27133503e-18,  1.24070025e-17,
                1.65426701e-17,  2.06783376e-17};
        observed = manager.convertX(X, "Hz", "keV");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                2.99792458e+17, 1.49896229e+17, 9.99308193e+16,
                7.49481145e+16, 5.99584916e+16};
        observed = manager.convertX(X, "Hz", "nm");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                2.99792458e+11,  1.49896229e+11,  9.99308193e+10,
                7.49481145e+10,  5.99584916e+10};
        observed = manager.convertX(X, "\u03bcm", "kHz");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                2.99792458e+14, 1.49896229e+14, 9.99308193e+13,
                7.49481145e+13, 5.99584916e+13};
        observed = manager.convertX(X, "Hz", "um");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);
        observed = manager.convertX(X, "Hz", "\u03bcm");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                2.99792458e+11, 1.49896229e+11, 9.99308193e+10,
                7.49481145e+10, 5.99584916e+10};
        observed = manager.convertX(X, "Hz", "mm");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                2.99792458e+10, 1.49896229e+10, 9.99308193e+9,
                7.49481145e+9, 5.99584916e+9};
        observed = manager.convertX(X, "Hz", "cm");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                2.99792458e+8, 1.49896229e+8, 9.99308193e+7,
                7.49481145e+7, 5.99584916e+7};
        observed = manager.convertX(X, "Hz", "m");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);
        observed = manager.convertX(X, "\u03bcm", "MHz");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                2.99792458e+15, 1.49896229e+15, 9.99308193e+14,
                7.49481145e+14, 5.99584916e+14};
        observed = manager.convertX(X, "Angstrom", "kHz");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                4.13566751e-15,  8.27133503e-15,  1.24070025e-14,
                1.65426701e-14,  2.06783376e-14};
        observed = manager.convertX(X, "Hz", "eV");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                3.45574370e+15,  1.72787185e+15,  1.15191457e+15,
                8.63935924e+14,  6.91148739e+14};
        observed = manager.convertX(X, "Hz", "km/s @ 12 CO (11.5GHz)");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        expected = new double[]{
                4.25825207e+14,  2.12912603e+14,  1.41941735e+14,
                1.06456302e+14,  8.51650412e+13};
        observed = manager.convertX(X, "Hz", "km/s @ 21cm");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);
    }

    @Test(expected = UnitsException.class)
    public void convertXWithInvalidString() throws Exception {
        manager.convertX(X, "blah", "Angstrom");
    }

    @Test(expected = UnitsException.class)
    public void convertXWithInvalidString2() throws Exception {
        manager.convertX(X, "Angstrom", "blah");
    }

    @Test(expected = UnitsException.class)
    public void convertXWithInvalidString3() throws Exception {
        manager.convertX(X, "blah", "blah");
    }

    @Test(expected = UnitsException.class)
    public void convertYWithInvalidString() throws Exception {
        manager.convertY(Y, X, "Jy", "Angstrom", "bah");
    }

    @Test(expected = UnitsException.class)
    public void convertYWithInvalidString2() throws Exception {
        manager.convertY(Y, X, "blah", "Angstrom", "Jy");
    }

    @Test(expected = UnitsException.class)
    public void convertYWithInvalidString3() throws Exception {
        manager.convertY(Y, X, "Jy", "blah", "mJy");
    }

    @Test(expected = UnitsException.class)
    public void cannotConvertInvalidErrorsUnit() throws Exception {
        manager.convertErrors(E, Y, X, "blah", "Hz", "Jy");
    }
    /**
     * [OL] The combinations here are plenty, as one can combine yUnit, xUnit and toUnit at free.
     * Maybe we can come up with a script that creates the text for all the tests.
     * Also, there is no support in Astropy for magnitudes, so for now I am using specview
     * as the oracle for conversions to magnitudes, but we should get convirmations from other
     * sources regarding the outputs of these conversions.
     *
     * Similarly, I could not use Astropy for converting to Rayleigh/Angstrom. We should
     * check the formula that Specview is using.
     */
    @Test
    public void convertY() throws Exception {
        // photon flux density to flux density
        double[] observed = manager.convertY(Y, X, "photon/s/cm2/Angstrom", "Angstrom", "erg/s/cm2/Hz");
        double[] expected = new double[]{
                6.62606957e-27,  2.65042783e-26,  5.96346261e-26,
                1.06017113e-25,  1.65651739e-25};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        // photon flux density to flux density
        observed = manager.convertY(Y, X, "photon/s/cm2/Angstrom", "Angstrom", "Watt/m2/μm");
        expected = new double[]{
                1.98644568e-07,  1.98644568e-07,  1.98644568e-07,
                1.98644568e-07,  1.98644568e-07};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        // photon flux density to flux density
        observed = manager.convertY(Y, X, "photon/s/cm2/Angstrom", "Angstrom", "Watt/m2/nm");
        expected = new double[]{
                1.98644568e-10, 1.98644568e-10, 1.98644568e-10,
                1.98644568e-10,  1.98644568e-10};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        // flux density to flux
        observed = manager.convertY(Y, X, "erg/s/cm2/Angstrom", "Angstrom", "Jy-Hz");
        expected = new double[]{
                1.00000000e+23,  4.00000000e+23,  9.00000000e+23,
                1.60000000e+24,  2.50000000e+24};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);
        observed = manager.convertY(Y, X, "erg/s/cm2/Hz", "Hz", "Jy-Hz");
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        // flux density to flux, with different spectral unit
        observed = manager.convertY(Y, X, "erg/s/cm2/Hz", "Angstrom", "Jy-Hz");
        expected = new double[]{
                2.99792458e+41,  2.99792458e+41,  2.99792458e+41,
                2.99792458e+41,  2.99792458e+41};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

        // flux density to mag
        // FIXME this uses specview as an oracle for itself
        observed = manager.convertY(Y, X, "erg/s/cm2/Hz", "Hz", "ABMAG");
        expected = new double[]{
                -48.6,  -49.35257,  -49.79280,
                -50.1051,  -50.34752};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);

    }

    @Test
    public void testConvertErrors() throws Exception {
        double[] observed = manager.convertErrors(E, Y, X, "erg/s/cm2/Hz", "Hz", "Jy");
        double[] expected = new double[]{
                1e+22, 2e+22, 3e+22, 4e+22, 5e+22};
        assertArrayEquals(expected, observed, Math.abs(Math.abs(expected[0])) / PRECISION);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        manager = new DefaultUnitsManager();

        xStrings = new ArrayList<String>();
        xStrings.add("Angstrom");
        xStrings.add("nm");
        xStrings.add("\u03bcm");
        xStrings.add("mm");
        xStrings.add("cm");
        xStrings.add("m");
        xStrings.add("Hz");
        xStrings.add("kHZ");
        xStrings.add("MHz");
        xStrings.add("GHz");
        xStrings.add("THz");
        xStrings.add("ev");
        xStrings.add("KeV");
        xStrings.add("MeV");
        xStrings.add("GeV");
        xStrings.add("1/\u03bcm");
        xStrings.add("km/s @ 12 CO (11.5GHz)");
        xStrings.add("km/s @ 21cm");

        yStrings = new ArrayList<String>();

        // photon flux densities
        yStrings.add("photon/s/cm2/Angstrom");
        yStrings.add("photon/s/cm2/Hz");

        // flux densities
        yStrings.add("erg/s/cm2/Hz");
        yStrings.add("erg/s/cm2/Angstrom");
        yStrings.add("Watt/m2/\u03BCm");
        yStrings.add("Watt/m2/nm");
        yStrings.add("Watt/m2/Hz");
        yStrings.add("Rayleigh/Angstrom");

        // fluxes
        yStrings.add("Jy-Hz");
        yStrings.add("erg/s/cm2");
        yStrings.add("Jy");
        yStrings.add("mJy");
        yStrings.add("\u03BCJy");

        // magnitudes
        yStrings.add("ABMAG");
        yStrings.add("STMAG");

        magnitudes = new ArrayList<String>();
        magnitudes.add("ABMAG");
        magnitudes.add("STMAG");
    }
}