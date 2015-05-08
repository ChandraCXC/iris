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
package cfa.vo.sed.science.stacker;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.sed.ExtSed;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.COUNTS;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.SherpaClient;
import java.util.ArrayList;
import java.util.List;
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
    private SedStackerStacker stacker;
    
    @Before
    public void setUp() {
	x1 = new double[]{5, 1, 10, 15, 50, 100};
	y1 = new double[] {0.5, 0.1, 1.0, 1.5, 5.0, 10.0};
	yerr1 = new double[] {0.05, 0.01, 0.1, 0.15, 0.5, 1.0};

	x2 = new double[]{2, 4, 5, 8, 10};
	y2 = new double[]{1, 2, 3, 4, 5};
	yerr2 = new double[]{0.1, 0.2, 0.3, 0.4, 0.5};

	x3 = new double[]{0.5, 3.0, 1.5, 5.0, 10.5, 21.0};
	y3 = new double[]{5.0, 7.0, 15.0, 4.5, 13.5, 10.5};
	yerr3 = new double[]{0.5, 0.7, 1.5, 0.45, 1.35, 1.05};

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
    
    //@Test
    public void testStackAvg() throws Exception {
	// Start the SAMP controller
	controller = new SedSAMPController("SEDStacker", "SEDStacker", this.getClass().getResource("/tools_tiny.png").toString());
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
	//double[] controlYerr = new double[] {0.5, 0.501120965, 0.3640055, 0.18104634, 0.4, 0.48102899, 0.15, 1.05, 0.5, 1.0}; // calculated from errors [sqrt(sum(errors^2))/N]
	double[] controlYerr = new double[] {0, 6.82169741, 2.5, 1.64991582, 0., 5.21216526, 0., 0., 0., 0.}; // calculated from stddev(flux)
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
    
    //@Test
    public void testStacker() throws Exception {
	// Start the SAMP controller
	controller = new SedSAMPController("SEDStacker", "SEDStacker", this.getClass().getResource("/tools_tiny.png").toString());
        controller.setAutoRunHub(false);
        controller.start(false);

	Thread.sleep(2000);
	System.out.println();

	while (!controller.isConnected()) {
	    System.out.println("waiting connection");
	    Thread.sleep(1000);
	}
	
	ExtSed sed1 = new ExtSed("Sed1");
	ExtSed sed2 = new ExtSed("Sed2");
	ExtSed sed3 = new ExtSed("Sed3");
	
	Segment seg1 = new Segment();
	
	for (int k=0; k<x1.length; k++) {
	    y1[k] = y1[k]*1e23;
	    yerr1[k] = yerr1[k]*1e23;
	}
	seg1.setFluxAxisValues(y1);
	seg1.setSpectralAxisValues(x1);
	seg1.setFluxAxisUnits("Jy");
	seg1.setSpectralAxisUnits("Angstrom");
	seg1.setDataValues(yerr1, SEDMultiSegmentSpectrum.E_UTYPE);
	sed1.addSegment(seg1);
	
	Segment seg2 = new Segment();
	seg2.setFluxAxisValues(y2);
	seg2.setSpectralAxisValues(x2);
	seg2.setFluxAxisUnits("erg/s/cm2/Hz");
	seg2.setSpectralAxisUnits("Angstrom");
	seg2.setDataValues(yerr2, SEDMultiSegmentSpectrum.E_UTYPE);
	sed2.addSegment(seg2);
	
	Segment seg3 = new Segment();
	seg3.setFluxAxisValues(y3);
	
	//convert the values in x3 to nm so I can test the unit conversions too.
	int k=0;
	for (double x : x3) {
	    x3[k] = x*0.1;
	    k++;
	}
	seg3.setSpectralAxisValues(x3);
	seg3.setFluxAxisUnits("erg/s/cm2/Hz");
	seg3.setSpectralAxisUnits("nm");
	seg3.setDataValues(yerr3, SEDMultiSegmentSpectrum.E_UTYPE);
	sed3.addSegment(seg3);
	
	SedStack stack = new SedStack("Stack");
	stack.add(sed1); stack.add(sed2); stack.add(sed3);
	
	// setup the stacking configuration
	StackConfiguration config = new StackConfiguration();
	config.setBinsize(2.0);
	config.setBinsizeUnit("Angstrom");
	config.setLogbin(false);
	config.setSmooth(false);
	config.setSmoothBinsize(20.);
	config.setStatistic("avg");
	config.setYUnits("erg/s/cm2/Hz");
	
	// stack
	stacker = new SedStackerStacker(controller);
	ExtSed result = stacker.stack(stack, config);
	
	List<double[]> xs = new ArrayList();
	List<double[]> ys = new ArrayList();
	xs.add(x1); xs.add(x2); xs.add(x3);
	ys.add(y1); ys.add(y2); ys.add(y3);
	
	// stack.getOrigSeds() should return original seds
	for (int j=0; j<stack.getOrigSeds().size(); j++) {
	    ExtSed origSed = stack.getOrigSeds().get(j);
	    double[] x = xs.get(j);
	    double[] y = ys.get(j);
	    
	    for (int i=0; i<stack.getOrigSeds().get(j).getSegment(0).getLength(); i++) {
		double xOrigValue = origSed.getSegment(0).getSpectralAxisValues()[i];
		double yOrigValue = origSed.getSegment(0).getFluxAxisValues()[i];
		assertEquals(xOrigValue, x[i]);
		assertEquals(yOrigValue, y[i]);
	    }
	}
	
	double[] controlY = new double[] {5.0, 5.36666667, 4.5, 2.66666667, 4.0, 6.5, 1.5, 10.5, 5.0, 10.0};
	double[] controlX = new double[] {   0.,    2.,    4.,    6.,    8.,   10.,   16.,   22.,   50.,  100.};
	//double[] controlYerr = new double[] {0.5, 0.501120965, 0.3640055, 0.18104634, 0.4, 0.48102899, 0.15, 1.05, 0.5, 1.0}; // calculated from errors [sqrt(sum(errors^2))/N]
	double[] controlYerr = new double[] {0, 6.82169741, 2.5, 1.64991582, 0., 5.21216526, 0., 0., 0., 0.}; // calculated from stddev(flux)
	double[] controlCounts = new double[] {1, 3, 2, 3, 1, 3, 1, 1, 1, 1};
	
	// test values of stacked Sed
	double[] yerrValues = (double[]) result.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE);
	double[] counts = (double[]) result.getAttachment(COUNTS);
	for (int i=0; i<result.getSegment(0).getLength(); i++) {
	    double xValue = result.getSegment(0).getSpectralAxisValues()[i];
	    double yValue = result.getSegment(0).getFluxAxisValues()[i];
	    assertEquals(controlX[i], xValue, 0.00001);
	    assertEquals(controlY[i], yValue, 0.00001);
	    assertEquals(controlYerr[i], yerrValues[i], 0.00001);
	    assertEquals(controlCounts[i], counts[i]);
	}
	    
	controller.stop();
    }
    
    @After
    public void tearDown() {
    }
    
}
