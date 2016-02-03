/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.stacker;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sed.builder.AsciiConf;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.iris.test.App;
import cfa.vo.iris.test.Ws;
import cfa.vo.sedlib.Segment;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SedStackTest {
    private Segment seg1;
    private Segment seg2;
    
    @Before
    public void setUp() throws Exception {

        SedBuilder builder = new SedBuilder();
        builder.init(new App(), new Ws());

        URL filename1 = AsciiConf.class
                .getResource("/test_data/ascii-conf-test.dat");
        SetupBean result1 = new AsciiConf().makeConf(filename1);
        seg1 = SegmentImporter.getSegments(result1).get(0);

        URL filename2 = AsciiConf.class
                .getResource("/test_data/ascii-conf-no-y_error.dat");
        SetupBean result2 = new AsciiConf().makeConf(filename2);
        seg2 = SegmentImporter.getSegments(result2).get(0);

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testRename() throws Exception {

        List<String> names = new ArrayList<>();
        names.add("Stack");
        for (int i = 0; i < 54; i++) {
            add(names, "Stack");
        }

        add(names, "Stack");

    }

    @Test
    public void testAddSedsFrameAddButton() throws Exception {
        SedStack stack = new SedStack("Stack");
        ExtSed sed = new ExtSed("Sed");
        sed.addSegment(seg1);
        sed.addSegment(seg2);
        ExtSed sed2 = new ExtSed("Sed2");
        sed2.addSegment(seg1);
        sed2.addSegment(seg2);
        stack.add(sed);
        stack.add(sed2);

        ExtSed sed3 = new ExtSed("Sed3");
        sed3.addSegment(seg1);
        sed3.addSegment(seg2);
        List<ExtSed> newSedList = new ArrayList<>();
        newSedList.add(sed3);

        addSedsFrame(stack, newSedList, false);
    }

    @Test
    public void testNormHashCodeChanged() throws Exception {

        SedStack stack = new SedStack("Stack");
        ExtSed sed = new ExtSed("Sed");
        sed.addSegment(seg1);
        sed.addSegment(seg2);
        ExtSed sed2 = new ExtSed("Sed2");
        sed2.addSegment(seg1);
        sed2.addSegment(seg2);
        stack.add(sed);
        stack.add(sed2);
        ExtSed sed3 = new ExtSed("Sed3");
        sed3.addSegment(seg1);
        sed3.addSegment(seg2);

        NormalizationConfiguration normConf = new NormalizationConfiguration();

        sed3.addAttachment("sedstacker: normConfHash", normConf.hashCode());
        sed.addAttachment("sedstacker: normConfHash", normConf.hashCode());
        sed2.addAttachment("sedstacker: normConfHash", normConf.hashCode());

        normConf.setIntegrate(true);
        normConf.setIntegrate(false);
        normConf.setIntegrate(true);
        normConf.setIntegrate(false);

        assertFalse(
                Integer.parseInt(sed3.getAttachment("sedstacker: normConfHash")
                        .toString()) == normConf.hashCode());
    }

    public void add(List<String> names, String newName) {

        char c = '@';
        int i = 1;
        int j = 1;
        while (names.contains(newName + (c == '@' ? ""
                : "." + StringUtils.repeat(String.valueOf(c), j)))) {
            int val = j * 26;
            if (i % val == 0) {
                c = '@';
                j++;
            }
            c++;
            i++;
        }
        names.add(newName + (c == '@' ? ""
                : "." + StringUtils.repeat(String.valueOf(c), j)));

    }

    public void addSedsFrame(SedStack stack, List<ExtSed> seds,
            boolean isSegmentAsSeds) throws Exception {
        for (ExtSed sed : seds) {

            if (!isSegmentAsSeds) {

                stack.add(sed);

            } else {

                for (int j = 0; j < sed.getNumberOfSegments(); j++) {

                    Segment seg = sed.getSegment(j);
                    ExtSed nsed = new ExtSed(
                            seg.getTarget().getName().getName());
                    nsed.addSegment(seg);
                    stack.add(nsed);

                }
            }
        }
    }
    
}