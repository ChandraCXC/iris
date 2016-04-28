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
        StringBuilder builder = new StringBuilder();

        builder
                .append("Iris Fitting Tool - Fit Summary\n")
                .append("SED ID: ").append(sed.getId()).append("\n\n")
                .append("Model Expression: ").append(sed.getFit().getModel().getName()).append("\n")
                .append("Components:\n");


        for (Model m : sed.getFit().getModel().getParts()) {
            builder.append("\t").append(m.getName()).append("\n");
            for (Parameter p : m.getPars()) {
                builder.append(formatDouble(p.getName(), p.getVal()));
            }
        }

        builder
                .append("\nFit Results:\n")
                .append(formatDouble("Final Fit Statistic", sed.getFit().getStatVal()))
                .append(formatDouble("Reduced Statistic", sed.getFit().getrStat()))
                .append(formatDouble("Probability (Q-value)", sed.getFit().getqVal()))
                .append(formatInt("Degrees of Freedom", sed.getFit().getDof()))
                .append(formatInt("Data Points", sed.getFit().getNumPoints()))
                .append(formatInt("Function Evaluations", sed.getFit().getnFev()))
                .append("\n")
                .append(formatString("Optimizer", sed.getFit().getMethod().toString()))
                .append(formatString("Statistic (Cost function)", sed.getFit().getStat().toString()))
                ;


        writer.write(builder.toString());
        writer.flush();
    }

    private String formatDouble(String name, Double value) {
        return String.format("\t\t%26s = %f\n", name, value);
    }

    private String formatInt(String name, Integer value) {
        return String.format("\t\t%26s = %d\n", name, value);
    }

    private String formatString(String name, String value) {
        return String.format("\t\t%26s = %s\n", name, value);
    }
}
