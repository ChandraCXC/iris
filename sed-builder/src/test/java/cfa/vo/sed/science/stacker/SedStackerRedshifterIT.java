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

import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.sed.ExtSed;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.REDSHIFT;

import cfa.vo.iris.utils.Default;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.SherpaClient;

import java.util.ArrayList;
import java.util.List;

import org.astrogrid.samp.Response;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Trigger;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import static org.junit.Assert.*;

public class SedStackerRedshifterIT extends AbstracSEDStackerIT {
    
    double[] controlY1;
    double[] controlX1;
    double[] controlYerr1;
    double[] controlX2;
    double[] controlX1WithExtraSegment;
    double[] controlY1WithExtraSegment;

    SedStackerRedshiftPayload payload;
    private SedStackerRedshifter redshifter;

    @Before
    public void before() throws Exception {
        controlY1 = new double[] { 0.1, 0.5, 1.0, 1.5, 5.0, 10.0 };
        controlX1 = new double[] { 0.90909091, 4.54545455, 9.09090909,
                13.63636364, 45.45454545, 90.90909091 };
        controlYerr1 = new double[] { 0.01, 0.05, 0.1, 0.15, 0.5, 1.0 };
        controlX2 = new double[] { 1.66666667, 3.33333333, 4.16666667,
                6.66666667, 8.33333333 };

        controlX1WithExtraSegment = new double[] { 0.90909091, 4.54545455,
                9.09090909, 13.63636364, 18.181818182, 45.45454545, 90.90909091 };
        controlY1WithExtraSegment = new double[] { 0.1, 0.5, 1.0, 1.5, 2.0,
                5.0, 10.0 };
    }

