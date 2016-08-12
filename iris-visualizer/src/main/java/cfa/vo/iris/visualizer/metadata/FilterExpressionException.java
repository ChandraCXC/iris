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

/**
 *
 * Filter expression exception messages
 */
class FilterExpressionException extends Exception {

    private static final String BAD_EXPRESSION = "Bad expression: ";
    
    public static final String DEFAULT_MSG = BAD_EXPRESSION
            + "Format is 'LHS operator RHS', where either the LHS or the "
            + "RHS must have a column specifier (e.g., '$columnNumber')";
    public static final String COLUMN_DNE_MSG = "";
    public static final String EMPTY_EXPRESSION_MSG = "Empty expression. "
            + "Cannot create a filter with an empty expression.";
    public static final String NON_NUMERIC_COLUMN_NAME_MSG = BAD_EXPRESSION
            + "Only numeric column specifiers are allowed.\n "
            + "Use the column number to specify the column "
            + "(e.g., to specify the first column, use '$1').";
    public static final String STRING_COLUMN_MSG = BAD_EXPRESSION
            + "Only numeric columns may be filtered at this time.";
    
    public FilterExpressionException(String string) {
        super(string);
    }
    
}
