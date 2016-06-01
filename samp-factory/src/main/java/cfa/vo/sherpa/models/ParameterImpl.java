/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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
package cfa.vo.sherpa.models;

import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.Element;

import java.lang.reflect.InvocationTargetException;

public final class ParameterImpl implements Parameter {
    private String name;
    private Double val;
    private Double min;
    private Double max;
    private Integer frozen;
    private Integer alwaysfrozen;
    private Integer hidden;
    private String units;
    private String link;

    public ParameterImpl() {
        // comply to java bean conventions, especially for json deserialization
    }

    public ParameterImpl(Element parElem) {
        setAlwaysfrozen(Integer.parseInt(parElem.getAttribute("alwaysfrozen")));
        setFrozen(Integer.parseInt(parElem.getAttribute("frozen")));
//        setHidden(Integer.parseInt(parElem.getAttribute("hidden")));
//        setLink(parElem.getAttribute("link"));
        setMax(Double.parseDouble(parElem.getAttribute("max")));
        setMin(Double.parseDouble(parElem.getAttribute("min")));
        setVal(Double.parseDouble(parElem.getAttribute("val")));
//        setUnits(parElem.getAttribute("units"));
        setName(parElem.getAttribute("name"));
    }

    public ParameterImpl(Parameter par) {
        try {
            BeanUtils.copyProperties(this, par);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Cannot copy parameter", e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Double getVal() {
        return val;
    }

    @Override
    public void setVal(Double value) {
        this.val = value;
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    public void setMin(Double min) {
        this.min = min;
    }

    @Override
    public Double getMax() {
        return max;
    }

    @Override
    public void setMax(Double max) {
        this.max = max;
    }

    @Override
    public Integer getFrozen() {
        return frozen;
    }

    @Override
    public void setFrozen(Integer frozen) {
        this.frozen = frozen;
    }

    @Override
    public Integer getHidden() {
        return hidden;
    }

    @Override
    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }

    @Override
    public Integer getAlwaysfrozen() {
        return alwaysfrozen;
    }

    @Override
    public void setAlwaysfrozen(Integer alwaysfrozen) {
        this.alwaysfrozen = alwaysfrozen;
    }

    @Override
    public String getUnits() {
        return units;
    }

    @Override
    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return name;
    }
}
