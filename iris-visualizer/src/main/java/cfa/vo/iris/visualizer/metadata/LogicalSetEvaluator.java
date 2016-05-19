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

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Constant;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import uk.ac.starlink.table.StarTable;

/**
 *
 * Evaluates filter expressions for an IrisStarJTable with logical operators 
 * (AND, OR, and NOT).
 */
public class LogicalSetEvaluator extends AbstractEvaluator<HashSet> {
        // logical operators
    public final static Operator NOT = new Operator("!", 2, Operator.Associativity.RIGHT, 3);
    public final static Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
    public final static Operator  OR = new Operator("||", 2, Operator.Associativity.LEFT, 1);
    
    private static final Parameters PARAMETERS;
    
    static {
        ArrayList<BracketPair> brackets = new ArrayList<>();
        brackets.add(BracketPair.BRACES);
        PARAMETERS = new Parameters();
        PARAMETERS.addExpressionBrackets(brackets);
        PARAMETERS.add(NOT);
        PARAMETERS.add(AND);
        PARAMETERS.add(OR);
    }
    
    private FilterDoubleExpressionValidator filterEvaluator;
    
    public LogicalSetEvaluator(StarTable table) {
        super(PARAMETERS);
        this.filterEvaluator = new FilterDoubleExpressionValidator(table);
    }

    /**
     * Evaluates the filter expression.
     * @param expression the filter expression to evaluate
     * @return A hash set of the table rows which fulfill the filter expression
     */
    @Override
    protected HashSet<Integer> toValue(String expression, Object o) {

        List<Integer> iRows = filterEvaluator.process(expression);
        HashSet<Integer> setRows = new HashSet<>();
        setRows.addAll(iRows);
        return setRows;
    }
    
    @Override
    public HashSet<Integer> evaluate(String expression) {
        return super.evaluate(expression);
    }
    
    @Override
    public HashSet<Integer> evaluate(String expression, Object context) {
        return super.evaluate(expression, context);
    }

    @Override
    protected HashSet evaluate(Constant constant, Object evaluationContext) {
        return super.evaluate(constant, evaluationContext); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected HashSet evaluate(Function function, Iterator<HashSet> arguments, Object evaluationContext) {
        return super.evaluate(function, arguments, evaluationContext); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Evaluate a logical expression. Returns 'null' if neither 
     * @param operator
     * @param operands
     * @param evaluationContext
     * @return 
     */
    @Override
    protected HashSet<Integer> evaluate(Operator operator, Iterator<HashSet> operands, Object evaluationContext) {
        HashSet<Integer> left;
        HashSet<Integer> right;
        if (operator == NOT) {
            // get the left and right operands
            left = operands.next();
            right = operands.next();
            // subtract sets
            left.removeAll(right);
            return left;
        } else if (operator == AND) {
            // get the left and right operands
            left = operands.next();
            right = operands.next();
            // return the intersetion
            left.retainAll(right);
            return left;
        } else if (operator == OR) {
            // get the left and right operands
            left = operands.next();
            right = operands.next();
            // return the union
            left.addAll(right);
            return left;
        } else {
            return null;
        }
    }
}
