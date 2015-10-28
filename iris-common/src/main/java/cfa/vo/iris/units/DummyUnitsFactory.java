package cfa.vo.iris.units;

/**
 * Created by olaurino on 10/28/15.
 */
public class DummyUnitsFactory implements IUnitsFactory {
    public final static DummyUnitsFactory INSTANCE = new DummyUnitsFactory();

    private DummyUnitsFactory() {

    }

    @Override
    public XUnits newXUnits(String unit_string) {
        return new DummyXUnits(unit_string);
    }

    @Override
    public YUnits newYUnits(String unit_string) {
        return new DummyYUnits(unit_string);
    }

    // These convert methods might actually just overload a single convert symbol
    @Override
    public double[] convertX(double[] x, XUnits xunit, XUnits nunit) throws UnitsException {
        return x;
    }

    @Override
    public double[] convertX(double[] x, String xunit, String nunit) throws UnitsException {
        return convertX(x, newXUnits(xunit), newXUnits(nunit));
    }

    @Override
    public double[] convertY(double[] y, double[] x, YUnits yunit, XUnits xunit, YUnits nunit, boolean raise) throws UnitsException {
        return y;
    }

    @Override
    public double[] convertErrors(double[] e, double[] y, double[] x, YUnits yunit, XUnits xunit, YUnits nunit, boolean raise) throws UnitsException {
        return e;
    }


}
