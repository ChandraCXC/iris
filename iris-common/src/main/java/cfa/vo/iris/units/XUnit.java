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
 * Interface for Spectral coordinate units support.
 *
 * The main goal of this interface is to decouple the implementation of the units conversion logic
 * from its behavior, so that different implementations can be plugged in.
 *
 */
public interface XUnit extends Serializable {
    /**
     * <p>Check that the instance is a valid unit, i.e. if it is supported by the implementation.
     *
     * <p>Implementations should try to be lenient for non-standard notations.
     *
     * <p>The following units should be supported, including alternative notations:
     *
     * <p>Spectral coordinates:<ul>
     * <li>Angstrom
     * <li>nm
     * <li>\u03bcm
     * <li>mm
     * <li>cm
     * <li>m
     * <li>Hz
     * <li>kHz
     * <li>MHz
     * <li>GHz
     * <li>THz
     * <li>eV
     * <li>keV
     * <li>MeV
     * <li>GeV
     * <li>1/\u03bcm
     * <li>km/s @ 12 CO (11.5GHz)
     * <li>km/s @ 21cm
     *
     *
     * @return if the instance is valid according to the implementation.
     */
    boolean isValid();

    /**
     * Convert an array from the units represented by the instance to the toUnit unit.
     *
     * @param x The input array
     * @param toUnit The targe unit
     * @return A converted array of doubles
     * @throws UnitsException
     */
    double[] convert(double[] x, XUnit toUnit) throws UnitsException;
}
