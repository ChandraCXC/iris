/**
 * Copyright (C) 2013 Smithsonian Astrophysical Observatory
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
package cfa.vo.sed.science.integration;

import java.util.List;

/**
 *
 * @author olaurino
 */
public interface IntegrationPayload {
    public List<TransmissionCurve> getCurves();
    public void addCurve(TransmissionCurve curve);
    public List<Window> getWindows();
    public void addWindow(Window window);
    public double[] getX();
    public void setX(double[] x);
    public double[] getY();
    public void setY(double[] y);
}
