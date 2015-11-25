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

import cfa.vo.interop.SAMPController;
import cfa.vo.sherpa.SherpaClient;
import java.util.logging.Logger;
import org.astrogrid.samp.client.SampException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Abstract integration test class for tests that need SAMP/sherpa-samp 
 * to run. This class will verify that there is a SAMP Hub up and running, and, 
 * if necessary, that sherpa-samp is connected. Only tests that use SAMP/sherpa
 * should extend this class.
 */
public class AbstractSAMPTest {
    
    private final Logger logger = Logger.getLogger(AbstractSAMPTest.class.getName());
    
    private final int SAMP_CONN_RETRIES = 3;
    
    protected SAMPController controller;
    private int TIMEOUT;
    private String controller_name;
    protected SherpaClient client;
    
    public AbstractSAMPTest() {
	this.controller_name = "Iris Test Controller";
    }
    
    public AbstractSAMPTest(int timeout, String controller_name) {
	this.controller_name = controller_name;
    }
    
    
//    /**
//     * This rule allows the component loader to be initialized only once for the whole suite.
//     * This works around the fact that @BeforeClass and @AfterClass methods need to be static.
//     */
//    @Rule
//    public ExternalResource resource = new ExternalResource() {
//        @Override
//        protected void before() throws Exception {
//            // asserts that the SAMP Hub is up, and that sherpa-samp is connected.
//	    connectToSAMPHub();
//	    isSherpaConnected();
//        }
//        @Override
//        protected void after() {
//            // disconnect controller
//	    controller.stop();
//        }
//    };
    
    // forces the concrete classes to make their own before class, and it will 
    // not override this Before class
    @Before
    public final void setUp() throws Exception {
    
	// asserts that the SAMP Hub is up, and that sherpa-samp is connected.
	connectToSAMPHub();
	isSherpaConnected();
    }
    
    @After
    public void tearDown() {
	controller.stop();
    }
    
    public void connectToSAMPHub() throws InterruptedException, Exception {
	
	// setup the SAMP controller
	controller = new SAMPController(
		this.controller_name, 
		this.controller_name, 
		this.getClass().getResource("/tools_tiny.png").toString()
	);
	controller.setAutoRunHub(false);
        controller.start(false);
	Thread.sleep(2000); // wait 2 seconds for connection
	
	// If the controller doesn't connect after 2 seconds, add additional
	// wait time.
	int count = 0;
        while (!controller.isConnected()) {
            if (++count > SAMP_CONN_RETRIES) {
                String msg = "Failed to connect to SAMP, failing Unit tests";
                logger.info(msg);
                fail(msg);
            }
            logger.info("waiting connection");
            Thread.sleep(1000);
        }
        assertTrue(controller.isConnected());
    }
    
    
    public void isSherpaConnected() throws SampException {
        this.client = SherpaClient.create(controller);
        assertTrue(SherpaClient.ping(controller));
    }
}