    @Test
    public void testRedshift() throws Exception {
        payload = (SedStackerRedshiftPayload) SAMPFactory
                .get(SedStackerRedshiftPayload.class);

        // Setup the stack payload
        payload.addSegment(segment1);
        payload.addSegment(segment2);
        payload.addSegment(segment3);
        payload.setCorrectFlux(false);
        payload.setZ0(0.0);

        // Setup and send SAMP message
        SAMPMessage message = SAMPFactory.createMessage("stack.redshift",
                payload, SedStackerRedshiftPayload.class);

        SherpaClient client = new SherpaClient(controller);

        Response rspns = controller.callAndWait(client.findSherpa(),
                message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        SedStackerRedshiftPayload response = (SedStackerRedshiftPayload) SAMPFactory
                .get(rspns.getResult(), SedStackerRedshiftPayload.class);

        // get response values
        SegmentPayload seg = response.getSegments().get(0);
        SegmentPayload seg2 = response.getSegments().get(1);

        assertEquals(response.getSegments().size(), 3);

        // tests
        for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlY1[i], seg.getY()[i], EPSILON);
        }
        for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlX1[i], seg.getX()[i], EPSILON);
        }
        for (int i = 0; i < seg.getY().length; i++) {
            assertEquals(controlYerr1[i], seg.getYerr()[i], EPSILON);
        }
        for (int i = 0; i < seg2.getX().length; i++) {
            assertEquals(controlX2[i], seg2.getX()[i], EPSILON);
        }
    }

    @Test
    public void testRedshifter() throws Exception {
        ExtSed sed1 = new ExtSed("Sed1");
        ExtSed sed2 = new ExtSed("Sed2");
        ExtSed sed3 = new ExtSed("Sed3");

        Segment seg1 = new Segment();
        seg1.setFluxAxisValues(y1);
        seg1.setSpectralAxisValues(x1);
        seg1.setFluxAxisUnits("Jy");
        seg1.setSpectralAxisUnits("Angstrom");
        seg1.setDataValues(yerr1, UTYPE.FLUX_STAT_ERROR);
        sed1.addSegment(seg1);

        // Segment seg11 = new Segment();
        // seg11.setFluxAxisValues(new double[] {2.0});
        // seg11.setSpectralAxisValues(new double[] {20});
        // seg11.setFluxAxisUnits("Jy");
        // seg11.setSpectralAxisUnits("nm");
        // seg11.setDataValues(yerr1, UTYPEs.FLUX_STAT_ERROR);
        // sed1.addSegment(seg11);
        sed1.addAttachment(REDSHIFT, 0.1);

        Segment seg2 = new Segment();
        seg2.setFluxAxisValues(y2);
        seg2.setSpectralAxisValues(x2);
        seg2.setFluxAxisUnits("erg/s/cm2");
        seg2.setSpectralAxisUnits("Angstrom");
        seg2.setDataValues(yerr2, UTYPE.FLUX_STAT_ERROR);
        sed2.addSegment(seg2);
        sed2.addAttachment(REDSHIFT, .2);

        Segment seg3 = new Segment();
        seg3.setFluxAxisValues(y3);
        seg3.setSpectralAxisValues(x3);
        seg3.setFluxAxisUnits("erg/s/cm2");
        seg3.setSpectralAxisUnits("Angstrom");
        seg3.setDataValues(yerr3, UTYPE.FLUX_STAT_ERROR);
        sed3.addSegment(seg3);
        sed3.addAttachment(REDSHIFT, 0.3);

        SedStack stack = new SedStack("Stack");
        stack.add(sed1);
        stack.add(sed2);
        stack.add(sed3);
        SedStack origStack = stack.copy(); // make a copy for testing

        // setup the redshift configuration
        RedshiftConfiguration redshiftConf = new RedshiftConfiguration();
        redshiftConf.setCorrectFlux(false);
        redshiftConf.setToRedshift(0.0);
        stack.getConf().setRedshiftConfiguration(redshiftConf);

        // redshift the Stack
        redshifter = new SedStackerRedshifter(controller, Default.getInstance().getUnitsManager());
        redshifter.shift(stack);

        List<double[]> xs = new ArrayList();
        List<double[]> ys = new ArrayList();
        xs.add(x1);
        xs.add(x2);
        xs.add(x3);
        ys.add(y1);
        ys.add(y2);
        ys.add(y3);

        // stack.getOrigSeds() should return original seds
        for (int j = 0; j < stack.getOrigSeds().size(); j++) {
            ExtSed origSed = stack.getOrigSeds().get(j);
            double[] x = xs.get(j);
            double[] y = ys.get(j);

            for (int i = 0; i < stack.getOrigSeds().get(j).getSegment(0)
                    .getLength(); i++) {
                double xOrigValue = origSed.getSegment(0)
                        .getSpectralAxisValues()[i];
                double yOrigValue = origSed.getSegment(0).getFluxAxisValues()[i];
                assertEquals(xOrigValue, x[i], EPSILON);
                assertEquals(yOrigValue, y[i], EPSILON);
            }
        }

        // stack.getSeds() should return redshifted seds
        ExtSed shiftedSed1 = stack.getSeds().get(0);
        double[] yerrValues = (double[]) shiftedSed1.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR);
        for (int i = 0; i < shiftedSed1.getSegment(0).getLength(); i++) {
            double xValue = shiftedSed1.getSegment(0).getSpectralAxisValues()[i];
            double yValue = shiftedSed1.getSegment(0).getFluxAxisValues()[i];
            assertEquals(controlX1[i], xValue, EPSILON);
            assertEquals(controlY1[i], yValue, EPSILON);
            assertEquals(controlYerr1[i], yerrValues[i], EPSILON);

        }
        ExtSed shiftedSed2 = stack.getSeds().get(1);
        for (int i = 0; i < shiftedSed2.getSegment(0).getLength(); i++) {
            double xValue = shiftedSed2.getSegment(0).getSpectralAxisValues()[i];
            assertEquals(xValue, controlX2[i], EPSILON);
        }
    }

    @Test
    public void testRedshifterNoZ() throws Exception {
        ExtSed sed1 = new ExtSed("Sed1");
        ExtSed sed2 = new ExtSed("Sed2");
        ExtSed sed3 = new ExtSed("Sed3");

        Segment seg1 = new Segment();
        seg1.setFluxAxisValues(y1);
        seg1.setSpectralAxisValues(x1);
        seg1.setFluxAxisUnits("Jy");
        seg1.setSpectralAxisUnits("Angstrom");
        seg1.setDataValues(yerr1, UTYPE.FLUX_STAT_ERROR);
        sed1.addSegment(seg1);
        sed1.addAttachment(REDSHIFT, 0.1);

        Segment seg2 = new Segment();
        seg2.setFluxAxisValues(y2);
        seg2.setSpectralAxisValues(x2);
        seg2.setFluxAxisUnits("erg/s/cm2");
        seg2.setSpectralAxisUnits("Angstrom");
        seg2.setDataValues(yerr2, UTYPE.FLUX_STAT_ERROR);
        sed2.addSegment(seg2);
        sed2.addAttachment(REDSHIFT, null);

        Segment seg3 = new Segment();
        seg3.setFluxAxisValues(y3);
        seg3.setSpectralAxisValues(x3);
        seg3.setFluxAxisUnits("erg/s/cm2");
        seg3.setSpectralAxisUnits("Angstrom");
        seg3.setDataValues(yerr3, UTYPE.FLUX_STAT_ERROR);
        sed3.addSegment(seg3);
        sed3.addAttachment(REDSHIFT, 0.3);

        SedStack stack = new SedStack("Stack");
        stack.add(sed1);
        stack.add(sed2);
        stack.add(sed3);

        // setup the redshift configuration
        RedshiftConfiguration redshiftConf = new RedshiftConfiguration();
        redshiftConf.setCorrectFlux(false);
        redshiftConf.setToRedshift(0.0);
        stack.getConf().setRedshiftConfiguration(redshiftConf);

        // redshift the Stack
        redshiftWithWindowInterceptor(stack, redshiftConf);

        // original values. make sure stack.getOrigSeds() returns original seds
        List<double[]> xs = new ArrayList();
        List<double[]> ys = new ArrayList();
        xs.add(x1);
        xs.add(x2);
        xs.add(x3);
        ys.add(y1);
        ys.add(y2);
        ys.add(y3);

        for (int j = 0; j < stack.getOrigSeds().size(); j++) {
            ExtSed origSed = stack.getOrigSeds().get(j);
            double[] x = xs.get(j);
            double[] y = ys.get(j);
            for (int i = 0; i < stack.getOrigSeds().get(j).getSegment(0)
                    .getLength(); i++) {
                double xOrigValue = origSed.getSegment(0)
                        .getSpectralAxisValues()[i];
                double yOrigValue = origSed.getSegment(0).getFluxAxisValues()[i];
                assertEquals(xOrigValue, x[i], EPSILON);
                assertEquals(yOrigValue, y[i], EPSILON);
            }
        }

        // stack.getSeds() should return redshifted seds
        ExtSed shiftedSed1 = stack.getSeds().get(0);
        double[] yerrValues = (double[]) shiftedSed1.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR);
        for (int i = 0; i < shiftedSed1.getSegment(0).getLength(); i++) {
            double xValue = shiftedSed1.getSegment(0).getSpectralAxisValues()[i];
            double yValue = shiftedSed1.getSegment(0).getFluxAxisValues()[i];
            assertEquals(controlX1[i], xValue, EPSILON);
            assertEquals(controlY1[i], yValue, EPSILON);
            assertEquals(controlYerr1[i], yerrValues[i], EPSILON);

        }
        // since sed2 doesn't have a redshift, the spectral values shouldn't
        // change.
        ExtSed shiftedSed2 = stack.getSeds().get(1);
        for (int i = 0; i < shiftedSed2.getSegment(0).getLength(); i++) {
            double xValue = shiftedSed2.getSegment(0).getSpectralAxisValues()[i];
            assertEquals(xValue, x2[i], EPSILON);
        }
    }

    private void redshiftWithWindowInterceptor(final SedStack stack, final RedshiftConfiguration config) {
        WindowInterceptor.init(new Trigger() {
            @Override
            public void run() throws Exception {
                final SedStackerRedshifter shifter = new SedStackerRedshifter(controller, Default.getInstance().getUnitsManager());
                shifter.shift(stack, config);
            }
        }).process(new WindowHandler() {
            @Override
            public Trigger process(Window window) throws Exception {
                window.titleEquals("Unshifter SEDs");
                return window.getButton("OK").triggerClick();
            }
        }).run();
    }
}
