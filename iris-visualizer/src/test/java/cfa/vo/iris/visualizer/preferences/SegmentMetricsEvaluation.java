package cfa.vo.iris.visualizer.preferences;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.Test;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.utils.Metrics;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;

public class SegmentMetricsEvaluation {
    
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);
    
    @AfterClass
    public static void tearDown() {
        executor.shutdown();
    }
    
    @Test
    public void testLargeSegment() throws Exception {
        Metrics m = new Metrics("testLargeSegment");
        URL benchmarkURL = TestData.class.getResource("test300k_VO.fits");
        ExtSed sed = ExtSed.read(benchmarkURL.openStream(), SedFormat.FITS);
        
        Metrics con = m.openMetrics("constructSegStarTable");
        SegmentStarTable t = new SegmentStarTable(sed.getSegment(0));
        con.close();
        
        Metrics sedModelCon = m.openMetrics("sedModelCon");
        SedModel model = new SedModel(sed, new IrisStarTableAdapter(executor));
        sedModelCon.close();
        
        System.out.println(m.report());
    }
    
    @Test
    public void testManySegments() throws Exception {
        Metrics m = new Metrics("testManySegments");
        
        ExtSed sed = new ExtSed("test", false);
        for (int i=0; i<200; i++) {
            sed.addSegment(TestUtils.createSampleSegment(new double[] {0}, new double[] {1}));
        }
        
        Metrics sedModelCon = m.openMetrics("sedModelCon");
        SedModel model = new SedModel(sed, new IrisStarTableAdapter(executor));
        sedModelCon.close();
        
        System.out.println(m.report());
    }
}
