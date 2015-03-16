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
public final class NormalizationConfiguration {
    
// Default Normalization configuration:
//    public NormalizationConfiguration() {
//	setIntegrate(true);
//	setAtPoint(false);
//	setMultiply(true);
//	setAdd(false);
//	setStats("Value");
//	setXUnits("Angstrom");
//	setXmin(Double.NEGATIVE_INFINITY);
//	setXmax(Double.POSITIVE_INFINITY);
//	setYValue(1.0);
//	setAtPointYValue(1.0);
//	setAtPointYUnits("erg/s/cm2/A");
//	setAtPointXValue(5000.0);
//	setAtPointXUnits("Angstrom");
//	setAtPointStats("Value");
//	setIntegrateValueYUnits("erg/s/cm2");
//    }
    
    private boolean integrate = true;

    public static final String PROP_INTEGRATE = "integrate";

    /**
     * Get the value of integrate
     *
     * @return the value of integrate
     */
    public boolean isIntegrate() {
	return integrate;
    }

    /**
     * Set the value of integrate
     *
     * @param integrate new value of integrate
     */
    public void setIntegrate(boolean integrate) {
	boolean oldIntegrate = this.integrate;
	this.integrate = integrate;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATE, oldIntegrate, integrate);
	// disable Y value text box if not normalizing at a point and using "Value" for the statistic.
	if (this.integrate) {
	    setAtPointYTextEnabled(false);
	    if(this.stats.equals("Value")) {
		setIntegrateYTextEnabled(true);
	    }
	} else {
	    setIntegrateYTextEnabled(false);
	}
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

    private Double xmax = Double.POSITIVE_INFINITY;

    public static final String PROP_XMAX = "xmax";

    /**
     * Get the value of xmax
     *
     * @return the value of xmax
     */
    public Double getXmax() {
	return xmax;
    }

    /**
     * Set the value of xmax
     *
     * @param xmax new value of xmax
     */
    public void setXmax(Double xmax) {
	Double oldXMax = this.xmax;
	this.xmax = xmax;
	propertyChangeSupport.firePropertyChange(PROP_XMAX, oldXMax, xmax);
    }

    private Double xmin = Double.NEGATIVE_INFINITY;

    public static final String PROP_XMIN = "xmin";

    /**
     * Get the value of xmin
     *
     * @return the value of xmin
     */
    public Double getXmin() {
	return xmin;
    }

    /**
     * Set the value of xmin
     *
     * @param xmin new value of xmin
     */
    public void setXmin(Double xmin) {
	Double oldXMin = this.xmin;
	this.xmin = xmin;
	propertyChangeSupport.firePropertyChange(PROP_XMIN, oldXMin, xmin);
    }

    private String stats = "Value";

    public static final String PROP_STATS = "stats";

    /**
     * Get the value of stats
     *
     * @return the value of stats
     */
    public String getStats() {
	return stats;
    }

    /** 
    * Set the value of stats
     *
     * @param stats new value of stats
     */
    public void setStats(String stats) {
	String oldStats = this.stats;
	this.stats = stats;
	propertyChangeSupport.firePropertyChange(PROP_STATS, oldStats, stats);
	// disable Y value text box if not normalizing at a point and using "Value" for the statistic.
	if (this.stats.equals("Value") && this.integrate) {
	    setIntegrateYTextEnabled(true);
	} else {
	    setIntegrateYTextEnabled(false);
	}
    }

    private Double yValue = 1.0;

    public static final String PROP_YVALUE = "yValue";

    /**
     * Get the value of yValue
     *
     * @return the value of yValue
     */
    public Double getYValue() {
	return yValue;
    }

    /**
     * Set the value of yValue
     *
     * @param yValue new value of yValue
     */
    public void setYValue(Double yValue) {
	Double oldYValue = this.yValue;
	this.yValue = yValue;
	propertyChangeSupport.firePropertyChange(PROP_YVALUE, oldYValue, yValue);
    }

    private String xUnits = "Angstrom";

    public static final String PROP_XUNITS = "xUnits";

    /**
     * Get the value of xUnits
     *
     * @return the value of xUnits
     */
    public String getXUnits() {
	return xUnits;
    }

    /**
     * Set the value of xUnits
     *
     * @param xUnits new value of xUnits
     */
    public void setXUnits(String xUnits) {
	String oldXUnits = this.xUnits;
	this.xUnits = xUnits;
	propertyChangeSupport.firePropertyChange(PROP_XUNITS, oldXUnits, xUnits);
    }

    
    private Double atPointXValue = 5000.0;

    public static final String PROP_ATPOINTXVALUE = "atPointXValue";

    /**
     * Get the value of atPointXValue
     *
     * @return the value of atPointXValue
     */
    public Double getAtPointXValue() {
	return atPointXValue;
    }

