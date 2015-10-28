package cfa.vo.iris.units;

/**
 * Created by olaurino on 10/28/15.
 */
public interface IUnitsFactory {
    XUnits newXUnits(String unit_string);

    YUnits newYUnits(String unit_string);

    double[] convertX(double[] x, XUnits xunit, XUnits nunit) throws UnitsException;

    double[] convertX(double[] x, String xunit, String nunit) throws UnitsException;

    double[] convertY(double[] y, double[] x, YUnits yunit, XUnits xunit, YUnits nunit, boolean raise)
            throws UnitsException;

    double[] convertErrors(double[] e, double[] y, double[] x, YUnits yunit,
                           XUnits xunit, YUnits nunit, boolean raise)
            throws UnitsException;
}
