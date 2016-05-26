/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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
