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
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.Ws;
import com.google.common.io.Files;
import java.io.File;
import java.net.URL;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author olaurino
 */
public class SameUrlDifferentFilesTest {

    public SameUrlDifferentFilesTest() {
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
    public void testSameUrlDifferentFiles() throws Exception {

        SedBuilder builder = new SedBuilder();
        builder.init(new App(), new Ws());

        URL firstURL = getClass().getResource("/test_data/3c273.xml");
        URL secondURL = getClass().getResource("/test_data/mine.vot");

        File firstFile = new File(firstURL.toURI());
        File secondFile = new File(secondURL.toURI());

        IFilter firstInstance = FilterCache.getInstance(NativeFileFormat.VOTABLE.getFilter(null).getClass(), firstURL);

        Thread.sleep(2000);

        IFilter secondInstance = FilterCache.getInstance(NativeFileFormat.VOTABLE.getFilter(null).getClass(), firstURL);

        Assert.assertEquals(true, firstInstance == secondInstance);

        String filename = firstFile.getAbsolutePath().concat("something");
        URL url = new URL("file:"+filename);
        File testFile = new File(filename);

        Files.copy(firstFile, testFile);

        IFilter firstTestInstance = FilterCache.getInstance(NativeFileFormat.VOTABLE.getFilter(null).getClass(), url);

        Thread.sleep(2000);

        Files.copy(secondFile, testFile);

        IFilter secondTestInstance = FilterCache.getInstance(NativeFileFormat.VOTABLE.getFilter(null).getClass(), url);

        Assert.assertEquals(true, firstTestInstance != secondTestInstance);

    }


}