/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.sed.quantities;

import cfa.vo.iris.sed.SedException;

/**
 *
 * @author olaurino
 */
public enum XUnit implements IUnit {
    ANGSTROM("Angstrom", "em.wl"),
    NM("nm", "em.wl"),
    MICRON("\u03bcm", "em.wl"),
    MM("mm","em.wl"),
    CM("cm", "em.wl"),
    M("m", "em.wl"),
    HERTZ("Hz", "em.freq"),
    KHZ("kHz", "em.freq"),
    MHZ("MHz", "em.freq"),
    GHZ("GHz", "em.freq"),
    THZ("THz", "em.freq"),
    EV("eV", "em.energy"),
    KEV("KeV", "em.energy"),
    MEV("MeV", "em.energy"),
    GEV("GeV", "em.energy"),
    WNMICRON("1/\u03bcm", "em.wavenumber"),
	//ARBITRARYXWL("Arbitrary wavelength", "em.wl"),
	//ARBITRARYXE("Arbitrary energy", "em.energy"),
	//ARBITRARYXWN("Arbitrary wavenumber", "em.wavenumber"),
	//ARBITRARYXFREQ("Arbitrary frequency", "em.freq"),
    KMPSCO("km/s @ 12 CO (11.5GHz)","em.velocity"), //Should be "spect.dopplerVeloc.radio"
    KMPS21CM("km/s @ 21cm","em.velocity"), 
    ;

    private String string;
    private String UCD;

    private XUnit(String string, String UCD) {
        this.string = string;
        this.UCD = UCD;
    }

    public String getString() {
        return string;
    }

    public String getUCD() {
        return UCD;
    }

    public static XUnit getFromUnitString(String string) throws SedException {
        XUnit[] arr = XUnit.values();
        for(XUnit unit : arr) {
            if(unit.getString().equals(string))
                return unit;
        }
        throw new SedException("non existent quantity");
    }

}
