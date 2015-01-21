/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.science.stacker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author olaurino
 */
public final class RedshiftConfiguration {
    
    private Double toRedshift;

    public static final String PROP_TOREDSHIFT = "toRedshift";

    public RedshiftConfiguration() {
        setToRedshift(0.0);
	setCorrectFlux(true);
    }
    
    /**
     * Get the value of toRedshift
     *
     * @return the value of toRedshift
     */
    public Double getToRedshift() {
        return toRedshift;
    }

    /**
     * Set the value of toRedshift
     *
     * @param toRedshift new value of toRedshift
     */
    public void setToRedshift(Double toRedshift) {
        Double oldToRedshift = this.toRedshift;
        this.toRedshift = toRedshift;
        propertyChangeSupport.firePropertyChange(PROP_TOREDSHIFT, oldToRedshift, toRedshift);
    }
    
    private boolean correctFlux;

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
    
}
