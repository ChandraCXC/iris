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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.smoketest;

import cfa.vo.interop.SAMPServiceBuilder;
import cfa.vo.interop.SampService;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.iris.interop.AbstractSedMessageHandler;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import cfa.vo.utils.Default;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.builder.photfilters.EnergyBin;
import cfa.vo.sed.builder.photfilters.PassBand;
import cfa.vo.sed.science.integration.SherpaIntegrator;
import cfa.vo.sed.science.integration.SimplePhotometryPoint;
import cfa.vo.sed.setup.ErrorType;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.*;
import junit.framework.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SherpaSmokeTest extends AbstractSmokeTest {

    private String testVotable;
    private SampService sampService;
    private boolean working = false;
    protected Boolean control;

    public SherpaSmokeTest(String testVotable, IrisApplication app) {
        this(testVotable, 60, app);
    }

    public SherpaSmokeTest(String testVotable, int timeout, IrisApplication app) {
        super(timeout);
        this.sampService = app.getSampService();

        this.testVotable = testVotable;

        Logger.getLogger("").setLevel(Level.OFF);

    }

    public void runTest() throws Exception {
        try {

            log("\n\n\nThis test will assess whether your installation is capable of performing some basic operations\n"
                    + "The test should take less than a minute. However, the actual time will depend on your system properties.\n\n");

            log("\n\n========================================");
            log("Starting Smoke Test with timeout: "+TIMEOUT);
            log("========================================");

            //Verify we can read test file.
            log("Testing file read...");
            File testFile = new File(testVotable);
            check(testFile.canRead(), "Can't read file " + testVotable);

            //check that sherpa can be pinged
            log("Pinging Sherpa...");
            SherpaClient c = new SherpaClient(sampService);
            waitUntil(c, "ping", "Cannot find Sherpa!");

            //Import the file using SedImporter
            log("Creating a Setup for the SED Builder...");
            SetupBean conf = createSetupBean(testFile);

            log("Importing the file...");
            List<Segment> segments = SegmentImporter.getSegments(conf);

            ExtSed sed = new ExtSed("testSed");
            sed.addSegment(segments);

            //Setup a client that handles SEDs
            log("Setting up a SAMP SED receiver...");
            SampService mockReceiver = new SAMPServiceBuilder("MockReceiver").build();
            mockReceiver.start();

            waitUntil(mockReceiver, "isSampUp", "Receiver never connected to the Hub");

            mockReceiver.addMessageHandler(new SmokeSedHandler());

            control = Boolean.FALSE;

            //Send the Sed
            log("Broadcasting the SED...");
            sed.sendSedMessage(sampService);

            waitUntil("control", Boolean.TRUE, "It looks like the SED wasn't processed");

            log("Preparing the Sherpa call...");
            double[] x = sed.getSegment(0).getSpectralAxisValues();
            double[] y = sed.getSegment(0).getFluxAxisValues();
            double[] err = (double[]) sed.getSegment(0).getCustomDataValues("Spectrum.Data.FluxAxis.Accuracy.StatError");

            Data data = c.createData("test");

            data.setX(x);
            data.setY(y);
            data.setStaterror(err);

            FitConfiguration fit = new FitConfiguration();

            Model m1 = c.createModel("powerlaw");
            m1.findParameter("refer").setVal(5000.);
            m1.findParameter("ampl").setVal(1.0);
            m1.findParameter("index").setVal(-0.5);
            CompositeModel cm = c.createCompositeModel("m1", m1);
            fit.setModel(cm);
            fit.setStat(Statistic.LeastSquares);
            fit.setMethod(OptimizationMethod.NelderMeadSimplex);

            log("Calling Sherpa and waiting for results...");
            FitResults fr = c.fit(data, fit);

            log("Verifying Sherpa response...");
            Assert.assertEquals(Boolean.TRUE, fr.getSucceeded());

            log("Running sample model integration...");

            List<PassBand> pbs = new ArrayList<>();

            EnergyBin b1 = new EnergyBin();
            EnergyBin b2 = new EnergyBin();

            b1.setMin(1000.0);
            b1.setMax(3000.0);
            b1.setUnits("Angstrom");


            b2.setMin(4000.0);
            b2.setMax(6000.0);
            b2.setUnits("Angstrom");

            pbs.add(b2);
            pbs.add(b1);

            Model p1 = c.createModel("powerlaw", "p1");
            Parameter gamma = p1.findParameter("index");
            gamma.setFrozen(0);
            gamma.setVal(0.0);

            Parameter ref = p1.findParameter("refer");
//            ref.setFrozen(0);
            ref.setVal(1.0);

            Parameter ampl = p1.findParameter("ampl");
            ampl.setFrozen(0);
            ampl.setVal(1.0);

            Model p2 = c.createModel("powerlaw", "p2");
            gamma = p2.findParameter("index");
            gamma.setFrozen(0);
            gamma.setVal(0.0);

            ref = p2.findParameter("refer");
//            ref.setFrozen(0);
            ref.setVal(1.0);

            ampl = p2.findParameter("ampl");
            ampl.setFrozen(0);
            ampl.setVal(5.0);



            SherpaIntegrator integrator = new SherpaIntegrator(c, Default.getInstance().getUnitsManager());

            //Some constants we need for conversions
            double H = 6.62620E-27;
            double C = 2.997925E+18;
            double HC = H*C;
            double rb2 = b2.getMax()/b2.getMin();
            double rb1 = b1.getMax()/b1.getMin();

            cm = c.createCompositeModel("p1+p2", p1, p2);
            cfa.vo.sed.science.integration.Response response = integrator.integrateComponents(pbs, cm, null, 10000);
            for (SimplePhotometryPoint point : response.getPoints()) {
                if (point.getId().equals("1000.0-3000.0 Angstrom")) {
                    Assert.assertEquals(2000., point.getWavelength());
                    Assert.assertEquals(Math.log(rb1*5.0*rb2)*HC, point.getFlux(), 0.001);
                }
            }

            cm = c.createCompositeModel("p1", p1);
            response = integrator.integrateComponents(pbs, cm, null, 10000);
            for (SimplePhotometryPoint point : response.getPoints()) {
                if (point.getId().equals("1000.0-3000.0 Angstrom")) {
                    Assert.assertEquals(2000., point.getWavelength());
                    Assert.assertEquals(Math.log(rb1)*HC, point.getFlux(), 0.001);
                }
            }

            cm = c.createCompositeModel("p2", p2);
            response = integrator.integrateComponents(pbs, cm, null, 10000);
            for (SimplePhotometryPoint point : response.getPoints()) {
                if (point.getId().equals("4000.0-6000.0 Angstrom")) {
                    Assert.assertEquals(5000., point.getWavelength());
                    Assert.assertEquals(Math.log(rb2*5.0)*HC, point.getFlux(), 0.001);
                }
            }

            working = true;

        } catch (Throwable ex) {
            working = false;
            throw ex;
        }
    }

    @Override
    protected int exit() {

        if (sampService != null) {
            sampService.shutdown();
        }

        String message = working ? "Everything seems to be working!" : "OOPS! Something went wrong!";

        System.out.println();
        System.out.println("===============================");
        System.out.println(message);
        System.out.println("===============================");

        if(!working) {
            log("Something went wrong. If a timeout occurred, try re-running the test with a longer timeout (e.g. iris smoketest 20).");
            log("If the Smoke Test is not working, your system may not be supported, or you have downloaded"
                    + " a distribution that does not match your Operating System.");
        }

        return working? 0 : 1;

    }

    private SetupBean createSetupBean(File testFile) {
        SetupBean conf = new SetupBean();

        conf.setErrorType(ErrorType.SymmetricColumn.name());
        conf.setSymmetricErrorColumnNumber(7);
        conf.setFileLocation("file://" + testFile.getAbsolutePath());
        conf.setFormatName("VOTABLE");
        conf.setPublisher("NED");
        conf.setTargetName("3c273");
        conf.setTargetRa("187.27791798");
        conf.setTargetDec("2.05238729");
        conf.setXAxisColumnNumber(5);
        conf.setYAxisColumnNumber(6);
        conf.setXAxisQuantity("FREQUENCY");
        conf.setXAxisUnit("HERTZ");
        conf.setYAxisQuantity("FLUXDENSITY");
        conf.setYAxisUnit("FLUXDENSITYFREQ1");

        return conf;
    }

    private class SmokeSedHandler extends AbstractSedMessageHandler {

        @Override
        public void processSed(Sed sed, String sedId) {
            try {
                check(sed.getNumberOfSegments() == 1, "Wrong Number of Segments in the sed");
                log("Sed received, and it looks good!");
                control = Boolean.TRUE;
            } catch (Exception ex) {
                Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
