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

import cfa.vo.interop.SAMPMessage;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.io.SedFormat;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;

/**
 * This class abstract a Sed Message, i.e. a SAMP message that contains a reference to a
 * Sed document. This class has two constructors
 *
 * @author olaurino
 */
public final class SedMessage implements SAMPMessage {
    private String sedId;
    private URL url;
    private SedFormat format = SedFormat.VOT;
    private Message message;

    public static final String MTYPE = "table.load.votable";

    /**
     *
     * @param message
     * @throws SampException
     */
    public SedMessage(Message message) throws SampException {
        try {
            this.message = message;
            setSedId((String) message.getParam("sed-id"));
            setUrl(new URL((String) message.getParam("url")));
        } catch(Exception ex) {
            throw new SampException(ex);
        }
    }

    /**
     *
     * @param sedId
     * @param url
     * @param format
     */
    public SedMessage(String sedId, URL url) {
        message = new Message(MTYPE);
        setSedId(sedId);
        setUrl(url);
    }

    /**
     *
     * @return
     * @throws SedException
     * @throws IOException
     */
    public Sed getSed() throws SedException, IOException {
        InputStream is = url.openStream();
        Sed sed = Sed.read(is, format);
        is.close();
        return sed;
    }

    @Override
    public Message get() {
        return message;
    }

    /**
     *
     * @return
     */
    public String getSedId() {
        return sedId;
    }

    /**
     *
     * @param sedId
     */
    public void setSedId(String sedId) {
        this.sedId = sedId;
        message.addParam("sed-id", sedId);
    }

    /**
     *
     * @return
     */
    public URL getUrl() {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(URL url) {
        this.url = url;
        message.addParam("url", url.toString());
    }
    

}
