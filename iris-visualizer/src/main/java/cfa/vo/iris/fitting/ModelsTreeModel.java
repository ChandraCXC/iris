package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.sherpa.models.Model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelsTreeModel extends DefaultTreeModel {
    private List<Model> presetList;

    public ModelsTreeModel() {
        super(new DefaultMutableTreeNode("Model Components"));
    }

    public ModelsTreeModel(List<Model> presetList, CustomModelsManager manager) {
        this();
        Collections.sort(presetList, new Comparator<Model>() {

            @Override
            public int compare(Model m1, Model m2) {
                return m1.getName().compareTo(m1.getName());
            }
        });
        this.presetList = presetList;

        DefaultMutableTreeNode r = (DefaultMutableTreeNode) this.getRoot();
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("Preset Model Components");
        r.add(parentNode);

        for(Model m : presetList) {
            parentNode.add(new DefaultMutableTreeNode(m));
        }

        try {
            r.add(manager.getCustomModels());
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error while reading custom models", e);
        }

    }

    public List<Model> getList() {
        return presetList;
    }
}
