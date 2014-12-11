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
public class NormConfig {
    
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

    private String xmax = "max";

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

    private String xmin = "min";

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

    private String yUnits = "erg/s/cm2";

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

    private Double xValue = 5000.0;

    public static final String PROP_XVALUE = "xValue";

    /**
     * Get the value of xValue
     *
     * @return the value of xValue
     */
    public Double getXValue() {
	return xValue;
    }

    /**
     * Set the value of xValue
     *
     * @param xValue new value of xValue
     */
    public void setXValue(Double xValue) {
	Double oldXValue = this.xValue;
	this.xValue = xValue;
	propertyChangeSupport.firePropertyChange(PROP_XVALUE, oldXValue, xValue);
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

    
    private Integer normOperator = 0;

    public static final String PROP_NORMOPERATOR = "normOperator";

    /**
     * Get the value of normOperator
     *
     * @return the value of normOperator
     */
    public Integer getNormOperator() {
	return normOperator;
    }

    /**
     * Set the value of normOperator
     *
     * @param normOperator new value of normOperator
     */
    public void setNormOperator(Integer normOperator) {
	Integer oldNormOperator = this.normOperator;
	this.normOperator = normOperator;
	propertyChangeSupport.firePropertyChange(PROP_NORMOPERATOR, oldNormOperator, normOperator);
    }

    
}
