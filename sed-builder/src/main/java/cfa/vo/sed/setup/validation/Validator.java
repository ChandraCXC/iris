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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 *
 * @author olaurino
 */
public class Validator implements PropertyChangeListener, ObservableListListener {

    private List<Validable> validables = new ArrayList();
    private Validation validation = new Validation();
    public static final String PROP_VALIDATION = "validation";

    /**
     * Get the value of validation
     *
     * @return the value of validation
     */
    public Validation getValidation() {
        validate();
        return validation;
    }

    /**
     * Set the value of validation
     *
     * @param validation new value of validation
     */
    public void setValidation(Validation validation) {
        Validation oldValidation = this.validation;
        this.validation = validation;
        propertyChangeSupport.firePropertyChange(PROP_VALIDATION, oldValidation, validation);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void add(Validable validable) {
        validables.add(validable);
        validable.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        validate();
    }

    public Validation validate() {
        validation.reset();

        for (Validable validable : validables) {
            Validation valid = validable.validate();
            validation.getWarnings().addAll(valid.getWarnings());
            validation.getErrors().addAll(valid.getErrors());
        }

        validation.update();

        return validation;
    }

    @Override
    public void listElementsAdded(ObservableList ol, int i, int i1) {
        validate();
    }

    @Override
    public void listElementsRemoved(ObservableList ol, int i, List list) {
        validate();
    }

    @Override
    public void listElementReplaced(ObservableList ol, int i, Object o) {
        validate();
    }

    @Override
    public void listElementPropertyChanged(ObservableList ol, int i) {
        validate();
    }
}
