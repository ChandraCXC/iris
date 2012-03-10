/*
 *  Copyright 2011 Smithsonian Astrophysical Observatory.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package cfa.vo.sherpa;

/**
 *
 * @author olaurino
 */
public interface Conf extends Confidence {
    Boolean getFast();

    void setFast(Boolean fast);

    Integer getMaxRstat();

    void setMaxRstat(Integer maxRstat);

    Integer getMaxfits();

    void setMaxfits(Integer maxFits);

    Integer getNumcores();

    void setNumcores(Integer numcores);

    Integer getOpeninterval();

    void setOpeninterval(Integer openinterval);

    Double getRemin();

    void setRemin(Double remin);

    Double getTol();

    void setTol(Double tol);
}
