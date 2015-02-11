/**
 * Copyright (C) 2013, 2015 Smithsonian Astrophysical Observatory
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
