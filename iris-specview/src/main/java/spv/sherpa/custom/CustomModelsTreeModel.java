/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spv.sherpa.custom;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author olaurino
 */
public class CustomModelsTreeModel extends DefaultTreeModel {
    public CustomModelsTreeModel(File dir) throws IOException {
        super(new DefaultMutableTreeNode(last(dir.getPath().split(File.separator))));

        if(!dir.exists())
            dir.mkdirs();
        else if(!dir.isDirectory())
            throw new IOException("Tables directory exists but is not a directory");

        for(File f : dir.listFiles()) {
            try {
                String name = last(f.getPath().split(File.separator));
                if(!name.endsWith(".specs")) {
                    String path = f.getAbsolutePath();
                    DefaultCustomModel model = new DefaultCustomModel(name, path);
                    File specs = new File(path+".specs");
                    List<String> attrs = Files.readLines(specs, Charsets.UTF_8);
                    model.setParnames(attrs.get(0));
                    model.setParvals(attrs.get(1));
                    model.setParmins(attrs.get(2));
                    model.setParmaxs(attrs.get(3));
                    model.setParfrozen(attrs.get(4));
                    ((DefaultMutableTreeNode) root).add(new DefaultMutableTreeNode(model));
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(CustomModelsTreeModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static <T> T last(T[] array) {
        return array[array.length - 1];
    }
}
