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

import cfa.vo.interop.PingMessage;
import cfa.vo.iris.interop.AbstractSedMessageHandler;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.sed.ExtSed;
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
import org.astrogrid.samp.Client;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.ResultHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.astrogrid.samp.Response;

/**
 *
 * @author olaurino
 */
public class SherpaSmokeTest extends AbstractSmokeTest {

    private String testVotable;
    private SedSAMPController controller;
    private boolean working = false;
    protected Boolean control;
//    private SherpaSamp sherpa;
//    private String sherpaDirS;

    public SherpaSmokeTest(String testVotable) {
        this(testVotable, 20);
    }

    public SherpaSmokeTest(String testVotable, int timeout) {

        super(timeout);

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

//            sherpaDirS = System.getProperty("IRIS_DIR") + "/lib/sherpa";
//
//            File sherpaDir = new File(sherpaDirS);
//
//            boolean isSherpaDir = false;
//
//            //Verify sherpaDir contains sherpa
//            for (File f : sherpaDir.listFiles()) {
//                if (f.getName().equals("startsherpa.py")) {
//                    isSherpaDir = true;
//                }
//            }
//
//            check(isSherpaDir, "The directory does not contain Sherpa");

            //Verify we can read test file.
            File testFile = new File(testVotable);
            check(testFile.canRead(), "Can't read file " + testVotable);

            //Start a SAMPController
            controller = new SedSAMPController("TestController", "An SED builder from the Virtual Astronomical Observatory", this.getClass().getResource("/iris_button_tiny.png").toString());
            controller.startWithResourceServer("/test", false);

            Thread.sleep(2000);//give the controller some time to settle down

            log("Waiting for the SAMP controller...");
            waitUntil(controller, "isConnected", "SAMP controller never connected!");

            //Start Sherpa
//            log("Starting Sherpa...");
//            sherpa = new SherpaSamp();
//            sherpa.start();
//
            Thread.sleep(TIMEOUT*1000);//sherpa needs some time to connect to the hub

            //check that sherpa can be pinged
            control = Boolean.FALSE;
            log("Pinging Sherpa...");
            controller.sendMessage(new PingMessage(), new PingResultHandler(), this.TIMEOUT);
            waitUntil("control", true, "Sherpa didn't respond to ping");//give sherpa TIMEOUT seconds to reply

            //Import the file using SedImporter
            log("Creating a Setup for the SED Builder...");
            SetupBean conf = createSetupBean(testFile);

            log("Importing the file...");
            List<Segment> segments = SegmentImporter.getSegments(conf);

            ExtSed sed = new ExtSed("testSed");
            sed.addSegment(segments);

            //Setup a client that handles SEDs
            log("Setting up a SAMP SED receiver...");
            SedSAMPController mockReceiver = new SedSAMPController("MockReceiver", "An SED builder from the Virtual Astronomical Observatory", this.getClass().getResource("/iris_button_tiny.png").toString());

            mockReceiver.start(false);
            mockReceiver.setAutoRunHub(false);

            log("Waiting for the SAMP SED receiver...");
            waitUntil(mockReceiver, "isConnected", "SAMP SED receiver never connected!");

            mockReceiver.addMessageHandler(new SmokeSedHandler());

            control = Boolean.FALSE;

            //Send the Sed
            log("Broadcasting the SED...");
            controller.sendSedMessage(sed);

            waitUntil("control", Boolean.TRUE, "It looks like the SED wasn't processed");//give the receiver TIMEOUT seconds to reply

            log("Preparing the Sherpa call...");
            double[] x = sed.getSegment(0).getSpectralAxisValues();
            double[] y = sed.getSegment(0).getFluxAxisValues();

            double[] err = (double[]) sed.getSegment(0).getCustomDataValues("Spectrum.Data.FluxAxis.Accuracy.StatError");

            SherpaClient c = new SherpaClient(controller);

            Data data = c.createData("test");

            data.setX(x);
            data.setY(y);
            data.setStaterror(err);

            AbstractModel m1 = c.createModel(Models.PowerLaw1D);

            c.getParameter(m1, "ref").setVal(5000.);
            c.getParameter(m1, "ampl").setVal(1.0);
            c.getParameter(m1, "gamma").setVal(-0.5);

            CompositeModel cm = c.createCompositeModel("m1", m1);

            Stat s = Stats.LeastSquares;

            Method method = c.getMethod(OptimizationMethod.NelderMeadSimplex);

            log("Calling Sherpa and waiting for results...");
            FitResults fr = c.fit(data, cm, s, method);

            log("Verifying Sherpa response...");
            Assert.assertEquals(Boolean.TRUE, fr.getSucceeded());

            log("Running sample model integration...");

            List<PassBand> pbs = new ArrayList();

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

            AbstractModel p1 = c.createModel(Models.PowerLaw1D, "p1");
            Parameter gamma = c.getParameter(p1, "gamma");
            gamma.setFrozen(0);
            gamma.setVal(0.0);

            Parameter ref = c.getParameter(p1, "ref");
//            ref.setFrozen(0);
            ref.setVal(1.0);

            Parameter ampl = c.getParameter(p1, "ampl");
            ampl.setFrozen(0);
            ampl.setVal(1.0);

            AbstractModel p2 = c.createModel(Models.PowerLaw1D, "p2");
            gamma = c.getParameter(p2, "gamma");
            gamma.setFrozen(0);
            gamma.setVal(0.0);

            ref = c.getParameter(p2, "ref");
//            ref.setFrozen(0);
            ref.setVal(1.0);

            ampl = c.getParameter(p2, "ampl");
            ampl.setFrozen(0);
            ampl.setVal(5.0);



            SherpaIntegrator integrator = new SherpaIntegrator(controller);

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
            Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
//        catch (Exception ex) {
//            Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
//        }
    }

