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

import cfa.vo.interop.PingMessage;
import cfa.vo.interop.ISAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;

import cfa.vo.iris.utils.Default;
import cfa.vo.iris.utils.Time;
import org.apache.commons.lang.StringUtils;
import org.astrogrid.samp.Client;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.SampException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SherpaClient {

    private ISAMPController sampController;
    private Map<String, AbstractModel> modelMap = new HashMap<>();
    private Integer stringCounter = 0;
    private static Logger logger = Logger.getLogger(SherpaClient.class.getName());

    protected SherpaClient(ISAMPController controller) {
        this.sampController = controller;
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
        Response response = sampController.callAndWait(sherpaPublicId, message.get(), 10);

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
        return findSherpa(sampController);
    }

    private static String findSherpa(ISAMPController controller) throws SampException {
        String returnString = "";
        logger.log(Level.INFO, "looking for Sherpa");
        try {
            for(Entry<String, Client> entry : (Set<Entry<String, Client>>) controller.getClientMap().entrySet())
                if (entry.getValue().getMetadata().getName().toLowerCase().equals("sherpa")) {
                    returnString = entry.getValue().getId();
                    break;
                }
            if (StringUtils.isEmpty(returnString)) {
                throw new Exception();
            }
            logger.log(Level.INFO, "found Sherpa with id: "+returnString);
            return returnString;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error occurred while looking for Sherpa", ex);
            throw new SampException("Cannot find Sherpa. If the problem persists, please refer to the troubleshooting section of the documentation.", ex);
        }
    }

    public ISAMPController getController() {
        return this.sampController;
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
        Response response = sampController.callAndWait(findSherpa(), message.get(), (int)amount);
        if (isException(response)) {
            throw getException(response);
        }

        return response;
    }

    public static boolean ping(ISAMPController controller) {
        Time step = Default.getInstance().getTimeStep().convertTo(TimeUnit.SECONDS);
        long seconds = step.getAmount();
        final int stepSeconds = seconds < 1? 1 : (int) seconds;
        try {
            logger.log(Level.INFO, "pinging Sherpa with a " + stepSeconds + " seconds timeout");
            String id = findSherpa(controller);
            controller.callAndWait(id, new PingMessage().get(), stepSeconds);
            logger.log(Level.INFO, "Sherpa replied");
            return true;
        } catch (SampException ex) {
            logger.log(Level.SEVERE, "Cannot ping Sherpa", ex);
            return false;
        }
    }

    public boolean ping() throws SampException {
        return ping(this.sampController);
    }

    public static SherpaClient create(final ISAMPController controller) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Time timeout = Default.getInstance().getSampTimeout().convertTo(TimeUnit.SECONDS);
        Time step = Default.getInstance().getTimeStep().convertTo(TimeUnit.MILLISECONDS);
        final int stepMillis = (int) step.getAmount();
        Callable<SherpaClient> callable = new Callable<SherpaClient>() {
            @Override
            public SherpaClient call() throws Exception {
                SherpaClient client = new SherpaClient(controller);
                String id = null;
                while (id == null) {
                    try {
                        id = findSherpa(controller);
                    } catch (SampException ex) {
                        Thread.sleep(stepMillis); // This will be interrupted if a timeout occurs
                    }
                }

                boolean sherpaConnected = false;
                while (!sherpaConnected) {
                    sherpaConnected = ping(controller);
                    if (!sherpaConnected) {
                        logger.log(Level.INFO, "Sherpa did not respond to ping, retrying in "+ stepMillis + " milliseconds");
                        Thread.sleep(stepMillis); // This will be interrupted if a timeout occurs
                    }
                }
                return client;
            }
        };
        Future<SherpaClient> futureSherpaClient = executor.submit(callable);
        try {
            return futureSherpaClient.get(timeout.getAmount(), timeout.getUnit());
        } catch (Exception ex) {
            throw new RuntimeException("Cannot find Sherpa!");
        } finally {
            executor.shutdown();
        }
    }
}