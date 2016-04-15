package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.SherpaClient;

public class FitController {
    private ExtSed sed;
    private CustomModelsManager modelsManager;
    private SherpaClient client;

    public FitController(SherpaClient client, ExtSed sed, CustomModelsManager modelsManager) {
        this.client = client;
        this.sed = sed;
        this.modelsManager = modelsManager;
    }
}
