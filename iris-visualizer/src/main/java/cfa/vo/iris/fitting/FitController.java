package cfa.vo.iris.fitting;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.fitting.custom.ModelsListener;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.DefaultModel;
import cfa.vo.utils.Default;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller class for the Fitting Tool component.
 *
 * The class is responsible for managing models and acting on them
 * at the View's request.
 */
public class FitController {

    private SedModel sedModel;
    private SherpaClient client;
    private ModelsController modelsController;
    private ObjectMapper mapper;

    private final Logger logger = Logger.getLogger(FitController.class.getName());

    /**
     * The model for the fitting tool is the same as the Viewer, i.e. {@link SedModel}, at least in the current scenario
     * where fits belong to SEDs.
     *
     * The controller also delegates models management to a {@link CustomModelsManager} that must be initialized
     * by the {@link cfa.vo.iris.visualizer.FittingToolComponent}.
     *
     * Finally, an instance of a {@link SherpaClient} is required in order to call the actual operations
     * from the sherpa-samp service.
     *
     * There is an abuse of notations between Models in the MVC sense and Fitting Model Components, which
     * are referred to as Model in the Iris/Sherpa interface specification.
     *
     * @param sedModel the current model
     * @param manager a manager for custom Fitting Model Components
     * @param client a representation of the sherpa-samp service.
     */
    public FitController(SedModel sedModel, CustomModelsManager manager, SherpaClient client) {
        this.sedModel = sedModel;
        this.client = client;
        modelsController = new ModelsController(manager);
        mapper = new ObjectMapper();
        mapper.registerModule(new MrBeanModule());
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
        sedModel.getFit().addModel(toAdd);
    }

    /**
     * Add a Custom Fitting Model Component to the underlying model. Implementation is tied to a concrete
     * realization of the {@link cfa.vo.iris.fitting.custom.CustomModel} interface for "historical" reason, but
     * this should probably be fixed.
     * @param m The {@link DefaultCustomModel} instance representing the Custom Model Component to be added.
     */
    public void addModel(DefaultCustomModel m) {
        logger.info("Added user model " + m);
        sedModel.getFit().addUserModel(m, client.createId());
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
        Data data = constructSherpaCall(sedModel);
        
        // Make call to sherpa to fit data
        FitResults retVal = client.fit(data, getFit());
        sedModel.getFit().integrateResults(retVal);
        
        // Record the version number on the SED in the FitConfiguration
        sedModel.getFit().setSedVersion(sedModel.getVersion());
        
        return retVal;
    }
    
    Data constructSherpaCall(SedModel model) throws UnitsException {
        Data data = SAMPFactory.get(Data.class);
        data.setName(SherpaClient.DATA_NAME);
        
        // Extract fitting data from SedModel, don't include masked points.
        sedModel.setFittingData(data, false);
        double[] xoldvalues = data.getX();
        double[] yoldvalues = data.getY();
        double[] erroldvalues = data.getStaterror();
        String xoldunits = sedModel.getXUnits();
        String yoldUnits = sedModel.getYUnits();
        
        // Convert data's units to fitting units
        UnitsManager um = Default.getInstance().getUnitsManager();
        data.setY(um.convertY(yoldvalues, xoldvalues, yoldUnits, xoldunits, SherpaClient.Y_UNIT));
        data.setStaterror(um.convertErrors(erroldvalues, yoldvalues, xoldvalues,
                yoldUnits, xoldunits, SherpaClient.Y_UNIT));
        data.setX(um.convertX(xoldvalues, xoldunits, SherpaClient.X_UNIT));
        
        // if there are no fitting ranges, return the data as-is.
        if (sedModel.getFit().getFittingRanges().isEmpty()) {
            return data;
        }
        
        // otherwise, apply the fitting ranges to the data
        applyFittingRanges(data);
        return data;
    }
    
    /**
     * Apply the fitting ranges to the data. Takes the FittingRanges in the 
     * SedModel's FitConfiguration, and filters out points that don't fall 
     * within the spectral fitting ranges.
     * @param data - data to filter
     */
    private void applyFittingRanges(Data data) {
        FittingRangeIntervalTree intervals = new FittingRangeIntervalTree(sedModel.getFit().getFittingRanges());
        
        List<Double> tmpX = new ArrayList<>();
        List<Double> tmpY = new ArrayList<>();
        List<Double> tmpYErr = new ArrayList<>();
        
        int ct = 0;
        for (int i=0; i<data.getX().length; i++) {
            // add the point if it falls within the fitting ranges
            if (intervals.isPointInTree(data.getX()[i])) {
                tmpX.add(data.getX()[i]);
                tmpY.add(data.getY()[i]);
                tmpYErr.add(data.getStaterror()[i]);
                ct++;
            }
        }
        
        // convert the values to doubles
        double[] filteredX = new double[ct];
        double[] filteredY = new double[ct];
        double[] filteredYErr = new double[ct];
        
        for (int i=0; i<ct; i++) {
            filteredX[i] = tmpX.get(i);
            filteredY[i] = tmpY.get(i);
            filteredYErr[i] = tmpYErr.get(i);
        }
        
        data.setX(filteredX);
        data.setY(filteredY);
        data.setStaterror(filteredYErr);
    }

