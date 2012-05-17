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

package cfa.vo.sed.builder;

import cfa.vo.sed.setup.ISetup;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import java.util.List;

/**
 *
 * @author omarlaurino
 */
public class SegmentBuilder implements ISegmentBuilder {

    private Segment segment = new Segment();

    public Segment build() {
        return segment;
    }

    public ISegmentBuilder withFlux(Object[] fluxArray, String units) throws SedNoDataException {
        return withFlux(fluxArray, new NullSegmentParameter(), units);
    }

    public ISegmentBuilder withSpectralCoordinate(Object[] specArray, String units) throws SedNoDataException {
        return withSpectralCoordinate(specArray, new NullSegmentParameter(), units);
    }

    public ISegmentBuilder withFlux(Object[] fluxArray, ISegmentParameter error, String units) throws SedNoDataException {

        //TODO check that Object is a number

        double[] newFluxArray = new double[fluxArray.length];

        for(int i=0; i<fluxArray.length; i++) {
            newFluxArray[i] = ((Number)fluxArray[i]).doubleValue();
        }

        segment.setFluxAxisValues(newFluxArray);
        segment.setFluxAxisUnits(units);

        if(!(error instanceof NullSegmentParameter) && error!=null) {
            segment.createChar().createFluxAxis().createAccuracy().createStatError().setValue((Double)error.getValue());
            segment.getChar().getFluxAxis().getAccuracy().getStatError().setUnit(units);
        }

        return this;
    }

    public ISegmentBuilder withSpectralCoordinate(Object[] specArray, ISegmentParameter error, String units) throws SedNoDataException {

        //TODO check that specArray is made of numbers

        double[] newSpecArray = new double[specArray.length];

        for(int i=0; i<specArray.length; i++) {
            newSpecArray[i] = ((Number)specArray[i]).doubleValue();
        }

        segment.setSpectralAxisValues(newSpecArray);
        segment.setSpectralAxisUnits(units);

        if(!(error instanceof NullSegmentParameter) && error!=null) {
            segment.createChar().createSpectralAxis().createAccuracy().createStatError().setValue((Double)error.getValue());
            segment.getChar().getSpectralAxis().getAccuracy().getStatError().setUnit(units);
        }

        return this;
    }

    public List<Segment> build(ISetup conf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
