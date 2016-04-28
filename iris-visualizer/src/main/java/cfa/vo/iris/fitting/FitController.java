package cfa.vo.iris.fitting;

import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.fitting.custom.ModelsListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Param;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.DefaultModel;
import cfa.vo.sherpa.models.Parameter;
import org.jdesktop.application.Application;

import javax.swing.tree.TreeModel;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class FitController {

    private ExtSed sed;
    private SherpaClient client;
    private ModelsController modelsController;

    private final Logger logger = Logger.getLogger(FitController.class.getName());

    public FitController(ExtSed sed, CustomModelsManager manager, SherpaClient client) {
        this.sed = sed;
        this.client = client;
        modelsController = new ModelsController(manager);
    }

    public void addListener(ModelsListener listener) {
        modelsController.addListener(listener);
    }

    public void addModel(Model m) {
        logger.info("Added model " + m);
        String id = client.createId();
        Model toAdd = new DefaultModel(m, id);
        sed.getFit().addModel(toAdd);
    }

    public void addModel(DefaultCustomModel m) {
        logger.info("Added user model " + m);
        sed.getFit().addUserModel(m, client.createId());
    }

    public void filterModels(String searchString) {
        modelsController.filterModels(searchString);
    }

    public FitResults fit() throws Exception {
        FitResults retVal = client.fit(sed);
        sed.getFit().integrateResults(retVal);
        return retVal;
    }

    public ConfidenceResults computeConfidence() throws Exception {
        return client.computeConfidence(sed);
    }

    public TreeModel getModelsTreeModel() {
        return modelsController.getModelsTreeModel();
    }

    public ExtSed getSed() {
        return sed;
    }

    public void setSed(ExtSed sed) {
        this.sed = sed;
    }

    public FitConfiguration getFit() {
        return sed.getFit();
    }

    public void save(OutputStream os) {
        PrintWriter writer = new PrintWriter(os);
        writer.write("Iris Fitting Tool - Fit Summary\n");
        writer.write(String.format("SED ID: %s\n\n", sed.toString()));
        writer.write(sed.getFit().toString());
        writer.flush();
    }
}
