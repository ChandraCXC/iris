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
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top source directory.
 */

package spv.components;

/**
 * Created by IntelliJ IDEA.
 * User: busko
 * Date: 2/12/12
 * Time: 3:03 PM
 */

/*
 *  Revision history:
 *  ----------------
 *
 *
 *  12 Feb 2011  -  Implemented (IB)
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.lang.reflect.Constructor;
import java.io.PrintWriter;

import com.sun.xml.tree.XmlDocument;
import com.sun.xml.tree.ElementNode;

import org.astrogrid.samp.client.*;
import org.astrogrid.samp.*;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.gui.GuiHubConnector;
import org.astrogrid.samp.gui.SubscribedClientListModel;
import org.w3c.dom.Node;
import spv.controller.SherpaFitManager;
import spv.controller.SpectrumContainer;
import spv.controller.SpvModelManager;
import spv.model.Server2;
import spv.spectrum.Spectrum;
import spv.spectrum.SpectrumTools;
import spv.spectrum.function.*;
import spv.util.*;
import spv.util.sed.SEDException;
import spv.fit.FittedSpectrum;
import spv.fit.SEDFittedSpectrum;
import spv.fit.MinimizationAlgorithm;
import spv.view.AbstractPlotWidget;
import spv.view.PlotStatus;

/*
 *  This is the controller for Sherpa fitting-related operations.
 *  <p>
 *  This class inherits most of its code from the regular Spv model
 *  manager class. It remains to be seen if this approach works in
 *  the long run.
 */

public class SherpaModelManager extends SpvModelManager {

    private static final String EXPRESSION_ATTRIBUTE = "Expression";

    // Constants used to access Sherpa response map.
    private static final String RSTAT = "rstat";
    private static final String QVAL = "qval";
    private static final String DOF = "dof";
    private static final String NUMPOINTS = "numpoints";
    private static final String NFEV = "nfev";
    private static final String STATVAL = "statval";

    private SEDFittedSpectrum sfsp;

    // SAMP communication handlers.
    private GuiHubConnector conn;
    private ResultHandler spectrumFitFitHandler;
    private ResultHandler spectrumFitCalcModelvaluesHandler;
    private ResultHandler spectrumFitConfidenceHandler;
    private ResultHandler stopHandler;

    private JTextField sherpaExpressionField;
    private Map lastFittingMap;
    private Map<String,String> pathMap;
    private Map<String,String> functionNameMap;

    private static List<String> fittingParameterNames = new ArrayList<String>();
    private static List<String> fittingParameterDescriptions = new ArrayList<String>();
    private Command callbackOnDispose;

    static {
        fittingParameterNames.add(STATVAL);
        fittingParameterNames.add(RSTAT);
        fittingParameterNames.add(QVAL);
        fittingParameterNames.add(DOF);
        fittingParameterNames.add(NUMPOINTS);
        fittingParameterNames.add(NFEV);

        fittingParameterDescriptions.add("Final fit statistic:     ");
        fittingParameterDescriptions.add("Reduced statistic:       ");
        fittingParameterDescriptions.add("Probability [Q-value]:   ");
        fittingParameterDescriptions.add("Degrees of freedom:      ");
        fittingParameterDescriptions.add("Data points:             ");
        fittingParameterDescriptions.add("Last function evaluation:");
    }

    /**
     *  Constructor.
     *
     *  @param  sp       the <code>Spectrum</code> object
     *  @param  conn     the connection to the SAMP hub
     *  @param  desktop  desktop to work with
     */
    public SherpaModelManager(Spectrum sp, GuiHubConnector conn, JDesktopPane desktop) {
        super(sp, null);

        this.conn = conn;

        super.setDesktop(desktop);

        sfsp = (SEDFittedSpectrum)fsp;

        pathMap = new HashMap<String,String>();
        functionNameMap = new HashMap<String,String>();
    }

    // .execute() is the main method used by the caller to activate the fit.
    // An instance of this class should always be created and associated with
    // a SED when the SED is displayed (see lines 275-283 in IrisVisualizer.java).
    // To activate the fit manager, call this method as in IrisVisualizer.java
    // line 459.

