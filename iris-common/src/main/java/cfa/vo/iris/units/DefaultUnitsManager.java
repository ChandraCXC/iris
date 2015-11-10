/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
