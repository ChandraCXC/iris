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

import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.stil.IrisStarJTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import java.util.List;
import java.util.concurrent.Executors;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * Tests for Filter Expression Validator
 */
public class FilterExpressionValidatorTest {
    
    private IrisStarTableAdapter adapter = new IrisStarTableAdapter(Executors.newSingleThreadExecutor());
    private FilterExpressionValidator validator;
    IrisStarJTable jTable;
    
    public FilterExpressionValidatorTest() {
        
    }
    
    @Before
    public void setUp() throws Exception {
        // setup table to filter
        double[] x = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        double[] y = x;
        Segment seg = TestUtils.createSampleSegment(x, y);
        
        IrisStarTable table = adapter.convertSegment(seg);
        jTable = new IrisStarJTable();
        jTable.setStarTable(table);
        
        // filter validator
        validator = new FilterExpressionValidator(jTable);
    }

    @Test
    public void testSimpleExpression() throws Exception {
        
        String expression; // filter expression
        
        // get all the points whose value is equal to 5
        expression = "$1 == 5";
        
        // the evaluator returns the array of rows whose data complies
        // with the filter expression
        assertArrayEquals(new int[]{4}, validator.process(expression));
        
        // get all points whose value is not equal to 5
        expression = "$1 != 5";
        assertArrayEquals(new int[]{0, 1, 2, 3, 5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
        // get all points whose value is less than 5
        expression = "$1 < 5";
        assertArrayEquals(new int[]{0, 1, 2, 3}, 
                validator.process(expression));
        
        // get all points whose value is greater than 5
        expression = "$1 > 5";
        assertArrayEquals(new int[]{5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
        // get all points whose value is less than or equal to 5
        expression = "$1 <= 5";
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, 
                validator.process(expression));
        
        // get all points whose value is greater than or equal to 5
        expression = "$1 >= 5";
        assertArrayEquals(new int[]{4, 5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
    }
    
    @Test
    public void testMoreComplicated() throws Exception {
        String expression; // filter expression
        
        // get all the points whose value is < 6
        expression = "$1 + 2 < 6";
        
        // the evaluator returns the array of rows whose data complies
        // with the filter expression
        assertArrayEquals(new int[]{0, 1, 2}, validator.process(expression));
        
        // get all the points whose value is greater than or equal to 10
        expression = "$2*3 -6 >= 10 - 1";
        assertArrayEquals(new int[]{4, 5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
        //
        // test comparing values in different columns
        //
        
        // should give all matching rows since $1 and $2 are equal
        expression = "$1 == $2";
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
        expression = "$2 *2 !=$1";
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
        expression = "$2 != $1";
        assertArrayEquals(new int[]{}, 
                validator.process(expression));
        
        expression = "$1 *4 < $2+10";
        assertArrayEquals(new int[]{0, 1, 2}, 
                validator.process(expression));
        
        expression = "$1 *4 > $2/2 + 10";
        assertArrayEquals(new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
        // two columns specified on one side of the comparison
        expression = "$1 + $2*2 == $2*3";
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 
                validator.process(expression));
        
    }
    
    @Test
    public void testFindColumnSpecifiers() throws Exception {
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
    
    //
    // Invalid expression tests.
    //
    // expressions should follow this format:
    //    LHS comparison_operator RHS
    // where either LHS or RHS must have a column specifier ($columnNumber).
    // More than one column specifier can be defined on either side of the 
    // operator. Column specifiers can be combined in any arthmatic way.
    
    @Rule public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void testInvalidExpressionEmpty() throws Exception {
        
        // empty expression. Should throw IllegalArgumentException.
        String expression = "";
        exception.expect(FilterExpressionException.class);
        exception.expectMessage(FilterExpressionException.EMPTY_EXPRESSION_MSG);
        validator.process(expression);
    }
    @Test
    public void testInvalidExpressionNoColumnSpecifiers() throws Exception {
        // if no column specifier is in the expression, raise a warning
        String expression = "1 * 5";
        // "must specify a column"
        exception.expect(FilterExpressionException.class);
        exception.expectMessage(FilterExpressionException.DEFAULT_MSG);
        validator.process(expression);
    }
    
    @Test
    public void testInvalidExpressionColumnDNE1() throws Exception {
        // column specified does not exist
        String expression = "$5 * 2 > $1";
        exception.expect(FilterExpressionException.class);
        exception.expectMessage(FilterExpressionException.COLUMN_DNE_MSG);
        validator.process(expression);
        System.out.println("");
    }
    
    @Test
    public void testInvalidExpressionColumnDNE2() throws Exception {
        // column specified does not exist
        String expression = "$whats_good * 2 > 1";
        exception.expect(FilterExpressionException.class);
        // TODO: change exception message to COLUMN_DNE_MSG when string
        // column specifiers are implemented.
        exception.expectMessage(FilterExpressionException.NON_NUMERIC_COLUMN_NAME_MSG);
        validator.process(expression);
    }
    @Test
    public void testInvalidExpressionBadParentheses() throws Exception {
        // badly placed parenthesis
        String expression = "($1 * 2)/ 3) > 5";
        exception.expect(FilterExpressionException.class);
        exception.expectMessage(FilterExpressionException.BAD_PARENTHESES_MSG);
        validator.process(expression);
    }
    
    @Test
    public void testInvalidExpressionNonsense() throws Exception {
        // badly placed parenthesis
        String expression = "< asdkurb";
        exception.expect(FilterExpressionException.class);
        exception.expectMessage(FilterExpressionException.DEFAULT_MSG);
        validator.process(expression);
    }    
}
