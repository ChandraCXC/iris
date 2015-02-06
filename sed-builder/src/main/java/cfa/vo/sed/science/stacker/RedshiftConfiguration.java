/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 67 * hash + (this.toRedshift != null ? this.toRedshift.hashCode() : 0);
	hash = 67 * hash + (this.correctFlux ? 1 : 0);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final RedshiftConfiguration other = (RedshiftConfiguration) obj;
	if (this.toRedshift != other.toRedshift && (this.toRedshift == null || !this.toRedshift.equals(other.toRedshift))) {
	    return false;
	}
	if (this.correctFlux != other.correctFlux) {
	    return false;
	}
	return true;
    }
    
    
}
