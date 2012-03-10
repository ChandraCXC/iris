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
public interface Config {
    
    Double getFtol();

    void setFtol(Double ftol);

    Integer getMaxfev();

    void setMaxfev(Integer maxfev);

    Double getEpsfcn();

    void setEpsfcn(Double epsvcn);

    Double getFactor();

    void setFactor(Double factor);

    Double getGtol();

    void setGtol(Double gtol);

    Double getXtol();

    void setXtol(Double xtol);

    Integer getPopulationSize();

    void setPopulationSize(Integer populationSize);

    Integer getSeed();

    void setSeed(Integer seed);

    Integer getWeightingFactor();

    void setWeightingFactor(Integer weightingFactor);

    Double getXprob();

    void setXprob(Double xprob);

    Integer getFinalsimplex();

    void setFinalsimplex(Integer finalsimplex);

    Integer getInitsimplex();

    void setInitsimplex(Integer initsimplex);

    Integer getIquad();

    void setIquad(Integer iquad);

    Integer getStep();

    void setStep(Integer step);

}
