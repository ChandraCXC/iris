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
public interface SedStackerStackPayload {
    
    public Double getBinsize();
    public void setBinsize(Double binsize);
    public Double getSmoothBinsize();
    public void setSmoothBinsize(Double boxSize);
    public String getStatistic();
    public void setStatistic(String statistic);
    public Boolean getLogBin();
    public void setLogBin(Boolean logBin);
    public Boolean getSmooth();
    public void setSmooth(Boolean smooth);
    
    public List<SegmentPayload> getSegments();
    public void addSegment(SegmentPayload segment);
    
}
