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

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import java.util.Iterator;

/**
 *
 * Evaluates expressions containing relational operators. The left-hand side
 * (LHS) and right-hand side (RHS) of the operators may contain mathematical 
 * operators like *, -, /, ^, etc.
 */
public class ComparisonDoubleEvaluator extends DoubleEvaluator {
    
    // relational operators. The precedence follows javaluator's standard. The   
    // precedence (or order of operation of the operator; 4th argument in the 
    // constructor) gets lower as as the argument gets lower. The reason these 
    // precendeces are 0 and -1 is because the lowest precedence default 
    // operators in the DoubleEvaluator object have precendece=1.
    public final static Operator LE = new Operator("<=", 2, Operator.Associativity.LEFT, 0);
    public final static Operator LT = new Operator("<", 2, Operator.Associativity.LEFT, 0);
    public final static Operator GE = new Operator(">=", 2, Operator.Associativity.LEFT, 0);
    public final static Operator GT = new Operator(">", 2, Operator.Associativity.LEFT, 0);
    public final static Operator EQ = new Operator("==", 2, Operator.Associativity.LEFT, -1);
    public final static Operator NE = new Operator("!=", 2, Operator.Associativity.LEFT, -1);
    
    private static final Parameters PARAMETERS;

    static {
        PARAMETERS = new Parameters();
        // Default parameters
        PARAMETERS.addConstants(DoubleEvaluator.getDefaultParameters().getConstants());
        PARAMETERS.addExpressionBrackets(DoubleEvaluator.getDefaultParameters().getExpressionBrackets());
        PARAMETERS.addFunctionBrackets(DoubleEvaluator.getDefaultParameters().getFunctionBrackets());
        PARAMETERS.addFunctions(DoubleEvaluator.getDefaultParameters().getFunctions());
        PARAMETERS.addOperators(DoubleEvaluator.getDefaultParameters().getOperators());
        PARAMETERS.add(GT); // greater than
        PARAMETERS.add(LT); // less than
        PARAMETERS.add(GE); // greater than or equal to
        PARAMETERS.add(LE); // less than or equal to
        PARAMETERS.add(EQ); // equal to
        PARAMETERS.add(NE); // not equal to
    }

    public ComparisonDoubleEvaluator() {
        super(PARAMETERS);
    }

    /**
     * Evaluate an expression with relational operators. Falls back on parent
     * class's evaluate method if no relational operators are present.
     * 
     * @param operator the operator for the operand(s)
     * @param operands the numbers which the operator operates on
     * @param evaluationContext auxiliary information for the evaluation. This 
     * is used for the parent 
     * 
     * @return the evaluated expression. If the expression contains relational operators, 
     * and the condition is met, the left-hand operand is returned. 
     * If the condition is not met, then the method returns a Double.NaN.
     * If the expression does not contain relational operators, the method falls
     * back on the DoubleEvaluator evaluate() method.
     */
    @Override
    protected Double evaluate(Operator operator, Iterator<Double> operands, Object evaluationContext) {
        Double left;  // left operand
        Double right; // right operand

        // for relational operators
        if (operator == EQ) {
            // get the left and right operands
            left = operands.next();
            right = operands.next();
            if (left.equals(right))
                return left;
        } else if (operator == NE) {
            left = operands.next();
            right = operands.next();
            if (!left.equals(right))
                return left;
        } else if (operator == GT) {
            left = operands.next();
            right = operands.next();
            if (left > right) {
                return left;
            }
        } else if (operator == GE) {
            left = operands.next();
            right = operands.next();
            if (left >= right) {
                return left;
            }
        } else if (operator == LT) {
            left = operands.next();
            right = operands.next();
            if (left < right) {
                return left;
            }
        } else if (operator == LE) {
            left = operands.next();
            right = operands.next();
            if (left <= right) {
                return left;
            }
        } else if (operator == MINUS){
            left = operands.next();
            right = operands.next();
            return left - right;
        } else {
            // fall back on the parent method for other Double operators (*, -, +, etc.)
            return super.evaluate(operator, operands, evaluationContext);
        }
        
        // return null if the condition is not met.
        return Double.NaN;
    }

    @Override
    protected Double toValue(String string, Object evaluationContext) {
        return Double.valueOf(string);
    }
}
