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
    
    public NormalizationConfiguration() {
	setIntegrate(true);
	setMultiply(true);
	setStats("Value");
	setXUnits("Angstrom");
	setXmax("max");
	setXmin("min");
	setYUnits("ergs/s/cm2");
	setYValue(1.0);
	setAtPointYValue(1.0);
	setAtPointYUnits("erg/s/cm2/A");
	setAtPointXValue(5000.0);
	setAtPointXUnits("Angstrom");
	setAtPointStats("Value");
    }
    
    private boolean integrate;

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

    private String xmax;

    public static final String PROP_XMAX = "xmax";

    /**
     * Get the value of xmax
     *
     * @return the value of xmax
     */
    public String getXmax() {
	return xmax;
    }

    /**
     * Set the value of xmax
     *
     * @param xmax new value of xmax
     */
    public void setXmax(String xmax) {
	String oldXMax = this.xmax;
	this.xmax = xmax;
	propertyChangeSupport.firePropertyChange(PROP_XMAX, oldXMax, xmax);
    }

    private String xmin;

    public static final String PROP_XMIN = "xmin";

    /**
     * Get the value of xmin
     *
     * @return the value of xmin
     */
    public String getXmin() {
	return xmin;
    }

    /**
     * Set the value of xmin
     *
     * @param xmin new value of xmin
     */
    public void setXmin(String xmin) {
	String oldXMin = this.xmin;
	this.xmin = xmin;
	propertyChangeSupport.firePropertyChange(PROP_XMIN, oldXMin, xmin);
    }

    private String stats;

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

    private String yUnits;

    public static final String PROP_YUNITS = "yUnits";

    /**
     * Get the value of yUnit
     *
     * @return the value of yUnit
     */
    public String getYUnits() {
	return yUnits;
    }

    /**
     * Set the value of yUnit
     *
     * @param yUnit new value of yUnit
     */
    public void setYUnits(String yUnit) {
	String oldYUnit = this.yUnits;
	this.yUnits = yUnit;
	propertyChangeSupport.firePropertyChange(PROP_YUNITS, oldYUnit, yUnit);
    }

    private String xUnits;

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

    
    private Double atPointXValue;

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
    

    private boolean multiply;

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
    
    private Double atPointYValue;

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

    private String atPointYUnits;

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

    private String atPointXUnits;
    
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

    private String atPointStats;

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
    }

    
}
