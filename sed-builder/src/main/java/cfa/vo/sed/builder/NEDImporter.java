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

package cfa.vo.sed.builder;

import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author olaurino
 */
public class NEDImporter {

    public static final String NED_DATA_DEFAULT_ENDPOINT =
            "http://vo.ned.ipac.caltech.edu/services/accessSED?REQUEST=getData&TARGETNAME=:targetName";

    public static Sed getSedFromName(String targetName) throws SegmentImporterException {
        return getSedFromName(targetName, NED_DATA_DEFAULT_ENDPOINT);
    }

    public static Sed getSedFromName(String targetName, String endpoint) throws SegmentImporterException {
        try {
            targetName = URLEncoder.encode(targetName, "UTF-8");
            endpoint = endpoint.replace(":targetName", targetName);
            URL nedUrl = new URL(endpoint);

            Sed sed = Sed.read(nedUrl.openStream(), SedFormat.VOT);

            if(sed.getNumberOfSegments()>0) {
                Segment seg = sed.getSegment(0);

                if(seg.createTarget().getPos()==null)
                            if(seg.createChar().createSpatialAxis().createCoverage().getLocation()!=null)
                                seg.createTarget().createPos().setValue(seg.getChar().getSpatialAxis().getCoverage().getLocation().getValue());
                            else
                                seg.createTarget().createPos().setValue(new DoubleParam[]{new DoubleParam(Double.NaN), new DoubleParam(Double.NaN)});
            }
            return sed;

        } catch (Exception ex) {
            throw new SegmentImporterException(ex);
        }
    }

//    public static Sed getError() throws SegmentImporterException {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(NEDImporter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        throw new SegmentImporterException(new ConnectException());
//    }

}
