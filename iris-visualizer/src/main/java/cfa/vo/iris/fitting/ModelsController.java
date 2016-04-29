package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.ModelsListener;
import cfa.vo.iris.utils.IPredicate;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.ModelFactory;
import cfa.vo.sherpa.models.Parameter;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelsController implements CustomModelsManager.CustomModelsListener {
    private CustomModelsManager modelsManager;
    private ModelsTreeModel model;
    private MutableTreeNode customModels;
    private ModelFactory factory = new ModelFactory();
    private StringPredicate predicate = new StringPredicate("");
    private List<ModelsListener> listeners = new ArrayList<>();

    private Logger logger = Logger.getLogger(ModelsController.class.getName());

    public ModelsController(CustomModelsManager modelsManager) {
        this.modelsManager = modelsManager;
        modelsManager.addListener(this);
        initModel();
    }

    public void addListener(ModelsListener listener) {
        listeners.add(listener);
    }

    public TreeModel getModelsTreeModel() {
        return model;
    }

    public void filterModels(String searchString) {
        predicate.setString(searchString);
        List<Model> sub = predicate.apply(model.getList());
        updateModels(sub);
    }

    private void initModel() {
        List<Model> models = new ArrayList<>(factory.getModels());
        try {
            customModels = modelsManager.getCustomModels();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading custom models", e);
        }
        model = new ModelsTreeModel(models, customModels);
    }

    private void updateModels(List<Model> models) {
        updateModels(new ModelsTreeModel(models, customModels));
    }

    private void updateModels(ModelsTreeModel tree) {
        for (ModelsListener l : listeners) {
            l.setModel(tree);
        }
    }

    @Override
    public void update(MutableTreeNode customTree) {
        customModels = customTree;
        model = new ModelsTreeModel(new ArrayList<>(factory.getModels()), customTree);
        updateModels(model);
    }

    private class StringPredicate implements IPredicate<Model> {

        private String string;

        public StringPredicate(String string) {
            this.string = string.toLowerCase();
        }

        public void setString(String string) {
            this.string = string.toLowerCase();
        }

        @Override
        public boolean apply(Model object) {
            boolean resp = false;
            if (object.getName() != null) {
                resp = object.getName().toLowerCase().contains(string);
            }
            if (object.getDescription() != null) {
                resp |= object.getDescription().toLowerCase().contains(string);
            }
            if (object.getPars() != null) {
                for (Parameter p : object.getPars()) {
                    resp |= p.getName().toLowerCase().contains(string);
                }
            }

            return resp;
        }

        public List<Model> apply(List<Model> all) {
            List<Model> sub = new ArrayList<>();

            for (Model m : all) {
                if (predicate.apply(m)) {
                    sub.add(m);
                }
            }

            return sub;
        }
    }
}
