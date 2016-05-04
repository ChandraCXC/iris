package cfa.vo.iris.gui.widgets;

import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.Parameter;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

class ModelViewerMouseAdapter extends MouseAdapter implements TreeSelectionListener {
    private ModelViewerPanel modelViewerPanel;
    private Object selectedObject;
    private final Logger logger = Logger.getLogger(ModelViewerMouseAdapter.class.getName());

    public ModelViewerMouseAdapter(ModelViewerPanel modelViewerPanel) {
        this.modelViewerPanel = modelViewerPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        process(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        process(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        process(e);
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        selectAction(null, treeSelectionEvent.getPath());
    }

    private void process(MouseEvent e) {
        TreePath selPath = modelViewerPanel.getModelsTree().getPathForLocation(e.getX(), e.getY());
        if (selPath != null) {
            selectAction(e, selPath);
        }
    }

    private void selectAction(MouseEvent e, TreePath selPath) {
        TreeNode selectedNode = findSelectedObject(selPath);

        if (e != null && e.isPopupTrigger()) {
            handlePopup(e);
        } else if (selectedNode.isLeaf()) {
            handleLeaf();
        } else {
            handleNonLeaf();
        }
    }

    private void handleLeaf() {
        Parameter par = (Parameter) selectedObject;
        modelViewerPanel.setSelectedParameter(par);
    }

    private void handlePopup(MouseEvent e) {
        if (selectedObject instanceof Model && modelViewerPanel.isEditable()) {
            makePopupMenu((Model)selectedObject).show(modelViewerPanel.getModelsTree(), e.getX(), e.getY());
        }
    }

    private void handleNonLeaf() {
        // do nothing for now
    }

    private TreeNode findSelectedObject(TreePath selPath) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        selectedObject = selectedNode.getUserObject();
        return selectedNode;
    }

    private JPopupMenu makePopupMenu(Model model) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Remove");
        item.addActionListener(makeDeleteActionListener(model));
        menu.add(item);
        return menu;
    }

    private ActionListener makeDeleteActionListener(final Model model) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                logger.info("Deleting " + model.getName());
                modelViewerPanel.removeModelComponent(model);
            }
        };
    }
}
