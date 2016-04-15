package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.fitting.custom.ModelsListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.ModelImpl;
import java.util.logging.Logger;

public class FitController {

    private ExtSed sed;
    private CustomModelsManager modelsManager;
    private SherpaClient client;

    private final Logger logger = Logger.getLogger(FitController.class.getName());

    public FitController(ExtSed sed, CustomModelsManager manager, SherpaClient client) {
        this.sed = sed;
        this.modelsManager = manager;
        this.client = client;
    }

    public void addListener(ModelsListener listener) {
        modelsManager.addListener(listener);
    }

    public void addModel(Model m) {
        logger.info("Added model " + m);
        String id = client.createId();
        Model toAdd = new ModelImpl(m, id);
        getFit().addModel(toAdd);
    }

    public void addModel(DefaultCustomModel m) {
        logger.info("Added user model " + m);
        getFit().addUserModel(m, client.createId());
    }

    public FitResults fit() throws Exception {
        FitResults retVal = client.fit(getFit().make(sed));
        getFit().integrateResults(retVal);
        return retVal;
    }

    public ConfidenceResults computeConfidence() throws Exception {
        return client.computeConfidence(getFit().make(sed));
    }

    public CustomModelsManager getModelsManager() {
        return modelsManager;
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
}
