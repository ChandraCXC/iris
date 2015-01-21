/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    
    /** get SEDs that were excluded from normalization
     * 
     * @return list of Strings of SED ID's that were excluded from normalizing 
     */
    public List<String> getExcluded();
    public void addExcluded(List<String> excluded);
    
}
