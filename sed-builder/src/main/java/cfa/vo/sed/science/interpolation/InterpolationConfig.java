/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.interpolation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author olaurino
 */
public class InterpolationConfig implements InterpolationPayload {

    private Integer boxSize = 20;
    public static final String PROP_BOXSIZE = "boxSize";

    /**
     * Get the value of boxSize
     *
     * @return the value of boxSize
     */
    @Override
    public Integer getBoxSize() {
        return boxSize;
    }

    /**
     * Set the value of boxSize
     *
     * @param boxSize new value of boxSize
     */
    @Override
    public void setBoxSize(Integer boxSize) {
        Integer oldBoxSize = this.boxSize;
        this.boxSize = boxSize;
        propertyChangeSupport.firePropertyChange(PROP_BOXSIZE, oldBoxSize, boxSize);
    }

    
    private Boolean smooth = Boolean.FALSE;
    public static final String PROP_SMOOTH = "smooth";

    /**
     * Get the value of smooth
     *
     * @return the value of smooth
     */
    @Override
    public Boolean getSmooth() {
        return smooth;
    }

    /**
     * Set the value of smooth
     *
     * @param smooth new value of smooth
     */
    @Override
    public void setSmooth(Boolean smooth) {
        Boolean oldSmooth = this.smooth;
        this.smooth = smooth;
        propertyChangeSupport.firePropertyChange(PROP_SMOOTH, oldSmooth, smooth);
    }

    
    private Boolean log = Boolean.TRUE;
    public static final String PROP_LOG = "log";

    /**
     * Get the value of log
     *
     * @return the value of log
     */
    @Override
    public Boolean getLog() {
        return log;
    }

    /**
     * Set the value of log
     *
     * @param log new value of log
     */
    @Override
    public void setLog(Boolean log) {
        Boolean oldLog = this.log;
        this.log = log;
        propertyChangeSupport.firePropertyChange(PROP_LOG, oldLog, log);
    }

    
    private Boolean normalize = false;
    public static final String PROP_NORMALIZE = "normalize";

    /**
     * Get the value of normalize
     *
     * @return the value of normalize
     */
    @Override
    public Boolean getNormalize() {
        return normalize;
    }

    /**
     * Set the value of normalize
     *
     * @param normalize new value of normalize
     */
    @Override
    public void setNormalize(Boolean normalize) {
        Boolean oldNormalize = this.normalize;
        this.normalize = normalize;
        propertyChangeSupport.firePropertyChange(PROP_NORMALIZE, oldNormalize, normalize);
    }

    
    private String method = "Linear Spline";
    public static final String PROP_METHOD = "method";

    /**
     * Get the value of method
     *
     * @return the value of method
     */
    @Override
    public String getMethod() {
        return method;
    }

    /**
     * Set the value of method
     *
     * @param method new value of method
     */
    @Override
    public void setMethod(String method) {
        String oldMethod = this.method;
        this.method = method;
        propertyChangeSupport.firePropertyChange(PROP_METHOD, oldMethod, method);
    }

    private Double xMin = Double.NEGATIVE_INFINITY;
    public static final String PROP_XMIN = "xMin";

    /**
     * Get the value of xmin
     *
     * @return the value of xmin
     */
    @Override
    public Double getXMin() {
        return xMin;
    }

    /**
     * Set the value of xmin
     *
     * @param xmin new value of xmin
     */
    @Override
    public void setXMin(Double xMin) {
        Double oldXMin = this.xMin;
        this.xMin = xMin;
        propertyChangeSupport.firePropertyChange(PROP_XMIN, oldXMin, xMin);
    }

    private Double xMax = Double.POSITIVE_INFINITY;
    public static final String PROP_XMAX = "xMax";

    /**
     * Get the value of xmax
     *
     * @return the value of xmax
     */
    @Override
    public Double getXMax() {
        return xMax;
    }

    /**
     * Set the value of xmax
     *
     * @param xmax new value of xmax
     */
    @Override
    public void setXMax(Double xMax) {
        Double oldXmax = this.xMax;
        this.xMax = xMax;
        propertyChangeSupport.firePropertyChange(PROP_XMAX, oldXmax, xMax);
    }
    
    private Integer nBins = 1000;
    public static final String PROP_NBINS = "nBins";

    /**
     * Get the value of nBins
     *
     * @return the value of nBins
     */
    @Override
    public Integer getNBins() {
        return nBins;
    }

    /**
     * Set the value of nBins
     *
     * @param nBins new value of nBins
     */
    @Override
    public void setNBins(Integer nBins) {
        Integer oldNBins = this.nBins;
        this.nBins = nBins;
        propertyChangeSupport.firePropertyChange(PROP_NBINS, oldNBins, nBins);
    }

    private String units = "Angstrom";
    public static final String PROP_UNITS = "units";

    /**
     * Get the value of units
     *
     * @return the value of units
     */
    @Override
    public String getUnits() {
        return units;
    }

    /**
     * Set the value of units
     *
     * @param units new value of units
     */
    @Override
    public void setUnits(String units) {
        String oldUnits = this.units;
        this.units = units;
        propertyChangeSupport.firePropertyChange(PROP_UNITS, oldUnits, units);
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

    private double[] x;
    public static final String PROP_X = "x";

    /**
     * Get the value of x
     *
     * @return the value of x
     */
    @Override
    public double[] getX() {
        return x;
    }

    /**
     * Set the value of x
     *
     * @param x new value of x
     */
    @Override
    public void setX(double[] x) {
        double[] oldX = this.x;
        this.x = x;
        propertyChangeSupport.firePropertyChange(PROP_X, oldX, x);
    }

    private double[] y;
    public static final String PROP_Y = "y";

    /**
     * Get the value of y
     *
     * @return the value of y
     */
    @Override
    public double[] getY() {
        return y;
    }

    /**
     * Set the value of y
     *
     * @param y new value of y
     */
    @Override
    public void setY(double[] y) {
        double[] oldY = this.y;
        this.y = y;
        propertyChangeSupport.firePropertyChange(PROP_Y, oldY, y);
    }
    
    @Override
    public String toString() {
        return "Interpolation Method: "+method+
                ", interval: ("+xMin+","+xMax+") "+ units+
                ", # bins: "+nBins+
                ", Normalized? "+normalize+
                ", Smoothed? "+smooth+ (smooth? "BoxSize: "+boxSize : "");
    }


}
