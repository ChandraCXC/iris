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

package cfa.vo.sed.builder.photfilters;

import java.net.URL;
import java.util.ArrayList;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.URLDataSource;
import uk.ac.starlink.votable.VOTableBuilder;

/**
 *
 * @author olaurino
 */
public class PhotometryFiltersList extends ArrayList<PhotometryFilter> {
    public PhotometryFiltersList() throws Exception {
        super();
        DataSource ds = new URLDataSource(getClass().getResource("/filters/svolist.vot"));
        VOTableBuilder b = new VOTableBuilder();
        StarTable table = b.makeStarTable(ds, true, StoragePolicy.ADAPTIVE);

        for(long i=0; i<table.getRowCount(); i++) {
            PhotometryFilter pf = new PhotometryFilter();
            pf.setId((String) table.getCell(i, 1));
            pf.setUnit((String) table.getCell(i, 2));
            pf.setBand((String) table.getCell(i, 6));
            pf.setInstrument((String) table.getCell(i, 7));
            pf.setFacility((String) table.getCell(i, 8));
            pf.setDescription((String) table.getCell(i, 11));
            pf.setWlmean((Float) table.getCell(i, 13));
            pf.setWleff((Float) table.getCell(i, 14));
            pf.setWlmin((Float) table.getCell(i, 15));
            pf.setWlmax((Float) table.getCell(i, 16));
            pf.setCurveURL(new URL((String) table.getCell(i, 30)));
            add(pf);
        }
    }
}
