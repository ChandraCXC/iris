/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.builder.science;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author olaurino
 */
public class ZConfig {

    private Double redshift = 0.0;
    public static final String PROP_REDSHIFT = "redshift";

    /**
     * Get the value of redshift
     *
     * @return the value of redshift
     */
    public Double getRedshift() {
        return redshift;
    }

    /**
     * Set the value of redshift
     *
     * @param redshift new value of redshift
     */
    public void setRedshift(Double redshift) {
        Double oldRedshift = this.redshift;
        this.redshift = redshift;
        propertyChangeSupport.firePropertyChange(PROP_REDSHIFT, oldRedshift, redshift);
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
    
    @Override
    public String toString() {
        return "Redshifted from Redshift: "+redshift;
    }

}
