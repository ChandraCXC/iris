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

import cfa.vo.iris.interop.AbstractSedMessageHandler;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author olaurino
 */
public class SedMessageTest {
    private Sed mySed;

    public SedMessageTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

     @Test
     public void sedMessageTest() throws Exception {
        System.setProperty("jsamp.hub.profiles", "std");

        SedSAMPController sampSender = new SedSAMPController("TestSender", "An SED builder from the Virtual Astronomical Observatory", this.getClass().getResource("/iris_button_tiny.png").toString());
        SedSAMPController sampReceiver = new SedSAMPController("TestReceiver", "An SED builder from the Virtual Astronomical Observatory", this.getClass().getResource("/iris_button_tiny.png").toString());

        sampSender.startWithResourceServer("/test", false);

        Thread.sleep(2000);

        sampReceiver.start(false);
        sampReceiver.setAutoRunHub(false);

        while(!sampSender.isConnected()) {
            System.out.println("waiting connection");
            Thread.sleep(1000);
        }

        while(!sampReceiver.isConnected()) {
            Thread.sleep(1000);
        }

        sampReceiver.addMessageHandler(new SedHandler());

        ExtSed sed = ExtSed.read(this.getClass().getResource("/test_data/3c273.xml").getFile(), SedFormat.VOT);

        Thread.sleep(5000);

        sampSender.sendSedMessage(sed);

        int i=0;

        for(; (mySed==null && i<20); i++) {
            Thread.sleep(1000);
        }

        if(i==20)
            Assert.fail("timeout waiting for SAMP response");

        Assert.assertEquals(1, mySed.getNumberOfSegments());

        sampReceiver.stop();

        Thread.sleep(2000);

        sampSender.stop();

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