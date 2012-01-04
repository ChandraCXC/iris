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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public abstract class AbstractValidator implements IValidator, PropertyChangeListener {

    private static String newline = System.getProperty("line.separator");

    private List<PropertyHandle> propHandleList = new ArrayList();

    private ValidationStatus status = new ValidationStatus();

    private List<String> messages = new ArrayList();

    private IValidator validator;

    private SetupBean conf;

    public ValidationStatus getValidationStatus() {
        return status;
    }

    public boolean isConfigurationValid() {
        return status.isValid();
    }

    public String getMessage() {
        return status.getMessage();
    }

    public AbstractValidator(IValidator validator, boolean register, SetupBean conf) {
        if(validator!=null)
            this.validator = validator;
        else
            this.validator = new NullValidator();
        if(register && conf!=null) {
            setConf(conf);
            addPropertyChangeListeners(this);
        }

    }

    protected SetupBean getConf() {
        return conf;
    }

    public final void setConf(SetupBean conf) {
        this.conf = conf;
        validator.setConf(conf);
    }

    protected String buildMessage() {

        StringBuilder msg = new StringBuilder();

        for(String m : messages) {
            msg.append(m).append(newline);
        }

        return msg.toString();
    }

    public abstract void validateConfiguration();

    public abstract String[] getRegisteredProperties();

    protected final void assertBoolean(String msg, boolean expression) {

        boolean valid = status.isValid();

        try {
            
            valid &= expression;

            if(!expression) {
                messages.add(msg);
            }
        } catch (Exception ex) {
            messages.add(msg);
        }

        status.update(valid, buildMessage());
    }

    public final void addPropertyChangeListeners(PropertyChangeListener listener) {
        for(String property : getRegisteredProperties()) {
            conf.addPropertyChangeListener(property, listener);
        }
        validator.addPropertyChangeListeners(listener);
    }

    public final void propertyChange(PropertyChangeEvent pce) {

        messages.clear();
        status.update(true, "");

        validateConfiguration();
        validator.propertyChange(pce);

        boolean valid = status.isValid() && validator.getValidationStatus().isValid();
        String msg = status.getMessage() +  validator.getValidationStatus().getMessage();

        status.update(valid, msg);
        
        
        for(PropertyHandle handle : propHandleList) {
           handle.firePropertyChange();
        }
    }

    public final void addPropertyHandle(String property, Object handle, Class clazz) {
        propHandleList.add(new PropertyHandle(property, handle, this, clazz));
    }

    private class PropertyHandle {
        private Object handle;
        private String property;
        private IValidator handler;
        private Class clazz;

        public PropertyHandle(String property, Object handle, IValidator handler, Class clazz) {
            this.handle = handle;
            this.property = property.replaceFirst(property.substring(0, 1), property.substring(0,1).toUpperCase());
            this.handler = handler;
            this.clazz = clazz;
        }

        public Object getHandle() {
            return handle;
        }

        public String getProperty() {
            return property;
        }

        public void firePropertyChange() {
            try {
                Method m = handle.getClass().getMethod("set" + property, clazz);
                String pref = clazz.equals(boolean.class) ? "is" : "get";
                Method m1 = handler.getClass().getMethod(pref+property);
                Object res = m1.invoke(handler);
                m.invoke(handle, res);
            } catch (Exception ex) {
                Logger.getLogger(AbstractValidator.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }

}
