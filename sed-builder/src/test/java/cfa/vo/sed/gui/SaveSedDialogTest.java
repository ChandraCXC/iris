/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.gui;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sed.builder.AsciiConf;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.DesktopWs;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.io.File;
import java.net.URL;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Sed;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 *
 * @author jbudynk
 */
public class SaveSedDialogTest {
    //ExtSed sed;
    //String output;
    //String output2;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
	
    }
    @Test
    public void testWriteAscii() throws Exception {
	
	SedBuilder builder = new SedBuilder();
	builder.init(new App(), new DesktopWs());

	URL fileURL = SaveSedDialog.class.getResource("/test_data/3c273.xml");

	Sed s = Sed.read(fileURL.openStream(), SedFormat.VOT);
	List<Segment> segList = new ArrayList();
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
	
	File output = File.createTempFile("writeAsciiTest_3c273",".dat");
	
	SaveSedDialog ssd = new SaveSedDialog(SedBuilder.getWorkspace().getRootFrame(), sed, false);
	ssd.setXunit("Hz");
	ssd.setYunit("erg/s/cm2/Angstrom");
	ssd.setFilePath("file:///"+output.getPath());	
	ssd.writeAscii(sed, output, false);
	
	URL url = new URL(ssd.getFilePath());
	SetupBean result = new AsciiConf().makeConf(url);
	Segment segment = SegmentImporter.getSegments(result).get(0);
	
	assertEquals(segment.getSpectralAxisUnits(), "Hz");
	assertEquals(segment.getFluxAxisUnits(), "erg/s/cm2/Angstrom");
	
	
	// Test SED created in Science window (from calculated fluxes
	File output2 = File.createTempFile("writeAsciiTest_3c273-2",".dat");
	ssd.setFilePath("file:///"+output2.getPath());	
	ssd.writeAscii(sed, output2, true);
	
	URL url2 = new URL(ssd.getFilePath());
	
	URLConnection urlConnection = url2.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        String line;
	String metadata = "";

        while ((line = bufferedReader.readLine()).startsWith("#")) {
	    metadata = metadata+line;
        }
	
	SetupBean result2 = new AsciiConf().makeConf(url2);
	Segment segment2 = SegmentImporter.getSegments(result2).get(0);
	
	assertEquals(segment2.getSpectralAxisUnits(), "Hz");
	assertEquals(segment2.getFluxAxisUnits(), "erg/s/cm2/Angstrom");
	assert(metadata.contains("Iris Flux Integration output"));
	
	output.deleteOnExit();
	output2.deleteOnExit();
	
    }
    
}
