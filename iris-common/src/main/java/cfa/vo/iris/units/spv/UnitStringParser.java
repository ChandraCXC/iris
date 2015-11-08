package cfa.vo.iris.units.spv;

import cfa.vo.iris.units.UnitsException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing unit strings with a constant multiplier.
 */
public class UnitStringParser {
    private static final Pattern p = Pattern.compile("([+-]?(?:(?:[1-9][0-9]*\\.?[0-9]*)|(?:0?\\.[0-9]+))(?:[Ee][+-]?[0-9]+)?)?[\\s\\*]?(.*)");

    private UnitStringParser() {
        /* Only static methods */
    }

    /**
     * <p>Return the factor and unit string from an input expression.</p>
     *
     * <p>The factor can be expressed in floating point (scientific) notation. The rest of the string
     * is always interpreted as a unit string: this class does not have any knowledge of what a valid unit
     * string is or is not and will defer this responsibility to other classes, in particular @XUnit and @YUnit.</p>
     *
     * <p>Examples of valid expression are '1e2 Jy', '.1e3*erg/s/cm2', '1e-5erg cm**(-2) s**(-1) Hz**(-1)',
     * 'Jy', '10Jy', etc.</p>
     *
     * @param toParse The input unit expression to be parsed in its factor and unit components.
     * @return A FactoredUnit instance
     * @throws UnitsException
     */
    public static FactoredUnit parse(String toParse) {

        Matcher m = p.matcher(toParse);
        m.matches();

        double factor = m.group(1)!=null && !m.group(1).isEmpty() ? Double.parseDouble(m.group(1)) : 1;
        String unit = m.group(2).trim();

        return new FactoredUnit(factor, unit);
    }

    /**
     * Class representing a unit string with a multiplication factor.
     */
    public static class FactoredUnit {
        private double factor;
        private String unit;

        public FactoredUnit(double factor, String unit) {
            this.factor = factor;
            this.unit = unit;
        }

        public double getFactor() {
            return factor;
        }

        public String getUnit() {
            return unit;
        }
    }
}
