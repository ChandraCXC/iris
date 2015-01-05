/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.stacker;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author jbudynk
 */
public class SedStackerExtSed extends ExtSed {

    public SedStackerExtSed(String id) {
	super(id);
    }
    
    public SedStackerExtSed(String id, boolean managed) {
	super(id, managed);
    }
    
    private Double normConst;

    public static final String PROP_NORMCONST = "normConst";

    /**
     * Get the value of normConst
     *
     * @return the value of normConst
     */
    public Double getNormConst() {
	return normConst;
    }

    /**
     * Set the value of normConst
     *
     * @param normConst new value of normConst
     */
    public void setNormConst(Double normConst) {
	Double oldNormConst = this.normConst;
	this.normConst = normConst;
	propertyChangeSupport.firePropertyChange(PROP_NORMCONST, oldNormConst, normConst);
    }

    private Double redshift;

    public static final String PROP_REDSHIFT = "redshift";

    /**
     * Get the value of redshift
     *
     * @return the value of redshift
     */
    public Double getRedshift() {
	return redshift;
    }

    /**
     * Set the value of redshift
     *
     * @param redshift new value of redshift
     */
    public void setRedshift(Double redshift) {
	Double oldRedshift = this.redshift;
	this.redshift = redshift;
	propertyChangeSupport.firePropertyChange(PROP_REDSHIFT, oldRedshift, redshift);
    }

    private int numOfPoints;

    public static final String PROP_NUMOFPOINTS = "numOfPoints";

    /**
     * Get the value of numOfPoints
     *
     * @return the value of numOfPoints
     */
    public int getNumOfPoints() {
	for (Segment seg : this.segmentList) {
	    numOfPoints += seg.getLength();
	}
	return numOfPoints;
    }

    /**
     * Set the value of numOfPoints
     *
     * @param numOfPoints new value of numOfPoints
     */
    public void setNumOfPoints(int numOfPoints) {
	int oldNumOfPoints = this.numOfPoints;
	this.numOfPoints = numOfPoints;
	propertyChangeSupport.firePropertyChange(PROP_NUMOFPOINTS, oldNumOfPoints, numOfPoints);
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
