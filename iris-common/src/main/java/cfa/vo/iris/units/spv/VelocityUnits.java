package cfa.vo.iris.units.spv;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 17/12/2006
 * Time: 17:03:02
 */

public class VelocityUnits extends XUnits {

    public static String PREFIX = "km/s";

    private double refwave = Double.NaN;

    /**
     *  Constructor.
     *
     *  @param  unitsString      string with the units
     *  @param  refwave          reference wavelength
     *  @param  refUnits         units in which reference wavelength is expresssed.
     */
    public VelocityUnits(String unitsString, double refwave, String refUnits) {
        super(unitsString);

        if (unitsString.indexOf("@") < 0) {
            originalSpelling =  unitsString + " @ " + String.valueOf(refwave) + " " + refUnits;
        } else {
            originalSpelling = unitsString;
        }

        this.unitsString = originalSpelling;

        XUnits orig = new XUnits(refUnits);
        this.refwave = orig.convertToStandardUnits(refwave);

        double factor = 1.0;
        Double hold = factors.get(this.unitsString.toLowerCase());
        if (hold != null) {
            factor = hold;
        }

        converters.put(toString(), new VConverter(this.refwave, factor));

        makeConverterObject();
    }

    public boolean isValid() {
        return ! Double.isNaN(refwave);
    }

    private static class VConverter implements Converter {
        private double refwave;
        private double factor;

        public VConverter(double refwave, double factor) {
            this.refwave = refwave;
            this.factor = factor;
        }
        public double convertFrom(double arg) {
            return (arg * factor / (Constant.C / 1.E13) * refwave) + refwave;
        }
        public double getReferenceWavelength() {
            return refwave;
        }
        public double convertTo(double arg) {
            return (arg - refwave) / refwave * (Constant.C / 1.E13) / factor;
        }
        public String getAxisLabel() {return "Velocity";}
        public String getUCD()       {return "em.velocity";}
    }

    // The standard velocity units form is km/s. Factors stored here
    // enable any units expressed in different forms to share the same
    // conversion code.

    private static Map<String, Double> factors = new HashMap<String, Double>();

    /*
     This method is required because otherwise this code (which was executed in a static block) would
     never be executed and velocity units would not be registered in the parent class.
     This is obviously a
     workaround. TODO a better job, probably offloading some of the management functionality to the UnitsManager.
      */

    public static void init() {
        factors.put(PREFIX, 1.0);
        factors.put("m/s",  1.E-3);

        new VelocityUnits(PREFIX + " @ 21cm",            1420.4058,  "MHz");
        new VelocityUnits(PREFIX + " @ 12 CO (11.5GHz)", 11.5271202, "GHz");
    }

}
