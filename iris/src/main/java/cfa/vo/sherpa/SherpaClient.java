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

import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.PingMessage;
import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.astrogrid.samp.Client;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.ResultHandler;
import org.astrogrid.samp.client.SampException;

/**
 *
 * @author olaurino
 */
public class SherpaClient {

    private SAMPController sampController;
    private Map<String, AbstractModel> modelMap = new HashMap();
    private Integer stringCounter = 0;
    private String sherpaPublicId;

    public SherpaClient(SAMPController controller) {
        this.sampController = controller;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
    }

    public Parameter getParameter(AbstractModel model, String name) {
        return model.getParameter(model.getId() + "." + name);
    }

    public FitResults fit(Data dataset, CompositeModel model, Stat stat, Method method) throws Exception {

        if (sherpaPublicId == null) {
            findSherpa();
            if (sherpaPublicId == null) {
                throw new Exception("Sherpa is not connected to the hub?");
            }
        }

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
        Response response = sampController.callAndWait(sherpaPublicId, message.get(), 10);

        return (FitResults) SAMPFactory.get(response.getResult(), FitResults.class);

    }

    public String createExpression(String expression, Model[] models) {

        expression = expression.replaceAll(" ", "");

        for (Entry<String, AbstractModel> entry : modelMap.entrySet()) {
            expression = expression.replaceAll(entry.getKey(), entry.getValue().getName());//FIXME naive implementation, works only (maybe) with less than 10 models
        }

        return expression;
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

    public void findSherpa() throws SampException {
        sampController.sendMessage(new PingMessage(), new PingResultHandler(), 10);
    }

    private class PingResultHandler implements ResultHandler {

        @Override
        public void result(Client client, Response rspns) {
            if (client.getMetadata().getName().toLowerCase().equals("sherpa")) {
                sherpaPublicId = client.getId();
            }
        }

        @Override
        public void done() {
        }
    }
}
