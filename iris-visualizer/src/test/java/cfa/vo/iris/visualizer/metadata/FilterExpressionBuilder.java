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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import uk.ac.starlink.table.StarTable;

/**
 *
 * Builds an expression tree of filters. Processing the filter expression tree 
 * returns the table rows which follow the given filter.
 */
public class FilterExpressionBuilder {
    
    private LogicalSetEvaluator filterEvaluator;
    
    public FilterExpressionBuilder(StarTable table) {
        this.filterEvaluator = new LogicalSetEvaluator(table);
    }
    
    public List<Integer> process(String expression) {
        HashSet<Integer> hashSetRows = filterEvaluator.evaluate(expression);
        
        return new ArrayList<>(hashSetRows);
    }
    
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


    
}
