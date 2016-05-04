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

package cfa.vo.sherpa.stats;

public enum Statistic implements Stat {
    Chi2("chi2"),
    Chi2DataVar("chi2datavar"),
    Chi2Gehrels("chi2gehrels"),
    Chi2ModelVariance("chi2modvar"),
    Chi2ConstantVariance("chi2constvar"),
    Chi2XspecVariance("chi2xspecvar"),
    CStat("cstat"),
    Cash("cash"),
    LeastSquares("leastsq");
    private String name;

    Statistic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return name();
    }

}
