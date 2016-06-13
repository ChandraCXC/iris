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
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.preferences.LayerModel;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Implementation of a JTree that is tightly coupled to the {@link VisualizerDataModel}.
 * Used for displaying and selecting Segments from the left-hand panel of the metadata 
 * browser. The selection model here is tied to the selectedStarTables field in 
 * the dataModel.
 * 
 */
@SuppressWarnings("serial")
public class StarTableJTree extends JTree {
    
    public static String PROP_SEDS = "seds";
    
    private List<ExtSed> seds = new ArrayList<>();
    
    private VisualizerDataModel dataModel;
    
    public StarTableJTree() {
        this.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                updateSelectedStarTables();
            }
        });
    }
    
    public void setDataModel(VisualizerDataModel dataModel) {
        this.dataModel = dataModel;
    }
    
    public void setSeds(List<ExtSed> seds) {
        List<ExtSed> oldSeds = this.seds;
        this.seds = seds;
        
        refresh();
        
        firePropertyChange(PROP_SEDS, oldSeds, seds);
    }
    
    /**
     * Only here for listening to property changes in the dataModel.
     */
    public void setSedStarTables(List<IrisStarTable> tables) {
        refresh();
    }
    
    /**
     * Forces the JTree to update it's model by recreating the model from scratch.
     * By default the first SED will be selected, if available.
     */
    private void refresh() {
        this.setModel(new StarTableTreeModel(seds));
        
        // Always expand all nodes by default
        this.getRowCount();
        for (int i=0; i<getRowCount(); i++) {
            expandRow(i);
        }
        
        // Select and display the first SED, if available.
        if (getRowCount() > 0) {
            this.getSelectionModel().addSelectionPath(getPathForRow(0));
        }
        
        updateSelectedStarTables();
    }

    /**
     * Adds an IrisStarTable to the selection path, if not already present.
     * @param table 
     */
    protected void addToSelection(IrisStarTable table) {
        
        if (!dataModel.getSedStarTables().contains(table)) {
            throw new IllegalArgumentException(table.getName() + " is not in the tree");
        }
        
        // If it is already selected do nothing
        if (dataModel.getSelectedStarTables().contains(table)) return;
        
        // Find this node in the tree
        TreePath path = findTablePath(table);
        
        // Otherwise find the table in the tree and update the selection
        this.getSelectionModel().addSelectionPath(path);
        updateSelectedStarTables();
    }
    
    /**
     * @param table
     * @return TreePath for the specified StarTable, if available.
     */
    private TreePath findTablePath(IrisStarTable table) {
        DefaultMutableTreeNode root = ( DefaultMutableTreeNode) getModel().getRoot();
        Enumeration e = root.breadthFirstEnumeration();
        
        DefaultMutableTreeNode node = null;
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if (node instanceof TableLeafNode) {
                TableLeafNode tn = (TableLeafNode) node;
                if (tn.table == table) {
                    return new TreePath(tn.getPath());
                }
            }
        }
        
        // Exception thrown before we get here
        return null;
    }
    
    private synchronized void updateSelectedStarTables() {
        
        // Get all selected tables and add them to the new selction list
        List<IrisStarTable> selection = new ArrayList<>();
        for (TreePath path : getSelectionModel().getSelectionPaths()) {
            Object comp = path.getLastPathComponent();
            
            if (comp instanceof SedTreeNode) {
                selection.addAll(((SedTreeNode) comp).subTables);
            }
            else if (comp instanceof TableLeafNode) {
                TableLeafNode tn = (TableLeafNode) comp;
                if (!selection.contains(tn.table)) selection.add(tn.table);
            }
        }
        dataModel.setSelectedStarTables(selection);
    }
    
    private class StarTableTreeModel extends DefaultTreeModel {
        
        public StarTableTreeModel() {
            super(new DefaultMutableTreeNode("Seds"));
        }
        
        public StarTableTreeModel(List<ExtSed> seds) {
            this();
            
            DefaultMutableTreeNode r = (DefaultMutableTreeNode) getRoot();
            for (ExtSed sed : seds) {
                if (sed == null) {
                    continue;
                }
                
                r.add(new SedTreeNode(sed));
            }
        }
    }
    
    private class SedTreeNode extends DefaultMutableTreeNode {
        
        List<IrisStarTable> subTables;
        ExtSed sed;
        
        public SedTreeNode(ExtSed sed) {
            super(sed);
            if (sed == null) {
                throw new IllegalArgumentException("Sed cannot be null");
            }
            
            this.sed = sed;
            subTables = new ArrayList<>();
            List<LayerModel> models = dataModel.getModelsForSed(sed);
            for (LayerModel model : models) {
                this.add(new TableLeafNode(model.getInSource()));
                subTables.add(model.getInSource());
            }
        }
        
        @Override
        public String toString() {
            return sed.getId();
        }
    }
    
    private class TableLeafNode extends DefaultMutableTreeNode {
        
        IrisStarTable table;
        
        public TableLeafNode(IrisStarTable table) {
            super(table);
            if (table == null) {
                throw new IllegalArgumentException("Table cannot be null");
            }
            this.table = table;
        }
        
        @Override
        public boolean isLeaf() {
            return true;
        }
        
        @Override
        public String toString() {
            return table.getName();
        }
    }
}
