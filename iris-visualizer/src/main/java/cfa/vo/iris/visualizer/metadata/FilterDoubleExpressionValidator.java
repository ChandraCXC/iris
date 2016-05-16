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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.table.StarTable;

/**
 *
 * A class that validates an input filter expression for numeric data.
 */
public class FilterDoubleExpressionValidator {
    
    StarTable starTable; // the StarTable to filter
    ComparisonDoubleEvaluator doubleEvaluator; // evaluator for numeric columns
    
    public FilterDoubleExpressionValidator(StarTable table) {
        this.starTable = table;
        this.doubleEvaluator = new ComparisonDoubleEvaluator();
    }
    
    /**
     * Find all rows the in the IrisStarJTable that comply with the given filter
     * expression.
     * 
     * @param expression
     * @return the array of row indices that comply with the filter expression. 
     * 
     * @throws IllegalArgumentException if the expression is invalid.
     */
    public List<Integer> process(String expression) throws IllegalArgumentException {
        
        // initial check for bad expressions
        if (expression.isEmpty()) {
            throw new IllegalArgumentException(FilterExpressionException.EMPTY_EXPRESSION_MSG);
        }
        if (!expression.contains("$")) {
            throw new IllegalArgumentException(FilterExpressionException.DEFAULT_MSG);
        }
        
        ColumnMapper mapper = new ColumnMapper(starTable, expression);
        
        // list to hold evaluated expressions
        List<Double> evaluatedExpression = new ArrayList<>();
        
        // Evaluate the expression for each row in the IrisStarJTable.
        for (int i=0; i<this.starTable.getRowCount(); i++) {
            
            // evaluate the expression
            try {
                evaluatedExpression.add(mapper.evaluateRow(starTable.getRow(i)));
            
            } catch (IOException ex) {
                Logger.getLogger(FilterDoubleExpressionValidator.class.getName()).log(Level.SEVERE, null, ex);
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
}
