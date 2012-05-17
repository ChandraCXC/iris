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
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.io.SedFormat;
import java.io.IOException;
import java.io.OutputStream;
import org.astrogrid.samp.httpd.ServerResource;

/**
 *
 * This class implements a ServerResource that serves Sed document in the VOTable format.
 *
 * Once instantiated this class can be passed to a SAMPController instance using
 * the
 *
 * @author olaurino
 */
public class SedServerResource implements ServerResource {

    private Sed sed;

    /**
     * The constructor simply needs a Sed object: it will be serialized using
     * SedLib and then served by an internal HTTP Server.
     * @param sed
     */
    public SedServerResource(Sed sed) {
        this.sed = sed;
    }

    @Override
    public String getContentType() {
        return "application/x-votable+xml";
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public void writeBody(OutputStream out) throws IOException {
        try {
            sed.write(out, SedFormat.VOT);
        } catch (SedException ex) {
            throw new IOException(ex);
        }
    }

}
