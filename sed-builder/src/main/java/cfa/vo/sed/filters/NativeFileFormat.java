/**
 * Copyright (C) Smithsonian Astrophysical Observatory
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

package cfa.vo.sed.filters;

import java.net.URL;

/**
 *
 * @author omarlaurino
 */
public enum NativeFileFormat implements IFileFormat {
    VOTABLE("VOTable", VoTableFilter.class),
    CSV("CSV", CSVFilter.class),
    FITS("FITS", FITSFilter.class),
    ASCIITABLE("ASCII Table", ASCIIFilter.class),
    IPAC("IPAC", IPACFilter.class),
    TST("TST", TSTFilter.class),
    ;

    private FileFormat fileFormat;

    NativeFileFormat(String name, Class<? extends IFilter> filterClass) {
        this.fileFormat = new FileFormat(name, filterClass);
    }

    @Override
    public IFilter getFilter(URL url) throws FilterException {
        return fileFormat.getFilter(url);
    }

    @Override
    public String getName() {
        return fileFormat.getName();
    }

    @Override
    public Plugin getPlugin() {
        return fileFormat.getPlugin();
    }

    @Override
    public void setPlugin(Plugin plugin) {
        fileFormat.setPlugin(plugin);
    }
}
