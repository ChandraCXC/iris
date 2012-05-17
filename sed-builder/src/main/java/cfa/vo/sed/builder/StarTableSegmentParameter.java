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

import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.ValueInfo;

/**
 *
 * @author omarlaurino
 */
public class StarTableSegmentParameter extends DescribedValue implements ISegmentParameter {

    private ValueInfo vinfo;

    public StarTableSegmentParameter(ValueInfo vinfo) {
        super(vinfo);
        this.vinfo = vinfo;
    }

    public StarTableSegmentParameter(ValueInfo vinfo, Object value) {
        super(vinfo, value);
        this.vinfo = this.getInfo();
    }

    public StarTableSegmentParameter(DescribedValue dValue) {
        this(dValue.getInfo(), dValue.getValue());
    }

    @Override
    public String toString() {
        return vinfo.getName();
    }

    public String getName() {
        return vinfo.getName();
    }

    public int[] getShape() {
        return vinfo.getShape();
    }

    public String getUCD() {
        return vinfo.getUCD();
    }

    public String getUnitString() {
        return vinfo.getUnitString();
    }

    public String getUtype() {
        return vinfo.getUtype();
    }

    public boolean isArray() {
        return vinfo.isArray();
    }

    public int getElementSize() {
        return vinfo.getElementSize();
    }

    public Class getContentClass() {
        return vinfo.getContentClass();
    }

    public String getDescription() {
        return vinfo.getDescription();
    }

}
