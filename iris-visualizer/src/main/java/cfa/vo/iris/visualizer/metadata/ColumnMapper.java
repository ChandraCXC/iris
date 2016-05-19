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

import com.fathzer.soft.javaluator.StaticVariableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.starlink.table.StarTable;

/**
 *
 * A class which maps column specifiers in a filter expression to columns in a 
 * StarTable, and evaluates each StarTable row with the expression. 
 */
public class ColumnMapper {

    private StarTable table;
    private List<Integer> columnIndexes;
    private String expression;
    private ComparisonDoubleEvaluator doubleEvaluator;

    public ColumnMapper(StarTable table, String expression) {
       this.expression = expression;
       this.table = table;
       this.doubleEvaluator = new ComparisonDoubleEvaluator();
    }

    /**
     * Evaluate a StarTable row based on the filter. 
     * @param tableRow the row to evaluate
     * @return the filter expression evaluated on the table row
     * @throws IllegalArgumentException if the column specifiers in the filter 
     * expression are invalid (e.g., do not exist, contain non-numeric values), 
     * or if the expression is malformed (mismatched parentheses).
     */
    public Double evaluateRow(Object[] tableRow) throws IllegalArgumentException {

        Double evaluatedExpression;
        List<String> columns = findColumnSpecifiers(expression);
        StaticVariableSet<Double> variables = new StaticVariableSet<>();

        // Get the specified column values. The values are added to the 
        // StaticVariableSet for the evaluator.
        for (String column : columns) {

            // right now, only numbered column specifiers are allowed.
            // TODO: generalize this for string-valued column specifiers
            try {
                variables.set("$"+column, (Double) tableRow[Integer.valueOf(column)]);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(FilterExpressionException.NON_NUMERIC_COLUMN_NAME_MSG);
            } catch (NoSuchElementException ex) {
                throw new NoSuchElementException("Bad expression: "
                + "Specified column $"+column+" does not exist.");
            } catch (ClassCastException ex) {
                throw new IllegalArgumentException(FilterExpressionException.STRING_COLUMN_MSG);
            }
        }

        // evaluate the expression
        evaluatedExpression = doubleEvaluator.evaluate(expression, variables);

        return evaluatedExpression;
    }

    /**
     * Finds all the column specifiers in the expression. Columns are
     * denoted by the prefix "$".
     * @param expression - the filter expression to parse for column names.
     * @return a list of Strings containing all column names, in order as they
     * appear in the filter expression.
     */
    public static List<String> findColumnSpecifiers(String expression) {

        List<String> colSpecifiers = new ArrayList<>();

        // split the expression into simple expressions, using the
        // column specifier "$" as the divider. This means the next numeric
        // characters in the array are the columns.
        String[] tmp = expression.split("\\$");

        String col;

        // parse each sub-expression to identify the columns
        for (String exp : tmp) {

            // look for next non-number.
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
}
