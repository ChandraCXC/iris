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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.science.interpolation;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.Param;
import cfa.vo.sherpa.SherpaClient;
import java.util.Arrays;
import org.astrogrid.samp.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import spv.spectrum.SEDMultiSegmentSpectrum;

/**
 *
 * @author jbudynk
 */
public class SherpaRedshifterTest {
    
    private SAMPController controller;
    private static String REDSHIFT_MTYPE = "spectrum.redshift.calc";
    
    public SherpaRedshifterTest() {
	
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    //@Test
    public void testShift() throws Exception {
	
	// This test is to make sure that the SEDs are redshifted correctly,
	// including that the flux errors are sorted along with their 
	// corresponding (x, y) points
	
	System.out.println("To run this test, you need a SAMP Hub running with Sherpa-SAMP connected.");
	
	double[] y = new double[]{
	    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30
	};
	double[] yerr = new double[]{
	    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30
	};
	double[] x = new double[]{
	    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
	};
	
	
	// Start the SAMP controller
	controller = new SedSAMPController("SEDStacker", "SEDStacker", this.getClass().getResource("/tools_tiny.png").toString());
        controller.setAutoRunHub(false); //set to 'true' when I figure out how to start Sherpa in the test...
        controller.start(false);

	Thread.sleep(2000);
	System.out.println();

	while (!controller.isConnected()) {
	    System.out.println("waiting connection");
	    Thread.sleep(1000);
	}
	
//	ExtSed inputSed = ExtSed.flatten(sed, "Angstrom", "Jy");
        
        RedshiftPayload payload = (RedshiftPayload) SAMPFactory.get(RedshiftPayload.class);
        payload.setX(x);
        payload.setY(y);
	payload.setYerr(yerr);
        payload.setFromRedshift(1);
        payload.setToRedshift(0);
        SAMPMessage message = SAMPFactory.createMessage(REDSHIFT_MTYPE, payload, RedshiftPayload.class);

	SherpaClient client = new SherpaClient(controller);
	
        Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        RedshiftPayload response = (RedshiftPayload) SAMPFactory.get(rspns.getResult(), RedshiftPayload.class);
	
	double[] controlYerr = new double[]{
	    1, 21, 11, 2, 22, 12, 3, 23, 13, 4, 24, 14, 25, 15, 5, 16, 6, 26, 17, 7, 27, 18, 8, 28, 29, 19, 9, 20, 10, 30
	};
	
	// tests
	// Make sure flux errors are sorted correctly with the SED points.
        for (int i = 0; i < response.getY().length; i++) {
            assertEquals(controlYerr[i], response.getYerr()[i], 0.00001);
        }
	
	controller.stop();
	
    }
    
    
    
}
