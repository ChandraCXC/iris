/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker.samp;

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
    
    public String getAtPointXUnits();
    
    public void setAtPointXUnits(String xunits);
    
    public String getAtPointYUnits();
    
    public void setAtPointYUnits(String yunits);
    
    public String getIntegrateXUnits();
    
    public void setIntegrateXUnits(String xunits);
    
    public String getIntegrateYUnits();
    
    public void setIntegrateYUnits(String yunits);
    
    public String getStats();
    
    public void setStats(String stats);
    
    public Double getY0();
    
    public void setY0(Double y0);
    
    public String getXmin();
    
    public void setXmin(String xmin);
    
    public String getXmax();
    
    public void setXmax(String xmax);
    
    
    
    public double[] getNormConstants();
    
    public void setNormConstants(double[] normConstants);
    
    
    
    public List<SegmentPayload> getSegments();
    
    public void addSegment(SegmentPayload segment);
    
}
