/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.fitting;

import cfa.vo.iris.sed.quantities.XUnit;

/**
 *
 * Simple object for holding the start and end points of a fitting range.
 */
public class FittingRange {
    private double startPoint;
    private double endPoint;
    private XUnit xunit;
    
    public FittingRange() {
    }
    
    public FittingRange(double startPoint, double endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.xunit = XUnit.ANGSTROM;
    }
    
    public FittingRange(double startPoint, double endPoint, XUnit xunit) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.xunit = xunit;
    }

    public double getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(double startPoint) {
        this.startPoint = startPoint;
    }

    public double getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(double endPoint) {
        this.endPoint = endPoint;
    }
    
    public XUnit getXUnit() {
        return this.xunit;
    }
    
    public void setXUnit(XUnit xunit) {
        this.xunit = xunit;
    }
}