    /**
     * Set the value of atPointXValue
     *
     * @param atPointXValue new value of atPointXValue
     */
    public void setAtPointXValue(Double atPointXValue) {
	Double oldAtPointXValue = this.atPointXValue;
	this.atPointXValue = atPointXValue;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTXVALUE, oldAtPointXValue, atPointXValue);
    }
    

    private boolean multiply = true;

    public static final String PROP_MULTIPLY = "multiply";

    /**
     * Get the value of multiply
     *
     * @return the value of multiply
     */
    public boolean isMultiply() {
	return multiply;
    }

    /**
     * Set the value of multiply
     *
     * @param multiply new value of multiply
     */
    public void setMultiply(boolean multiply) {
	boolean oldMultiply = this.multiply;
	this.multiply = multiply;
	propertyChangeSupport.firePropertyChange(PROP_MULTIPLY, oldMultiply, multiply);
    }
    
    private Double atPointYValue = 1.0;

    public static final String PROP_ATPOINTYVALUE = "atPointYValue";

    /**
     * Get the value of atPointYValue
     *
     * @return the value of atPointYValue
     */
    public Double getAtPointYValue() {
	return atPointYValue;
    }

    /**
     * Set the value of atPointYValue
     *
     * @param atPointYValue new value of atPointYValue
     */
    public void setAtPointYValue(Double atPointYValue) {
	Double oldAtPointYValue = this.atPointYValue;
	this.atPointYValue = atPointYValue;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTYVALUE, oldAtPointYValue, atPointYValue);
    }

    private String atPointYUnits = "erg/s/cm2/Angstrom";

    public static final String PROP_ATPOINTYUNITS = "atPointYUnits";

    /**
     * Get the value of atPointYUnits
     *
     * @return the value of atPointYUnits
     */
    public String getAtPointYUnits() {
	return atPointYUnits;
    }

    /**
     * Set the value of atPointYUnits
     *
     * @param atPointYUnits new value of atPointYUnits
     */
    public void setAtPointYUnits(String atPointYUnits) {
	String oldAtPointYUnit = this.atPointYUnits;
	this.atPointYUnits = atPointYUnits;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTYUNITS, oldAtPointYUnit, atPointYUnits);
    }

    private String atPointXUnits = "Angstrom";
    
    public static final String PROP_ATPOINTXUNITS = "atPointXUnits";

    /**
     * Get the value of atPointXUnits
     *
     * @return the value of atPointXUnits
     */
    public String getAtPointXUnits() {
	return atPointXUnits;
    }

    /**
     * Set the value of atPointXUnits
     *
     * @param atPointXUnits new value of atPointXUnits
     */
    public void setAtPointXUnits(String atPointXUnits) {
	String oldAtPointXUnits = this.atPointXUnits;
	this.atPointXUnits = atPointXUnits;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTXUNITS, oldAtPointXUnits, atPointXUnits);
    }

    private String atPointStats = "Value";

    public static final String PROP_ATPOINTSTATS = "atPointStats";

    /**
     * Get the value of atPointStats
     *
     * @return the value of atPointStats
     */
    public String getAtPointStats() {
	return atPointStats;
    }

    /**
     * Set the value of atPointStats
     *
     * @param atPointStats new value of atPointStats
     */
    public void setAtPointStats(String atPointStats) {
	String oldAtPointStats = this.atPointStats;
	this.atPointStats = atPointStats;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTSTATS, oldAtPointStats, atPointStats);
	// disable Y value text box if not normalizing at a point and using "Value" for the statistic.
	if (atPointStats.equals("Value") && this.atPoint) {
	    setAtPointYTextEnabled(true);
	    setIntegrateYTextEnabled(false);
	} else {
	    setAtPointYTextEnabled(false);
	}
    }

    private boolean add = false;

    public static final String PROP_ADD = "add";

    /**
     * Get the value of add
     *
     * @return the value of add
     */
    public boolean isAdd() {
	return add;
    }

    /**
     * Set the value of add
     *
     * @param add new value of add
     */
    public void setAdd(boolean add) {
	boolean oldAdd = this.add;
	this.add = add;
	propertyChangeSupport.firePropertyChange(PROP_ADD, oldAdd, add);
    }

    private boolean atPoint = false;

    public static final String PROP_ATPOINT = "atPoint";

    /**
     * Get the value of atPoint
     *
     * @return the value of atPoint
     */
    public boolean isAtPoint() {
	return atPoint;
    }

    /**
     * Set the value of atPoint
     *
     * @param atPoint new value of atPoint
     */
    public void setAtPoint(boolean atPoint) {
	boolean oldAtPoint = this.atPoint;
	this.atPoint = atPoint;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINT, oldAtPoint, atPoint);
	// disable Y value text box if not normalizing at a point and using "Value" for the statistic.
	if (this.atPoint) {
	    setIntegrateYTextEnabled(false);
	    if (this.atPointStats.equals("Value")) {
		setAtPointYTextEnabled(true);
	    }
	} else {
	    setAtPointYTextEnabled(false);
	}
    }

    private String integrateValueYUnits = "erg/s/cm2";

    public static final String PROP_INTEGRATEVALUEYUNITS = "integrateValueYUnits";

    /**
     * Get the value of integrateValueYUnits
     *
     * @return the value of integrateValueYUnits
     */
    public String getIntegrateValueYUnits() {
	return integrateValueYUnits;
    }

    /**
     * Set the value of integrateValueYUnits
     *
     * @param integrateValueYUnits new value of integrateValueYUnits
     */
    public void setIntegrateValueYUnits(String integrateValueYUnits) {
	String oldIntegrateValueYUnits = this.integrateValueYUnits;
	this.integrateValueYUnits = integrateValueYUnits;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATEVALUEYUNITS, oldIntegrateValueYUnits, integrateValueYUnits);
    }

    private boolean atPointYTextEnabled = false;

    public static final String PROP_ATPOINTYTEXTENABLED = "atPointYTextEnabled";

    public boolean isAtPointYTextEnabled() {
	return atPointYTextEnabled;
    }

    // used to disable Y value text box if using Average or Median.
    public void setAtPointYTextEnabled(boolean atPointYTextEnabled) {
	boolean oldAtPointYTextEnabled = this.atPointYTextEnabled;
	this.atPointYTextEnabled = atPointYTextEnabled;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTYTEXTENABLED, oldAtPointYTextEnabled, atPointYTextEnabled);
    }
    
    private boolean integrateYTextEnabled = true;

    public static final String PROP_INTEGRATEYTEXTENABLED = "integrateYTextEnabled";

    public boolean isIntegrateYTextEnabled() {
	return integrateYTextEnabled;
    }

    // used to disable Y value text box if using Average or Median.
    public void setIntegrateYTextEnabled(boolean integrateYTextEnabled) {
	boolean oldIntegrateYTextEnabled = this.integrateYTextEnabled;
	this.integrateYTextEnabled = integrateYTextEnabled;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTYTEXTENABLED, oldIntegrateYTextEnabled, integrateYTextEnabled);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 41 * hash + (this.integrate ? 1 : 0);
	hash = 41 * hash + (this.xmax != null ? this.xmax.hashCode() : 0);
	hash = 41 * hash + (this.xmin != null ? this.xmin.hashCode() : 0);
	hash = 41 * hash + (this.stats != null ? this.stats.hashCode() : 0);
	hash = 41 * hash + (this.yValue != null ? this.yValue.hashCode() : 0);
	hash = 41 * hash + (this.xUnits != null ? this.xUnits.hashCode() : 0);
	hash = 41 * hash + (this.atPointXValue != null ? this.atPointXValue.hashCode() : 0);
	hash = 41 * hash + (this.multiply ? 1 : 0);
	hash = 41 * hash + (this.atPointYValue != null ? this.atPointYValue.hashCode() : 0);
	hash = 41 * hash + (this.atPointYUnits != null ? this.atPointYUnits.hashCode() : 0);
	hash = 41 * hash + (this.atPointXUnits != null ? this.atPointXUnits.hashCode() : 0);
	hash = 41 * hash + (this.atPointStats != null ? this.atPointStats.hashCode() : 0);
	hash = 41 * hash + (this.integrateValueYUnits != null ? this.integrateValueYUnits.hashCode() : 0);
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
	final NormalizationConfiguration other = (NormalizationConfiguration) obj;
	if (this.integrate != other.integrate) {
	    return false;
	}
	if (this.xmax != other.xmax && (this.xmax == null || !this.xmax.equals(other.xmax))) {
	    return false;
	}
	if (this.xmin != other.xmin && (this.xmin == null || !this.xmin.equals(other.xmin))) {
	    return false;
	}
	if ((this.stats == null) ? (other.stats != null) : !this.stats.equals(other.stats)) {
	    return false;
	}
	if (this.yValue != other.yValue && (this.yValue == null || !this.yValue.equals(other.yValue))) {
	    return false;
	}
	if ((this.xUnits == null) ? (other.xUnits != null) : !this.xUnits.equals(other.xUnits)) {
	    return false;
	}
	if (this.atPointXValue != other.atPointXValue && (this.atPointXValue == null || !this.atPointXValue.equals(other.atPointXValue))) {
	    return false;
	}
	if (this.multiply != other.multiply) {
	    return false;
	}
	if (this.atPointYValue != other.atPointYValue && (this.atPointYValue == null || !this.atPointYValue.equals(other.atPointYValue))) {
	    return false;
	}
	if ((this.atPointYUnits == null) ? (other.atPointYUnits != null) : !this.atPointYUnits.equals(other.atPointYUnits)) {
	    return false;
	}
	if ((this.atPointXUnits == null) ? (other.atPointXUnits != null) : !this.atPointXUnits.equals(other.atPointXUnits)) {
	    return false;
	}
	if ((this.atPointStats == null) ? (other.atPointStats != null) : !this.atPointStats.equals(other.atPointStats)) {
	    return false;
	}
	if ((this.integrateValueYUnits == null) ? (other.integrateValueYUnits != null) : !this.integrateValueYUnits.equals(other.integrateValueYUnits)) {
	    return false;
	}
	return true;
    }

    
    
}
