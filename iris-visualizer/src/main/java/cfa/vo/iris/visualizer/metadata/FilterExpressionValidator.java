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

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.starlink.table.ColumnData;

/**
 *
 * A class that validates an input filter expression.
 */
public class FilterExpressionValidator {
    
    List<IrisStarTable> starTables; // list of starTables to filter
    
    public FilterExpressionValidator(List<IrisStarTable> tables) {
        this.starTables = tables;
    }
    
    public int[] process(String expression) {
        
        List<String> colSpecifiers = findColumnSpecifiers(expression);
        
        DoubleEvaluator evaluator = new DoubleEvaluator();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        
        Iterator itr = colSpecifiers.iterator();
        while (itr.hasNext()) {
//            variables.set(itr.next(), );
        }
        
        for (IrisStarTable table : this.starTables) {
//            ColumnData column = table.getPlotterTable().getColumnData();
        }
        
        return null;
    }
    
    /**
     * Finds all the column specifiers in the expression. Columns are
     * denoted by the prefix "$".
     * @param expression - the filter expression to parse for column names.
     * @return a list of Strings containing all column names, in order as they
     * appear in the filter expression.
     */
    static List<String> findColumnSpecifiers(String expression) {
        
        List<String> colSpecifiers = new ArrayList<>();
        
        // split the expression into simple expressions, using the
        // column specifier "$" as the divider. This means the next numeric
        // characters in the array are the columns.
        String[] tmp = expression.split("\\$");
        
        // parse each sub-expression to identify the columns
        for (String exp : tmp) {
            
            // look for next non-number.
            String col;
            Matcher matcher = Pattern.compile("\\d+").matcher(exp);
            matcher.find();
            try {
                col = String.valueOf(matcher.group());
            } catch (IllegalStateException ex) {
                // catches any empty strings before the first "$"
                continue;
            }
            
            // extract the column identifier from the sub-expression
            colSpecifiers.add(exp.substring(0, exp.indexOf(col)+col.length()));
        }
        
        return colSpecifiers;
    }
    
    public enum OperatorType {
        EQ("=="),
        GE(">="),
        GT(">"),
        LE("<="),
        LT("<"),
        NE("!=");
        
        public String symbol;
    
        private OperatorType(String symbol) {
            this.symbol = symbol;
        }
    }
}
