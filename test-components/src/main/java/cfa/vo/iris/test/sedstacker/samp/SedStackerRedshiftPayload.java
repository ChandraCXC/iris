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
public interface SedStackerRedshiftPayload {
    
    public List<SegmentPayload> getSegments();

    public void addSegment(SegmentPayload payload);

    public Double getZ0();

    public void setZ0(Double redshift);
    
    public Boolean getCorrectFlux();

    public void setCorrectFlux(Boolean correctFlux);
}
