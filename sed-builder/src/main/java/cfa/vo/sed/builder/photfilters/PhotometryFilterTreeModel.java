/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.photfilters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author olaurino
 */
public class PhotometryFilterTreeModel extends DefaultTreeModel {
    private Map<String, List<PhotometryFilter>> map = new HashMap();
    private List<PhotometryFilter> originalList;

    public PhotometryFilterTreeModel(List<PhotometryFilter> list) {
        super(new DefaultMutableTreeNode("Photometry Filters by Facility"));
        this.originalList = list;
        for(PhotometryFilter f : list) {
            String parent = f.getId().split("/")[0];

            if(!map.containsKey(parent))
                map.put(parent, new ArrayList());

            map.get(parent).add(f);

        }

        for(Entry<String, List<PhotometryFilter>> parent : map.entrySet()) {
            DefaultMutableTreeNode r = (DefaultMutableTreeNode) this.getRoot();
            DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(parent.getKey());
            r.add(parentNode);

            for(PhotometryFilter f : parent.getValue())
                parentNode.add(new DefaultMutableTreeNode(f));
        }
    }

    public List<PhotometryFilter> getList() {
        return originalList;
    }

}
