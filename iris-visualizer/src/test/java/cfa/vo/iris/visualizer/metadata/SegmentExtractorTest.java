package cfa.vo.iris.visualizer.metadata;

import static org.junit.Assert.*;

import org.junit.Test;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.DateParam;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.TextParam;

public class SegmentExtractorTest {
    
    IrisStarTableAdapter adapter = new IrisStarTableAdapter(null);
    
    @Test
    public void testSingleSegmentExtractionAllRows() throws Exception {
        
        // Simple segment, three rows selected
        Segment seg1 = TestUtils.createSampleSegment();
        int[][] selection = new int[][] {{0,1,2}};
        
        IrisStarTable[] tables = new IrisStarTable[1];
        tables[0] = adapter.convertSegment(seg1);
        
        ExtSed sed = new ExtSed("FilterSed");
        SegmentExtractor extractor = new SegmentExtractor(tables, selection, sed);
        extractor.constructSed();
        
        // One segment that is identical to the original should be extracted
        assertEquals(1, sed.getNumberOfSegments());
        Segment newSeg = sed.getSegment(0);
        assertEquals(newSeg, seg1);
        
    }
    
    @Test
    public void testSingleSegmentExtractionSomeRows() throws Exception {
        
        Segment seg1 = TestUtils.createSampleSegment();
        int[][] selection = new int[][] {{1}};

        IrisStarTable[] tables = new IrisStarTable[1];
        tables[0] = adapter.convertSegment(seg1);
        
        ExtSed sed = new ExtSed("FilterSed");
        SegmentExtractor extractor = new SegmentExtractor(tables, selection, sed);
        extractor.constructSed();
        
        // One segment should be extracted
        assertEquals(1, sed.getNumberOfSegments());
        
        // Segment should have one point that is equal to 1st point in original segment
        Segment newSeg = sed.getSegment(0);
        assertEquals(1, newSeg.getData().getLength());
        assertEquals(seg1.getData().getPoint().get(1), newSeg.getData().getPoint().get(0));
    }
    
    @Test
    public void testMultipleSegmentExtractionAllRows() throws Exception {
        
        // Two segments, all rows selected
        Segment seg1 = TestUtils.createSampleSegment();
        Segment seg2 = TestUtils.createSampleSegment(new double[] {100,200,300}, new double[] {100,200,300});
        int[][] selection = new int[][] {{0,1,2},{0,1,2}};
        
        IrisStarTable[] tables = new IrisStarTable[2];
        tables[0] = adapter.convertSegment(seg1);
        tables[1] = adapter.convertSegment(seg2);
        
        tables[0].setName("test");
        tables[1].setName("test");
        
        ExtSed sed = new ExtSed("FilterSed");
        SegmentExtractor extractor = new SegmentExtractor(tables, selection, sed);
        extractor.constructSed();
        
        // Verify 2 segments are equal
        assertEquals(2, sed.getNumberOfSegments());
        assertEquals(sed.getSegment(0), seg1);
        assertEquals(sed.getSegment(1), seg2);
    }
    
    @Test
    public void testMultipleSegmentExtractionSomeRows() throws Exception {
        
        // Two segments, no rows selected from first, last 2 from second
        Segment seg1 = TestUtils.createSampleSegment();
        Segment seg2 = TestUtils.createSampleSegment(new double[] {100,200,300}, new double[] {100,200,300});
        int[][] selection = new int[][] {{},{1,2}};
        
        IrisStarTable[] tables = new IrisStarTable[2];
        tables[0] = adapter.convertSegment(seg1);
        tables[1] = adapter.convertSegment(seg2);
        
        ExtSed sed = new ExtSed("FilterSed");
        SegmentExtractor extractor = new SegmentExtractor(tables, selection, sed);
        extractor.constructSed();
        
        // Verify 2 segments are equal
        assertEquals(1, sed.getNumberOfSegments());
        assertEquals(2, sed.getSegment(0).getData().getLength());
    }
    
    @Test
    public void verifyCloning() throws Exception {
        Segment seg1 = TestUtils.createSampleSegment();
        seg1.createCuration();
        seg1.getCuration().setDate(new DateParam("04042014"));
        
        seg1.createTarget();
        seg1.getTarget().setName(new TextParam("Some Star"));

        IrisStarTable[] tables = new IrisStarTable[1];
        tables[0] = adapter.convertSegment(seg1);

        
        ExtSed sed = new ExtSed("FilterSed");
        SegmentExtractor extractor = new SegmentExtractor(tables, new int[][] {{0}}, sed);
        extractor.constructSed();
        Segment clone = sed.getSegment(0);
        
        assertEquals(clone.getCuration().getDate(), seg1.getCuration().getDate());
        assertEquals(clone.getTarget().getName(), seg1.getTarget().getName());
    }
}
