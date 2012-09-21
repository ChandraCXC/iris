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

package cfa.vo.sed.filters;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sed.builder.ISegmentColumn;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.dm.PhotometryCatalog;
import cfa.vo.sed.builder.dm.PhotometryCatalogEntry;
import cfa.vo.sed.builder.dm.PhotometryPoint;
import cfa.vo.sed.builder.dm.PhotometryPointSegment;
import cfa.vo.sed.builder.photfilters.PhotometryFilter;
import cfa.vo.sed.builder.photfilters.PhotometryFiltersList;
import cfa.vo.sed.quantities.SPVYQuantity;
import cfa.vo.sed.quantities.YUnit;
import cfa.vo.sed.setup.PhotometryCatalogBuilder;
import cfa.vo.sed.setup.PhotometryPointBuilder;
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.Oracle;
import cfa.vo.sed.test.Ws;
import cfa.vo.sedlib.io.SedFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author olaurino
 */
public class PhotometryPointCloneTest {

    public PhotometryPointCloneTest() {
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
     public void photometryCatalogBuilderTest() throws Exception {
        SedBuilder builder = new SedBuilder();
        builder.init(new App(), new Ws());

        ExtSed sed = new ExtSed("test");

        AbstractSingleStarTableFilter filter = (AbstractSingleStarTableFilter) NativeFileFormat.FITS.getFilter(getClass().getResource("/test_data/2FGL_file.fits"));

        PhotometryCatalogBuilder cbuilder = new PhotometryCatalogBuilder(filter, sed, 0);

        PhotometryPointBuilder pbuilder = new PhotometryPointBuilder("Point0");

        cbuilder.addPointBuilder(pbuilder);

        PhotometryFiltersList filters = new PhotometryFiltersList();

        PhotometryFilter photFilter = filters.get(0);

        pbuilder.getSpectralAxisBuilder().setFilter(photFilter);
        pbuilder.getSpectralAxisBuilder().setMode("Photometry Filter");

        pbuilder.getFluxAxisBuilder().setQuantity(SPVYQuantity.FLUX);
        pbuilder.getFluxAxisBuilder().setUnit(YUnit.FLUX0);
        ISegmentColumn col = filter.getMetadata().get(0).getColumns().get(4);
        pbuilder.getFluxAxisBuilder().setValueColumn(col);
        col = filter.getMetadata().get(0).getColumns().get(5);
        pbuilder.getFluxAxisBuilder().setErrorColumn(col);

        PhotometryCatalog catalog = cbuilder.build();

        catalog.addTo(sed);

        Oracle oracle = new Oracle();

        oracle.put("id", "Point0");
        oracle.put("spectralAxis.filter.id", "IUE/IUE.1250-1300");
        oracle.put("fluxAxis.value", 2.44858E-9);
        oracle.put("fluxAxis.error", 1.93153E-10);
        oracle.put("fluxAxis.quantity", SPVYQuantity.FLUX);
        oracle.put("fluxAxis.unit", YUnit.FLUX0);

        oracle.test(catalog.get(0).get(0).getPoint());

        pbuilder.getFluxAxisBuilder().setQuantity(SPVYQuantity.FLUXDENSITY);
        pbuilder.getFluxAxisBuilder().setUnit(YUnit.FLUXDENSITYWL0);

        catalog = cbuilder.build();

        oracle.put("fluxAxis.quantity", SPVYQuantity.FLUXDENSITY);
        oracle.put("fluxAxis.unit", YUnit.FLUXDENSITYWL0);

        oracle.test(catalog.get(0).get(0).getPoint());
        
        pbuilder = (PhotometryPointBuilder) pbuilder.clone();
        pbuilder.validate();

//        pbuilder = new PhotometryPointBuilder("Point1");
//
//        photFilter = filters.get(1);
//
//        pbuilder.getSpectralAxisBuilder().setFilter(photFilter);
//        pbuilder.getSpectralAxisBuilder().setMode("Photometry Filter");
//
//        pbuilder.getFluxAxisBuilder().setQuantity(SPVYQuantity.FLUX);
//        pbuilder.getFluxAxisBuilder().setUnit(YUnit.FLUX1);
//        col = filter.getMetadata().get(0).getColumns().get(6);
//        pbuilder.getFluxAxisBuilder().setValueColumn(col);
//        col = filter.getMetadata().get(0).getColumns().get(7);
//        pbuilder.getFluxAxisBuilder().setErrorColumn(col);
//
//        cbuilder.addPointBuilder(pbuilder);
//
//        catalog = cbuilder.build();

        int c = 0;

        for(PhotometryCatalogEntry entry : catalog) {
            System.out.println("Entry "+(++c));
            for(PhotometryPointSegment segment : entry) {
                PhotometryPoint point = segment.getPoint();
                System.out.println("\tPoint: " + point.getId());
                System.out.println("\t\tFilter: "+ point.getSpectralAxis().getFilter().toString());
                System.out.println("\t\tFlux: " + point.getFluxAxis().getValue());
                System.out.println("\t\tError: " + point.getFluxAxis().getError());
                System.out.println("\t\tQuantity: " + point.getFluxAxis().getQuantity());
                System.out.println("\t\tUnit: " + point.getFluxAxis().getUnit());
            }
        }

        catalog.addTo(sed);

        sed.write(System.out, SedFormat.VOT);

     }

}