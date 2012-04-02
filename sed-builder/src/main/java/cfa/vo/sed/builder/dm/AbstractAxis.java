/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sed.quantities.IQuantity;
import cfa.vo.sed.quantities.IUnit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author olaurino
 */
public abstract class AbstractAxis<QuantityClass extends IQuantity> implements Axis<QuantityClass> {

//    private QuantityClass quantity;
//    private IUnit unit;
//    private Double value;

    private QuantityClass quantity;
    public static final String PROP_QUANTITY = "quantity";

    /**
     * Get the value of quantity
     *
     * @return the value of quantity
     */
    @Override
    public QuantityClass getQuantity() {
        return quantity;
    }

    /**
     * Set the value of quantity
     *
     * @param quantity new value of quantity
     */
    @Override
    public void setQuantity(QuantityClass quantity) {
        QuantityClass oldQuantity = this.quantity;
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
    @Override
    public IUnit getUnit() {
        return unit;
    }

    /**
     * Set the value of unit
     *
     * @param unit new value of unit
     */
    @Override
    public void setUnit(IUnit unit) {
        IUnit oldUnit = this.unit;
        this.unit = unit;
        propertyChangeSupport.firePropertyChange(PROP_UNIT, oldUnit, unit);
    }

    private Double value;
    public static final String PROP_VALUE = "value";

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    @Override
    public Double getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    @Override
    public void setValue(Double value) {
        Double oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
    }

    @Override
    public boolean isValid() {
        return validate().isValid();
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
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }


}
