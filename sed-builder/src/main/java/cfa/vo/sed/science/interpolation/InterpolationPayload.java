/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.interpolation;

/**
 *
 * @author olaurino
 */
public interface InterpolationPayload {
    public Boolean getNormalize();
    
    public void setNormalize(Boolean normalize);
    
    public String getMethod();
    
    public void setMethod(String method);
    
    public Double getXMin();
    
    public void setXMin(Double xmin);
    
    public Double getXMax();
    
    public void setXMax(Double xmax);
    
    public Integer getNBins();
    
    public void setNBins(Integer nbins);
    
    public String getUnits();
    
    public void setUnits(String units);
    
    public double[] getX();
    
    public void setX(double[] x);
    
    public double[] getY();
    
    public void setY(double[] y);
    
    public Boolean getLog();
    
    public void setLog(Boolean log);
    
    public Boolean getSmooth();
    
    public void setSmooth(Boolean smooth);
    
    public Integer getBoxSize();
    
    public void setBoxSize(Integer boxSize);
    
}