    public void execute (Object arg) {

        // this prevents fitting of error arrays to be performed. Not a very good
        // solution but will make do for now.

        if (pw != null) {
            String selectedY = ((AbstractPlotWidget) pw).selected_y;
            if (selectedY.contains("err") || selectedY.contains("Err") || selectedY.contains("ERR")) {
                return;
            }
        }

        super.execute(arg);

        sfsp = (SEDFittedSpectrum)fsp;

        buildGUIDisplays();

        buildSampHandlers();
    }

    // This is used to let external callers to execute
    // code when the dispose() method in this class gets
    // executed by, say, an independent GUI.

    public void setCallbackOnDispose(Command callbackOnDispose) {
        this.callbackOnDispose = callbackOnDispose;
    }

    public JInternalFrame getInternalFrame() {
        return frame.getInternalFrame();
    }

    public SEDFittedSpectrum getSEDFittedSpectrum() {
        return sfsp;
    }

    protected SpectrumContainer addDefaultComponent() {

        SherpaFunction function = FunctionFactorySherpaHelper.GetByName("powerlaw");
        addComponent(function);

        // Store decorated spectrum in server.

        SpectrumContainer container = new SpectrumContainer(fsp, this);
        container.setPlotStatus(ps);
        Server2.GetInstance().storeSpectrumContainer(container);
        fsp.addObserver(this);

        // Sherpa functions do not support notifications.
//        function.setDependentObject(fsp);
//        fsp.addObserver(function);

        return container;
    }

    public Function getRawFunction() {
        return FunctionFactorySherpaHelper.GetFromWidget();
    }

    public void setVisible(boolean visible) {
        frame.getInternalFrame().setVisible(visible);
        if (fitManager != null) {
            fitManager.memoryFrame.setVisible(visible);
        }
    }

