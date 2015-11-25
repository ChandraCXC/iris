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
package cfa.vo.sed.science.stacker;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.sed.ExtSed;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.NORM_CONSTANT;
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
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import static org.junit.Assert.*;


public class SedStackerNormalizerIT extends AbstracSEDStackerIT {

    SedStackerNormalizePayload payload;
    
    @Before
    public void before() throws Exception {
        // TODO: Sync up SEDs across tests
        x1 = new double[]{1, 5, 10, 15, 50, 100};
        y1 = new double[]{0.1, 0.5, 1.0, 1.5, 5.0, 10.0};
        yerr1 = new double[]{0.01, 0.05, 0.1, 0.15, 0.5, 1.0};
        
        x3 = new double[]{0.5, 1.5, 3.0, 5.0, 10.5, 21.0};
        y3 = new double[]{5.0, 15.0, 7.0, 4.5, 13.5, 10.5};
        yerr3 = new double[]{0.5, 1.5, 0.7, 0.45, 1.35, 1.05};
    }

    @Test
    public void testNormalize() throws Exception {
        payload = (SedStackerNormalizePayload) SAMPFactory
                .get(SedStackerNormalizePayload.class);

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
        SAMPMessage message = SAMPFactory.createMessage("stack.normalize",
                payload, SedStackerNormalizePayload.class);

        SherpaClient client = new SherpaClient(controller);

        Response rspns = controller.callAndWait(client.findSherpa(),
                message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        SedStackerNormalizePayload response = (SedStackerNormalizePayload) SAMPFactory
                .get(rspns.getResult(), SedStackerNormalizePayload.class);

        // get response values
        double[] resy1 = response.getSegments().get(0).getY();
        double[] resy2 = response.getSegments().get(1).getY();
        Double resnorm3 = response.getSegments().get(2).getNormConstant();
        SegmentPayload seg1 = response.getSegments().get(0);
        Double z = seg1.getZ();
        Double norm = seg1.getNormConstant();

        assertEquals(response.getSegments().size(), 3);

        // tests
        for (int i = 0; i < resy1.length; i++) {
            assertEquals(0.49234923 * y1[i], resy1[i], EPSILON);
        }
        for (int i = 0; i < resy2.length; i++) {
            assertEquals(9.846 * y2[i], resy2[i], EPSILON);
        }
        assertEquals(1.1529274, resnorm3, EPSILON);
    }

    @Test
    public void testNormalizer() throws Exception {
        ExtSed sed1 = new ExtSed("Sed1");
        ExtSed sed2 = new ExtSed("Sed2");
        ExtSed sed3 = new ExtSed("Sed3");

        Segment seg1 = new Segment();

        for (int k = 0; k < x1.length; k++) {
            y1[k] = y1[k] * 1e23;
            yerr1[k] = yerr1[k] * 1e23;
        }
        seg1.setFluxAxisValues(y1);
        seg1.setSpectralAxisValues(x1);
        seg1.setFluxAxisUnits("Jy");
        seg1.setSpectralAxisUnits("Angstrom");
        seg1.setDataValues(yerr1, UTYPE.FLUX_STAT_ERROR);
        sed1.addSegment(seg1);

        Segment seg2 = new Segment();
        seg2.setFluxAxisValues(y2);
        seg2.setSpectralAxisValues(x2);
        seg2.setFluxAxisUnits("erg/s/cm2/Hz");
        seg2.setSpectralAxisUnits("Angstrom");
        seg2.setDataValues(yerr2, UTYPE.FLUX_STAT_ERROR);
        sed2.addSegment(seg2);

        Segment seg3 = new Segment();
        seg3.setFluxAxisValues(y3);

        // convert the values in x3 to nm so I can test the unit conversions
        // too.
        for (int k = 0; k < x3.length; k++) {
            x3[k] = x3[k] * 0.1;
        }
        seg3.setSpectralAxisValues(x3);
        seg3.setFluxAxisUnits("erg/s/cm2/Hz");
        seg3.setSpectralAxisUnits("nm");
        seg3.setDataValues(yerr3, UTYPE.FLUX_STAT_ERROR);
        sed3.addSegment(seg3);

        SedStack stack = new SedStack("Stack");
        stack.add(sed1);
        stack.add(sed2);
        stack.add(sed3);

        // setup the redshift configuration
        NormalizationConfiguration config = new NormalizationConfiguration();
        config.setMultiply(true);
        config.setIntegrate(true);
        config.setStats("Average");
        config.setXUnits("Angstrom");
        config.setXmax(Double.POSITIVE_INFINITY);
        config.setXmin(Double.NEGATIVE_INFINITY);
        config.setIntegrateValueYUnits("erg/s/cm2/Hz");
        config.setYValue(1.0);

        // normalize the Stack
        SedStackerNormalizer normalizer = new SedStackerNormalizer(controller, Default.getInstance().getUnitsManager());
        normalizer.normalize(stack, config);

        List<double[]> xs = new ArrayList<>();
        List<double[]> ys = new ArrayList<>();
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

        for (int j = 0; j < stack.getSed(0).getSegment(0).getLength(); j++)
            assertEquals(0.49234923 * y1[j], stack.getSed(0).getSegment(0)
                    .getFluxAxisValues()[j],  EPSILON * 0.49234923 * y1[j]);
        for (int j = 0; j < stack.getSed(1).getSegment(0).getLength(); j++)
            assertEquals(9.846 * y2[j], stack.getSed(1).getSegment(0)
                    .getFluxAxisValues()[j], EPSILON);

        assertEquals(
                1.1529274,
                Double.valueOf(stack.getSed(2).getAttachment(NORM_CONSTANT)
                        .toString()), EPSILON);
    }

    @Test
    public void testNormalizerOutsideRange() throws Exception {
        ExtSed sed1 = new ExtSed("Sed1");
        ExtSed sed2 = new ExtSed("Sed2");
        ExtSed sed3 = new ExtSed("Sed3");

        Segment seg1 = new Segment();

        // for (int k=0; k<x1.length; k++) {
        // y1[k] = y1[k]*1e23;
        // yerr1[k] = yerr1[k]*1e23;
        // }
        seg1.setFluxAxisValues(y1);
        seg1.setSpectralAxisValues(x1);
        seg1.setFluxAxisUnits("erg/s/cm2/Angstrom");
        seg1.setSpectralAxisUnits("Angstrom");
        seg1.setDataValues(yerr1, UTYPE.FLUX_STAT_ERROR);
        sed1.addSegment(seg1);

        Segment seg2 = new Segment();
        seg2.setFluxAxisValues(y2);
        seg2.setSpectralAxisValues(x2);
        seg2.setFluxAxisUnits("erg/s/cm2/Angstrom");
        seg2.setSpectralAxisUnits("Angstrom");
        seg2.setDataValues(yerr2, UTYPE.FLUX_STAT_ERROR);
        sed2.addSegment(seg2);

        Segment seg3 = new Segment();
        seg3.setFluxAxisValues(y3);

        // convert the values in x3 to nm so I can test the unit conversions
        // too.
        for (int k = 0; k < x3.length; k++) {
            x3[k] = x3[k] * 0.1;
        }
        seg3.setSpectralAxisValues(x3);
        seg3.setFluxAxisUnits("erg/s/cm2/Angstrom");
        seg3.setSpectralAxisUnits("nm");
        seg3.setDataValues(yerr3, UTYPE.FLUX_STAT_ERROR);
        sed3.addSegment(seg3);

        SedStack stack = new SedStack("Stack");
        stack.add(sed1);
        stack.add(sed2);
        stack.add(sed3);

        // setup the redshift configuration
        NormalizationConfiguration config = new NormalizationConfiguration();
        config.setMultiply(true);
        config.setIntegrate(true);
        config.setStats("Value");
        config.setXUnits("Angstrom");
        config.setXmax(9.0);
        config.setXmin(1.5);
        config.setIntegrateValueYUnits("erg/s/cm2");
        config.setYValue(1.0);

        // normalize the Stack
        normalizeWithWindowInterceptor(stack, config);

        List<double[]> xs = new ArrayList<>();
        List<double[]> ys = new ArrayList<>();
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

        for (int j = 0; j < stack.getSed(0).getSegment(0).getLength(); j++)
            assertEquals(y1[j], stack.getSed(0).getSegment(0)
                    .getFluxAxisValues()[j], EPSILON);
        for (int j = 0; j < stack.getSed(1).getSegment(0).getLength(); j++)
            assertEquals(0.0625 * y2[j], stack.getSed(1).getSegment(0)
                    .getFluxAxisValues()[j], EPSILON);

        assertEquals(
                0.035714285714,
                Double.valueOf(stack.getSed(2).getAttachment(NORM_CONSTANT)
                        .toString()), EPSILON);
    }

    private void normalizeWithWindowInterceptor(final SedStack stack, final NormalizationConfiguration config) {
        WindowInterceptor.init(new Trigger() {
            @Override
            public void run() throws Exception {
                final SedStackerNormalizer normalizer = new SedStackerNormalizer(controller, Default.getInstance().getUnitsManager());
                normalizer.normalize(stack, config);
            }
        }).process(new WindowHandler() {
            @Override
            public Trigger process(Window window) throws Exception {
                window.titleEquals("Unnormalized SEDs");
                return window.getButton("OK").triggerClick();
            }
        }).run();
    }
}
