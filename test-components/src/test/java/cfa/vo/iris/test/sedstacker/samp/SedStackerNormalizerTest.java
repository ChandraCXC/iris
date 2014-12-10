/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker.samp;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.test.sedstacker.NormConfig;
import cfa.vo.iris.test.sedstacker.Stack;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.SherpaClient;
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
public class SedStackerNormalizerTest {
    
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
    
    SedStackerNormalizePayload payload;
    
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
    
    @After
    public void tearDown() {
    }

    @Test
    public void testNormalize() throws Exception {
	
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
	
	payload = (SedStackerNormalizePayload) SAMPFactory.get(SedStackerNormalizePayload.class);
	
	// Setup the normalization payload
	payload.addSegment(segment1);
        payload.addSegment(segment2);
        payload.addSegment(segment3);
	
	payload.setNormOperator(0);
	payload.setY0(1.0);
	payload.setXmin("MIN");
	payload.setXmax("MAX");
	payload.setStats("avg");
	payload.setIntegrate(Boolean.TRUE);

	// Setup and send SAMP message
	SAMPMessage message = SAMPFactory.createMessage("stack.normalize", payload, SedStackerNormalizePayload.class);

	SherpaClient client = new SherpaClient(controller);

	Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
	if (client.isException(rspns)) {
	    Exception ex = client.getException(rspns);
	    throw ex;
	}
	
	SedStackerNormalizePayload response = (SedStackerNormalizePayload) SAMPFactory.get(rspns.getResult(), SedStackerNormalizePayload.class);
	
	// get response values
        double[] resy1 = response.getSegments().get(0).getY();
	double[] resy2 = response.getSegments().get(1).getY();
	Double resnorm3 = response.getSegments().get(2).getNormConstant();
	SegmentPayload seg1 = response.getSegments().get(0);
	Double z = seg1.getZ();
	Double norm = seg1.getNormConstant();
	
	assertEquals(response.getSegments().size(),3);
	
	// tests
        for (int i = 0; i < resy1.length; i++) {
            assertEquals(0.49234923 * y1[i], resy1[i], 0.00001);
        }
	for (int i = 0; i < resy2.length; i++) {
            assertEquals(9.846 * y2[i], resy2[i], 0.00001);
        }
	assertEquals(1.1529274, resnorm3, 0.00001);
	
	controller.stop();
    }
    
}
