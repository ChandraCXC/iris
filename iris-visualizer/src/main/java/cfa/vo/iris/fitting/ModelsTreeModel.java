package cfa.vo.iris.fitting;

import cfa.vo.sherpa.models.Model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

public class ModelsTreeModel extends DefaultTreeModel {
    private List<Model> presetList;

    public ModelsTreeModel() {
        super(new DefaultMutableTreeNode("Model Components"));
    }

    public ModelsTreeModel(List<Model> presetList, MutableTreeNode customTree) {
        this();
        Collections.sort(presetList, new Comparator<Model>() {

            @Override
            public int compare(Model m1, Model m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });
        this.presetList = presetList;

        DefaultMutableTreeNode r = (DefaultMutableTreeNode) this.getRoot();
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("Preset Model Components");
        r.add(parentNode);

        for(Model m : presetList) {
            parentNode.add(new DefaultMutableTreeNode(m));
        }

        if (customTree != null) {
            r.add(customTree);
        }
    }

    public List<Model> getList() {
        return presetList;
    }
}
