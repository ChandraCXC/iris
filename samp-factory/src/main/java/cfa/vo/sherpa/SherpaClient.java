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

import cfa.vo.utils.Default;
import cfa.vo.utils.Time;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.SampException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SherpaClient {

    private SampService sampService;
    private Map<String, AbstractModel> modelMap = new HashMap<>();
    private Integer stringCounter = 0;
    private static Logger logger = Logger.getLogger(SherpaClient.class.getName());

    protected SherpaClient(SampService sampService) {
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

        String sherpaPublicId = findSherpa();

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
        Response response = sampService.getSampClient().callAndWait(sherpaPublicId, message.get(), 10);

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

    protected String findSherpa() throws SampException {
        return findSherpa(sampService);
    }

    private static String findSherpa(SampService service) throws SampException {
        if (!service.isSampUp()) {
            logger.log(Level.WARNING, "Not connected to the hub, giving up looking for Sherpa");
            return "";
        }
        logger.log(Level.INFO, "looking for Sherpa");
        Message msg = new Message("x-samp.query.by-meta");
        msg.addParam("key", "samp.name");
        msg.addParam("value", "Sherpa");
        List<String> ids = (List<String>) service.getSampClient().callAndWait("hub", msg, 3000).getResult().get("ids");
        if (!ids.isEmpty()) {
            String retval = ids.get(0);
            logger.log(Level.INFO, "found Sherpa with id: "+retval);
            return retval;
        } else {
            logger.log(Level.WARNING, "Sherpa not found connected to the hub");
            return "";
        }
    }

    public SampService getService() {
        return this.sampService;
    }

    protected boolean isException(Response rspns) {
        return !rspns.isOK();
    }

    protected Exception getException(Response rspns) throws Exception {
        try {
            String message = (String) rspns.getResult().get("message");
            return new SEDException(message);
        } catch (Exception ex) {
            Logger.getLogger(SherpaClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        } 
    }
    
    public class SEDException extends Exception {
        public SEDException(String msg) {
            super(msg);
        }
    }

    public Response sendMessage(final SAMPMessage message) throws Exception {
        Time timeout = Default.getInstance().getSampTimeout();
        long amount = TimeUnit.SECONDS.convert(timeout.getAmount(), timeout.getUnit());
        Response response = sampService.getSampClient().callAndWait(findSherpa(), message.get(), (int) amount);
        if (isException(response)) {
            throw getException(response);
        }

        return response;
    }

    public static boolean ping(SampService sampService) {
        Time step = Default.getInstance().getTimeStep().convertTo(TimeUnit.SECONDS);
        long seconds = step.getAmount();
        final int stepSeconds = seconds < 1? 1 : (int) seconds;
        try {
            logger.log(Level.INFO, "pinging Sherpa with a " + stepSeconds + " seconds timeout");
            String id = findSherpa(sampService);
            if (!id.isEmpty()) {
                sampService.getSampClient().callAndWait(id, new PingMessage().get(), stepSeconds);
                logger.log(Level.INFO, "Sherpa replied");
                return true;
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Cannot ping Sherpa");
        }
        return false;
    }

    public boolean ping() throws SampException {
        return ping(this.sampService);
    }

    public boolean ping(int nTimes, long intervalMillis) throws SampException {
        for (int i=0; i<nTimes; i++) {
            if (ping(this.sampService)) {
                return true;
            }
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
            }
        }
        return false;
    }

    public static SherpaClient create(final SampService sampService) {
        return new SherpaClient(sampService);
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        final Time timeout = Default.getInstance().getSampTimeout().convertTo(TimeUnit.SECONDS);
//        Time step = Default.getInstance().getTimeStep().convertTo(TimeUnit.MILLISECONDS);
//        final int stepMillis = (int) step.getAmount();
//        Callable<SherpaClient> callable = new Callable<SherpaClient>() {
//            @Override
//            public SherpaClient call() throws Exception {
//                SherpaClient client = new SherpaClient(sampService);
//                String id = null;
//                while (id == null) {
//                    try {
//                        id = findSherpa(sampService);
//                    } catch (SampException ex) {
//                        Thread.sleep(stepMillis); // This will be interrupted if a timeout occurs
//                    }
//                }
//
//                boolean sherpaConnected = false;
//                while (!sherpaConnected) {
//                    sherpaConnected = ping(sampService);
//                    if (!sherpaConnected) {
//                        logger.log(Level.INFO, "Sherpa did not respond to ping, retrying in "+ stepMillis + " milliseconds");
//                        Thread.sleep(stepMillis); // This will be interrupted if a timeout occurs
//                    }
//                }
//                return client;
//            }
//        };
//        Future<SherpaClient> futureSherpaClient = executor.submit(callable);
//        try {
//            return futureSherpaClient.get(timeout.getAmount(), timeout.getUnit());
//        } catch (Exception ex) {
//            throw new RuntimeException("Cannot find Sherpa!", ex);
//        } finally {
//            executor.shutdown();
//        }
    }
}