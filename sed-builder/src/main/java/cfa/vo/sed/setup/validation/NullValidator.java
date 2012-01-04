/**
 * Copyright (C) Smithsonian Astrophysical Observatory
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

import cfa.vo.sed.setup.SetupBean;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author olaurino
 */
public class NullValidator implements IValidator {

    private ValidationStatus status = new ValidationStatus(true, "");

    @Override
    public ValidationStatus getValidationStatus() {
        return status;
    }

    public String[] getRegisteredProperties() {
        return new String[]{};
    }

    @Override
    public void validateConfiguration() {
        
    }

    public void addPropertyChangeListeners(PropertyChangeListener listener) {

    }

    public void addPropertyHandle(String property, Object handle, Class clazz) {

    }

    public void propertyChange(PropertyChangeEvent pce) {
        
    }

    public void setConf(SetupBean conf) {
        
    }

    public boolean isConfigurationValid() {
        return true;
    }

    public String getMessage() {
        return "";
    }


}
