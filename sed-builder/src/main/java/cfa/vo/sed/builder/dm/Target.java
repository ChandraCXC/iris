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
import cfa.vo.sed.setup.validation.AbstractValidable;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Segment;

/**
 *
 * @author olaurino
 */
public class Target extends AbstractValidable implements SegmentComponent {
    public static final String PROP_DEC = "dec";
    public static final String PROP_NAME = "name";
    public static final String PROP_RA = "ra";

    private String name = "UNKNOWN";
    private Double ra;
    private Double dec;

    @Override
    public void addTo(Segment segment) {
        segment.createTarget().createName().setValue(name);
        DoubleParam raD = new DoubleParam(Double.NaN);
        DoubleParam decD = new DoubleParam(Double.NaN);
        if (ra != null) {
            raD = new DoubleParam(ra);
        }
        if (dec != null) {
            decD = new DoubleParam(dec);
        }
        DoubleParam[] pos = new DoubleParam[]{raD, decD};
        segment.createTarget().createPos().setValue(pos);

        segment.createChar().createSpatialAxis().createCoverage().createLocation().setValue(pos);
    }

    public Double getDec() {
        return dec;
    }

    public String getName() {
        return name;
    }

    public Double getRa() {
        return ra;
    }

    public void setDec(Double dec) {
        Double oldDec = this.dec;
        this.dec = dec;
        propertyChangeSupport.firePropertyChange(ExtendedTarget.PROP_DEC, oldDec, dec);
    }

    public void setName(String name) {
        if(name==null || name.isEmpty())
            name = "UNKNOWN";
        
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(ExtendedTarget.PROP_NAME, oldName, name);
    }

    public void setRa(Double ra) {
        Double oldRa = this.ra;
        this.ra = ra;
        propertyChangeSupport.firePropertyChange(ExtendedTarget.PROP_RA, oldRa, ra);
    }

    @Override
    public Validation validate() {
        return new Validation();
    }

}
