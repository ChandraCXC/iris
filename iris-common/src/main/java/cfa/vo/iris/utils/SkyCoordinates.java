/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
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

package cfa.vo.iris.utils;

import jsky.coords.DMS;
import jsky.coords.HMS;

/**
 * Utility class that provides static method for parsing HMS and DMS string coordinates.
 * @author olaurino
 */
public class SkyCoordinates {

    /**
     * Get the decimal degrees representation of a RA string.
     * If the coordinate is already expressed in decimal degrees then the result is just
     * the parsed number.
     *
     * @param ra A string representing the RA coordinate.
     * @return The RA coordinate in decimal degrees.
     */
    public static Double getRaDeg(String ra) {
        try {
            ra = SpaceTrimmer.sideTrim(ra);
            if(isNumber(ra))
                return Double.valueOf(ra);
            else {
                HMS hms = new HMS(ra);
                return hms.getVal();
            }
        } catch (Exception ex) {
            return Double.NaN;
        }
    }

    /**
     * Get the decimal degrees representation of a DEC string.
     * If the coordinate is already expressed in decimal degrees then the result is just
     * the parsed number.
     *
     * @param ra A string representing the DEC coordinate.
     * @return The DEC coordinate in decimal degrees.
     */
    public static Double getDecDeg(String dec) {
        try {
            dec = SpaceTrimmer.sideTrim(dec);
            if(isNumber(dec))
                return Double.valueOf(dec);
            else {
                DMS dms = new DMS(dec);
                return dms.getVal();
            }
        } catch (Exception ex) {
            return Double.NaN;
        }
    }

    /**
     * Return the decimal degrees representation of a RA string, as a string.
     * @param ra A string representing the RA coordinate.
     * @return The RA coordinate in decimal degrees.
     */
    public static String getRaDegString(String ra) {
        return getRaDeg(ra).toString();
    }

    /**
     * Return the decimal degrees representation of a DEC string, as a string.
     * @param ra A string representing the DEC coordinate.
     * @return The DEC coordinate in decimal degrees.
     */
    public static String getDecDegString(String dec) {
        return getDecDeg(dec).toString();
    }

    private static boolean isNumber(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
