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
public interface SedStackerStackPayload {
    
    public Double getBinSize();
    
    public void setBinSize(Double binsize);
    
    public String getBinUnits();
    
    public void setBinUnits(String xunits);
    
    public int getSmoothBoxSize();
    
    public void setSmoothBoxSize(int boxSize);
    
    public String getStatistic();
    
    public void setStatistic(String statistic);
    
    public boolean isLogBinning();
    
    public void setLogBinning(boolean logbinning);
    
    public boolean isSmooth();
    
    public void setSmooth(boolean smooth);
    
    
    public double[] getCounts();
    
    public void setCounts(double[] counts);
    
    
    
    public List<SegmentPayload> getSegments();
    
    public void addSegment(SegmentPayload segment);
    
}
