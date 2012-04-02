/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spv.sherpa.custom;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author olaurino
 */
public final class CustomModelsManager {

    private File rootDir;
    private File tablesDir;
    private File functionsDir;
    private File templatesDir;

    private CustomModelsTreeModel tablesTree;
    private CustomModelsTreeModel templatesTree;
    private CustomModelsTreeModel functionsTree;


    public CustomModelsManager(File rootDir) throws IOException {
        this.rootDir = rootDir;
        getCustomModels();
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

        Files.write(model.getParnames()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParvals()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParmins()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParmaxs()+"\n", specs, Charsets.UTF_8);
        Files.append(model.getParfrozen()+"\n", specs, Charsets.UTF_8);
    }

    public void removeModel(String path) {
        File file = new File(path);
        file.delete();
        file = new File(path+".specs");
        file.delete();
    }

    public DefaultTreeModel getCustomModels() throws IOException {
        tablesDir = new File(rootDir+File.separator+"tables");
        if(!tablesDir.exists())
            tablesDir.mkdir();
        else if(!tablesDir.isDirectory())
            throw new IOException("Tables directory exists but is not a directory");

        functionsDir = new File(rootDir+File.separator+"functions");
        if(!functionsDir.exists())
            functionsDir.mkdir();
        else if(!functionsDir.isDirectory())
            throw new IOException("Functions directory exists but is not a directory");

        templatesDir = new File(rootDir+File.separator+"templates");
        if(!templatesDir.exists())
            templatesDir.mkdir();
        else if(!templatesDir.isDirectory())
            throw new IOException("Templates directory exists but is not a directory");

        tablesTree = new CustomModelsTreeModel(tablesDir);
        templatesTree = new CustomModelsTreeModel(templatesDir);
        functionsTree = new CustomModelsTreeModel(functionsDir);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Custom Model Components");
        root.add((MutableTreeNode) tablesTree.getRoot());
        root.add((MutableTreeNode) functionsTree.getRoot());
        root.add((MutableTreeNode) templatesTree.getRoot());

        DefaultTreeModel customTree = new DefaultTreeModel(root);

        return customTree;
    }
}
