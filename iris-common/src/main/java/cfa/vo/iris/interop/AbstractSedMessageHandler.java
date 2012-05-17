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

package cfa.vo.iris.interop;

import cfa.vo.sedlib.Sed;
import java.util.Map;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.AbstractMessageHandler;
import org.astrogrid.samp.client.HubConnection;

/**
 *
 * This convenience class can be used by clients that want to receive SEDs from the
 * SAMP hub. I abstracts all the protocol details.
 *
 * You can extend this class by implementing the processSed method. The arguments of this method
 * are the Sed extracted from the message and its ID. The ID can be used by subsequent
 * messages that refer to this particular Sed.
 *
 * Once the class has been instantiated it can be passed as a MessageHandler to a SAMPController instance
 * using the addMessageHandler method.
 *
 * @author olaurino
 */
public abstract class AbstractSedMessageHandler extends AbstractMessageHandler {

    private Sed sed;

    private String sedId;

    public AbstractSedMessageHandler() {
        super(new String[]{SedMessage.MTYPE});
    }

    @Override
    public Map processCall(HubConnection hc, String string, Message msg) throws Exception {
        SedMessage message = new SedMessage(msg);
        sed = message.getSed();
        sedId = message.getSedId();
        processSed(sed, sedId);
        return null;
    }

    /**
     *
     * By implementing this method clients can access directly the Sed object and its ID.
     *
     * This method is called each time the
     *
     * @param sed
     * @param sedId
     */
    public abstract void processSed(Sed sed, String sedId);

}
