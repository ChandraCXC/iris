/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.builder.photfilters;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class IQConfig {

    private List<PassBand> passbands = new ArrayList();
    public static final String PROP_PASSBANDS = "passbands";

    /**
     * Get the value of passbands
     *
     * @return the value of passbands
     */
    public List<PassBand> getPassbands() {
        return passbands;
    }

    /**
     * Set the value of passbands
     *
     * @param passbands new value of passbands
     */
    public void setPassbands(List<PassBand> passbands) {
        List<PassBand> oldPassbands = this.passbands;
        this.passbands = passbands;
        propertyChangeSupport.firePropertyChange(PROP_PASSBANDS, oldPassbands, passbands);
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

}
