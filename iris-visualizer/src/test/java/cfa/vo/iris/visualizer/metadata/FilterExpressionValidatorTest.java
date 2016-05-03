/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.metadata;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import java.util.List;
import java.util.concurrent.Executors;
import static org.junit.Assert.*;
import org.junit.Test;
import uk.ac.starlink.table.StarTable;

/**
 *
 * @author jbudynk
 */
public class FilterExpressionValidatorTest {
    
    private IrisStarTableAdapter adapter = new IrisStarTableAdapter(Executors.newSingleThreadExecutor());
    private FilterExpressionValidator validator;
    
    public FilterExpressionValidatorTest() {
        
    }

    @Test
    public void testSimpleExpression() throws Exception {
        
        double[] x = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        double[] y = x;
        
        Segment seg = TestUtils.createSampleSegment(x, y);
        
        IrisStarTable table = adapter.convertSegment(seg);
        
        SegmentStarTable plotterTable = table.getPlotterTable();
        StarTable dataTable = table.getSegmentDataTable();
        
        // get all the points whose spectral value is greater than 5
        String expression = "$1 > 5";
        
        
    }
    
    @Test
    public void testFindColumnSpecifiers() {
        String expression = "$2 > 5";
        List<String> colSpecifier = FilterExpressionValidator.findColumnSpecifiers(expression);
        assertEquals("2", colSpecifier.get(0));
        
        expression = "$1 > 5 AND $12*2 <= 10";
        colSpecifier = FilterExpressionValidator.findColumnSpecifiers(expression);
        assertEquals(2, colSpecifier.size());
        assertEquals("1", colSpecifier.get(0));
        assertEquals("12", colSpecifier.get(1));
        
        expression = "$1 > 5 AND $12*2 <= 10 OR 2*$1 > 10";
        colSpecifier = FilterExpressionValidator.findColumnSpecifiers(expression);
        assertEquals(3, colSpecifier.size());
        assertEquals("1", colSpecifier.get(0));
        assertEquals("12", colSpecifier.get(1));
        assertEquals("1", colSpecifier.get(2));
    }
    
}
