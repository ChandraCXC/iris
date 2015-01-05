/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.stacker;

import java.util.List;

/**
 *
 * @author jbudynk
 */
public interface SedStackerNormalizePayload {
    
    public Boolean getIntegrate();
    public void setIntegrate(Boolean integrate);
    public Integer getNormOperator();
    public void setNormOperator(Integer normOperator);
    public String getStats();
    public void setStats(String stats);
    public Double getY0();
    public void setY0(Double y0);
    public Double getX0();
    public void setX0(Double x0);
    public String getXmin();
    public void setXmin(String xmin);
    public String getXmax();
    public void setXmax(String xmax);
    
    public double[] getNormConstants();
    public void setNormConstants(double[] normConstants);
    
    public List<SegmentPayload> getSegments();
    public void addSegment(SegmentPayload segment);
    
}
