package cfa.vo.iris.visualizer.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.preferences.SegmentModel;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class StarTableJTree extends JTree {
    
    public static String PROP_SEDS = "seds";
    public static String PROP_SED_STARTABLES = "sedStarTables";
    public static String PROP_SELECTED_STARTABLES = "selectedStarTables";
    
    private List<ExtSed> seds = new ArrayList<>();
    private List<IrisStarTable> sedStarTables = new ArrayList<>();
    private List<IrisStarTable> selectedStarTables = new ArrayList<>();
    
    private VisualizerDataModel dataModel;
    
    public StarTableJTree() {
        this.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                handleSelectionEvent(tse);
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
    
    public List<IrisStarTable> getSedStarTables() {
        return selectedStarTables;
    }
    
    public void setSedStarTables(List<IrisStarTable> tables) {
        List<IrisStarTable> oldTables = this.sedStarTables;
        this.sedStarTables = tables;
        
        refresh();
        
        firePropertyChange(PROP_SED_STARTABLES, oldTables, sedStarTables);
    }
    
    public List<IrisStarTable> getSelectedStarTables() {
        return selectedStarTables;
    }
    
    public void setSelectedStarTables(List<IrisStarTable> tables) {
        List<IrisStarTable> oldTables = this.selectedStarTables;
        this.selectedStarTables = tables;
        dataModel.setSelectedStarTables(selectedStarTables);
        firePropertyChange(PROP_SELECTED_STARTABLES, oldTables, selectedStarTables);
    }
    
    private void refresh() {
        this.setModel(new StarTableTreeModel(seds));
        
        // Always expand SED nodes by default
        this.getRowCount();
        for (int i=0; i<getRowCount(); i++) {
            expandRow(i);
        }
    }
    
    private void handleSelectionEvent(TreeSelectionEvent tse) {
        if (tse.getPaths().length == 0) {
            return;
        }
        
        // Get all selected tables and add them to the new selction list
        List<IrisStarTable> selection = new ArrayList<>();
        for (TreePath path : getSelectionModel().getSelectionPaths()) {
            Object comp = path.getLastPathComponent();
            
            if (comp instanceof SedTreeNode) {
                selection.addAll(((SedTreeNode) comp).subTables);
            }
            else if (comp instanceof TableLeafNode) {
                selection.add(((TableLeafNode) comp).table);
            }
        }
        this.setSelectedStarTables(selection);
    }
    
    private class StarTableTreeModel extends DefaultTreeModel {
        
        public StarTableTreeModel() {
            super(new DefaultMutableTreeNode("Seds"));
        }
        
        public StarTableTreeModel(List<ExtSed> seds) {
            this();
            
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
            for (ExtSed sed : seds) {
                root.add(new SedTreeNode(sed));
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
            List<SegmentModel> models = dataModel.getModelsForSed(sed);
            for (SegmentModel model : models) {
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
