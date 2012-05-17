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

package cfa.vo.sed.setup.validation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author olaurino
 */
public abstract class AbstractValidable implements Validable {

    private Validator validator;
    public static final String PROP_VALIDATOR = "validator";

    /**
     * Get the value of validator
     *
     * @return the value of validator
     */
    @Override
    public Validator getValidator() {
        return validator;
    }

    /**
     * Set the value of validator
     *
     * @param validator new value of validator
     */
    public void setValidator(Validator validator) {
        Validator oldValidator = this.validator;
        this.validator = validator;
        propertyChangeSupport.firePropertyChange(PROP_VALIDATOR, oldValidator, validator);
    }


    public AbstractValidable() {
        validator = new Validator();
        validator.add(this);
    }


    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public abstract Validation validate();

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChangeSupport.getPropertyChangeListeners();
    }

}
