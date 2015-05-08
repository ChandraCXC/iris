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
public interface RedshiftPayload {

    public double[] getX();

    public void setX(double[] x);

    public double[] getY();

    public void setY(double[] y);
    
    public double[] getYerr();
    
    public void setYerr(double[] yerr);

    public double getFromRedshift();

    public void setFromRedshift(double redshift);

    public double getToRedshift();

    public void setToRedshift(double redshift);
}
