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

package cfa.vo.sherpa.models;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class CompositeModelTreeModel extends DefaultTreeModel {

    public CompositeModelTreeModel(CompositeModel model, List<UserModel> userModels) {
        super(new DefaultMutableTreeNode("Model Components"));

        if(model.getParts() != null) {

            for (Model m : model.getParts()) {
                DefaultMutableTreeNode r = (DefaultMutableTreeNode) this.getRoot();
                DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(m);

                if (m.getName().startsWith("usermodel")) {
                    m.setName(getUserModelName(m, userModels));
                } else if (m.getName().startsWith("tablemodel") || (m.getName().startsWith("template"))) {
                    m.setName(getTableModelName(m, userModels));
                }

                r.add(parentNode);

                for (Parameter par : m.getPars()) {
                    parentNode.add(new DefaultMutableTreeNode(par));
                }

            }
        }
        
    }

    private String getUserModelName(Model m, List<UserModel> userModels) {
        String ret = m.getName();
        for (UserModel um : userModels) {
            if (ret.equals(um.getName())) {
                return um.getFunction()+"."+ret.split("\\.")[1];
            }
        }
        return ret;
    }

    private String getTableModelName(Model m, List<UserModel> userModels) {
        String ret = m.getName();
        for (UserModel um : userModels) {
            if (ret.equals(um.getName())) {
                String[] bits = um.getFile().split("/");
                return bits[bits.length-1]+"."+ret.split("\\.")[1];
            }
        }
        return ret;
    }
    
}
