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

package cfa.vo.sed.filters;

import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.setup.ErrorType;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.setup.validation.AxesValidator;
import cfa.vo.sed.setup.validation.ErrorValidator;
import cfa.vo.sed.setup.validation.IValidator;
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.URLTestConverter;
import cfa.vo.sed.test.Ws;
import cfa.vo.sedlib.Segment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author olaurino
 */
public class FITSFilterTest {

    public FITSFilterTest() {
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

    /**
     * Test of getTableBuilder method, of class FITSFilter.
     */
    @Test
    public void testGetTableBuilder() throws Exception {

        SedBuilder builder = new SedBuilder();
        builder.init(new App(), new Ws());

        SetupBean conf = new SetupBean();

        IValidator val = new AxesValidator(new ErrorValidator(), true, (SetupBean) conf);

        conf.setErrorType(ErrorType.ConstantValue.name());
        conf.setConstantErrorValue("2.0");
        conf.setFileLocation(URLTestConverter.getURL("test:///test_data/3c273_842410_2_hw.fits").toString());
        conf.setFormatName("FITS");
        conf.setPublisher("");
        conf.setTargetName("3c273");
        conf.setTargetRa("187.27791798");
        conf.setTargetDec("2.05238729");
        conf.setXAxisColumnNumber(0);
        conf.setYAxisColumnNumber(1);
        conf.setXAxisQuantity("FREQUENCY");
        conf.setXAxisUnit("HERTZ");
        conf.setYAxisQuantity("FLUX");
        conf.setYAxisUnit("FLUX0");

        val.isConfigurationValid();

        Segment segment = SegmentImporter.getSegments(conf).get(0);
    }

}