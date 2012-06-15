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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.quantities;

import cfa.vo.sed.builder.SedImporterException;

/**
 *
 * @author olaurino
 */
public enum YUnit implements IUnit {
    FLUXDENSITYWL0("erg cm**(-2) s**(-1) angstrom**(-1)", "em.wl"),
    FLUXDENSITYWL1("W m**(-2) m**(-1)", "em.wl"),
    FLUXDENSITYWL2("keV cm**(-2) s**(-1) angstrom**(-1)", "em.wl"),
    FLUXDENSITYWL3("Watt m**(-2)"  + '\u03BC' + "m**(-1)", "em.wl"),
    FLUXDENSITYFREQ0("erg cm**(-2) s**(-1) Hz**(-1)", "em.freq"),
    FLUXDENSITYWL4("erg cm**(-2) s**(-1) Hz**(-1)", "em.freq"),
    FLUXDENSITYFREQ1("Jy", "em.freq"),
    FLUXDENSITYFREQ2("W m**(-2) Hz**(-1)", "em.freq"),
    FLUXDENSITYENERGY0("keV cm**(-2) s**(-1) kev**(-1)", "em.energy"),
    PHOTONFLUXDENSITY0("cm**(-2) s**(-1) keV**(-1)", "em.energy;meta.number"),
    PHOTONFLUXDENSITY1("photon cm**(-2) s**(-1) angstrom**(-1)", "em.wl"),
    PHOTONFLUXDENSITY2("photon cm**(-2) s**(-1) Hz**(-1)", "em.freq"),
    FLUX0("Jy Hz", "em.wl"),
    FLUX1("erg cm**(-1) s**(-1)", "em.wl"),
    SURFACEBRIGHTNESSWL0("erg cm**(-2) s**(-1) angstrom**(-1) arcsec**(-2)", "em.wl"),
    SURFACEBRIGHTNESSFREQ0("Jy sr**(-1)", "em.freq"),
    COUNTS("count", ""),
    COUNTRATE("count/s", "arith.rate"),
    FLUXRATIO("", "arith.ratio"),
    LUMINOSITYWL0("erg s**(-1) angstrom**(-1)", "em.wl"),
    LUMINOSITYWL1("W/m", "em.wl"),
    LUMINOSITYFREQ0("erg s**(-1) Hz**(-1)", "em.freq"),
    LUMINOSITYFREQ1("W/Hz", "em.freq"),
    LUMINOSITYEN("erg s**(-1) keV**(-1)", "em.freq"),
    LUMINOSITYLOGFREQ0("erg s**(-1)", "em.energy"),
    LUMINOSITYLOGFREQ1("W", "em.energy"),
    ENERGYDENSITY0("erg cm**(-3)", ""),
    ENERGYDENSITY1("J m**(-3)", ""),
    POLAR("", ""),
    RADIANCE("erg cm**(-2) s**(-1) sr**(-1) angstrom**(-1)", "em.wl"),
    ANTENNATEMP("K", ""),
    BRIGHTTEMP("K", ""),
    MAG("mag", ""),
    FLUXBEAM("Jy/beam", "instr.beam"),
    SURFACEBRIGHTNESS("mag arcsec**(-2)", ""),
    TRANSMISSION("", ""),
    EFFAREA("cm**2", ""),
    CONTFLUX("erg cm**(-2) s**(-1) angstrom**(-1) arcsec**(-2)", "em.wl"),
    ;

    private String string;
    private String UCD;

    private YUnit(String string, String UCD) {
        this.string = string;
        this.UCD = UCD;
    }

    public String getString() {
        return string;
    }

    public String getUCD() {
        return UCD;
    }

    public static YUnit getFromUnitString(String string) throws SedImporterException {
        YUnit[] arr = YUnit.values();
        for(YUnit unit : arr) {
            if(unit.getString().equals(string))
                return unit;
        }
        throw new SedImporterException("non existent unit");
    }

}
