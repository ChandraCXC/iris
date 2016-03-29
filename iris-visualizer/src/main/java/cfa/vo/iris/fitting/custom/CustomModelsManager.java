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

package cfa.vo.iris.fitting.custom;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import javax.swing.tree.*;

public class CustomModelsManager {

    private File tablesDir;
    private File functionsDir;
    private File templatesDir;

    private CustomModelsTreeModel tablesTree;
    private CustomModelsTreeModel templatesTree;
    private CustomModelsTreeModel functionsTree;

    private List<ModelsListener> listeners = new ArrayList<>();


    public CustomModelsManager(File rootDir) throws IOException {

        tablesDir = new File(rootDir+File.separator+"tables");
        if(!tablesDir.exists())
            tablesDir.mkdirs();
        else if(!tablesDir.isDirectory())
            throw new IOException("Tables directory exists but is not a directory");

        functionsDir = new File(rootDir+File.separator+"functions");
        if(!functionsDir.exists())
            functionsDir.mkdirs();
        else if(!functionsDir.isDirectory())
            throw new IOException("Functions directory exists but is not a directory");

        templatesDir = new File(rootDir+File.separator+"templates");
        if(!templatesDir.exists())
            templatesDir.mkdirs();
        else if(!templatesDir.isDirectory())
            throw new IOException("Templates directory exists but is not a directory");
    }

    public DefaultTreeModel getTables() throws IOException {
        getCustomModels();
        return tablesTree;
    }

    public DefaultTreeModel getFunctions() throws IOException {
        getCustomModels();
        return functionsTree;
    }

    public DefaultTreeModel getTemplates() throws IOException {
        getCustomModels();
        return templatesTree;
    }

    public void addModel(CustomModel model, String type) throws IOException {
        File file = new File(model.getUrl().getFile());
        File destination = null;
        if(type.equals("Template Library"))
            destination=templatesDir;
        if(type.equals("Python Function"))
            destination=functionsDir;
        if(type.equals("Table"))
            destination=tablesDir;

        String name;

        if(model.getName()==null)
            model.setName("");

        name = model.getName().isEmpty() ? model.getUrl().getFile().substring(model.getUrl().getFile().lastIndexOf(File.separator)+1) : model.getName();

        File target = new File(destination.getAbsolutePath()+File.separator+name);

        if(file.equals(target)) {
            File temp = new File(file.getAbsolutePath()+".temp");
            Files.copy(file, temp);
            Files.copy(temp, target);
            temp.delete();
        } else {
            Files.copy(file, target);
        }

        File specs = new File(target.getAbsolutePath()+".specs");

        String functionName = destination.equals(functionsDir) ? model.getFunctionName() : "None";

        Files.write(functionName+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParnames()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParvals()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParmins()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParmaxs()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParfrozen()+"\n", specs, Charsets.UTF_8);
        updateListeners();
    }

    public void removeModel(String path) {
        File file = new File(path);
        file.delete();
        file = new File(path+".specs");
        file.delete();
    }

    public MutableTreeNode getCustomModels() throws IOException {
        tablesTree = new CustomModelsTreeModel(tablesDir, CustomModelType.TABLEMODEL);
        templatesTree = new CustomModelsTreeModel(templatesDir, CustomModelType.TEMPLATE);
        functionsTree = new CustomModelsTreeModel(functionsDir, CustomModelType.USERMODEL);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("User Model Components");
        root.add((MutableTreeNode) tablesTree.getRoot());
        root.add((MutableTreeNode) functionsTree.getRoot());
        root.add((MutableTreeNode) templatesTree.getRoot());

        return root;
    }

    public void addListener(ModelsListener listener) {
        listeners.add(listener);
    }

    private void updateListeners() throws IOException {
        MutableTreeNode tree = getCustomModels();
        for (ModelsListener l : listeners) {
            l.update(tree);
        }
    }
}
