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

import java.io.Serializable;

/**
 * <p>Interface for dependent (flux, flux density, magnitude) coordinate units support.
 *
 * <p>The main goal of this interface is to decouple the implementation of the units conversion logic
 * from its behavior, so that different implementations can be plugged in.
 *
 */
public interface YUnit extends Serializable {
    /**
     * <p>Check that the instance is a valid unit, i.e. if it is supported by the implementation.
     * Implementations should try to be lenient for spelling errors and non-standard notations.
     *
     * <p>The following units should be supported, including alternative notations:
     *
     * <p>Photon Flux Density:<ul>
     * <li>photon/s/cm2/Angstrom
     * <li>photon/s/cm2/Hz
     * </ul></p>
     *
     * <p>Flux Density:<ul>
     * <li>erg/s/cm2/Hz
     * <li>erg/s/cm2/Angstrom
     * <li>Watt/m2/\u03BCm
     * <li>Watt/m2/nm
     * <li>Watt/m2/Hz
     *
     *
     * <p>Flux:<ul>
     * <li>Jy-Hz
     * <li>erg/s/cm2
     * <li>Jy
     * <li>mJy
     * <li>\u03BCJy
     *
     * <p>Magnitude:<ul>
     * <li>ABMAG
     * <li>STMAG
     *
     * *
     */
    boolean isValid();

    /**
     *
     * <p>Return the unit's multiplication factor.
     *
     * <p>Some units may be represented as a power of a different unit, e.g. 10**2.Jy.
     * This method returns the factor, e.g. in the above case it would return 100.
     *
     * @return A double representing the factor of this unit.
     */
    double getFactor();

    /**
     *
     * Return whether this unit represents a magnitude.
     *
     * @return
     */
    boolean isMagnitude();

    /**
     * <p>Convert an array from the units represented by the instance to the toUnit unit.
     *
     * <p>When converting from or to flux densities the spectral coordinate is also required to perform the conversion.
     * For this reason this method also accepts an array for the dependent variable and its units.
     *
     * @param y The input array that needs to be converted.
     * @param x The spectral coordinates corresponding to the y array.
     * @param xUnit The units of the spectral coordinates array.
     * @param toUnit The target units.
     * @return A double array representing the input array in the target units.
     * @throws UnitsException
     */
    double[] convert(double[] y, double[] x, XUnit xUnit, YUnit toUnit) throws UnitsException;

    /**
     * <p>Convert an array of statistical errors for a depenent coordinate array.</p>
     *
     * <p>The array of values y as well as the corresponding spectral coordinate x are required
     * in order to perform the calcularion.</p>
     *
     * <p>Note that the error array and the y array are assumed to have the same units.</p>
     *
     * @param e The input array to be converted
     * @param y The array of values to which this array corresponds
     * @param x The array of values for the spectral axis
     * @param xUnit The units of the spectral values.
     * @param toUnit The target unit.
     * @return A double array representing the input array in the target units.
     * @throws UnitsException
     */
    double[] convertErrors(double[]e, double[] y, double[] x, XUnit xUnit, YUnit toUnit) throws UnitsException;
}
