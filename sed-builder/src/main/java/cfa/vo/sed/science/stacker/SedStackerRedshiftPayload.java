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
public interface SedStackerRedshiftPayload {
    
    public List<SegmentPayload> getSegments();

    public void addSegment(SegmentPayload payload);
    
    /** get SEDs that were excluded from redshifting
     * 
     * @return list of Strings of SED ID's that were excluded from shifting 
     */
    public List<String> getExcludeds();
    
    public void addExcluded(String excludedIds);

    public Double getZ0();

    public void setZ0(Double redshift);
    
    public Boolean getCorrectFlux();

    public void setCorrectFlux(Boolean correctFlux);
}
