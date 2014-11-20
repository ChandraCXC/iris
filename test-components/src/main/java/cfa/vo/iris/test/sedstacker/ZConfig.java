/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author jbudynk
 */
public class ZConfig {
    
    private Double newz;

    public static final String PROP_NEWZ = "newz";

    /**
     * Get the value of newz
     *
     * @return the value of newz
     */
    public Double getNewz() {
	return newz;
    }

    /**
     * Set the value of newz
     *
     * @param newz new value of newz
     */
    public void setNewz(Double newz) {
	Double oldNewz = this.newz;
	this.newz = newz;
	propertyChangeSupport.firePropertyChange(PROP_NEWZ, oldNewz, newz);
    }

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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

    private boolean correctFlux = true;

    public static final String PROP_CORRECTFLUX = "correctFlux";

    /**
     * Get the value of correctFlux
     *
     * @return the value of correctFlux
     */
    public boolean isCorrectFlux() {
	return correctFlux;
    }

    /**
     * Set the value of correctFlux
     *
     * @param correctFlux new value of correctFlux
     */
    public void setCorrectFlux(boolean correctFlux) {
	boolean oldCorrectFlux = this.correctFlux;
	this.correctFlux = correctFlux;
	propertyChangeSupport.firePropertyChange(PROP_CORRECTFLUX, oldCorrectFlux, correctFlux);
    }

}
