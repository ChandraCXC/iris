/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.builder;

import cfa.vo.sed.test.Ws;
import cfa.vo.sed.test.App;
import java.net.URL;
import cfa.vo.sedlib.Segment;
import cfa.vo.sed.setup.SetupBean;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jamie
 */
public class AsciiConfTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of makeConf method, of class AsciiConf.
	 */
	@Test
	public void testMakeConf() throws Exception {
		
		System.out.println("makeConf");
		
		URL filenameOK = AsciiConf.class.getResource("/test_data/ascii-conf-test.dat");
		SetupBean resultOK = new AsciiConf().makeConf(filenameOK);
		
		double[] errors = new double[4];
		errors[0]=1.0;
		errors[1]=1.0;
		errors[2]=Double.NaN;
		errors[3]=1.0;
		
		SedBuilder builder = new SedBuilder();
                builder.init(new App(), new Ws());

		Segment segmentOK = SegmentImporter.getSegments(resultOK).get(0);
                
		assertEquals(segmentOK.getFluxAxisValues().length, 4);
		assertEquals(segmentOK.getFluxAxisUnits(), "mJy");
		assertEquals(segmentOK.getSpectralAxisUnits(), "cm");
                segmentOK.getDataInfo("Spectrum.Data.FluxAxis.Accuracy.StatError");
                double[] resultErrors = (double[]) segmentOK.getDataValues("Spectrum.Data.FluxAxis.Accuracy.StatError");
                for (int i=0; i<4; i++) {
                    assertEquals(errors[i], resultErrors[i]);
                }
	}
	
	@Test
	public void testMakeConfNoErrors() throws Exception {
		URL filenameNoErrors = AsciiConf.class.getResource("/test_data/ascii-conf-no-y_error.dat");
		SetupBean resultNoErrors = new AsciiConf().makeConf(filenameNoErrors);
                
		Segment segmentNoErrors = SegmentImporter.getSegments(resultNoErrors).get(0);
		assertEquals(segmentNoErrors.getFluxAxisValues().length, 4);
		assertEquals(segmentNoErrors.getFluxAxisUnits(), "Jy");
		assertEquals(segmentNoErrors.getSpectralAxisUnits(), "Hz");
                assertEquals(null, segmentNoErrors.getDataInfo("Spectrum.Data.FluxAxis.Accuracy.StatError"));
	}
        
        @Test (expected=AsciiConfException.class)
	public void testMakeConfBadFileFormat() throws Exception {
		URL filename = AsciiConf.class.getResource("/test_data/3c273.dat");
		SetupBean result = new AsciiConf().makeConf(filename);
	}
}