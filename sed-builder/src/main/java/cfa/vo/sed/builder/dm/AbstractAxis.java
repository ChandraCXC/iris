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

import cfa.vo.sed.setup.validation.AbstractValidable;
import cfa.vo.sed.quantities.IQuantity;
import cfa.vo.sed.quantities.IUnit;

/**
 *
 * @author olaurino
 */
public abstract class AbstractAxis<QuantityClass extends IQuantity> extends AbstractValidable implements Axis<QuantityClass> {

//    private QuantityClass quantity;
//    private IUnit unit;
//    private Double value;

    private QuantityClass quantity;
    public static final String PROP_QUANTITY = "quantity";

    /**
     * Get the value of quantity
     *
     * @return the value of quantity
     */
    @Override
    public QuantityClass getQuantity() {
        return quantity;
    }

    /**
     * Set the value of quantity
     *
     * @param quantity new value of quantity
     */
    @Override
    public void setQuantity(QuantityClass quantity) {
        QuantityClass oldQuantity = this.quantity;
        this.quantity = quantity;
        propertyChangeSupport.firePropertyChange(PROP_QUANTITY, oldQuantity, quantity);
    }

    private IUnit unit;
    public static final String PROP_UNIT = "unit";

    /**
     * Get the value of unit
     *
     * @return the value of unit
     */
    @Override
    public IUnit getUnit() {
        return unit;
    }

    /**
     * Set the value of unit
     *
     * @param unit new value of unit
     */
    @Override
    public void setUnit(IUnit unit) {
        IUnit oldUnit = this.unit;
        this.unit = unit;
        propertyChangeSupport.firePropertyChange(PROP_UNIT, oldUnit, unit);
    }

    private Double value;
    public static final String PROP_VALUE = "value";

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    @Override
    public Double getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    @Override
    public void setValue(Double value) {
        Double oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
    }

    

}
