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

import cfa.vo.iris.visualizer.stil.IrisStarJTable;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * A class that validates an input filter expression for numeric data.
 */
public class FilterDoubleExpressionValidator {
    
    IrisStarJTable starJTable; // the stacked startable to filter
    ComparisonDoubleEvaluator doubleEvaluator; // evaluator for numeric columns
    
    public FilterDoubleExpressionValidator(IrisStarJTable table) {
        this.starJTable = table;
        this.doubleEvaluator = new ComparisonDoubleEvaluator();
    }
    
    /**
     * Find all rows the in the IrisStarJTable that comply with the given filter
     * expression.
     * 
     * @param expression
     * @return the array of row indices that comply with the filter expression. 
     * 
     * @throws cfa.vo.iris.visualizer.metadata.FilterExpressionException if the 
     *         expression is invalid.
     */
    public List<Integer> process(String expression) throws FilterExpressionException {
        
        // initial check for bad expressions
        if (expression.isEmpty()) {
            throw new FilterExpressionException(FilterExpressionException.EMPTY_EXPRESSION_MSG);
        }
        if (!expression.contains("$")) {
            throw new FilterExpressionException(FilterExpressionException.DEFAULT_MSG);
        }
        
        // get column specifiers
        List<String> colSpecifiers = findColumnSpecifiers(expression);
        
        // javaluator data structure. Stores table column values
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        
        // list to hold evaluated expressions
        List<Double> evaluatedExpression = new ArrayList<>();
        
        // Evaluate the expression for each row in the IrisStarJTable.
        for (int i=0; i<this.starJTable.getStarTable().getRowCount(); i++) {
            
            int colNumber;
            
            // Get the specified column values. The values are added to the  
            // StaticVariableSet for the evaluator.
            for (String colName : colSpecifiers) {
                
                // right now, only numbered column specifiers are allowed.
                // TODO: fix this hack:
                // to make sure only numbers are used as column specifiers, 
                // throw an exception here:
                try {
                    colNumber = Integer.parseInt(colName);
                } catch (NumberFormatException ex) {
                    throw new FilterExpressionException(FilterExpressionException.NON_NUMERIC_COLUMN_NAME_MSG);
                }
                
                try {
                    if (!this.starJTable.getStarTable().getColumnInfo(colNumber).getContentClass().isAssignableFrom(String.class)) {
                        variables.set("$"+String.valueOf(colNumber), (Double) this.starJTable.getStarTable().getCell(i, colNumber));
                    } else {
                        throw new FilterExpressionException("Only numeric columns may be filtered at this time.");
                    }
                } catch (NoSuchElementException ex) {
                    throw new FilterExpressionException("Bad expression: "
                    + "Specified column $"+colNumber+" does not exist.");
                } catch (IOException ex) {
                    Logger.getLogger(FilterDoubleExpressionValidator.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
            
            // evaluate the expression
            // TODO: should I just let javaluator throw the exception, 
            // or is it better to catch javaluator's exception, and create our 
            // like I do here?
            try {
                evaluatedExpression.add(doubleEvaluator.evaluate(expression, variables));
            } catch (IllegalArgumentException ex) {
                throw new FilterExpressionException(FilterExpressionException.BAD_PARENTHESES_MSG);
            }
        }
        
        // return array of indices to select
        return getFilteredIndices(evaluatedExpression);
    }
    
    private List<Integer> getFilteredIndices(List<Double> evaluatedExpression) {
        // TODO: come up with more efficient way of getting non-null indices.
        
        // get non-null indices from evaluatedExpression list
        List<Integer> indices = new ArrayList<>();
        for (int i=0; i<evaluatedExpression.size(); i++) {
            Double val = evaluatedExpression.get(i);
            if (!val.equals(Double.NaN))
                indices.add(i);
        }
        return indices;
    }
    
    /**
     * Finds all the column specifiers in the expression. Columns are
     * denoted by the prefix "$".
     * @param expression - the filter expression to parse for column names.
     * @return a list of Strings containing all column names, in order as they
     * appear in the filter expression.
     */
    static List<String> findColumnSpecifiers(String expression) throws FilterExpressionException {
        
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
    
    /**
     * Process a simple expression and return a list of values
     * @param expression
     * @param col
     * @return evaluated expression
     * If no values in <tt>col</tt> match the condition, the function returns 
     * null.
     * @throws cfa.vo.iris.visualizer.metadata.FilterExpressionException if the 
     *         expression is invalid.
     */
    public List<Double> processSingleExpression(String expression, double[] col) throws FilterExpressionException {
        
        DoubleEvaluator evaluator = new DoubleEvaluator();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        
        // to hold evaluated expression
        List<Double> evaluatedExpression = new ArrayList<>();
        
        // find the column name
        String colName = findColumnSpecifiers(expression).get(0);
        
        // evaluate for every element in col
        for (double value : col) {
            variables.set(colName, value); // set variable name from colName
            evaluatedExpression.add(evaluator.evaluate(expression, variables));
        }
        
        return evaluatedExpression;
    }
}
