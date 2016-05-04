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
