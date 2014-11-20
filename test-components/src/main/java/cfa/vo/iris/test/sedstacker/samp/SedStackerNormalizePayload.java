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
    
    public boolean getIntegrate();
    
    public void setIntegrate(boolean integrate);
    
    public boolean getMultiply();
    
    public void setMultiply(boolean multiply);
    
    
    
    public String getAtPointXUnits();
    
    public void setAtPointXUnits(String xunits);
    
    public String getAtPointYUnits();
    
    public void setAtPointYUnits(String yunits);
    
    public String getAtPointYType();
    
    public void setAtPointYType(String ytype);
    
    public Double getAtPointYValue();
    
    public void setAtPointYValue(Double yvalue);
    
    
    
    public String getIntegrateXUnits();
    
    public void setIntegrateXUnits(String xunits);
    
    public String getIntegrateYUnits();
    
    public void setIntegrateYUnits(String yunits);
    
    public String getIntegrateYType();
    
    public void setIntegrateYType(String ytype);
    
    public Double getIntegrateYValue();
    
    public void setIntegrateYValue(Double ytype);
    
    public String getIntegrateBoundsXMin();
    
    public void setIntegrateBoundsXMin(String xmin);
    
    public String getIntegrateBoundsXMax();
    
    public void setIntegrateBoundsXMax(String xmax);
    
    
    
    public double[] getNormConstants();
    
    public void setNormConstants(double[] normConstants);
    
    
    
    public List<SegmentPayload> getSegments();
    
    public void addSegment(SegmentPayload segment);
    
}
