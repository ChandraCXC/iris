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

import cfa.vo.interop.SAMPControllerBuilder;
import cfa.vo.iris.interop.AbstractSedMessageHandler;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.SAMPClientResource;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class SedMessageIT {
    
    private static final Logger logger = Logger.getLogger(SedMessageIT.class.getName());
    private Sed mySed;

    @Rule
    public SAMPClientResource sender = new SAMPClientResource(new SAMPControllerBuilder("TestSender").withResourceServer("/test"));

    @Rule
    public SAMPClientResource clientResource = new SAMPClientResource(new SAMPControllerBuilder("TestReceiver"));

    @Test
    public void sedMessageTest() throws Exception {
        clientResource.getHubController().addMessageHandler(new SedHandler());

        ExtSed sed = ExtSed.read(this.getClass().getResource("/test_data/3c273.xml").getFile(), SedFormat.VOT);

        sed.sendSedMessage(sender.getHubController());

        int i=0;

        for(; (mySed==null && i<20); i++) {
            Thread.sleep(1000);
        }

        if(i==20)
            Assert.fail("timeout waiting for SAMP response");

        Assert.assertEquals(1, mySed.getNumberOfSegments());

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