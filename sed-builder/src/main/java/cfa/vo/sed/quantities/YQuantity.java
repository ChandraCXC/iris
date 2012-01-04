/**
 * Copyright (C) Smithsonian Astrophysical Observatory
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

import java.util.List;

/**
 *
 * @author olaurino
 */
public enum YQuantity implements IQuantity{
    FLUX(new Flux()),
    FLUXDENSITY(new FluxDensity()),
//    LUMINOSITY(new Luminosity()),
    MAGNITUDE(new Magnitude()),
    PHOTONFLUXDENSITY(new PhotonFluxDensity()),
//    SURFACEBRIGHTNESS(new SurfaceBrightness()),
//    COUNTS(new Counts()),
//    COUNTRATE(new CountRate()),
//    FLUXRATIO(new FluxRatio()),
//    ENERGYDENSITY(new EnergyDensity()),
//    POLARIZEDFLUX(new PolarizedFlux()),
//    POLARIZATION(new Polarization()),
//    ANTENNATEMP(new AntennaTemp()),
//    BRIGHTNESSTEMP(new BrightnessTemp()),
//    TRANSMISSION(new Transmission()),
//    EFFECTIVEAREA(new EffectiveArea()),
//    CONTINUUMFLUX(new ContinuumFlux()),
    ;

    private IQuantity quantity;

    private YQuantity(IQuantity quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return quantity.getName();
    }

    public String getDescription() {
        return quantity.getDescription();
    }

    public String getUCD() {
        return quantity.getUCD();
    }

    public List<IUnit> getPossibleUnits() {
        return quantity.getPossibleUnits();
    }

}
