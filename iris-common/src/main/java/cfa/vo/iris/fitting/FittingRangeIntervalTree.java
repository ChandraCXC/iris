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
package cfa.vo.iris.fitting;

import java.util.List;

/**
 * An interval tree which holds FittingRanges. This class is used to search for 
 * points which fall within a list of FittingRanges.
 */
public class FittingRangeIntervalTree {

    private TreeNode root;
    
    public class TreeNode {
        
        private FittingRange range;
        private double max;
        private TreeNode left;
        private TreeNode right;
        
        public TreeNode(FittingRange range) {
            this.range = range;
            this.left = null;
            this.right = null;
            this.max = range.getEndPoint();
        }
    }
    
    public FittingRangeIntervalTree(List<FittingRange> ranges) {
        
        for (FittingRange range : ranges) {
            insert(range);
        }
    }
    
    public TreeNode insert(FittingRange range) {
        if (root == null) {
            root = new TreeNode(range);
            return root;
        }
        return insert(root, range);
    }
    
    /**
     * Insert a new node into the fitting ranges interval tree
     * @param node - the TreeNode to start inserting from.
     * @param range - the FittingRange to insert
     * @return the TreeNode updated with the new FittingRange node
     */
    public TreeNode insert(TreeNode node, FittingRange range) {
        
        // if node is null, create a new node
        if (node == null) {
            node = new TreeNode(range);
            return node;
        }
        
        double low = node.range.getStartPoint();
        
        // if new fitting range's start point is lower, then add it to the left
        if (range.getStartPoint() < low) {
            node.left = insert(node.left, range);
        } else {
            // otherwise, add it to the right tree
            node.right = insert(node.right, range);
        }
        
        // update the max and min of the ancestor if necessary
        if (node.max < range.getEndPoint())
            node.max = range.getEndPoint();
        
        return node;
    }
    
    /**
     * Check if the given value is in the tree.
     * @param x - the value to search for
     * @return - true if the point lies within any of the ranges in the tree. 
     * Returns false if otherwise.
     */
    public boolean isPointInTree(double x) {
        return isPointInRanges(x, root);
    }
    
    /**
     * Check if the given value is in the tree, starting from a given TreeNode.
     * @param x - the value to search for
     * @param node - the node to start searching from
     * @return true if the point is found; false otherwise
     */
    private boolean isPointInRanges(double x, TreeNode node) {
        
        // return false if node is null
        if (node == null)
            return false;
        
        // check if point is in the node
        if (x > node.range.getStartPoint() && x < node.range.getEndPoint()) {
            return true;
        }
        
        // if point is not in the current node, check the left and right nodes
        if (node.left != null && node.left.max >= x)
            return isPointInRanges(x, node.left);
        
        return isPointInRanges(x, node.right);
    }
}
