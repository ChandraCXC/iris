/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.photfilters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author olaurino
 */
public class PhotometryFilterTreeModel extends DefaultTreeModel {
    private Map<String, List<PhotometryFilter>> map = new TreeMap();
    private List<PhotometryFilter> originalList;

    public PhotometryFilterTreeModel(List<PhotometryFilter> list) {
        super(new DefaultMutableTreeNode("Photometry Filters by Facility"));
        Collections.sort(list, new Comparator<PhotometryFilter>() {

            @Override
            public int compare(PhotometryFilter t, PhotometryFilter t1) {
                return t.getId().compareTo(t1.getId());
            }
        });
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
