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
import cfa.vo.sed.builder.dm.ExtendedTarget;
import cfa.vo.sed.builder.dm.Target;
import cfa.vo.sed.setup.validation.Validation;
import cfa.vo.sed.filters.IFilter;

/**
 *
 * @author olaurino
 */
public class ExtendedTargetBuilder extends AbstractValidable implements Builder<ExtendedTarget> {

    private ISegmentColumn nameColumn;
    public static final String PROP_NAMECOLUMN = "nameColumn";

    /**
     * Get the value of nameColumn
     *
     * @return the value of nameColumn
     */
    public ISegmentColumn getNameColumn() {
        return nameColumn;
    }

    /**
     * Set the value of nameColumn
     *
     * @param nameColumn new value of nameColumn
     */
    public void setNameColumn(ISegmentColumn nameColumn) {
        ISegmentColumn oldNameColumn = this.nameColumn;
        this.nameColumn = nameColumn;
        propertyChangeSupport.firePropertyChange(PROP_NAMECOLUMN, oldNameColumn, nameColumn);
    }

    private ISegmentColumn raColumn;
    public static final String PROP_RACOLUMN = "raColumn";

    /**
     * Get the value of raColumn
     *
     * @return the value of raColumn
     */
    public ISegmentColumn getRaColumn() {
        return raColumn;
    }

    /**
     * Set the value of raColumn
     *
     * @param raColumn new value of raColumn
     */
    public void setRaColumn(ISegmentColumn raColumn) {
        ISegmentColumn oldRaColumn = this.raColumn;
        this.raColumn = raColumn;
        propertyChangeSupport.firePropertyChange(PROP_RACOLUMN, oldRaColumn, raColumn);
    }

    private ISegmentColumn decColumn;
    public static final String PROP_DECCOLUMN = "decColumn";

    /**
     * Get the value of decColumn
     *
     * @return the value of decColumn
     */
    public ISegmentColumn getDecColumn() {
        return decColumn;
    }

    /**
     * Set the value of decColumn
     *
     * @param decColumn new value of decColumn
     */
    public void setDecColumn(ISegmentColumn decColumn) {
        ISegmentColumn oldDecColumn = this.decColumn;
        this.decColumn = decColumn;
        propertyChangeSupport.firePropertyChange(PROP_DECCOLUMN, oldDecColumn, decColumn);
    }

    private String publisher = "UNKNOWN";
    public static final String PROP_PUBLISHER = "publisher";

    /**
     * Get the value of publisher
     *
     * @return the value of publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Set the value of publisher
     *
     * @param publisher new value of publisher
     */
    public void setPublisher(String publisher) {
        String oldPublisher = this.publisher;
        this.publisher = publisher;
        propertyChangeSupport.firePropertyChange(PROP_PUBLISHER, oldPublisher, publisher);
    }

    private Validation validation = new Validation();

    @Override
    public Validation validate() {
        validation.reset();

        if(nameColumn==null)
            validation.addWarning("No Name Column selected");

        if(raColumn==null)
            validation.addWarning("No Ra Column selected");

        if(raColumn!=null && !Number.class.isAssignableFrom(raColumn.getContentClass()))
            validation.addError("Ra Column doesn't contain numbers");

        if(decColumn==null)
            validation.addWarning("No Dec Column selected");

        if(decColumn!=null && !Number.class.isAssignableFrom(decColumn.getContentClass()))
            validation.addError("Dec Column doesn't contain numbers");

        return validation;
    }

    @Override
    public ExtendedTarget build(IFilter filter, int row) throws Exception {
        Target t = new Target();

        if(nameColumn!=null) {
            String name = filter.getColumnData(0, nameColumn.getNumber())[row].toString();
            t.setName(name);
        }

        if(raColumn!=null) {
            Double ra = ((Number) filter.getData(0, raColumn.getNumber())[row]).doubleValue();
            t.setRa(ra);
        }

        if(decColumn!=null) {
            Double dec = ((Number) filter.getData(0, decColumn.getNumber())[row]).doubleValue();
            t.setDec(dec);
        }

        return new ExtendedTarget(t, publisher);

    }

}
