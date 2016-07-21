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
import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sherpa.models.*;
import org.astrogrid.samp.Response;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class SherpaClient {
    public static final String DATA_NAME = "fitdata";
    public static final String X_UNIT = "Angstrom";
    public static final String Y_UNIT = "erg/s/cm2/Angstrom";

    private ModelFactory modelFactory = new ModelFactory();
    private SampService sampService;
    private AtomicInteger stringCounter = new AtomicInteger();
    private static final String FIT_MTYPE = "spectrum.fit.fit";
    private static final String CONFIDENCE_MTYPE = "spectrum.fit.confidence";
    private static final String EVALUATE_MTYPE = "spectrum.fit.calc.model.values";
    private Logger logger = Logger.getLogger(SherpaClient.class.getName());

    public SherpaClient(SampService sampService) {
        this.sampService = sampService;
    }

    FitResults fit(SherpaFitConfiguration conf) throws Exception {
        fixDatasets(conf);
        SAMPMessage message = SAMPFactory.createMessage(FIT_MTYPE, conf, SherpaFitConfiguration.class);
        Response response = sendMessage(message);

        return SAMPFactory.get(response.getResult(), FitResults.class);
    }

    public FitResults fit(ExtSed sed) throws Exception {
        SherpaFitConfiguration conf = make(sed);
        return fit(conf);
    }

    public double[] evaluate(double[] x, FitConfiguration fit) throws Exception {
        SherpaFitConfiguration conf = make(x, fit);
        return evaluate(conf);
    }

    public double[] evaluate(SherpaFitConfiguration conf) throws Exception {
        fixDatasets(conf);
        SAMPMessage message = SAMPFactory.createMessage(EVALUATE_MTYPE, conf, SherpaFitConfiguration.class);
        Response response = sendMessage(message);
        Data out = SAMPFactory.get(response.getResult(), Data.class);
        return out.getY();
    }

    public FitResults fit(Data data, FitConfiguration fit) throws Exception {
        SherpaFitConfiguration conf = make(data, fit);
        return fit(conf);
    }

    public Data createData(String name) {
        Data data = SAMPFactory.get(Data.class);
        data.setName(name);
        return data;
    }
    
    public ConfidenceResults computeConfidence(Data data, FitConfiguration configuration) throws Exception {
        return computeConfidence(make(data, configuration));
    }

    public ConfidenceResults computeConfidence(ExtSed sed) throws Exception {
        return computeConfidence(make(sed));
    }
    
    private ConfidenceResults computeConfidence(SherpaFitConfiguration conf) throws Exception {
        fixDatasets(conf);
        SAMPMessage message = SAMPFactory.createMessage(CONFIDENCE_MTYPE, conf, SherpaFitConfiguration.class);
        Response response = sendMessage(message);
        return SAMPFactory.get(response.getResult(), ConfidenceResults.class);
    }

    public CompositeModel createCompositeModel(String expression, Model... models) {
        CompositeModel cm = SAMPFactory.get(CompositeModel.class);
        cm.setName(expression);
        for (Model model : models) {
            cm.addPart(model);
        }
        return cm;
    }

    public Model createModel(String name) {
        return createModel(name, createId());
    }

    public Model createModel(String name, String id) {
        Model m = modelFactory.getModel(name, id);
        return m;
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

    public String createId() {
        return createId("m");
    }

    public String createId(String prefix) {
        return prefix + stringCounter.incrementAndGet();
    }

    private SherpaFitConfiguration make(Data data, FitConfiguration fit) {
        SherpaFitConfiguration fc = SAMPFactory.get(SherpaFitConfiguration.class);

        fc.addDataset(data);
        fc.addModel(fit.getModel());
        fc.setStat(fit.getStat());
        fc.setMethod(fit.getMethod());
        fc.setConfidence(fit.getConfidence());
        for (UserModel m : fit.getUserModelList()) {
            fc.addUsermodel(m);
        }

        return fc;
    }

    private SherpaFitConfiguration make(double[] x, FitConfiguration fit) {
        Data data = SAMPFactory.get(Data.class);
        data.setName(DATA_NAME);
        data.setX(x);
        return make(data, fit);
    }

    SherpaFitConfiguration make(ExtSed sed) throws Exception {
        FitConfiguration fit = sed.getFit();
        Data data = SAMPFactory.get(Data.class);
        data.setName(DATA_NAME);
        ExtSed flat = ExtSed.flatten(sed, X_UNIT, Y_UNIT);
        data.setX(flat.getSegment(0).getSpectralAxisValues());
        data.setY(flat.getSegment(0).getFluxAxisValues());
        data.setStaterror((double[]) flat.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR));

        return make(data, fit);
    }

    private void fixDatasets(SherpaFitConfiguration conf) {
        for (Data dataset : conf.getDatasets()) {
            if (dataset.getStaterror() == null) {
                int len = dataset.getX().length;
                double[] staterr = new double[len];
                for (int i = 0; i < dataset.getX().length; i++) {
                    staterr[i] = Double.NaN;
                }
                dataset.setStaterror(staterr);
            }
        }
    }
}