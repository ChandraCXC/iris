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
public class SedStackerStackerTest {
    
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
    
    SedStackerStackPayload payload;
    
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
    public void testStackAvg() throws Exception {
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
	
	payload = (SedStackerStackPayload) SAMPFactory.get(SedStackerStackPayload.class);
	
	// Setup the stack payload
	payload.addSegment(segment1);
        payload.addSegment(segment2);
        payload.addSegment(segment3);
	payload.setBinsize(2.0);
	payload.setLogBin(false);
	payload.setSmooth(false);
	payload.setSmoothBinsize(5.0);
	payload.setStatistic("avg");

	// Setup and send SAMP message
	SAMPMessage message = SAMPFactory.createMessage("stack.stack", payload, SedStackerStackPayload.class);

	SherpaClient client = new SherpaClient(controller);

	Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
	if (client.isException(rspns)) {
	    Exception ex = client.getException(rspns);
	    throw ex;
	}
	
	SedStackerStackPayload response = (SedStackerStackPayload) SAMPFactory.get(rspns.getResult(), SedStackerStackPayload.class);
	
	// get response values
        SegmentPayload seg = response.getSegments().get(0);
	double[] controlY = new double[] {5.0, 5.36666667, 4.5, 2.66666667, 4.0, 6.5, 1.5, 10.5, 5.0, 10.0};
	double[] controlX = new double[] {   0.,    2.,    4.,    6.,    8.,   10.,   16.,   22.,   50.,  100.};
	double[] controlYerr = new double[] {0.5, 1.5033629, 0.72801099, 0.54313902, 0.4, 1.44308697, 0.15, 1.05, 0.5, 1.0};
	double[] controlCounts = new double[] {1, 3, 2, 3, 1, 3, 1, 1, 1, 1};
	
	assertEquals(response.getSegments().size(),1);
	
	// tests
        for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlY[i], seg.getY()[i], 0.00001);
        }
	for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlX[i], seg.getX()[i], 0.00001);
        }
	for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlYerr[i], seg.getYerr()[i], 0.00001);
        }
	for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlCounts[i], seg.getCounts()[i], 0.00001);
        }
	
	controller.stop();
    }
    
    @After
    public void tearDown() {
    }
    
}
