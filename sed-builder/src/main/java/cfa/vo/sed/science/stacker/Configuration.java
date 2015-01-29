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
public class Configuration {
    
    private RedshiftConfiguration redshiftConfiguration = new RedshiftConfiguration();

    public static final String PROP_REDSHIFTCONFIGURATION = "redshiftConfiguration";

    /**
     * Get the value of redshiftConfiguration
     *
     * @return the value of redshiftConfiguration
     */
    public RedshiftConfiguration getRedshiftConfiguration() {
        return redshiftConfiguration;
    }

    /**
     * Set the value of redshiftConfiguration
     *
     * @param redshiftConfiguration new value of redshiftConfiguration
     */
    public void setRedshiftConfiguration(RedshiftConfiguration redshiftConfiguration) {
        RedshiftConfiguration oldRedshiftConfiguration = this.redshiftConfiguration;
        this.redshiftConfiguration = redshiftConfiguration;
        propertyChangeSupport.firePropertyChange(PROP_REDSHIFTCONFIGURATION, oldRedshiftConfiguration, redshiftConfiguration);
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
    
    private NormalizationConfiguration normConfiguration = new NormalizationConfiguration();

    public static final String PROP_NORMCONFIGURATION = "normConfiguration";

    /**
     * Get the value of normConfiguration
     *
     * @return the value of normConfiguration
     */
    public NormalizationConfiguration getNormConfiguration() {
	return normConfiguration;
    }

    /**
     * Set the value of normConfiguration
     *
     * @param normConfiguration new value of normConfiguration
     */
    public void setNormConfiguration(NormalizationConfiguration normConfiguration) {
	NormalizationConfiguration oldNormConfiguration = this.normConfiguration;
	this.normConfiguration = normConfiguration;
	propertyChangeSupport.firePropertyChange(PROP_NORMCONFIGURATION, oldNormConfiguration, normConfiguration);
    }

    private StackConfiguration stackConfiguration = new StackConfiguration();

    public static final String PROP_STACKCONFIGURATION = "stackConfiguration";

    /**
     * Get the value of stackConfiguration
     *
     * @return the value of stackConfiguration
     */
    public StackConfiguration getStackConfiguration() {
	return stackConfiguration;
    }

    /**
     * Set the value of stackConfiguration
     *
     * @param stackConfiguration new value of stackConfiguration
     */
    public void setStackConfiguration(StackConfiguration stackConfiguration) {
	StackConfiguration oldStackConfiguration = this.stackConfiguration;
	this.stackConfiguration = stackConfiguration;
	propertyChangeSupport.firePropertyChange(PROP_STACKCONFIGURATION, oldStackConfiguration, stackConfiguration);
    }

    
}
