/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.fitting;

import cfa.vo.sherpa.ConfidenceResults;
import org.apache.commons.lang.NotImplementedException;
import org.jdesktop.beansbinding.Converter;

import java.util.ArrayList;
import java.util.List;

public class ConfResultsConverter extends Converter<ConfidenceResults, List<ConfResultsConverter.ParameterLimits>> {

    @Override
    public List<ParameterLimits> convertForward(ConfidenceResults res) {
        List<ParameterLimits> retVal = new ArrayList<>();
        List<String> names = res.getParnames();
        double[] mins = res.getParmins();
        double[] maxes = res.getParmaxes();
        int l = maxes.length;
        for (int i=0; i<l; i++) {
            retVal.add(new ParameterLimits(names.get(i), mins[i], maxes[i]));
        }

        return retVal;
    }

    @Override
    public ConfidenceResults convertReverse(List<ParameterLimits> o) {
        throw new NotImplementedException(); // read-only binding
    }

    public class ParameterLimits {
        private String name;
        private Double lowerLimit;
        private Double upperLimit;

        public ParameterLimits(String name, Double lower, Double upper) {
            this.name = name;
            this.lowerLimit = lower;
            this.upperLimit = upper;
        }

        public String getName() {
            return name;
        }

        public Double getLowerLimit() {
            return lowerLimit;
        }

        public Double getUpperLimit() {
            return upperLimit;
        }
    }
}
