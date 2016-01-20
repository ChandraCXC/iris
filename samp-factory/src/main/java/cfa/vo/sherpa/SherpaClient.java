/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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

import cfa.vo.interop.*;
import org.astrogrid.samp.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SherpaClient {

    private SampService sampService;
    private Map<String, AbstractModel> modelMap = new HashMap<>();
    private Integer stringCounter = 0;
    private Logger logger = Logger.getLogger(SherpaClient.class.getName());

    public SherpaClient(SampService sampService) {
        this.sampService = sampService;
    }

    public Parameter getParameter(AbstractModel model, String name) throws Exception {
        Parameter par = model.getParameter(model.getId() + "." + name);
        if (par == null) {
            throw new Exception("Parameter "+ name+ " not found in model " + model.getName());
        }
        return par;
    }

    public FitResults fit(Data dataset, CompositeModel model, Stat stat, Method method) throws Exception {

        FitConfiguration fc = (FitConfiguration) SAMPFactory.get(FitConfiguration.class);

        if (dataset.getStaterror() == null) {
            int len = dataset.getX().length;
            double[] staterr = new double[len];
            for (int i = 0; i < dataset.getX().length; i++) {
                staterr[i] = Double.NaN;
            }
            dataset.setStaterror(staterr);
        }

        if (dataset.getSyserror() == null) {
            int len = dataset.getX().length;
            double[] syserr = new double[len];
            for (int i = 0; i < dataset.getX().length; i++) {
                syserr[i] = Double.NaN;
            }
            dataset.setSyserror(syserr);
        }

        fc.addDataset(dataset);
        fc.addModel(model);
        fc.setStat(stat);
        fc.setMethod(method);

        SAMPMessage message = SAMPFactory.createMessage("spectrum.fit.fit", fc, FitConfiguration.class);
        Response response = sendMessage(message);

        return (FitResults) SAMPFactory.get(response.getResult(), FitResults.class);
    }

    public Data createData(String name) {
        Data data = (Data) SAMPFactory.get(Data.class);
        data.setName(name);
        return data;
    }

    public CompositeModel createCompositeModel(String expression, Model... models) {
        CompositeModel cm = (CompositeModel) SAMPFactory.get(CompositeModel.class);
        cm.setName(expression);
        for (Model model : models) {
            cm.addPart(model);
        }
        return cm;
    }

    public AbstractModel createModel(Models model) {
        String id = "m" + (++stringCounter).toString();
        return createModel(model, id);
    }

    public AbstractModel createModel(Models model, String id) {
        AbstractModel m = model.getModel(id);
        modelMap.put(id, m);
        return m;
    }

    public Parameter createParameter(String name) {
        Parameter p = (Parameter) SAMPFactory.get(Parameter.class);
        p.setName(name);
        return p;
    }

    public Method getMethod(OptimizationMethod optMethod) {
        Method method = (Method) SAMPFactory.get(optMethod.getMethodClass());
        method.setName(optMethod.getMethodClass().getSimpleName());
        return method;
    }

    public SampService getService() {
        return this.sampService;
    }

    public Response sendMessage(final SAMPMessage message) throws Exception {
        return sampService.callSherpaAndRetry(message);
    }

    public boolean ping() {
        return sampService.pingSherpa();
    }

    public boolean ping(int nTimes, long intervalMillis) {
        for (int i=0; i<nTimes; i++) {
            if (ping()) {
                return true;
            }
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
            }
        }
        return false;
    }

    public SherpaClient create(final SampService sampService) {
        return new SherpaClient(sampService);
    }
}