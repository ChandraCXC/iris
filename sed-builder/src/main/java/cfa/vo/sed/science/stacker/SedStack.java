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
public class SedStack {
    private String name;
    
    // FIXME Need to check for uniqueness
    public SedStack(String name) {
        this.name = name;
        this.setConf(new Configuration());
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    private Configuration conf;

    public static final String PROP_CONF = "conf";

    /**
     * Get the value of conf
     *
     * @return the value of conf
     */
    public Configuration getConf() {
        return conf;
    }

    /**
     * Set the value of conf
     *
     * @param conf new value of conf
     */
    public final void setConf(Configuration conf) {
        Configuration oldConf = this.conf;
        this.conf = conf;
        propertyChangeSupport.firePropertyChange(PROP_CONF, oldConf, conf);
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
