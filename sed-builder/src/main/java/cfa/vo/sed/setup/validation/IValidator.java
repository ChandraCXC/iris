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

import cfa.vo.sed.setup.SetupBean;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author olaurino
 */
public interface IValidator {

    public boolean isConfigurationValid();

    public String getMessage();

    public ValidationStatus getValidationStatus();

    public String[] getRegisteredProperties();

    public void setConf(SetupBean conf);

    public void validateConfiguration();

    public void addPropertyChangeListeners(PropertyChangeListener listener);

    public void addPropertyHandle(String property, Object handle, Class clazz);

    public void propertyChange(PropertyChangeEvent pce);
}
