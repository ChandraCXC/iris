/**
 * Copyright (C) 2012, 2015, 2017 Smithsonian Astrophysical Observatory
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
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.units.YUnit;
import cfa.vo.iris.units.spv.UnitsFactory;
import cfa.vo.sed.builder.AsciiConf;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.io.File;
import java.net.URL;
import cfa.vo.sedlib.common.Utypes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.*;
import org.junit.Test;


public class SaveSedDialogTest extends AbstractComponentGUITest {

    @Override
    protected IrisComponent getComponent() {
        return new SedBuilder();
    }
    
    @Test
    public void testWriteNoTargetName() throws Exception {

        double[] x = {0.1, 0.2, 0.3};

        ExtSed sed = new ExtSed("test", false);
        sed.addSegment(TestUtils.createSampleSegment(x, x, "Hz", "erg/s/cm2/Angstrom"));
        
        File output = File.createTempFile("testWriteNoTargetName", ".dat");
        
        SaveSedDialog ssd = new SaveSedDialog(SedBuilder.getWorkspace().getRootFrame(), sed, false);
        ssd.setXunit("Hz");
        ssd.setYunit("erg/s/cm2/Angstrom");
        ssd.setFilePath("file:///" + output.getPath());
        ssd.writeAscii(sed, output, false);
        
        String expected = TestUtils.readFile(getClass(), "testWriteNoTargetName.dat");
        expected = removeFileCreated(expected);
        String result = FileUtils.readFileToString(output, "UTF-8");
        result = removeFileCreated(result);
        assertEquals(expected, result);
        
        output.deleteOnExit();
    }

    @Test
    public void testAsciiWrite() throws Exception {

        DoubleParam[] pos = {new DoubleParam(194.0465271), new DoubleParam(-5.789311)};
        double[] x = {0.1, 0.2, 0.3};

        ExtSed sed = new ExtSed("test", false);
        sed.addSegment(TestUtils.createSampleSegment(x, x, "Hz", "erg/s/cm2/Angstrom"));
        sed.getSegment(0).createTarget().createName().setValue("3C 273");
        sed.getSegment(0).getTarget().createPos().setValue(pos);

        File output = File.createTempFile("writeAsciiTest_3c273", ".dat");

        SaveSedDialog ssd = new SaveSedDialog(SedBuilder.getWorkspace().getRootFrame(), sed, false);
        ssd.setXunit("Hz");
        ssd.setYunit("erg/s/cm2/Angstrom");
        ssd.setFilePath("file:///" + output.getPath());
        ssd.writeAscii(sed, output, false);
        
        String expected = TestUtils.readFile(getClass(), "writeAsciiTest_3c273.dat");
        expected = removeFileCreated(expected);
        String result = FileUtils.readFileToString(output, "UTF-8");
        result = removeFileCreated(result);
        assertEquals(expected, result);

        // read it back in
        URL url = new URL(ssd.getFilePath());
        SetupBean bean = new AsciiConf().makeConf(url);
        Segment segment = SegmentImporter.getSegments(bean).get(0);

        assertEquals(segment.getSpectralAxisUnits(), "Hz");
        assertEquals(segment.getFluxAxisUnits(), "erg/s/cm2/Angstrom");
        Segment origSegment = sed.getSegment(0);
        YUnit yunit = UnitsFactory.makeYUnits(origSegment.getFluxAxisUnits());
        XUnit xunit = UnitsFactory.makeXUnits(origSegment.getSpectralAxisUnits());
        double[] yaxis = yunit.convert(origSegment.getFluxAxisValues(), origSegment.getSpectralAxisValues(), xunit, UnitsFactory.makeYUnits("erg/s/cm2/Angstrom"));
        double[] xaxis = xunit.convert(origSegment.getSpectralAxisValues(), xunit);
        double[] err = yunit.convertErrors((double[]) origSegment.getDataValues(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR), yaxis, xaxis, xunit, UnitsFactory.makeYUnits("erg/s/cm2/Angstrom"));
        assertArrayEquals(yaxis, segment.getFluxAxisValues(), 1e-6);
        assertArrayEquals(xaxis, segment.getSpectralAxisValues(), 1e-6);
        assertArrayEquals(err, (double[]) segment.getDataValues(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR), 1e-6);

        // Test SED created in Science window (from calculated fluxes)
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
        String targetName = segment2.getTarget().getName().getValue();
        assertEquals("3C 273", targetName);
        assert (metadata.contains("Iris Flux Integration output"));

        output.deleteOnExit();
        output2.deleteOnExit();

    }
    
    @Test
    public void testAsciiWriteIntegratedFluxes() throws Exception {
        ExtSed sed = createIntegratedFluxes();

        File output = File.createTempFile("testAsciiWriteIntegratedFluxes", ".dat");
        
        SaveSedDialog ssd = new SaveSedDialog(SedBuilder.getWorkspace().getRootFrame(), sed, false);
        ssd.setXunit("Hz");
        ssd.setYunit("erg/s/cm2/Angstrom");
        ssd.setFilePath("file:///" + output.getPath());
        ssd.writeAscii(sed, output, true);
        
        String expected = TestUtils.readFile(getClass(), "testAsciiWriteIntegratedFluxes.dat");
        expected = removeFileCreated(expected);
        String result = FileUtils.readFileToString(output, "UTF-8");
        result = removeFileCreated(result);
        assertEquals(expected, result);
        
        output.deleteOnExit();
    }
    
    public ExtSed createIntegratedFluxes() throws Exception {
        ExtSed newSed = new ExtSed("Integrated", false);
        
        double[] x = new double[]{1.0, 2.0, 3.0};
        double[] y = new double[]{1.0, 2.0, 3.0};
        
        Segment segment = new Segment();
        segment.setSpectralAxisValues(x);
        segment.setFluxAxisValues(y);
        segment.setSpectralAxisUnits("Hz");
        segment.setFluxAxisUnits("erg/s/cm2/Angstrom");
        segment.createChar().createSpectralAxis().setUcd("em.wl");
        segment.createChar().createFluxAxis().setUcd("phot.flux;em.wl");
        newSed.addSegment(segment);
        newSed.checkChar();
        
        return newSed;
    }

    private String removeFileCreated(String original) {
        return original.replaceAll("File created on .*", "");
    }

}
