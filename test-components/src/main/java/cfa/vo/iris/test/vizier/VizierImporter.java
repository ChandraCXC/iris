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
package cfa.vo.iris.test.vizier;

import cfa.vo.sed.builder.SegmentImporterException;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Field;
import cfa.vo.sedlib.Point;
import cfa.vo.sedlib.PositionParam;
import cfa.vo.sedlib.Segment;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.util.URLDataSource;
import uk.ac.starlink.votable.VOTableBuilder;

/**
 *
 * @author olaurino
 */

public class VizierImporter {
    
    public static final String VIZIER_DATA_DEFAULT_ENDPOINT =
            "http://cdsarc.u-strasbg.fr/viz-bin/sed?-c=:targetName&-c.rs=:searchRadius";
    
    public static final String DEFAULT_SEARCH_RADIUS = "5";

    public static Collection<Segment> getSedFromName(String targetName) throws SegmentImporterException {
        return getSedFromName(targetName, DEFAULT_SEARCH_RADIUS, VIZIER_DATA_DEFAULT_ENDPOINT);
    }
    
    public static Collection<Segment> getSedFromName(String targetName, String searchRadius) throws SegmentImporterException {
        return getSedFromName(targetName, searchRadius, VIZIER_DATA_DEFAULT_ENDPOINT);
    }
    
    public static Collection<Segment> getSedFromName(String targetName, String searchRadius, String endpoint) throws SegmentImporterException {
        try {
            targetName = URLEncoder.encode(targetName, "UTF-8");
            searchRadius = URLEncoder.encode(searchRadius, "UTF-8");
            endpoint = endpoint.replace(":targetName&-c.rs=:searchRadius", targetName+"&-c.rs="+searchRadius);
            URL nedUrl = new URL(endpoint);

            VOTableBuilder vob = new VOTableBuilder();
            StarTable table = vob.makeStarTable(new URLDataSource(nedUrl), true, StoragePolicy.ADAPTIVE);

            Map<String, Segment> segMap = new HashMap();

            for (long i = 0; i < table.getRowCount(); i++) {
                Double ra = (Double) table.getCell(i, 0);
                Double dec = (Double) table.getCell(i, 1);
                String coordString = ra.toString() + dec.toString();
                if (!segMap.containsKey(coordString)) {
                    Segment s = new Segment();
                    segMap.put(coordString, s);
                    s.createData().createPoint();
                    s.createChar().createSpectralAxis().setUnit("GHz");
                    s.createChar().createSpectralAxis().setUcd("em.freq");
                    s.createChar().createFluxAxis().setUnit("Jy");
                    s.createChar().createFluxAxis().setUcd("phot.flux.density;em.freq");
                    s.createChar().createFluxAxis().createAccuracy().createStatError().setUnit("Jy");
                    s.createChar().createFluxAxis().createAccuracy().createStatError().setUcd("stat.error;phot.flux.density");
                    s.createTarget().createName().setValue(targetName);
                    PositionParam pp = new PositionParam();
                    pp.setValue(new DoubleParam[]{new DoubleParam(ra), new DoubleParam(dec)});
                    s.createTarget().setPos(pp);
                    s.createCuration().createPublisher().setValue("Vizier - CDS");
                }
                Segment s = segMap.get(coordString);
                Point p = new Point();
                Double spectral = (Double) table.getCell(i, 4);
                p.createSpectralAxis().setValue(new DoubleParam(spectral));
                Double flux = ((Float) table.getCell(i, 5)).doubleValue();
                p.createFluxAxis().setValue(new DoubleParam(flux));
                Double err = ((Float) table.getCell(i, 6)).doubleValue();
                p.createFluxAxis().createAccuracy().setStatError(new DoubleParam(err));
                s.createData().createPoint().add(p);
                Field xf = new Field();
                xf.setUcd("em.freq");
                xf.setUnit("GHz");
                s.createData().setDataInfo(xf, "Spectrum.Data.SpectralAxis.Value");
                Field yf = new Field();
                yf.setUcd("phot.flux.density;em.freq");
                yf.setUnit("Jy");
                s.createData().setDataInfo(yf, "Spectrum.Data.FluxAxis.Value");
                Field ef = new Field();
                ef.setUcd("stat.error;phot.flux.density");
                ef.setUnit("Jy");
                s.createData().setDataInfo(ef, "Spectrum.Data.FluxAxis.Accuracy.StatError");
            }


            return segMap.values();

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
