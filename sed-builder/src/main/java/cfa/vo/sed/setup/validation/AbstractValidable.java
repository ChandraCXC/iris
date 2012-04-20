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
