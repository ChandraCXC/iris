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
public class StackConfig {
    
    private String statistic = "Average";

    public static final String PROP_STATISTIC = "statistic";

    /**
     * Get the value of statistic
     *
     * @return the value of statistic
     */
    public String getStatistic() {
	return statistic;
    }

    /**
     * Set the value of statistic
     *
     * @param statistic new value of statistic
     */
    public void setStatistic(String statistic) {
	String oldStatistic = this.statistic;
	this.statistic = statistic;
	propertyChangeSupport.firePropertyChange(PROP_STATISTIC, oldStatistic, statistic);
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

    private Double binSize = 1.0;

    public static final String PROP_BINSIZE = "binSize";

    /**
     * Get the value of binSize
     *
     * @return the value of binSize
     */
    public Double getBinSize() {
	return binSize;
    }

    /**
     * Set the value of binSize
     *
     * @param binSize new value of binSize
     */
    public void setBinSize(Double binSize) {
	Double oldBinSize = this.binSize;
	this.binSize = binSize;
	propertyChangeSupport.firePropertyChange(PROP_BINSIZE, oldBinSize, binSize);
    }

    private String binUnits = "Angstrom";

    public static final String PROP_BINUNITS = "binUnits";

    /**
     * Get the value of binUnits
     *
     * @return the value of binUnits
     */
    public String getBinUnits() {
	return binUnits;
    }

    /**
     * Set the value of binUnits
     *
     * @param binUnits new value of binUnits
     */
    public void setBinUnits(String binUnits) {
	String oldBinUnits = this.binUnits;
	this.binUnits = binUnits;
	propertyChangeSupport.firePropertyChange(PROP_BINUNITS, oldBinUnits, binUnits);
    }

    private boolean smooth;

    public static final String PROP_SMOOTH = "smooth";

    /**
     * Get the value of smooth
     *
     * @return the value of smooth
     */
    public boolean isSmooth() {
	return smooth;
    }

    /**
     * Set the value of smooth
     *
     * @param smooth new value of smooth
     */
    public void setSmooth(boolean smooth) {
	boolean oldSmooth = this.smooth;
	this.smooth = smooth;
	propertyChangeSupport.firePropertyChange(PROP_SMOOTH, oldSmooth, smooth);
    }

    private int smoothBoxSize = 20;

    public static final String PROP_SMOOTHBOXSIZE = "smoothBoxSize";

    /**
     * Get the value of smoothBoxSize
     *
     * @return the value of smoothBoxSize
     */
    public int getSmoothBoxSize() {
	return smoothBoxSize;
    }

    /**
     * Set the value of smoothBoxSize
     *
     * @param smoothBoxSize new value of smoothBoxSize
     */
    public void setSmoothBoxSize(int smoothBoxSize) {
	int oldSmoothBoxSize = this.smoothBoxSize;
	this.smoothBoxSize = smoothBoxSize;
	propertyChangeSupport.firePropertyChange(PROP_SMOOTHBOXSIZE, oldSmoothBoxSize, smoothBoxSize);
    }

    private boolean logBinning = false;

    public static final String PROP_LOGBINNING = "logBinning";

    /**
     * Get the value of logBinning
     *
     * @return the value of logBinning
     */
    public boolean isLogBinning() {
	return logBinning;
    }

    /**
     * Set the value of logBinning
     *
     * @param logBinning new value of logBinning
     */
    public void setLogBinning(boolean logBinning) {
	boolean oldLogBinning = this.logBinning;
	this.logBinning = logBinning;
	propertyChangeSupport.firePropertyChange(PROP_LOGBINNING, oldLogBinning, logBinning);
    }

}
