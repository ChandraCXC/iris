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

    private Double smoothBinsize = 20.0;

    public static final String PROP_SMOOTHBINSIZE = "smoothBinsize";

    /**
     * Get the value of smoothBinsize
     *
     * @return the value of smoothBinsize
     */
    public Double getSmoothBinsize() {
	return smoothBinsize;
    }

    /**
     * Set the value of smoothBinsize
     *
     * @param smoothBinsize new value of smoothBinsize
     */
    public void setSmoothBinsize(Double smoothBinsize) {
	Double oldSmoothBinsize = this.smoothBinsize;
	this.smoothBinsize = smoothBinsize;
	propertyChangeSupport.firePropertyChange(PROP_SMOOTHBINSIZE, oldSmoothBinsize, smoothBinsize);
    }

    private boolean logBin = false;

    public static final String PROP_LOGBIN = "logBin";

    /**
     * Get the value of logBin
     *
     * @return the value of logBin
     */
    public boolean isLogBin() {
	return logBin;
    }

    /**
     * Set the value of logBin
     *
     * @param logBin new value of logBin
     */
    public void setLogBin(boolean logBin) {
	boolean oldLogBin = this.logBin;
	this.logBin = logBin;
	propertyChangeSupport.firePropertyChange(PROP_LOGBIN, oldLogBin, logBin);
    }

}
