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

package cfa.vo.iris.common;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.RangeParam;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.SedWritingException;
import cfa.vo.sedlib.common.ValidationError;
import cfa.vo.sedlib.io.SedFormat;
import java.io.IOException;
import java.io.OutputStream;
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
public class SedTest {
    private Sed mySed;

    public SedTest() {
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
     public void sedMessageTest() throws Exception {
//        System.out.println(getClass().getResource("/test_data/3C273.vot").getPath());
//        Sed sed = Sed.read(getClass().getResource("/test_data/3C273.vot").getPath(), SedFormat.VOT);
//        SedManager man = new SedManager();
//        SedManager.SpecviewSed s = man.new SpecviewSed(sed, "3c273");
//        Assert.assertEquals("3c273", s.getId());
//        Assert.assertEquals("NASA/IPAC Extragalactic Database (NED)", s.getSegment(0).getCuration().getPublisher().getValue());
//        s.addAttachment("test", "TEST");
//        String string = (String) s.getAttachment("test");
//        Assert.assertEquals("TEST", string);
//        s.write(getClass().getResource("/test_data/test.vot").getPath(), SedFormat.VOT);
     }

    public class SedManager extends SedlibSedManager {
        public class SpecviewSed extends ExtSed {

            private Sed sed;

            public SpecviewSed(Sed sed, String name) {
                super(name);
                this.sed = sed;
            }

            public void write(String filename, SedFormat format) throws SedInconsistentException, SedWritingException, IOException {
                sed.write(filename, format);
            }

            public void write(OutputStream os, SedFormat format) throws SedInconsistentException, SedWritingException, IOException {
                sed.write(os, format);
            }

            public boolean validate(List<ValidationError> errors) {
                return sed.validate(errors);
            }

            public boolean validate() {
                return sed.validate();
            }

            public void setNamespace(String namespace) {
                sed.setNamespace(namespace);
            }

            public void removeSegment(int segment) {
                sed.removeSegment(segment);
            }

            public boolean isSetNamespace() {
                return sed.isSetNamespace();
            }

            public int hashCode() {
                return sed.hashCode();
            }

            public Segment getSegment(int segment) {
                return sed.getSegment(segment);
            }

            public int getNumberOfSegments() {
                return sed.getNumberOfSegments();
            }

            public String getNamespace() {
                return sed.getNamespace();
            }

            public Sed filterSed(List<RangeParam> rangeParamList) throws SedInconsistentException {
                return sed.filterSed(rangeParamList);
            }

            public Sed filterSed(RangeParam rangeParam) throws SedInconsistentException {
                return sed.filterSed(rangeParam);
            }

            public Sed filterSed(double start, double end, String unit) throws SedInconsistentException {
                return sed.filterSed(start, end, unit);
            }

            public boolean equals(Object obj) {
                return sed.equals(obj);
            }

            public SpecviewSed clone() {
                SpecviewSed s = (SpecviewSed) super.clone();
                return s;
            }

            public void addSegment(List<Segment> segments, int offset) throws SedInconsistentException, SedNoDataException {
                sed.addSegment(segments, offset);
            }

            public void addSegment(List<Segment> segments) throws SedInconsistentException, SedNoDataException {
                sed.addSegment(segments);
            }

            public void addSegment(Segment segment, int offset) throws SedInconsistentException, SedNoDataException {
                sed.addSegment(segment, offset);
            }

            public void addSegment(Segment segment) throws SedInconsistentException, SedNoDataException {
                sed.addSegment(segment);
            }


        }
    }

}