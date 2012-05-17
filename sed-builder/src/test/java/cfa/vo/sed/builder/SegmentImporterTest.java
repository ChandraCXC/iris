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

import cfa.vo.sed.setup.SetupManager;
import cfa.vo.sed.setup.ISetup;
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.ConfigFactory;
import cfa.vo.sed.test.URLTestConverter;
import cfa.vo.sed.test.Ws;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author olaurino
 */
public class SegmentImporterTest {

    public SegmentImporterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Creating configuration files");

        URL fileURL = URLTestConverter.getURL("test:///test_data/fileformats.ini");

        List<ISetup> confList = ConfigFactory.getAllFormatsConfigurations();

        SetupManager.write(confList, fileURL);

        fileURL = SegmentImporterTest.class.getResource("/test_data/");
        fileURL = new URL("file:///"+fileURL.getFile()+"error_types.ini");

        confList = ConfigFactory.getAllErrorTypesConfigurations();
        SetupManager.write(confList, fileURL);

        SedBuilder builder = new SedBuilder();
        builder.init(new App(), new Ws());

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

    /**
     * Test of getSegments method, of class SegmentImporter.
     */
    @Test
    public void testGetSegments_URL() throws Exception {
        URL fileURL = URLTestConverter.getURL("test:///test_data/fileformats.ini");
        List<Segment> segments = SegmentImporter.getSegments(fileURL);

        Sed sed = new Sed();

        sed.addSegment(segments);

        URL outUrl = getClass().getResource("/test_data/");

        String outfile = outUrl.getFile()+"out";

        sed.write(outfile+".vot", SedFormat.VOT);
        sed.write(outfile+".fits", SedFormat.FITS);

        fileURL = URLTestConverter.getURL("test:///test_data/error_types.ini");
        segments = SegmentImporter.getSegments(fileURL);

        sed = new Sed();
        sed.addSegment(segments);

        sed.write(outfile+".vot", SedFormat.VOT);
        sed.write(outfile+".fits", SedFormat.FITS);

    }


}