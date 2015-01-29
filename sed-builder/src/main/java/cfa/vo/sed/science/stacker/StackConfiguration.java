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
public final class StackConfiguration {
    
    public StackConfiguration() {
	setStatistic("avg");
	setBinsize(0.5);
	setBinsizeUnit("Angstrom");
	setYUnits("erg/s/cm2/A");
	setLogbin(true);
	setSmooth(false);
	setSmoothBinsize(20.0);
    }
    
    private Double binsize;

    public static final String PROP_BINSIZE = "binsize";

    /**
     * Get the value of binsize
     *
     * @return the value of binsize
     */
    public Double getBinsize() {
	return binsize;
    }

    /**
     * Set the value of binsize
     *
     * @param binsize new value of binsize
     */
    public void setBinsize(Double binsize) {
	Double oldBinsize = this.binsize;
	this.binsize = binsize;
	propertyChangeSupport.firePropertyChange(PROP_BINSIZE, oldBinsize, binsize);
    }
    
    private String statistic;

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

    private boolean logbin;

    public static final String PROP_LOGBIN = "logbin";

    /**
     * Get the value of logbin
     *
     * @return the value of logbin
     */
    public boolean isLogbin() {
	return logbin;
    }

    /**
     * Set the value of logbin
     *
     * @param logbin new value of logbin
     */
    public void setLogbin(boolean logbin) {
	boolean oldLogbin = this.logbin;
	this.logbin = logbin;
	propertyChangeSupport.firePropertyChange(PROP_LOGBIN, oldLogbin, logbin);
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

    private Double smoothBinsize;

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

    private String binsizeUnit;

    public static final String PROP_BINSIZEUNIT = "binsizeUnit";

    /**
     * Get the value of binsizeUnit
     *
     * @return the value of binsizeUnit
     */
    public String getBinsizeUnit() {
	return binsizeUnit;
    }

    /**
     * Set the value of binsizeUnit
     *
     * @param binsizeUnit new value of binsizeUnit
     */
    public void setBinsizeUnit(String binsizeUnit) {
	String oldBinsizeUnit = this.binsizeUnit;
	this.binsizeUnit = binsizeUnit;
	propertyChangeSupport.firePropertyChange(PROP_BINSIZEUNIT, oldBinsizeUnit, binsizeUnit);
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
    
    private String yUnits;

    public static final String PROP_YUNITS = "yUnits";

    /**
     * Get the value of yUnits
     *
     * @return the value of yUnits
     */
    public String getYUnits() {
	return yUnits;
    }

    /**
     * Set the value of yUnits
     *
     * @param yUnits new value of yUnits
     */
    public void setYUnits(String yUnits) {
	String oldYUnits = this.yUnits;
	this.yUnits = yUnits;
	propertyChangeSupport.firePropertyChange(PROP_YUNITS, oldYUnits, yUnits);
    }


}
