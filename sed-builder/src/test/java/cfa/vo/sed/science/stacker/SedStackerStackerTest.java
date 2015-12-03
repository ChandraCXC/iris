/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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
import cfa.vo.iris.sed.ExtSed;

import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.SherpaClient;

import static org.junit.Assert.*;

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.SampException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cfa.vo.sedlib.common.SedNoDataException;

public class SedStackerStackerTest {
    
    protected static double[] x1 = new double[] { 5, 1, 10, 15, 50, 100 };
    protected static double[] y1 = new double[] { 0.5, 0.1, 1.0, 1.5, 5.0, 10.0 };
    protected static double[] yerr1 = new double[] { 0.05, 0.01, 0.1, 0.15, 0.5, 1.0 };

    protected static double[] x2 = new double[] { 2, 4, 5, 8, 10 };
    protected static double[] y2 = new double[] { 1, 2, 3, 4, 5 };
    protected static double[] yerr2 = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };

    protected static double[] x3 = new double[] { 0.5, 3.0, 1.5, 5.0, 10.5, 21.0 };
    protected static double[] y3 = new double[] { 5.0, 7.0, 15.0, 4.5, 13.5, 10.5 };
    protected static double[] yerr3 = new double[] { 0.5, 0.7, 1.5, 0.45, 1.35, 1.05 };
    
    Segment segment1;
    Segment segment2;
    Segment segment3;
    
    ExtSed sed1;
    ExtSed sed2;
    ExtSed sed3;
    
    private SedStack sedStack;
    
    private SedStackerStacker stacker;
    
    private StackerPayloadStub response;
    
    protected SherpaClientStub client;
    protected SAMPControllerStub controller;
    
    @Before
    public void setUp() throws Exception {
        this.controller = new SAMPControllerStub("name");
        this.client = new SherpaClientStub(controller);
        
        this.stacker = new SedStackerStacker(client) {
            @Override
            protected void showMessageDialog(Component parent, Object msg, String title, int type) {
                logger.info("Expected message dialogue: " + msg);
            }
            @Override
            protected SedStackerStackPayload translateResponse(Response rspns) throws Exception {
                return response;
            }
        };
    }
    
    private void initialize() throws Exception {
        segment1 = new Segment();
        segment1.setFluxAxisValues(y1);
        segment1.setFluxAxisUnits("Jy");
        segment1.setSpectralAxisValues(x1);
        segment1.setSpectralAxisUnits("Angstrom");
        
        segment2 = new Segment();
        segment2.setFluxAxisValues(y2);
        segment2.setFluxAxisUnits("erg/s/cm2/Angstrom");
        segment2.setSpectralAxisValues(x2);
        segment2.setSpectralAxisUnits("Hz");
        
        segment3 = new Segment();
        segment3.setFluxAxisValues(y3);
        segment3.setFluxAxisUnits("mJy");
        segment3.setSpectralAxisValues(x3);
        segment3.setSpectralAxisUnits("m");

        sed1 = new ExtSed("1");
        sed1.addSegment(segment1);
        
        sed2 = new ExtSed("2");
        sed2.addSegment(segment2);
        
        sed3 = new ExtSed("3");
        sed3.addSegment(segment3);
        
        List<ExtSed> seds = new LinkedList<ExtSed>();
        seds.add(sed1);
        seds.add(sed2);
        seds.add(sed3);
        
        sedStack = new SedStack("test", seds);
        
        controller.rspns = new Response();
        
        response = new StackerPayloadStub();
    }

    @Test
    public void testEmptyStack() throws Exception {
        // pushing through an empty stack we expect an SedNoDataException and a message dialogue.
        // Note that the message dialogue is stubbed out of the call.
        SedStack stack = new SedStack(null);
        stack.setSeds(new ArrayList<ExtSed>());
        
        try {
            stacker.stack(stack);
        } catch(SedNoDataException ex) {
            return;
        }
        Assert.fail();
    }
    
    @Test
    public void testSherpaException() throws Exception {
        // The check for a sherpa client should fail.
        initialize();
        client.findSherpa = false;
        try {
            stacker.stack(sedStack);
        } catch(Exception ex) {
            // Tied to message, would be better if we had a more specific exception thrown.
            assertTrue(ex.getMessage().contains("Sherpa not found"));
            return;
        } finally {
            client.findSherpa = true;
        }
        Assert.fail();
    }
    
    @Test
    public void testUnitConversion() throws Exception {
        // The convert units method should ensure that all SEDs in a stack
        // have the same units
        initialize();
        
        stacker.convertUnits(sedStack, "Angstrom", "erg/s/cm2/Angstrom");
        
        assertEquals(sedStack.getSeds().get(0).getSegment(0).getFluxAxisUnits(),
                sedStack.getSeds().get(1).getSegment(0).getFluxAxisUnits());
        assertEquals(sedStack.getSeds().get(0).getSegment(0).getFluxAxisUnits(),
                sedStack.getSeds().get(2).getSegment(0).getFluxAxisUnits());
        
        assertEquals(sedStack.getSeds().get(0).getSegment(0).getSpectralAxisUnits(),
                sedStack.getSeds().get(1).getSegment(0).getSpectralAxisUnits());
        assertEquals(sedStack.getSeds().get(0).getSegment(0).getSpectralAxisUnits(),
                sedStack.getSeds().get(2).getSegment(0).getSpectralAxisUnits());
    }
    
    @Test
    public void checkMemory() throws Exception {
        initialize();
        
        // Normal request should pass just fine
        stacker.checkMemory(sedStack);

        // Send in a request that should barf on memory requirements
        double[] breakStuff = new double[] {1, 1000000000};
        segment1.setSpectralAxisValues(breakStuff);
        try {
            stacker.checkMemory(sedStack);
        } catch (StackException ex) {
            return;
        }
        fail();
    }
    
    @Test
    public void testSAMPException() throws Exception {
        initialize();
        
        // Simulating a sherpa client exception, verify it propagates to the caller.
        client.hasException = true;
        try {
            stacker.stack(sedStack);
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("client"));
            return;
        } finally {
            client.hasException = false;
        }
        fail();
    }
    
    @Test
    public void testSuccessful() throws Exception {
        initialize();
        
        response.segments = new LinkedList<SegmentPayload>();
        SegmentPayload payload = new SegmentPayloadStub();
        response.segments.add(payload);
        
        stacker.stack(sedStack);
    }
    
    //
    //
    // Stubs, use these to set expectations
    //
    //
    private static class SAMPBuilder extends SAMPController.Builder {
        public SAMPBuilder(String name) {
            super(name);
        }
    }

    private static class SAMPControllerStub extends SAMPController {
        public SAMPControllerStub(String name) {
            super(new Builder(name));
        }
        
        public Response rspns;
        
        @SuppressWarnings("rawtypes")
        @Override
        public Response callAndWait(String arg0, Map arg1, int arg2) throws SampException {
            return rspns;
        }
    }
    
    private static class SherpaClientStub extends SherpaClient {
        
        public SherpaClientStub(SAMPController controller) {
            super(controller);
        }

        public boolean findSherpa = true;
        @Override
        public String findSherpa() throws SampException {
            if (findSherpa) return "";
            throw new SampException("Sherpa not found");
        }

        public boolean hasException = false;
        @Override
        public boolean isException(Response rspns) {
            return hasException;
        }
        
        @Override
        public Exception getException(Response rspns) {
            return new RuntimeException("client exception");
        }
    }
    
    private static class StackerPayloadStub implements SedStackerStackPayload {
        public List<SegmentPayload> segments;
        @Override
        public Double getBinsize() {return null;}
        @Override
        public void setBinsize(Double binsize) {}
        @Override
        public Double getSmoothBinsize() {return null;}
        @Override
        public void setSmoothBinsize(Double boxSize) {}
        @Override
        public String getStatistic() {return null;}
        @Override
        public void setStatistic(String statistic) {}
        @Override
        public Boolean getLogBin() {return null;}
        @Override
        public void setLogBin(Boolean logBin) {}
        @Override
        public Boolean getSmooth() {return null;}
        @Override
        public void setSmooth(Boolean smooth) {}
        @Override
        public List<SegmentPayload> getSegments() {return segments;}
        @Override
        public void addSegment(SegmentPayload segment) {}
    }
    
    private static class SegmentPayloadStub implements SegmentPayload {
        @Override
        public double[] getX() {return x1;}
        @Override
        public void setX(double[] x) {}
        @Override
        public double[] getY() {return y1;}
        @Override
        public void setY(double[] y) {}
        @Override
        public double[] getYerr() {return yerr1;}
        @Override
        public void setYerr(double[] yerr) {}
        @Override
        public Double getZ() {return 0.0;}
        @Override
        public void setZ(Double redshift) {}
        @Override
        public Double getNormConstant() {return 0.0;}
        @Override
        public void setNormConstant(Double normConstant) {}
        @Override
        public double[] getCounts() {return yerr1;}
        @Override
        public void setCounts(double[] counts) {}
        @Override
        public String getId() {return null;}
        @Override
        public void setId(String id) {}
    }
}
