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

package cfa.vo.sherpa;

import java.util.List;

/**
 *
 * @author olaurino
 */
public interface FitResults {
    Boolean getSucceeded();

    double[] getParvals();

    void addParname(String parname);

    List<String> getParnames();

    Double getStatval();

    Integer getNumpoints();

    Double getDof();

    Double getQval();

    Double getRstat();

    Integer getNfev();
}
