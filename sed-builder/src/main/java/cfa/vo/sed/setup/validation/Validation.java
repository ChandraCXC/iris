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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class Validation {
    private List<String> warnings = new ArrayList();
    private List<String> errors = new ArrayList();


    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void reset() {
        warnings = new ArrayList();
        errors = new ArrayList();

    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    private boolean valid;
    public static final String PROP_VALID = "valid";

    /**
     * Get the value of valid
     *
     * @return the value of valid
     */
    public boolean isValid() {
        update();
        return valid;
    }

    /**
     * Set the value of valid
     *
     * @param valid new value of valid
     */
    public void setValid(boolean valid) {
        boolean oldValid = this.valid;
        this.valid = valid;
        propertyChangeSupport.firePropertyChange(PROP_VALID, oldValid, valid);
    }


    private String string;
    public static final String PROP_STRING = "string";

    /**
     * Get the value of string
     *
     * @return the value of string
     */
    public String getString() {
        update();
        return string;
    }

    /**
     * Set the value of string
     *
     * @param string new value of string
     */
    public void setString(String string) {
        String oldString = this.string;
        this.string = string;
        propertyChangeSupport.firePropertyChange(PROP_STRING, oldString, string);
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


    public void update() {
        StringBuilder sb = new StringBuilder();
        for(String s : errors) {
            sb.append("Error: ").append(s).append("\n");
        }
        for(String s : warnings) {
            sb.append("Warning: ").append(s).append("\n");
        }

        setString(sb.toString());
        setValid(errors.isEmpty());
    }

    @Override
    public String toString() {
        update();
        return string;
    }
}
