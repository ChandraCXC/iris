/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
 
package cfa.vo.iris.test.unit.it;

import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.sherpa.SherpaClient;
import java.util.logging.Logger;
import org.astrogrid.samp.client.SampException;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExternalResource;

/**
 * Abstract integration test class for tests that need SAMP/sherpa-samp 
 * to run. This class will verify that there is a SAMP Hub up and running, and, 
 * if necessary, that sherpa-samp is connected. Only tests that use SAMP/sherpa
 * should extend this class.
 */
public class AbstractSAMPTest {
    
    private final Logger logger = Logger.getLogger(AbstractSAMPTest.class.getName());
    
    private final int SAMP_CONN_RETRIES = 3;
    
    protected SedSAMPController controller;
    private String controller_name;
    protected SherpaClient client;
    
    public AbstractSAMPTest() {
        this.controller_name = "Iris Test Controller";
    }
    
    public AbstractSAMPTest(String controller_name) {
        this.controller_name = controller_name;
    }
    
    
    /**
     * Create before/after methods for each test class that extends this class.
     * Setup/teardown SedSAMPControllers after each test.
     * 
     * Note that in the future we can implement the ExternalResource interface
     * for various kinds of tests (NED Service, SAMP, Sherpa, etc.) in 
     * independent classes. Then a test that needs multiple setup/teardown 
     * methods can just instantiate the necessary Rules.
     */
    @Rule
    public ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Exception {
            // asserts that the SAMP Hub is up, and that sherpa-samp is connected.
            connectToSAMPHub();
            isSherpaConnected();
        }
        @Override
        protected void after() {
            // disconnect controller
            controller.stop();
        }
    };
    
    /*
    * Creates a SedSAMPController and connects it to a SAMP Hub. Fails if
    * it does not connect to a SAMP Hub.
    */
    public void connectToSAMPHub() throws InterruptedException, Exception {
	
	// setup the SAMP controller
    controller = new SedSAMPController(
            this.controller_name, 
            this.controller_name, 
            this.getClass().getResource("").toString()
	);
    connectToSAMPHub(controller);
    }

    /*
    * Connects a SedSAMPController to a SAMP Hub. Fails if it does not connect to a 
    * SAMP Hub.
    */
    public void connectToSAMPHub(SedSAMPController controller) throws InterruptedException, Exception {
        controller.setAutoRunHub(false);
        controller.start(false);

        // Wait SAMP_CONN_RETRIES for the controller to connect to the SAMP hub.
        int count = 0;
        while (!controller.isConnected()) {
            if (++count > SAMP_CONN_RETRIES) {
                String msg = "Failed to connect to SAMP, failing tests";
                logger.info(msg);
                fail(msg);
            }
            logger.info("waiting connection");
            Thread.sleep(1000);
        }
        assertTrue(controller.isConnected());
    }
    

    /*
    * Checks if Sherpa is connected to the SAMP hub
    */    
    public void isSherpaConnected() throws SampException, InterruptedException {
	
        this.client = new SherpaClient(this.controller);
        // Give Sherpa some time to connect to SAMP Hub
        // TODO: do this smarter, like in connectToSAMPHub()
        Thread.sleep(SAMP_CONN_RETRIES*1000);
        assertTrue(!this.client.findSherpa().isEmpty());
    }
}
