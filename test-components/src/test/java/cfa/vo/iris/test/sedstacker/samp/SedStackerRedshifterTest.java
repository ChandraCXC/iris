/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker.samp;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbudynk
 */
public class SedStackerRedshifterTest {
    
    double[] x1;
    double[] y1;
    double[] yerr1;
    double[] x2;
    double[] y2;
    double[] yerr2;
    double[] x3;
    double[] y3;
    double[] yerr3;
    
    SegmentPayload segment1;
    SegmentPayload segment2;
    SegmentPayload segment3;
    
    SedStackerRedshiftPayload payload;
    
    private SAMPController controller;
    
    @Before
    public void setUp() {
	x1 = new double[]{1, 5, 10, 15, 50, 100};
	y1 = new double[] {0.1, 0.5, 1.0, 1.5, 5.0, 10.0};
	yerr1 = new double[] {0.01, 0.05, 0.1, 0.15, 0.5, 1.0};

	x2 = new double[]{2, 4, 5, 8, 10};
	y2 = new double[]{1, 2, 3, 4, 5};
	yerr2 = new double[]{0.1, 0.2, 0.3, 0.4, 0.5};

	x3 = new double[]{0.5, 1.5, 3.0, 5.0, 10.5, 21.0};
	y3 = new double[]{5.0, 15.0, 7.0, 4.5, 13.5, 10.5};
	yerr3 = new double[]{0.5, 1.5, 0.7, 0.45, 1.35, 1.05};

	segment1 = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	segment1.setX(x1);
	segment1.setY(y1);
	segment1.setYerr(yerr1);
	segment1.setZ(0.1);

	segment2 = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	segment2.setX(x2);
	segment2.setY(y2);
	segment2.setYerr(yerr2);
	segment2.setZ(0.2);

	segment3 = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	segment3.setX(x3);
	segment3.setY(y3);
	segment3.setYerr(yerr3);
	segment3.setZ(0.3);
    }
    
    @Test
    public void testRedshift() throws Exception {
	// Start the SAMP controller
	controller = new SedSAMPController("SEDStacker", "SEDStacker", null);
        controller.setAutoRunHub(false);
        controller.start(false);

	Thread.sleep(2000);
	System.out.println();

	while (!controller.isConnected()) {
	    System.out.println("waiting connection");
	    Thread.sleep(1000);
	}
	
	payload = (SedStackerRedshiftPayload) SAMPFactory.get(SedStackerRedshiftPayload.class);
	
	// Setup the stack payload
	payload.addSegment(segment1);
        payload.addSegment(segment2);
        payload.addSegment(segment3);
	payload.setCorrectFlux(false);
	payload.setZ0(0.0);

	// Setup and send SAMP message
	SAMPMessage message = SAMPFactory.createMessage("stack.redshift", payload, SedStackerRedshiftPayload.class);

	SherpaClient client = new SherpaClient(controller);

	Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
	if (client.isException(rspns)) {
	    Exception ex = client.getException(rspns);
	    throw ex;
	}
	
	SedStackerRedshiftPayload response = (SedStackerRedshiftPayload) SAMPFactory.get(rspns.getResult(), SedStackerRedshiftPayload.class);
	
	// get response values
        SegmentPayload seg = response.getSegments().get(0);
	SegmentPayload seg2 = response.getSegments().get(1);
	double[] controlY1 = new double[] { 0.1, 0.5, 1.0, 1.5, 5.0, 10.0};
	double[] controlX1 = new double[] {0.90909091, 4.54545455, 9.09090909, 13.63636364, 45.45454545, 90.90909091};
	double[] controlYerr1 = new double[] {0.01, 0.05, 0.1, 0.15, 0.5, 1.0};
	double[] controlX2 = new double[] {1.66666667, 3.33333333, 4.16666667, 6.66666667, 8.33333333};
	
	assertEquals(response.getSegments().size(), 3);
	
	// tests
        for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlY1[i], seg.getY()[i], 0.00001);
        }
	for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlX1[i], seg.getX()[i], 0.00001);
        }
	for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlYerr1[i], seg.getYerr()[i], 0.00001);
        }
	for (int i = 0; i < seg2.getX().length; i++) {
            assertEquals(controlX2[i], seg2.getX()[i], 0.00001);
        }
	
	controller.stop();
    }
    
    @After
    public void tearDown() {
    }
    
}
