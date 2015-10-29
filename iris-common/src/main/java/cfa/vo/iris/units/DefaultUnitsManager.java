package cfa.vo.iris.units;

import cfa.vo.iris.units.spv.UnitsFactory;
import cfa.vo.iris.units.spv.VelocityUnits;

public class DefaultUnitsManager implements UnitsManager {

    public DefaultUnitsManager() {
        /*
        See comment in VelocityUnits.init.
         */
        VelocityUnits.init();
    }

    @Override
    public XUnit newXUnits(String unitString) {
        return UnitsFactory.makeXUnits(unitString);
    }

    @Override
    public YUnit newYUnits(String unitString) {
        return UnitsFactory.makeYUnits(unitString);
    }

    // These convert methods might actually just overload a single convert symbol
    @Override
    public double[] convertX(double[] x, XUnit xunit, XUnit toUnit) throws UnitsException {
        return xunit.convert(x, toUnit);
    }

    @Override
    public double[] convertX(double[] x, String xunit, String toUnit) throws UnitsException {
        return convertX(x, newXUnits(xunit), newXUnits(toUnit));
    }

    @Override
    public double[] convertY(double[] y, double[] x, YUnit yunit, XUnit xunit, YUnit toUnit) throws UnitsException {
        return yunit.convert(y, x, xunit, toUnit);
    }

    @Override
    public double[] convertY(double[] y, double[] x, String yunit, String xunit, String toUnit) throws UnitsException {
        return convertY(y, x, newYUnits(yunit), newXUnits(xunit), newYUnits(toUnit));
    }

    @Override
    public double[] convertErrors(double[] e, double[] y, double[] x, YUnit yunit, XUnit xunit, YUnit toUnit) throws UnitsException {
        return yunit.convertErrors(e, y, x, xunit, toUnit);
    }

    @Override
    public double[] convertErrors(double[] e, double[] y, double[] x, String yunit, String xunit, String toUnit) throws UnitsException {
        return convertErrors(e, y, x, newYUnits(yunit), newXUnits(xunit), newYUnits(toUnit));
    }


}
