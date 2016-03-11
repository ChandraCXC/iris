package cfa.vo.iris.fitting;

import cfa.vo.sherpa.models.Model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.*;

public class ModelsTreeModel extends DefaultTreeModel {
    private Map<String, List<Model>> map = new TreeMap();
    private List<Model> presetList;

    public ModelsTreeModel(List<Model> presetList) {
        super(new DefaultMutableTreeNode("Model Components"));
        this.presetList = presetList;
        Collections.sort(presetList, new Comparator<Model>() {

            @Override
            public int compare(Model m1, Model m2) {
                return m1.getName().compareTo(m1.getName());
            }
        });

        map.put("Preset Model Components", presetList);

        for(Map.Entry<String, List<Model>> parent : map.entrySet()) {
            DefaultMutableTreeNode r = (DefaultMutableTreeNode) this.getRoot();
            DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(parent.getKey());
            r.add(parentNode);

            for(Model m : parent.getValue())
                parentNode.add(new DefaultMutableTreeNode(m));
        }
    }

    public List<Model> getList() {
        return presetList;
    }
}
