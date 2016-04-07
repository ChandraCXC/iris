package cfa.vo.iris.gui.widgets;

import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.Parameter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

class ModelViewerMouseAdapter extends MouseAdapter {
    private ModelViewerPanel modelViewerPanel;
    private DefaultMutableTreeNode selectedNode;
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

    private void process(MouseEvent e) {
        TreePath selPath = modelViewerPanel.getModelsTree().getPathForLocation(e.getX(), e.getY());
        if (selPath != null) {
            selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (selectedNode.isLeaf()) {
                Parameter par = (Parameter) selectedNode.getUserObject();
                modelViewerPanel.setSelectedParameter(par);
            }
            checkPopup(e);
        }
    }

    private void checkPopup(MouseEvent e) {
        Object obj = selectedNode.getUserObject();
        if (!selectedNode.isLeaf() && obj instanceof Model && e.isPopupTrigger() && modelViewerPanel.isEditable()) {
            makePopupMenu().show(modelViewerPanel.getModelsTree(), e.getX(), e.getY());
        }
    }

    private JPopupMenu makePopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Remove");
        item.addActionListener(makeDeleteActionListener());
        menu.add(item);
        return menu;
    }

    private ActionListener makeDeleteActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(selectedNode != null){
                    logger.info("Deleting " + selectedNode);
                    modelViewerPanel.removeModelComponent((Model)selectedNode.getUserObject());
                    modelViewerPanel.getModelsTree().repaint();
                    modelViewerPanel.getModelsTree().updateUI();
                }
            }
        };
    }
}
