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

package cfa.vo.sed.builder.dm;

import cfa.vo.sed.setup.validation.Validation;
import cfa.vo.sed.quantities.AxisMetadata;
import cfa.vo.sed.quantities.SPVYQuantity;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.Utypes;

/**
 *
 * @author olaurino
 */
public class FluxAxis extends AbstractAxis<SPVYQuantity> {

    private Double error;
    public static final String PROP_ERROR = "error";

    /**
     * Get the value of error
     *
     * @return the value of error
     */
    public Double getError() {
        return error;
    }

    /**
     * Set the value of error
     *
     * @param error new value of error
     */
    public void setError(Double error) {
        Double oldError = this.error;
        this.error = error;
        propertyChangeSupport.firePropertyChange(PROP_ERROR, oldError, error);
    }

    @Override
    public void addTo(Segment segment) throws SedException {

        AxisMetadata md = new AxisMetadata(getQuantity(), getUnit());

        segment.createChar().createFluxAxis().setUcd(md.getUCD());
        segment.createChar().createFluxAxis().setUnit(md.getUnitString());
        if(getValue()!=null)
            segment.setFluxAxisValues(new double[]{getValue()});
        else
            segment.setFluxAxisValues(new double[]{Double.NaN});
        segment.setFluxAxisUnits(md.getUnitString());
        if(error!=null)
            segment.createData().setDataValues(new double[]{error}, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);

    }

    @Override
    public Validation validate() {
        Validation v = new Validation();

//        if(getValue()==null || getValue().isNaN())
//            v.addError("Missing/Invalid Y Axis value");

        if(error!=null && error.isNaN())
            v.addError("Invalid Y Axis Error");

        if(getQuantity()==null)
            v.addError("Missing Y Axis Quantity");

        return v;
    }

}
