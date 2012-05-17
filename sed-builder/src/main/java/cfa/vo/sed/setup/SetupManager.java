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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.setup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

/**
 *
 * @author olaurino
 *
 * The SetupManager class offers convenience methods for ISetup
 * objects I/O.
 *
 */
public class SetupManager {

    /**
     * Read a list of Configuration objects from a file
     *
     * @param fileURL The URL where the file can be found
     * @return a list of ISetup objects. This implementation returns {@link SetupBean} lists.
     * @throws IOException
     */
    public static List<ISetup> read(URL fileURL) throws IOException {

        List<ISetup> confList = new ArrayList();

        Ini ini = new Ini(fileURL);
        for(Section sec : ini.values()) {
            ISetup conf = (ISetup) sec.as(ISetup.class);
            
            conf = SetupBean.copy(conf);
            confList.add(conf);
            
        }

        return confList;
    }

    /**
     * Write a list of configuration objects to a file.
     *
     * @param confList The list of objects to serialize
     * @param fileURL The location of the file that has to be written
     * @throws IOException
     */
    public static void write(List<ISetup> confList, URL fileURL) throws IOException {
        Ini ini = new Ini();

        OutputStream fos = new FileOutputStream(fileURL.getFile());

        for(int i=0; i<confList.size(); i++) {
            ISetup conf=confList.get(i);
            Section sec = ini.add("Segment"+i);
            sec.from(conf);
        }

        ini.store(fos);

        fos.close();

    }

}
