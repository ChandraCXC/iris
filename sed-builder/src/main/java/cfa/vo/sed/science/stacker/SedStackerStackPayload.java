/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