    @Override
    protected void exit() {

        if (controller != null) {
            controller.stop();
        }

//        if (sherpa != null) {
//            try {
//                sherpa.shutdown();
//                Thread.sleep(2000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        String message = working ? "Everything seems to be working!" : "OOPS! Something went wrong!";

        System.out.println();
        System.out.println("===============================");
        System.out.println(message);
        System.out.println("===============================");

        if(!working) {
            log("Something went wrong. If a timeout occurred, try re-running the test with a longer timeout (e.g. iris smoketest 20).");
            log("If the Smoke Test is not working, your system may not be supported, or you have downloaded"
                    + " a distribution that does not match your Operating System.");
//            log("\n\nChecking Architecture");
//            try {
//                checkArch();
//            } catch (Exception ex1) {
//                Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex1);
//            }
//            Assert.fail();
        }


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

    private class PingResultHandler implements ResultHandler {

        @Override
        public void result(Client client, Response rspns) {
            log(client.getMetadata().getName() + " response status: " + rspns.getStatus());
            if (client.getMetadata().getName().toLowerCase().equals("sherpa")) {
                control = Boolean.TRUE;
            }
        }

        @Override
        public void done() {
        }
    }

//    private void checkArch() throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder("file", sherpaDirS+"/bin/python2.6");
//
//        Process p = pb.start();
//
//        InputStream is = p.getInputStream();
//
//        p.waitFor();
//
//        String s = new Scanner(is).useDelimiter("\\A").next().toLowerCase();
//
//        String arch = System.getProperty("os.arch").toLowerCase();
//
//        if(arch.equals("amd64"))
//            arch = "x86_64";
//
//        String os = System.getProperty("os.name").toLowerCase();
//
//        if(os.equals("mac os x"))
//            os = "mach-o";
//
//        if(s.contains(os)) {
//            if(!(s.contains(arch) || s.contains(arch.replaceAll("_", "-"))))
//                Logger.getLogger("").log(Level.SEVERE, "\nIris may be installed for the wrong architecture. However, Iris could still work, so the test will continue...");
//        } else {
//            Logger.getLogger("").log(Level.SEVERE, "\nIt seems like you installed Iris for the wrong Operating System. However, the test will continue, since there is the unlikely possibility"
//                    + " that Iris will work anyway or that there was an error probing the running operating system.");
//        }
//
//        log("\nOperating system: "+os);
//        log("Architecture: "+arch);
//        log("Python Executable: "+s);
//    }

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
