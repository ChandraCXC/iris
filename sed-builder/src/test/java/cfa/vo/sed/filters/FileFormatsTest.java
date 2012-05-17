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

import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.Ws;
import cfa.vo.sed.setup.ISetup;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.setup.SetupManager;
import cfa.vo.sed.setup.validation.AxesValidator;
import cfa.vo.sed.setup.validation.ErrorValidator;
import cfa.vo.sed.setup.validation.IValidator;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.test.ConfigFactory;
import cfa.vo.sed.test.URLTestConverter;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author olaurino
 */
public class FileFormatsTest {

    public FileFormatsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Creating configuration files");

        URL fileURL = URLTestConverter.getURL("test:///test_data/fileformats.ini");
        System.out.println(fileURL);
        List<ISetup> confList = ConfigFactory.getAllFormatsConfigurations();

        SetupManager.write(confList, fileURL);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of read and write method, of class SetupManager.
     * The write method is tested each time this test is run to create the test files.
     */
    @Test
    public void testConfigurationIOFormats() throws Exception {
        SedBuilder builder = new SedBuilder();
        builder.init(new App(), new Ws());

        System.out.println("read all configurations in fileformats.ini");
        URL fileURL = URLTestConverter.getURL("test:///test_data/fileformats.ini");
        System.out.println(fileURL);
        List<ISetup> expResult = ConfigFactory.getAllFormatsConfigurations();
        List<ISetup> result = SetupManager.read(fileURL);
        assertEquals(expResult.size(), result.size());
        for(int i=0; i<result.size(); i++) {
            ISetup conf = result.get(i);
            assertTrue(expResult.get(i).equals(conf));
            IValidator val = new AxesValidator(new ErrorValidator(), true, (SetupBean) conf);
            assertTrue(val.isConfigurationValid());
//            SegmentImporter.getSegmentsMetadata(URLTestConverter.getURL(conf.getFileLocation()), NativeFileFormat.valueOf(conf.getFormatName()));
        }

        SegmentImporter.getSegments(result);
    }

}