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
import cfa.vo.sed.builder.dm.SpectralAxis;
import cfa.vo.sed.setup.validation.Validation;
import cfa.vo.sed.builder.photfilters.PhotometryFilter;
import cfa.vo.sed.filters.IFilter;
import cfa.vo.sed.quantities.IUnit;
import cfa.vo.sed.quantities.XQuantity;

/**
 *
 * @author olaurino
 */
public class SpectralAxisBuilder extends AbstractValidable implements Builder<SpectralAxis> {

    private SpectralAxis axis;

    public void setAxis(SpectralAxis axis) {
        this.axis = axis;
    }

    private XQuantity quantity;
    public static final String PROP_QUANTITY = "quantity";

    /**
     * Get the value of quantity
     *
     * @return the value of quantity
     */
    public XQuantity getQuantity() {
        return quantity;
    }

    /**
     * Set the value of quantity
     *
     * @param quantity new value of quantity
     */
    public void setQuantity(XQuantity quantity) {
        XQuantity oldQuantity = this.quantity;
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

    private String mode;
    public static final String PROP_MODE = "mode";

    /**
     * Get the value of mode
     *
     * @return the value of mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Set the value of mode
     *
     * @param mode new value of mode
     */
    public void setMode(String mode) {
        String oldMode = this.mode;
        this.mode = mode;
        propertyChangeSupport.firePropertyChange(PROP_MODE, oldMode, mode);
    }

    private Double binmin;
    public static final String PROP_BINMIN = "binmin";

    /**
     * Get the value of binmin
     *
     * @return the value of binmin
     */
    public Double getBinmin() {
        return binmin;
    }

    /**
     * Set the value of binmin
     *
     * @param binmin new value of binmin
     */
    public void setBinmin(Double binmin) {
        Double oldBinmin = this.binmin;
        this.binmin = binmin;
        propertyChangeSupport.firePropertyChange(PROP_BINMIN, oldBinmin, binmin);
    }

    private Double binmax;
    public static final String PROP_BINMAX = "binmax";

    /**
     * Get the value of binmax
     *
     * @return the value of binmax
     */
    public Double getBinmax() {
        return binmax;
    }

    /**
     * Set the value of binmax
     *
     * @param binmax new value of binmax
     */
    public void setBinmax(Double binmax) {
        Double oldBinmax = this.binmax;
        this.binmax = binmax;
        propertyChangeSupport.firePropertyChange(PROP_BINMAX, oldBinmax, binmax);
    }

    private PhotometryFilter filter_;
    public static final String PROP_FILTER = "filter";

    /**
     * Get the value of filter
     *
     * @return the value of filter
     */
    public PhotometryFilter getFilter() {
        return filter_;
    }

    /**
     * Set the value of filter
     *
     * @param filter new value of filter
     */
    public void setFilter(PhotometryFilter filter) {
        PhotometryFilter oldFilter = this.filter_;
        this.filter_ = filter;
        propertyChangeSupport.firePropertyChange(PROP_FILTER, oldFilter, filter);
    }

    @Override
    public SpectralAxis build(IFilter filter, int row) throws Exception {

        axis.setMode(mode);
        axis.setBinmin(binmin);
        axis.setBinmax(binmax);
        axis.setFilter(filter_);
        axis.setQuantity(quantity);
        axis.setUnit(unit);
        Double value = null;
        if(mode.equals("Single Value")) {
            Number[] data = filter.getData(0, valueColumn.getNumber());
            if(data!=null) {
                Number v = data[valueColumn.getNumber()];
                if(v!=null) {
                    value = v.doubleValue();
                    axis.setValue(value);
                }
            }
        }
        
        return axis;
    }

    @Override
    public Validation validate() {
        Validation v = new Validation();

        if(mode == null || !mode.matches("Single Value|Energy Bin|Photometry Filter"))
            v.addError("Missing X Axis Type. Please select a Type");

        if(mode!=null) {
            if(mode.equals("Single Value") && (getValueColumn()==null)) {
                v.addError("Missing X Axis Value Column");
            }

            if(mode.equals("Energy Bin") && (binmin==null || Double.isNaN(binmin))) {
                v.addError("Missing/Invalid X Axis Bin Min");
            }

            if(mode.equals("Energy Bin") && (binmax==null || Double.isNaN(binmax))) {
                v.addError("Missing/Invalid X Axis Bin Max");
            }

            if(mode.equals("Photometry Filter") && getFilter()==null) {
                v.addError("No Photometry Filter selected");
            }

        }

        if(getQuantity()==null && (mode!=null && !mode.equals("Photometry Filter")))
            v.addError("Missing X Axis Quantity");

        if(valueColumn!=null && !Number.class.isAssignableFrom(valueColumn.getContentClass()))
            v.addError("X Axis Column doesn't contain numbers");

        return v;
    }

}
