package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.fitting.custom.ModelsListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.DefaultModel;

import javax.swing.tree.TreeModel;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Controller class for the Fitting Tool component.
 *
 * The class is responsible for managing models and acting on them
 * at the View's request.
 */
public class FitController {

    private ExtSed sed;
    private SherpaClient client;
    private ModelsController modelsController;

    private final Logger logger = Logger.getLogger(FitController.class.getName());

    /**
     * The model for the fitting tool is actually the whole SED, at least in the current scenario
     * where fits belong to SEDs.
     *
     * The model also delegates models management to a {@link CustomModelsManager} that must be initialized
     * by the {@link cfa.vo.iris.visualizer.FittingToolComponent}.
     *
     * Finally, an instance of a {@link SherpaClient} is required in order to call the actual operations
     * from the sherpa-samp service.
     *
     * There is an abuse of notations between Models in the MVC sense and Fitting Model Components, which
     * are referred to as Model in the Iris/Sherpa interface specification.
     *
     * @param sed the current model
     * @param manager a manager for custom Fitting Model Components
     * @param client a representation of the sherpa-samp service.
     */
    public FitController(ExtSed sed, CustomModelsManager manager, SherpaClient client) {
        this.sed = sed;
        this.client = client;
        modelsController = new ModelsController(manager);
    }

    /**
     * As part of the abstraction to the {@link FittingMainView} the controller
     * allows clients to register to events pertaining custom Model Components, i.e. when models are
     * added or removed from the models manager.
     * @param listener a {@link ModelsListener} implementation
     */
    public void addListener(ModelsListener listener) {
        modelsController.addListener(listener);
    }

    /**
     * Add a Fitting Model Component to the underlying model
     * @param m The {@link Model} instance representing the Fitting Model Component to be added.
     */
    public void addModel(Model m) {
        logger.info("Added model " + m);
        String id = client.createId();
        Model toAdd = new DefaultModel(m, id);
        sed.getFit().addModel(toAdd);
    }

    /**
     * Add a Custom Fitting Model Component to the underlying model. Implementation is tied to a concrete
     * realization of the {@link cfa.vo.iris.fitting.custom.CustomModel} interface for "historical" reason, but
     * this should probably be fixed.
     * @param m The {@link DefaultCustomModel} instance representing the Custom Model Component to be added.
     */
    public void addModel(DefaultCustomModel m) {
        logger.info("Added user model " + m);
        sed.getFit().addUserModel(m, client.createId());
    }

    /**
     * Filter existing models according to a simple string matching. Implementation is delegated to
     * {@link ModelsController#filterModels(String)}
     *
     * @param searchString A string to be matched.
     *
     * @see {@link ModelsController#filterModels(String)}
     */
    public void filterModels(String searchString) {
        modelsController.filterModels(searchString);
    }

    /**
     * Fit the current model and return results. Fitting is delegated to the sherpa-samp service attached to
     * this instance.
     * @return {@link FitResults} object representing the results of the fit.
     * @throws Exception An exception may be thrown by the sherpa-samp service if the fitting operation failed.
     */
    public FitResults fit() throws Exception {
        FitResults retVal = client.fit(sed);
        sed.getFit().integrateResults(retVal);
        return retVal;
    }

    /**
     * Compute confidence intervals for the current model. Computation is delegated to the sherpa-samp service attached to
     * this instance.
     * @return {@link ConfidenceResults} object representing the results of the confidence limits calculation
     * @throws Exception an exception may be thrown by the sherpa-samp service if the operation failed
     */
    public ConfidenceResults computeConfidence() throws Exception {
        ConfidenceResults retVal = client.computeConfidence(sed);
        getFit().setConfidenceResults(retVal);
        return retVal;
    }

    /**
     * Return a {@link TreeModel} representation of the current fitting model
     * @return a {@link TreeModel} instance
     */
    public TreeModel getModelsTreeModel() {
        return modelsController.getModelsTreeModel();
    }

    /**
     * Return the current model
     * @return {@link ExtSed} instance
     */
    public ExtSed getSed() {
        return sed;
    }

    /**
     * Set the current model
     * @param sed {@link ExtSed} instance
     */
    public void setSed(ExtSed sed) {
        this.sed = sed;
    }

    /**
     * Convenience method that returns the {@link FitConfiguration} object attached to the current SED.
     * @return {@link FitConfiguration} instance
     */
    public FitConfiguration getFit() {
        return sed.getFit();
    }

    /**
     * Save a summary of the current model in a human readable format to an {@link OutputStream}
     * @param os {@link OutputStream} to write to
     */
    public void save(OutputStream os) {
        PrintWriter writer = new PrintWriter(os);
        writer.write("Iris Fitting Tool - Fit Summary\n");
        writer.write(String.format("SED ID: %s\n\n", sed.toString()));
        writer.write(sed.getFit().toString());
        writer.flush();
    }
}