    /**
     * Compute confidence intervals for the current model. Computation is delegated to the sherpa-samp service attached to
     * this instance.
     * @return {@link ConfidenceResults} object representing the results of the confidence limits calculation
     * @throws Exception an exception may be thrown by the sherpa-samp service if the operation failed
     */
    public ConfidenceResults computeConfidence() throws Exception {
        ConfidenceResults retVal = client.computeConfidence(constructSherpaCall(sedModel), getFit());
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
     * @return {@link SedModel} instance
     */
    public SedModel getSedModel() {
        return sedModel;
    }

    /**
     * Set the current model
     * @param sedModel {@link SedModel} instance
     */
    public void setSedModel(SedModel sedModel) {
        this.sedModel = sedModel;
    }

    /**
     * Convenience method that returns the {@link FitConfiguration} object attached to the current SED.
     * @return {@link FitConfiguration} instance
     */
    public FitConfiguration getFit() {
        return sedModel.getFit();
    }

    /**
     * Save a summary of the current model's fit configuration in a human readable format to an {@link OutputStream}
     * @param os {@link OutputStream} to write to
     */
    public void save(OutputStream os) {
        PrintWriter writer = new PrintWriter(os);
        writer.write("Iris Fitting Tool - Fit Summary\n");
        writer.write(String.format("SED ID: %s\n\n", sedModel.getSed().toString()));
        writer.write(sedModel.getFit().toString());
        writer.flush();
    }

    /**
     * Save current model's fit configuration in json format to an {@link OutputStream}.
     *
     * Saved document could be read back to reconstruct an instance of the current model.
     * @param os
     */
    public void saveJson(OutputStream os) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(os, sedModel.getFit());
    }

    /**
     * Load model in json format from {@link InputStream}.
     *
     * The Controller sets the current model's internal state to the new {@link FitConfiguration},
     * but it also returns the read object for convenience.
     *
     * @param is
     * @return the {@link FitConfiguration} instance deserialized from the input stream.
     */
    public FitConfiguration loadJson(InputStream is) throws IOException {
        FitConfiguration conf = mapper.readValue(is, FitConfiguration.class);
        sedModel.setFit(conf);
        return conf;
    }

    /**
     * Evaluate Fitting Model. Returns a {@link SegmentStarTable} instance with the x and y values evaluated
     * according to the model passed as argument
     * @param sedModel The {@link SedModel} whose fit must be evaluated.
     * @return a {@link SegmentStarTable} instance
     * @throws Exception
     */
    public void evaluateModel(SedModel sedModel) throws Exception {
        
        // Update the model version with the current version of the Sed
        sedModel.setModelVersion(sedModel.getVersion());
        sedModel.setHasModelFunction(true);
        
        String xUnit = sedModel.getXUnits();
        String yUnit = sedModel.getYUnits();

        UnitsManager uManager = Default.getInstance().getUnitsManager();

        for (IrisStarTable ist: sedModel.getDataTables()) {
            SegmentStarTable table = ist.getPlotterDataTable();
            double[] x = table.getSpecValues();
            double[] xStandardUnit = uManager.convertX(x, xUnit, SherpaClient.X_UNIT);
            double[] yStandardUnit = client.evaluate(xStandardUnit, sedModel.getFit());
            double[] y = uManager.convertY(yStandardUnit, xStandardUnit,
                    SherpaClient.Y_UNIT, SherpaClient.X_UNIT, yUnit);

            table.setModelValues(y);
            table.setResidualValues(calcResiduals(table.getFluxValues(), y));
            table.setRatioValues(calcRatios(table.getFluxValues(), y));
        }
    }

    private double[] calcResiduals(double[] expected, double[] actual) {
        int len = expected.length;

        if (len != actual.length) {
            throw new IllegalArgumentException("Expected and Actual array should have the same size");
        }

        double[] ret = new double[len];

        for (int i=0; i<len; i++) {
            ret[i] = actual[i] - expected[i];
        }

        return ret;
    }

    private double[] calcRatios(double[] expected, double[] actual) {
        int len = expected.length;

        if (len != actual.length) {
            throw new IllegalArgumentException("Expected and Actual array should have the same size");
        }

        double[] ret = new double[len];

        for (int i=0; i<len; i++) {
            double e = expected[i];
            ret[i] = Math.abs(e - actual[i])/e;
        }

        return ret;
    }
}
