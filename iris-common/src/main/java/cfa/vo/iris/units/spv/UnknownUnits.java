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

import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.YUnit;

public class UnknownUnits implements XUnit, YUnit {
    private static final String ERROR = "Cannot convert invalid units";

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public double getFactor() {
        return 1;
    }

    @Override
    public boolean isMagnitude() {
        return false;
    }

    @Override
    public double[] convert(double[] y, double[] x, XUnit xUnit, YUnit toUnit) throws UnitsException {
        throw new UnitsException(ERROR);
    }

    @Override
    public double[] convertErrors(double[] e, double[] y, double[] x, XUnit xUnit, YUnit toUnit) throws UnitsException {
        throw new UnitsException(ERROR);
    }

    @Override
    public double[] convert(double[] x, XUnit toUnit) throws UnitsException {
        throw new UnitsException(ERROR);
    }

}
