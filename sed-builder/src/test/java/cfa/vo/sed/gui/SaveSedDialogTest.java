/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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

package cfa.vo.sed.gui;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.sed.builder.AsciiConf;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.io.File;
import java.net.URL;
import java.util.List;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Sed;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;


public class SaveSedDialogTest extends AbstractComponentGUITest {

    @Override
    protected IrisComponent getComponent() {
        return new SedBuilder();
    }

    public void testWriteAscii() throws Exception {

        URL fileURL = SaveSedDialog.class.getResource("/test_data/3c273.xml");

        Sed s = Sed.read(fileURL.openStream(), SedFormat.VOT);
        List<Segment> segList = new ArrayList<>();
        // To assign the RA and DEC correctly, in case the pos.eq is in
        // SpatialAxis.Coverage.Location rather than Target.Pos
        for (int i = 0; i < s.getNumberOfSegments(); i++) {
            Segment seg = s.getSegment(i);
            if (s.getSegment(i).createTarget().getPos() == null) {
                if (seg.createChar().createSpatialAxis().createCoverage().getLocation() != null) {
                    seg.createTarget().createPos().setValue(seg.getChar().getSpatialAxis().getCoverage().getLocation().getValue());
                } else {
                    seg.createTarget().createPos().setValue(new DoubleParam[]{new DoubleParam(Double.NaN), new DoubleParam(Double.NaN)});
                }
            }
            segList.add(s.getSegment(i));
        }

        ExtSed sed = new ExtSed(fileURL.getPath());
        sed.addSegment(segList);

        File output = File.createTempFile("writeAsciiTest_3c273", ".dat");

        SaveSedDialog ssd = new SaveSedDialog(SedBuilder.getWorkspace().getRootFrame(), sed, false);
        ssd.setXunit("Hz");
        ssd.setYunit("erg/s/cm2/Angstrom");
        ssd.setFilePath("file:///" + output.getPath());
        ssd.writeAscii(sed, output, false);

        URL url = new URL(ssd.getFilePath());
        SetupBean result = new AsciiConf().makeConf(url);
        Segment segment = SegmentImporter.getSegments(result).get(0);

        assertEquals(segment.getSpectralAxisUnits(), "Hz");
        assertEquals(segment.getFluxAxisUnits(), "erg/s/cm2/Angstrom");


        // Test SED created in Science window (from calculated fluxes
        File output2 = File.createTempFile("writeAsciiTest_3c273-2", ".dat");
        ssd.setFilePath("file:///" + output2.getPath());
        ssd.writeAscii(sed, output2, true);

        URL url2 = new URL(ssd.getFilePath());

        URLConnection urlConnection = url2.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        String line;
        String metadata = "";

        while ((line = bufferedReader.readLine()).startsWith("#")) {
            metadata = metadata + line;
        }

        SetupBean result2 = new AsciiConf().makeConf(url2);
        Segment segment2 = SegmentImporter.getSegments(result2).get(0);

        assertEquals(segment2.getSpectralAxisUnits(), "Hz");
        assertEquals(segment2.getFluxAxisUnits(), "erg/s/cm2/Angstrom");
        assert (metadata.contains("Iris Flux Integration output"));

        output.deleteOnExit();
        output2.deleteOnExit();

    }

}
