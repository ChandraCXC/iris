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

package cfa.vo.sed.setup;

import cfa.vo.sed.builder.ISegmentColumn;
import cfa.vo.sed.setup.validation.AbstractValidable;
import cfa.vo.sed.builder.dm.FluxAxis;
import cfa.vo.sed.setup.validation.Validation;
import cfa.vo.sed.filters.IFilter;
import cfa.vo.sed.quantities.IUnit;
import cfa.vo.sed.quantities.SPVYQuantity;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class FluxAxisBuilder extends AbstractValidable implements Builder<FluxAxis>, Cloneable {

    private FluxAxis axis;

    public void setAxis(FluxAxis axis) {
        this.axis = axis;
    }
    
    @Override
    public Object clone() {
        try {
            FluxAxisBuilder cloned = (FluxAxisBuilder) super.clone();
            return cloned;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(SpectralAxisBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private SPVYQuantity quantity;
    public static final String PROP_QUANTITY = "quantity";

    /**
     * Get the value of quantity
     *
     * @return the value of quantity
     */
    public SPVYQuantity getQuantity() {
        return quantity;
    }

    /**
     * Set the value of quantity
     *
     * @param quantity new value of quantity
     */
    public void setQuantity(SPVYQuantity quantity) {
        SPVYQuantity oldQuantity = this.quantity;
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
    public IUnit getUnit() {
        return unit;
    }

    /**
     * Set the value of unit
     *
     * @param unit new value of unit
     */
    public void setUnit(IUnit unit) {
        IUnit oldUnit = this.unit;
        this.unit = unit;
        propertyChangeSupport.firePropertyChange(PROP_UNIT, oldUnit, unit);
    }

    private ISegmentColumn valueColumn;
    public static final String PROP_VALUECOLUMN = "valueColumn";

    /**
     * Get the value of valueColumn
     *
     * @return the value of valueColumn
     */
    public ISegmentColumn getValueColumn() {
        return valueColumn;
    }

    /**
     * Set the value of valueColumn
     *
     * @param valueColumn new value of valueColumn
     */
    public void setValueColumn(ISegmentColumn valueColumn) {
        ISegmentColumn oldValueColumn = this.valueColumn;
        this.valueColumn = valueColumn;
        propertyChangeSupport.firePropertyChange(PROP_VALUECOLUMN, oldValueColumn, valueColumn);
    }

    private ISegmentColumn errorColumn;
    public static final String PROP_ERRORCOLUMN = "errorColumn";

    /**
     * Get the value of errorColumn
     *
     * @return the value of errorColumn
     */
    public ISegmentColumn getErrorColumn() {
        return errorColumn;
    }

    /**
     * Set the value of errorColumn
     *
     * @param errorColumn new value of errorColumn
     */
    public void setErrorColumn(ISegmentColumn errorColumn) {
        ISegmentColumn oldErrorColumn = this.errorColumn;
        this.errorColumn = errorColumn;
        propertyChangeSupport.firePropertyChange(PROP_ERRORCOLUMN, oldErrorColumn, errorColumn);
    }

    @Override
    public FluxAxis build(IFilter filter, int row) throws Exception {
        axis.setQuantity(quantity);
        axis.setUnit(unit);

        Number val = filter.getData(0, valueColumn.getNumber())[row];
        if(val!=null)
            axis.setValue(val.doubleValue());

        if(errorColumn!=null) {
            Number errVal = filter.getData(0, errorColumn.getNumber())[row];
            if(errVal!=null)
                axis.setError(errVal.doubleValue());
        }
            

        return axis;
    }

    @Override
    public Validation validate() {
        Validation v = new Validation();

        if(getValueColumn()==null)
            v.addError("Missing Y Axis Value Column");

        if(getValueColumn()!=null && !Number.class.isAssignableFrom(getValueColumn().getContentClass()))
            v.addError("Y Axis Value Column doesn't contain numbers");

        if(getErrorColumn()!=null && !Number.class.isAssignableFrom(getErrorColumn().getContentClass()))
            v.addError("Y Axis Error Column doesn't contain numbers");

        if(getQuantity()==null)
            v.addError("Missing Y Axis Quantity");

        return v;
    }

}
