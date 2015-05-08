/**
 * Copyright (C) 2013, 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.sed.science.interpolation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author olaurino
 */
public class ZConfig {

    private Double newz = 0.0;
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