    public void deleteComponents() {

        super.deleteComponents();

        if (deletedComponents.size() > 0) {

            // remove user components.

            for (int i = 0; i < deletedComponents.size(); i++) {
                Function function = deletedComponents.get(i);
                String key = function.getName();

                Set<String> keySet = pathMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    try {
                        String key2 = iterator.next();
                        String key3 = key2.split("\\.")[0];
                        if (key3.equals(key)) {
                            pathMap.remove(key2);
                            functionNameMap.remove(key2);
                        }
                    } catch (ConcurrentModificationException e) {
                        break;
                    }
                }
            }
        }
    }

    public void dispose() {
        if (JOptionPane.showConfirmDialog(frame.getFrame(),
                "** Model will be lost! ** You can save it to file with the File menu", "Confirm",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) ==
                JOptionPane.OK_OPTION) {

            if (fitManager != null) {
                fitManager.dispose();
            }

            sherpaExpressionField.setText("c1");

            if (callbackOnDispose != null) {
                callbackOnDispose.execute(this);
            }

            super.dispose();
        }
    }

    protected void goFit() {

        Map<String, Object> sampParams = createSampParams();

        if (fitManager == null) {
            fitManager = new SherpaFitManager(sfsp, this, controller, conn, spectrumFitFitHandler,
                    spectrumFitConfidenceHandler, stopHandler, sampParams, desktop);
        }
    }

    // Overrides base class to explicitly turn off the pan
    // canvas. In this class the pan canvas is repurposed to be a
    // residuals display, and thus must be removed when going back
    // to non-fit mode. This can cause problems, however, if fit
    // mode was entered with the pan canvas already on in the first
    // place. It will be forcibly turned off.

    protected void customChangePlotStatus(PlotStatus plotStatus) {
        plotStatus.setPan(false);
    }


    ///////////////////////////////////////////////////////////////////
    //
    //                 Saving to XML.
    //
    ///////////////////////////////////////////////////////////////////


    protected Function buildFunctionFromXML(Node node) throws FunctionException {
        return FunctionFactorySherpaHelper.BuildFromXML(node);
    }

    protected void saveElements(XmlDocument document, ElementNode root) {
        XMLUtilities.BuildDOMElement(document, root, EXPRESSION_ATTRIBUTE, sherpaExpressionField.getText());
    }

    protected void processNode(Node node) {
        if (node.getNodeName().equals((EXPRESSION_ATTRIBUTE))) {
            Node firstChild = node.getFirstChild();
            if (firstChild != null) {
                String expression = firstChild.getNodeValue();
                sherpaExpressionField.setText(expression);
            }
        }
    }

    protected void printFitInfo(PrintWriter pw) {
        if (lastFittingMap != null) {
            pw.println("Fit parameters:");
            for (int i = 0; i < fittingParameterNames.size(); i++) {
                String name =  fittingParameterNames.get(i);
                String value = (String) lastFittingMap.get(name);
                pw.println("        " + fittingParameterDescriptions.get(i) + " " + value);
            }
            pw.println();
        }
    }


    ///////////////////////////////////////////////////////////////////
    //
    //                 Fitting via Sherpa.
    //
    ///////////////////////////////////////////////////////////////////


    public void update (Observable observable, Object o) {
        if (observable instanceof SEDFittedSpectrum) {
            sfsp = ((SEDFittedSpectrum)observable);
        }
        super.update(observable, o);
    }

    private List<Map> createModel(List<Map> parts, String name) {
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("name", name);
        model.put("parts", parts);

        List<Map> models = new ArrayList<Map>();
        models.add(model);
        return models;
    }

    private List<Map> createUserModels() {

        // "usermodels" is a List of Maps; each
        // Map contains a name and a path to
        // the model file.
        
        List<Map> userModels = new ArrayList<Map>();

        Set<String> keys = pathMap.keySet();
        Iterator<String> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {

            String key = keysIterator.next();
            String path = pathMap.get(key);
            String name = functionNameMap.get(key);

            Map<String,String> userModel = new HashMap<String,String>();

            if (path != null) {
                userModel.put("name", key);
                userModel.put("file", path);
                userModel.put("function", name);

                userModels.add(userModel);
            }
        }

        return userModels;
    }

    private List<Map> createDatasets(double[] x, double[] y, double[] err) {

        // "datasets" is a List of Maps

        List<Map> datasets = new ArrayList<Map>();

        Map<String,String> dataset = new HashMap<String,String>();
        dataset.put("name", FittedSpectrum.RemovePrefix(fsp.getName()));
        SpectrumTools tools = new SpectrumTools();
        double[] wavelengths = x;
        wavelengths = tools.removeDatamarkers(wavelengths);
        double[] fluxes = y;
        fluxes = tools.removeDatamarkers(fluxes);
        double[] errors = err;
        if (errors == null) {
            errors = new double[fluxes.length];
        }
        errors = tools.removeDatamarkers(errors);

        dataset.put("x", SpvEncodeDoubleArray.encodeBase64(wavelengths));
        dataset.put("y", SpvEncodeDoubleArray.encodeBase64(fluxes));
        dataset.put("staterror", SpvEncodeDoubleArray.encodeBase64(errors));
        datasets.add(dataset);

        return datasets;
    }

    private List<Map> createParts() {

        // "parts" is a List of Maps

        List<Map> parts = new ArrayList<Map>();

        ComponentDatabase cdb = fsp.getContinuousIntensity().getComponentDatabase();
        Enumeration componentList = cdb.getComponentList();

        pathMap = new HashMap<String, String>();
        functionNameMap = new HashMap<String,String>();

        int index = 1;
        while (componentList.hasMoreElements()) {
            SherpaFunction component = (SherpaFunction) componentList.nextElement();

            Map<String,Object> part = new HashMap<String,Object>();
            // Each Map contains two entries: a "name" with the component type and
            // identification in the form "bbody.c1", and a "pars" list of Maps.
            String name = component.getName() + ".c" + String.valueOf(index);
            part.put("name", name);

            // Added this to support user-defined components: we store
            // the component URL in this separate map to use later in
            // complementing the SAMP request.

            String path = component.getPath();
            pathMap.put(name, path);

            // Same for Sherpa-required function name.

            String functionName = component.getFunctionName();
            functionNameMap.put(name, functionName);

            List<Map> pars = new ArrayList<Map>();
            int npars = component.getNumberOfParameters();
            for (int i = 0; i < npars; i++) {

                // Each Map stores the parameter name and fields.
                SherpaFParameter parameter = (SherpaFParameter) component.getParameter(i);
                Map<String,String> parameterFields = new HashMap<String,String>();

                String parameterName = "c" + String.valueOf(index) + "." +  parameter.getName();
                parameterFields.put("name", parameterName);

                parameterFields.put("val", String.valueOf(parameter.getValue()));
                parameterFields.put("min", String.valueOf(parameter.getMin()));
                parameterFields.put("max", String.valueOf(parameter.getMax()));
                parameterFields.put("frozen", parameter.isFixed() ? "1" : "0");
                parameterFields.put("alwaysfrozen", parameter.isAlwaysFixed() ? "1" : "0");

                pars.add(parameterFields);
            }
            part.put("pars",pars);
            parts.add(part);
            index++;
        }

        return parts;
    }

    private String createModelName() {
        return sherpaExpressionField.getText();
    }

    private void storeFitParameterValues(Map map) {
        storeParameterValues(map);
        storeFitResultParameters(map);

        MapLogger sl = new MapLogger("Fitting results:");
        sl.log(map);

        printDatabase(SpvLogger.getLogStream());
    }

    private void storeConfidenceParameterValues(Map map) {
        storeParameterValues(map);
        storeConfidenceResultParameters(map);

        ((SherpaFitManager) fitManager).updateConfidenceTaable(map);

        MapLogger sl = new MapLogger("Confidence results:");
        sl.log(map);

        printDatabase(SpvLogger.getLogStream());

        setGUIDoneFitting();
    }

    private void handleStop() {
        setGUIDoneFitting();
    }

    private void storeFitResultParameters(Map map) {

        lastFittingMap = map;

        String statval = (String) map.get(STATVAL);
        String nfev = (String) map.get(NFEV);
        fitManager.tolfield.setText(statval + "  at function evaluation  " + nfev);
        ((SherpaFitManager) fitManager).numpointsfield.setText((String) map.get(NUMPOINTS));
        ((SherpaFitManager) fitManager).doffield.setText((String) map.get(DOF));
        fitManager.chisqfield.setText((String) map.get(QVAL));
        fitManager.iterfield.setText((String) map.get(RSTAT));
    }

    private void storeConfidenceResultParameters(Map map) {
        fitManager.sigmafield.setText((String) map.get("sigma"));
        fitManager.percentfield.setText((String) map.get("percent"));
    }

    private void storeParameterValues(Map map) {

        List parnames = (List) map.get("parnames");

        String parvalsString = (String) map.get("parvals");
        String parminsString = (String) map.get("parmins");
        String parmaxesString = (String) map.get("parmaxes");

        double[] parvals = null;
        double[] parmins = null;
        double[] parmaxes = null;
        try {
            parvals = SpvEncodeDoubleArray.decodeBase64(parvalsString);
            if (parminsString != null && parmaxesString != null) {
                parmins  = SpvEncodeDoubleArray.decodeBase64(parminsString);
                parmaxes = SpvEncodeDoubleArray.decodeBase64(parmaxesString);
            }

            ComponentDatabase cdb = fsp.getContinuousIntensity().getComponentDatabase();

            // Turn off notifications so display won't flash.
            cdb.enableNotifications(false);

            // Parnames returned by Sherpa look like this:
            //
            // [c1.space, c1.kT, c1.ampl, c2.r0, c2.beta, c2.xpos, c2.ampl]
            //
            //  so we split at the dot, and remove the 'c' to get the component index.
            //  Then we store the parameter value in the component.

            // todo how to handle fit parameter's parameters? Out-of-limit values generate unhandled exception.

            for (int i = 0; i < parnames.size(); i++) {
                String parname = (String) parnames.get(i);
                String name = getParameterName(parname);
                int index = getComponentIndex(parname);

                SherpaFunction component = (SherpaFunction) cdb.getComponent(index-1);
                Parameter parameter = component.getParameter(name);

                parameter.setValue(parvals[i]);
                if (parmins != null) {
                    // we cheat and use Error to store upper limit and
                    // uncertainity to store lower limit.
                    // GUI should handle then accordingly.
                    parameter.setError(parmaxes[i]);
                    parameter.setUncertainity(parmins[i]);
                    parameter.refreshGUI();
                }
            }

            // turn notifiations back on, and kick cdb's butt.
            cdb.enableNotifications(true);
            cdb.notifyComponentListeners();

            ((SherpaFitManager) fitManager).setParameters(createSampParams());

        } catch (OutOfRangeFunctionException e) {
            e.printStackTrace();
        }
    }

    private String getParameterName(String parname) {
        return parname.split("\\W")[1];
    }

    private int getComponentIndex(String parname) {
        String prefix = parname.split("\\W")[0];
        String indexString = prefix.substring(1,prefix.length());
        return Integer.valueOf(indexString);
    }

    private void handleModelValues(Map map) {

        // todo what to do with model errors?. Cannot display right now.

        List data = (List) map.get("results");

        String x = (String) data.get(0);
        String y = (String) data.get(1);
        String e = (String) data.get(2);

        double[] wave  = SpvEncodeDoubleArray.decodeBase64(x);
        double[] flux  = SpvEncodeDoubleArray.decodeBase64(y);
        double[] error = SpvEncodeDoubleArray.decodeBase64(e);

        // this handles data markers in target array in fsp spectrum.
        double[] hflux = fsp.getValues();
        int j = 0;
        for (int i = 0; i < hflux.length; i++) {
            if (hflux[i] != Constant.DATA_MARKER) {
                try {
                    hflux[i] = flux[j++];
                } catch (ArrayIndexOutOfBoundsException e1) {
                }
            }
        }

        ((SEDFittedSpectrum) fsp).setModelValues(hflux);

        setGUIDoneFitting();

        //todo store everything when here, so we can undo if asked.
        // Use a static Document, and re-use code in super class that
        // writes/reads from XML.
        // Problem: code in superclass does not save the model array.
    }


    ///////////////////////////////////////////////////////////////////
    //
    //                 GUI customization.
    //
    ///////////////////////////////////////////////////////////////////


    private void setGUIDoneFitting() {

        setStandbyGUI(false);

        try {
            fitManager.setGUIStatus(MinimizationAlgorithm.STOPPED_AT_BEGINNING);
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
    }

    // Adds the text field where the Sherpa expression is kept and edited.

    protected void buildJPanel4() {
        sherpaExpressionField  = new JTextField("c1");

        // Dispose fit manager when expression gets changed.
        // Note that Return must be hit for it to happen.
        sherpaExpressionField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent documentEvent) {
                disposeFitManager();
            }
            public void removeUpdate(DocumentEvent documentEvent) {
                disposeFitManager();
            }
            public void changedUpdate(DocumentEvent documentEvent) {
                disposeFitManager();
            }
        });

        GridLayout gridLayout1 = new GridLayout(  );
        gridLayout1.setColumns(1);
        gridLayout1.setRows( 5 );
        JPanel4.setLayout( gridLayout1 );

        JPanel sherpaPanel = new JPanel(new BorderLayout());
        JLabel sherpaExpressionLabel = new JLabel("Model expression");
        sherpaPanel.add(sherpaExpressionLabel, BorderLayout.LINE_START);
        sherpaPanel.add(sherpaExpressionField, BorderLayout.CENTER);

        JPanel4.add(sherpaPanel);

        JPanel4.add(prompt_field, null, -1);
        JPanel4.add(npanel, null, -1);
        JPanel4.add(mpanel, null, -1);
        JPanel4.add(spanel, null, -1);
    }

    public void setSpecialGUI (boolean state) {

        super.setSpecialGUI(false);

        bundorange.setVisible(false);
        breset.setVisible(false);
        badjust.setVisible(false);
        brecenter.setVisible(false);
        bcopy.setVisible(false);
        bconstrain.setVisible(false);
        bunconstrain.setVisible(false);
        bundelete.setVisible(false);

        help_menu.setEnabled(false);
    }

    protected void labelButtons(JButton defaultButton, JButton fixButton) {
        defaultButton.setText( "Thaw all" );
        defaultButton.setToolTipText( "Reset all components to default thaw mode" );

        fixButton.setText( "Freeze all" );
        fixButton.setToolTipText( "Freeze all parameters in entire model" );
    }

    protected void addUndoButton(JPanel panel) {
        panel.add(new JButton("Undo"));
    }


    ///////////////////////////////////////////////////////////////////
    //
    //                 SAMP-related code.
    //
    ///////////////////////////////////////////////////////////////////


    private Map<String, Object> createSampParams() {
        double[] x = sfsp.getWavelengthsInRanges();
        double[] y = sfsp.getValuesInRanges();
        double[] e = sfsp.getErrorsInRanges();
        List<Map> datasets = createDatasets(x, y, e);

        List<Map> parts = createParts();
        String modelName = createModelName();

        List<Map> models = createModel(parts, modelName);
        List<Map> userModels = createUserModels();

        Map<String,String> stat = new HashMap<String,String>();

        Map<String,String> method = new HashMap<String,String>();
        method.put("name", "levmar");

        Map<String,Object> sampParams = new HashMap<String,Object>();
        sampParams.put("datasets", datasets);
        sampParams.put("models", models);
        sampParams.put("usermodels", userModels);
        sampParams.put("stat", stat);
        sampParams.put("method", method);

        return sampParams;
    }

    private void requestModelValues() {
        Map<String, Object> response = new HashMap<String, Object>();

        double[] x = sfsp.getWavelengths();
        double[] y = sfsp.getValues();
        double[] e = sfsp.getErrors();
        List<Map> datasets = createDatasets(x, y, e);

        List<Map> partsList = createParts();
        String modelName = createModelName();

        List<Map> modelsList = createModel(partsList, modelName);
        List<Map> userModels = createUserModels();

        response.put("datasets", datasets);
        response.put("models", modelsList);
        response.put("usermodels", userModels);

        // this must be cleaned and refactored.

        Message message = new Message(Include.MTYPE_SPECTRUM_FIT_CALC_MODEL_VALUES);
        Set keys = response.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = response.get(key);
            message.addParam(key, value);
        }

        try {
            ListModel listModel = new SubscribedClientListModel(conn, Include.MTYPE_SPECTRUM_FIT_CALC_MODEL_VALUES);
            for (int i = 0; i < listModel.getSize(); i++) {
                Client client = (Client) listModel.getElementAt(i);
                conn.call(client.getId(), message, spectrumFitCalcModelvaluesHandler, 0);
            }
        } catch (SampException ex) {
            ex.printStackTrace();
        }
    }

    private void buildSampHandlers() {
        spectrumFitFitHandler = new SpectrumFitFitHandler();
        spectrumFitConfidenceHandler = new SpectrumFitConfidenceHandler();
        spectrumFitCalcModelvaluesHandler = new SpectrumFitCalcModelValuesHandler();
        stopHandler = new StopHandler();
    }

    private void detectSherpaException(Response response) throws SEDException {
        if (!response.isOK()) {

            Map result = response.getResult();

            String exceptionString = (String) result.get("exception");
            String exceptionMessage = (String) result.get("message");

            SEDException sedex = null;
            try {
                Class<?> exceptionClass = Class.forName("spv.util.sed." + exceptionString);
                Constructor<?> constructor = exceptionClass.getConstructor(new Class[]{String.class});
                sedex = (SEDException) constructor.newInstance(exceptionMessage);
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
            throw sedex;
        }
    }

    abstract class SpectrumResultHandler implements ResultHandler {
        protected Map map;
        public void result(Client client, Response response) {
            try {
                detectSherpaException(response);
            } catch (SEDException e) {
                ExceptionHandler.handleException(e);
            }
            map = response.getResult();
        }
        public void done() {
        }
    }

    class SpectrumFitFitHandler extends SpectrumResultHandler {
        public void result(Client client, Response response) {
            super.result(client, response);
            String exceptionString = (String) map.get("exception");
            if (exceptionString == null && !map.isEmpty()) {
                storeFitParameterValues(map);
                requestModelValues();
            } else if (fitManager != null){
                fitManager.setGUIStatus(MinimizationAlgorithm.STOPPED_AT_BEGINNING);
            }
        }
    }

    class SpectrumFitConfidenceHandler extends SpectrumResultHandler {
        public void result(Client client, Response response) {
            super.result(client, response);
            String exceptionString = (String) map.get("exception");
            if (exceptionString == null && !map.isEmpty()) {
                storeConfidenceParameterValues(map);
            } else if (fitManager != null){
                fitManager.setGUIStatus(MinimizationAlgorithm.STOPPED_AT_BEGINNING);
            }
        }
    }

    class SpectrumFitCalcModelValuesHandler extends SpectrumResultHandler {
        public void result(Client client, Response response) {
            super.result(client, response);
            String exceptionString = (String) map.get("exception");
            if (exceptionString == null && !map.isEmpty()) {
                handleModelValues(map);
            } else if (fitManager != null){
                fitManager.setGUIStatus(MinimizationAlgorithm.STOPPED_AT_BEGINNING);
            }
        }
    }

    class StopHandler extends SpectrumResultHandler {
        public void result(Client client, Response response) {
            super.result(client, response);
            String exceptionString = (String) map.get("exception");
            if (exceptionString == null && !map.isEmpty()) {
                handleStop();
            } else if (fitManager != null){
                fitManager.setGUIStatus(MinimizationAlgorithm.STOPPED_AT_BEGINNING);
            }
        }
    }
}