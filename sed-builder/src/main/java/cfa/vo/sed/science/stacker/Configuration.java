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
