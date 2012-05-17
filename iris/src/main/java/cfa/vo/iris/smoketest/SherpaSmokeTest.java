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
import cfa.vo.sed.setup.ErrorType;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.AbstractModel;
import cfa.vo.sherpa.CompositeModel;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.Method;
import cfa.vo.sherpa.Models;
import cfa.vo.sherpa.OptimizationMethod;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.Stat;
import cfa.vo.sherpa.Stats;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.astrogrid.samp.Client;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.ResultHandler;
import spv.fit.StartSherpa;

/**
 *
 * @author olaurino
 */
public class SherpaSmokeTest extends AbstractSmokeTest {

    private String testVotable;
    private SedSAMPController controller;
    private boolean working = false;
    protected Boolean control;
    private StartSherpa sherpa;
    private String sherpaDirS;

    public SherpaSmokeTest(String testVotable) {
        this(testVotable, 10);
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

            sherpaDirS = System.getProperty("IRIS_DIR") + "/lib/sherpa";

            File sherpaDir = new File(sherpaDirS);

            boolean isSherpaDir = false;

            //Verify sherpaDir contains sherpa
            for (File f : sherpaDir.listFiles()) {
                if (f.getName().equals("startsherpa.py")) {
                    isSherpaDir = true;
                }
            }

            check(isSherpaDir, "The directory does not contain Sherpa");

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
            log("Starting Sherpa...");
            sherpa = new StartSherpa();
            sherpa.start();

            Thread.sleep(10000);//sherpa needs some time to connect to the hub

            //check that sherpa can be pinged
            control = Boolean.FALSE;
            log("Pinging Sherpa...");
            controller.sendMessage(new PingMessage(), new PingResultHandler(), 10);
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

            working = true;

        } catch (RuntimeException ex) {
            Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void exit() {

        if (controller != null) {
            controller.stop();
        }

        if (sherpa != null) {
            try {
                sherpa.shutdown();
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String message = working ? "Everything seems to be working!" : "OOPS! Something went wrong!";

        System.out.println();
        System.out.println("===============================");
        System.out.println(message);
        System.out.println("===============================");

        if(!working) {
            log("Something went wrong. If a timeout occurred, try re-running the test with a longer timeout (e.g. ./smoketest 20).");
            log("If the Smoke Test is not working, your system may not be supported, or you have downloaded"
                    + " a distribution that does not match your Operating System.");
            log("\n\nChecking Architecture");
            try {
                checkArch();
            } catch (Exception ex1) {
                Logger.getLogger(SherpaSmokeTest.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Assert.fail();
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

    private void checkArch() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("file", sherpaDirS+"/bin/python2.6");

        Process p = pb.start();

        InputStream is = p.getInputStream();

        p.waitFor();

        String s = new Scanner(is).useDelimiter("\\A").next().toLowerCase();

        String arch = System.getProperty("os.arch").toLowerCase();

        if(arch.equals("amd64"))
            arch = "x86_64";

        String os = System.getProperty("os.name").toLowerCase();

        if(os.equals("mac os x"))
            os = "mach-o";

        if(s.contains(os)) {
            if(!(s.contains(arch) || s.contains(arch.replaceAll("_", "-"))))
                Logger.getLogger("").log(Level.SEVERE, "\nIris may be installed for the wrong architecture. However, Iris could still work, so the test will continue...");
        } else {
            Logger.getLogger("").log(Level.SEVERE, "\nIt seems like you installed Iris for the wrong Operating System. However, the test will continue, since there is the unlikely possibility"
                    + " that Iris will work anyway or that there was an error probing the running operating system.");
        }

        log("\nOperating system: "+os);
        log("Architecture: "+arch);
        log("Python Executable: "+s);
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
