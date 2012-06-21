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
public enum SPVYUnit implements IUnit {
    FLUXDENSITYWL0("erg/s/cm2/Angstrom", "em.wl"),
    FLUXDENSITYWL1("Watt/m2/"  + '\u03BC' + "m", "em.wl"),
    FLUXDENSITYWL4("erg/s/cm2/Hz", "em.freq"),
    FLUX0("Jy-Hz", "em.wl"),
    FLUX1("erg/s/cm2", "em.freq"),
    FLUXDENSITYFREQ1("Jy", "em.freq"),
    FLUXDENSITYFREQ3("mJy", "em.freq"),
    FLUXDENSITYFREQ2("Watt/m2/Hz", "em.freq"),
    PHOTONFLUX("photon/cm2/s", "em.freq"),
    PHOTONFLUXDENSITY0("photon/s/cm2/Hz", "em.freq"),
    PHOTONFLUXDENSITY1("photon/s/cm2/Angstrom", "em.wl"),
    ABMAG("ABMAG", ""),
    STMAG("STMAG", ""),
    OBMAG("OBMAG", ""),
    ;

    private String string;
    private String UCD;

    private SPVYUnit(String string, String UCD) {
        this.string = string;
        this.UCD = UCD;
    }

    public String getString() {
        return string;
    }

    public String getUCD() {
        return UCD;
    }

    public static SPVYUnit getFromUnitString(String string) throws SedImporterException {
        SPVYUnit[] arr = SPVYUnit.values();
        for(SPVYUnit unit : arr) {
            if(unit.getString().equals(string))
                return unit;
        }
        throw new SedImporterException("non existent unit");
    }

}
