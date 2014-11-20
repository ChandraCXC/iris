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

    private String integrateBoundsXMax = "max";

    public static final String PROP_INTEGRATEBOUNDSXMAX = "integrateBoundsXMax";

    /**
     * Get the value of integrateBoundsXMax
     *
     * @return the value of integrateBoundsXMax
     */
    public String getIntegrateBoundsXMax() {
	return integrateBoundsXMax;
    }

    /**
     * Set the value of integrateBoundsXMax
     *
     * @param integrateBoundsXMax new value of integrateBoundsXMax
     */
    public void setIntegrateBoundsXMax(String integrateBoundsXMax) {
	String oldIntegrateBoundsXMax = this.integrateBoundsXMax;
	this.integrateBoundsXMax = integrateBoundsXMax;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATEBOUNDSXMAX, oldIntegrateBoundsXMax, integrateBoundsXMax);
    }

    private String integrateBoundsXMin = "min";

    public static final String PROP_INTEGRATEBOUNDSXMIN = "integrateBoundsXMin";

    /**
     * Get the value of integrateBoundsXMin
     *
     * @return the value of integrateBoundsXMin
     */
    public String getIntegrateBoundsXMin() {
	return integrateBoundsXMin;
    }

    /**
     * Set the value of integrateBoundsXMin
     *
     * @param integrateBoundsXMin new value of integrateBoundsXMin
     */
    public void setIntegrateBoundsXMin(String integrateBoundsXMin) {
	String oldIntegrateBoundsXMin = this.integrateBoundsXMin;
	this.integrateBoundsXMin = integrateBoundsXMin;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATEBOUNDSXMIN, oldIntegrateBoundsXMin, integrateBoundsXMin);
    }

    private String integrateYType = "Value";

    public static final String PROP_INTEGRATEYTYPE = "integrateYType";

    /**
     * Get the value of integrateYType
     *
     * @return the value of integrateYType
     */
    public String getIntegrateYType() {
	return integrateYType;
    }

    /**
     * Set the value of integrateYType
     *
     * @param integrateYType new value of integrateYType
     */
    public void setIntegrateYType(String integrateYType) {
	String oldIntegrateYType = this.integrateYType;
	this.integrateYType = integrateYType;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATEYTYPE, oldIntegrateYType, integrateYType);
    }

    private Double integrateYValue = 1.0;

    public static final String PROP_INTEGRATEYVALUE = "integrateYValue";

    /**
     * Get the value of integrateYValue
     *
     * @return the value of integrateYValue
     */
    public Double getIntegrateYValue() {
	return integrateYValue;
    }

    /**
     * Set the value of integrateYValue
     *
     * @param integrateYValue new value of integrateYValue
     */
    public void setIntegrateYValue(Double integrateYValue) {
	Double oldIntegrateYValue = this.integrateYValue;
	this.integrateYValue = integrateYValue;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATEYVALUE, oldIntegrateYValue, integrateYValue);
    }

    private String integrateYUnits = "erg/s/cm2";

    public static final String PROP_INTEGRATEYUNITS = "integrateYUnits";

    /**
     * Get the value of integrateYUnit
     *
     * @return the value of integrateYUnit
     */
    public String getIntegrateYUnits() {
	return integrateYUnits;
    }

    /**
     * Set the value of integrateYUnit
     *
     * @param integrateYUnit new value of integrateYUnit
     */
    public void setIntegrateYUnits(String integrateYUnit) {
	String oldIntegrateYUnit = this.integrateYUnits;
	this.integrateYUnits = integrateYUnit;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATEYUNITS, oldIntegrateYUnit, integrateYUnit);
    }

    private String integrateXUnits = "Angstrom";

    public static final String PROP_INTEGRATEXUNITS = "integrateXUnits";

    /**
     * Get the value of integrateXUnits
     *
     * @return the value of integrateXUnits
     */
    public String getIntegrateXUnits() {
	return integrateXUnits;
    }

    /**
     * Set the value of integrateXUnits
     *
     * @param integrateXUnits new value of integrateXUnits
     */
    public void setIntegrateXUnits(String integrateXUnits) {
	String oldIntegrateXUnits = this.integrateXUnits;
	this.integrateXUnits = integrateXUnits;
	propertyChangeSupport.firePropertyChange(PROP_INTEGRATEXUNITS, oldIntegrateXUnits, integrateXUnits);
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

    private String atPointYUnits = "erg/s/cm2";

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
	String oldAtPointYUnits = this.atPointYUnits;
	this.atPointYUnits = atPointYUnits;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTYUNITS, oldAtPointYUnits, atPointYUnits);
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

    private String atPointYType = "Value";

    public static final String PROP_ATPOINTYTYPE = "atPointYType";

    /**
     * Get the value of atPointYType
     *
     * @return the value of atPointYType
     */
    public String getAtPointYType() {
	return atPointYType;
    }

    /**
     * Set the value of atPointYType
     *
     * @param atPointYType new value of atPointYType
     */
    public void setAtPointYType(String atPointYType) {
	String oldAtPointYType = this.atPointYType;
	this.atPointYType = atPointYType;
	propertyChangeSupport.firePropertyChange(PROP_ATPOINTYTYPE, oldAtPointYType, atPointYType);
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

}
