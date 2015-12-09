/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
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

package cfa.vo.iris.common;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPControllerBuilder;
import cfa.vo.iris.interop.AbstractSedMessageHandler;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.it.AbstractSAMPTest;
import cfa.vo.iris.utils.Default;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SedMessageIT extends AbstractSAMPTest {
    
    private static final Logger logger = Logger.getLogger(SedMessageIT.class.getName());
    
    private Sed mySed;

    public SedMessageIT() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    @Test
    public void sedMessageTest() throws Exception {
        System.setProperty("jsamp.hub.profiles", "std");

        long timeout = Default.getInstance().getSampTimeout().convertTo(TimeUnit.MILLISECONDS).getAmount();
        SAMPController sampSender = new SAMPControllerBuilder("TestSender")
                .withResourceServer("/test")
                .buildAndStart(timeout);
        SAMPController sampReceiver = new SAMPControllerBuilder("TestReceiver")
                .buildAndStart(timeout);
        assertTrue(sampSender.isConnected());
        assertTrue(sampReceiver.isConnected());

        sampReceiver.addMessageHandler(new SedHandler());

        ExtSed sed = ExtSed.read(this.getClass().getResource("/test_data/3c273.xml").getFile(), SedFormat.VOT);

        sed.sendSedMessage(sampSender);

        int i=0;

        for(; (mySed==null && i<20); i++) {
            Thread.sleep(1000);
        }

        if(i==20)
            Assert.fail("timeout waiting for SAMP response");

        Assert.assertEquals(1, mySed.getNumberOfSegments());

        sampSender.stop();
        sampReceiver.stop();

        Segment segment = sed.getSegment(0);

        Assert.assertEquals("NASA/IPAC Extragalactic Database (NED)", segment.getCuration().getPublisher().getValue());

        Assert.assertEquals("3c273", sed.getId());

    }

    private class SedHandler extends AbstractSedMessageHandler {

        @Override
        public void processSed(Sed sed, String sedId) {
            mySed = sed;
        }

    }

}