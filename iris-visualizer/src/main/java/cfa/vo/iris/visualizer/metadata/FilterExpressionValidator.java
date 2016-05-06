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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * A class that validates an input filter expression.
 */
public class FilterExpressionValidator {
    
    IrisStarJTable starJTable; // the stacked startable to filter
    
    public FilterExpressionValidator(IrisStarJTable table) {
        this.starJTable = table;
    }
    
    public int[] process(String expression) {
        
        List<String> colSpecifiers = findColumnSpecifiers(expression);
        
        ComparisonDoubleEvaluator evaluator = new ComparisonDoubleEvaluator();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        
        List<Double> evaluatedExpression = new ArrayList<>();
        
        for (int i=0; i<this.starJTable.getStarTable().getRowCount(); i++) {
            for (String colName : colSpecifiers) {
                int colNumber = Integer.parseInt(colName);
                try {
                    // assuming the column is a Double
                    Object val = this.starJTable.getStarTable().getCell(i, colNumber);
                    variables.set("$"+String.valueOf(colNumber), (Double) this.starJTable.getStarTable().getCell(i, colNumber));
                } catch (IOException ex) {
                    Logger.getLogger(FilterExpressionValidator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            evaluatedExpression.add(evaluator.evaluate(expression, variables));
        }
        
        // return array of indices to select
        return getFilteredIndices(evaluatedExpression);
    }
    
    private int[] getFilteredIndices(List<Double> evaluatedExpression) {
        // TODO: come up with more efficient way of getting non-null indices.
        
        // get non-null indices from evaluatedExpression list
        List<Integer> tmp = new ArrayList<>();
        for (int i=0; i<evaluatedExpression.size(); i++) {
            Double val = evaluatedExpression.get(i);
            if (!val.equals(Double.NaN))
                tmp.add(i);
        }
        
        // convert from List<Integer> to int[]
        int[] indices = new int[tmp.size()];
        for (int i=0; i<tmp.size(); i++)
            indices[i] = tmp.get(i);
        return indices;
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
    
//    private void inOrder2PostOrder(String expression) {
//        
//    }
    
//    public TreeNode createTreeNode() {
//        Iterator<Character>itr = postOrder.iterator();
//        Tree tree = new Tree();
//        NodeStack nodeStack = new NodeStack();
//        Tree.TreeNode node;
//        while (itr.hasNext()) {
//            Character c = itr.next();
//            if(!isDigit(c)){
//                node = tree.createNode(c);
//                node.right = nodeStack.pop();
//                node.left = nodeStack.pop();
//                nodeStack.push(node);
//            }else{
//                node = tree.createNode(c);
//                nodeStack.push(node);
//            }
//        }
//        node = nodeStack.pop();
//        return node;
//    }
    
    /**
     * Process a simple expression and return a list of values
     * @return evaluated expression
     * If no values in <tt>col</tt> match the condition, the function returns 
     * null.
     */
    public List<Double> processSingleExpression(String expression, double[] col) {
        
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
    
    /**
     * Compare two evaluated expressions with a given comparison operator: 
     * 
     * <tt>arg1 operator arg2</tt>
     * 
     * If no values match the condition, the function returns 
     * null. 
     *
     * @param arg1
     * @param arg2
     * @param operator
     * @return 
     */
    public List<Double> compareValues(List<Double> arg1, List<Double> arg2, ComparisonType operator) {
        
        List<Double> result;
        
        switch (operator) {
            case EQ:
                result = compareEquals(arg1, arg2);
                break;
            case GE:
                result = compareGreaterOrEqual(arg1, arg2);
                break;
            case GT:
                result = compareGreater(arg1, arg2);
                break;
            case LE:
                result = compareLessOrEqual(arg1, arg2);
                break;
            case LT:
                result = compareLess(arg1, arg2);
                break;
            case NE:
                result = compareNotEquals(arg1, arg2);
                break;
            default:
                return null;
        }
        return result;
    }
    
        /**
     * Compare two evaluated expressions with a given comparison operator: 
     * 
     * <tt>arg1 operator arg2</tt>
     * 
     * If no values match the condition, the function returns 
     * null. 
     *
     * @param arg1
     * @param arg2
     * @param operator
     * @return 
     */
    public List<Double> compareValues(List<Double> arg1, Double arg2, ComparisonType operator) {
        
        List<Double> result;
        
        switch (operator) {
            case EQ:
                result = compareEquals(arg1, arg2);
                break;
            case GE:
                result = compareGreaterOrEqual(arg1, arg2);
                break;
            case GT:
                result = compareGreater(arg1, arg2);
                break;
            case LE:
                result = compareLessOrEqual(arg1, arg2);
                break;
            case LT:
                result = compareLess(arg1, arg2);
                break;
            case NE:
                result = compareNotEquals(arg1, arg2);
                break;
            default:
                return null;
        }
        return result;
    }
    
    public static Double compareEquals(Double arg1, Double arg2) {
        
        Double result;
        if (arg1.equals(arg2))
                result = arg1;
        return null;
    }
    
    public static List<Double> compareEquals(List<Double> arg1, List<Double> arg2) {
        
        // arg1 and arg2 must be of equal size
        if (arg1.size() != arg2.size()) {
            throw new IllegalArgumentException("Size of arg2 must be equal to arg1.");
        }
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i).equals(arg2.get(i)))
                results.add(arg1.get(i));
        }
        return results;
    }
    
    public static List<Double> compareEquals(List<Double> arg1, Double arg2) {
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i).equals(arg2))
                results.add(arg1.get(i));
        }
        return null;
    }
    
    List<Double> compareNotEquals(List<Double> arg1, List<Double> arg2) {
        
        // arg1 and arg2 must be of equal size
        if (arg1.size() != arg2.size()) {
            throw new IllegalArgumentException("Size of arg2 must be equal to arg1.");
        }
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (!arg1.get(i).equals(arg2.get(i)))
                results.add(arg1.get(i));
        }
        return results;
    }
    
    List<Double> compareNotEquals(List<Double> arg1, Double arg2) {
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (!arg1.get(i).equals(arg2))
                results.add(arg1.get(i));
        }
        return null;
    }
    
    List<Double> compareGreater(List<Double> arg1, List<Double> arg2) {
        
        // arg1 and arg2 must be of equal size
        if (arg1.size() != arg2.size()) {
            throw new IllegalArgumentException("Size of arg2 must be equal to arg1.");
        }
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) > arg2.get(i))
                results.add(arg1.get(i));
        }
        return results;
    }
    
    List<Double> compareGreater(List<Double> arg1, Double arg2) {
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) > arg2)
                results.add(arg1.get(i));
        }
        return null;
    }
    
    List<Double> compareGreaterOrEqual(List<Double> arg1, List<Double> arg2) {
        
        // arg1 and arg2 must be of equal size
        if (arg1.size() != arg2.size()) {
            throw new IllegalArgumentException("Size of arg2 must be equal to arg1.");
        }
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) >= arg2.get(i))
                results.add(arg1.get(i));
        }
        return results;
    }
    
    List<Double> compareGreaterOrEqual(List<Double> arg1, Double arg2) {
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) >= arg2)
                results.add(arg1.get(i));
        }
        return null;
    }
    
    List<Double> compareLessOrEqual(List<Double> arg1, List<Double> arg2) {
        
        // arg1 and arg2 must be of equal size
        if (arg1.size() != arg2.size()) {
            throw new IllegalArgumentException("Size of arg2 must be equal to arg1.");
        }
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) <= arg2.get(i))
                results.add(arg1.get(i));
        }
        return results;
    }
    
    List<Double> compareLessOrEqual(List<Double> arg1, Double arg2) {
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) <= arg2)
                results.add(arg1.get(i));
        }
        return null;
    }
    
    List<Double> compareLess(List<Double> arg1, List<Double> arg2) {
        
        // arg1 and arg2 must be of equal size
        if (arg1.size() != arg2.size()) {
            throw new IllegalArgumentException("Size of arg2 must be equal to arg1.");
        }
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) < arg2.get(i))
                results.add(arg1.get(i));
        }
        return results;
    }
    
    List<Double> compareLess(List<Double> arg1, Double arg2) {
        
        List<Double> results = new ArrayList<>();
        for (int i=0; i<arg1.size(); i++) {
            if (arg1.get(i) < arg2)
                results.add(arg1.get(i));
        }
        return null;
    }
    
    public enum ComparisonType {
        EQ("=="),
        GE(">="),
        GT(">"),
        LE("<="),
        LT("<"),
        NE("!=");
        
        public String symbol;
    
        private ComparisonType(String symbol) {
            this.symbol = symbol;
        }
    }
}
