/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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
 * Date: 2/13/12
 * Time: 3:03 PM
 */

import cfa.vo.interop.SAMPController;
import cfa.vo.iris.*;
import cfa.vo.iris.events.*;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import org.astrogrid.samp.client.MessageHandler;
import spv.SpvInitialization;
import spv.controller.ModelManager2;
import spv.controller.SpectrumContainer;
import spv.fit.FittedSpectrum;
import spv.glue.SpectrumVisualEditor;
import spv.sherpa.custom.CustomModelsManager;
import spv.sherpa.custom.CustomModelsManagerView;
import spv.sherpa.custom.DefaultCustomModel;
import spv.spectrum.Spectrum;
import spv.spectrum.SpectrumException;
import spv.spectrum.factory.SED.SEDFactoryModule;
import spv.spectrum.function.*;
import spv.util.Command;
import spv.util.ExceptionHandler;
import spv.util.Include;
import spv.util.NonSupportedUnits;
import spv.util.properties.SpvProperties;
import spv.view.AbstractPlotWidget;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class IrisVisualizer implements IrisComponent {

    private IrisDisplayManager idm;
    private static IWorkspace ws;
    private IrisApplication app;
    private JInternalFrame currentFrame;
    private SedlibSedManager sedManager;
    private SEDFactoryModule factory = new SEDFactoryModule();
    private Point lastLocation;
    private CustomModelsManager customManager;
    private CustomModelsManagerView customManagerView;
    private IrisVisualizer visualizer;  // self-reference for use in inner classes

    public void init(IrisApplication app, IWorkspace workspace) {

        visualizer = this;

        sedManager = (SedlibSedManager) workspace.getSedManager();

        SpvInitialization spvinit = new SpvInitialization(new String[]{}, null);

        SpvProperties.SetProperty(Include.APP_NAME, Include.IRIS_APP_NAME);
        SpvProperties.SetProperty(Include.APP_VERSION, Include.IRIS_VERSION);
        spvinit.initialize(null, false);

        FunctionFactorySherpaHelper.initialize();

        this.app = app;
        ws = workspace;

        idm = new IrisDisplayManager(sedManager, ws, this);
        idm.setDesktopMode(true);
        idm.setConnection(app.getSAMPController());

        File rootDir = new File(app.getConfigurationDir() + File.separator + "analysis" + File.separator + "custom_models");

        try {

            customManager = new CustomModelsManager(rootDir);

            TreeRefresher treeRefresher = new TreeRefresher();
            treeRefresher.execute(null);
            FunctionFactorySherpaHelper.SetTreeRefresher(treeRefresher);

        } catch (Exception ex) {
            Logger.getLogger(IrisVisualizer.class.getName()).log(Level.SEVERE, null, ex);
            int ans = NarrowOptionPane.showConfirmDialog(ws.getRootFrame(), "Error initializing Custom Fit Component Manager: " + ex.getMessage()
                    + "\nDo you want to reset custom models?", "Iris Visualizer", NarrowOptionPane.ERROR_MESSAGE);
            if (ans == NarrowOptionPane.OK_OPTION) {
                try {
                    if (rootDir.isDirectory()) {
                        deleteDirectory(rootDir);
                    } else {
                        rootDir.delete();
                    }
                    rootDir.mkdir();
                    customManager = new CustomModelsManager(rootDir);


                } catch (IOException ex1) {
                    Logger.getLogger(IrisVisualizer.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }

        SedEvent.getInstance().add(new SedListener() {

            public void process(final ExtSed source, SedCommand payload) {

                if (payload == SedCommand.SELECTED ||
                    payload == SedCommand.CHANGED) {

                    if (source.getNumberOfSegments() > 0) {
                        display(source);
//                    } else {
//                        remove(source);
                    }

                } else if (payload == SedCommand.REMOVED) {

                    remove(source);

                }
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            public void process(Segment source, final SegmentPayload payload) {

                ExtSed sed = payload.getSed();

//                if (sed.getNumberOfSegments() > 0) {
//                    display(sed);
//                } else {
//                    remove(sed);
//                }

//                if (payload.getSedCommand() == SedCommand.REMOVED) {
//                    return;
//                }

//                ExtSed sed = payload.getSed();

                // If the sed structure was modified, invalidate
                // any model associated with it.

                invalidateModel(sed);

                display(payload.getSed());
            }
        });

        MultipleSegmentEvent.getInstance().add(new MultipleSegmentListener() {

            public void process(List<Segment> source, final SegmentPayload payload) {
                ExtSed sed = payload.getSed();

                // If the sed structure was modified, invalidate
                // any model associated with it.

                invalidateModel(sed);

                display(payload.getSed());
            }
        });
    }

    public void invalidateModel(ExtSed sed) {
        if (sed != null) {
            SpectrumContainer container = (SpectrumContainer) sed.getAttachment(IrisDisplayManager.FIT_MODEL);
            if (container != null) {

                ModelManager2 mm = container.getModelManager();
                if (mm != null && mm.isActive()) {
                    mm.dispose();
                }

                sed.removeAttachment(IrisDisplayManager.FIT_MODEL);
            }
        }
    }

    public void disposeCurrentFrame() {
        if (currentFrame != null) {
            lastLocation = currentFrame.getLocation();
            currentFrame.setVisible(false);
            currentFrame.dispose();
            currentFrame = null;
        }
    }

    private void remove(ExtSed source) {
        invalidateModel(source);  // Might be needed in the future?
        idm.remove(source.getId());
    }

    private synchronized void display(ExtSed sed) {

        manageAssociatedManagerWindows(sed);

        try {

            SpectrumContainer container = (SpectrumContainer) sed.getAttachment(IrisDisplayManager.FIT_MODEL);

            // There is no Sed attachment, so build a model manager and attach it.

            if (container == null) {
                if (buildAttachment(sed)) {
                    return;
                }
            }

            // VAOPD-879: spectrum name must be identical with Sed name.
            if (container != null) {
                container.getSpectrum().setName(sed.getId());
            }

            // Now display the Sed.

            idm.display(sed, sed.getId());

            // and add its frame to the workspace.

            JInternalFrame frame = idm.getInternalFrame();

            // VAOPD-863
            frame.setTitle(sed.getId());

            if (container != null) {
                JFrame modelManagerFrame = container.getModelManager().getFrame();
                if (modelManagerFrame != null) {
                    modelManagerFrame.setTitle(sed.getId());
                }
            }

            if (frame != currentFrame) {
                lastLocation = null;
                disposeCurrentFrame();
                currentFrame = frame;
                currentFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                if (lastLocation != null) {
                    currentFrame.setLocation(lastLocation);
                }
                frame.setTitle("Iris Visualizer");
                ws.addFrame(frame);
            }

        } catch (Exception ex) {
            LogEvent.getInstance().fire(this, new LogEntry("Error: " + ex.getMessage(), sed));
            Logger.getLogger("IrisVisualizer").log(Level.SEVERE, null, ex);
        }
    }

    public boolean buildAttachment(ExtSed sed) throws SedNoDataException, SedInconsistentException, SpectrumException {

        Spectrum sp = factory.readAllSegments(null, sed);

        if (sp == null) {
            return true;
        }

        sp.setName(sed.getId());

        JDesktopPane desktop = ws.getDesktop();
        SherpaModelManager modelManager = new SherpaModelManager(sp, idm.getSAMPConnector(), desktop, sedManager, sed);
        modelManager.setActive(false);

        SpectrumContainer container = new SpectrumContainer(sp, modelManager);
        sed.addAttachment(IrisDisplayManager.FIT_MODEL, container);

        // This is needed to capture the 'Quit' button action
        // that comes from the model manager GUI.
        modelManager.setCallbackOnDispose(new OnDisposeCommand(sed));

        return false;
    }

    private void manageAssociatedManagerWindows(ExtSed sed) {

        ExtSed displaying = idm.getDisplaying();

        if (displaying != null) {
            if (!sed.getId().equals(displaying.getId())) {

                // displayed Sed is exiting: make its model manager and metadata windows invisible.

                SpectrumContainer container = (SpectrumContainer) displaying.getAttachment(IrisDisplayManager.FIT_MODEL);

                if (container != null) {

                    ModelManager2 modelManager = container.getModelManager();
                    modelManager.setVisible(false);
                    SpectrumVisualEditor editor = idm.getVisualEditor();

                    if (editor != null) {
                        editor.getJFrame().setVisible(false);
                    }
                }

                // new Sed is entering display: make its model manager window visible if active.

                if (sed != null) {

                    container = (SpectrumContainer) sed.getAttachment(IrisDisplayManager.FIT_MODEL);

                    if (container != null) {
                        ModelManager2 modelManager = container.getModelManager();
                        modelManager.setVisible(modelManager.isActive());
                    }
                }
            }
        }
    }

    public String getName() {
        return "Analysis";
    }

    public String getDescription() {
        return "Iris Visualization and Analysis";
    }

    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("plot");
    }

    public List<IMenuItem> getMenus() {
        return new VisualizerMenus();
    }

    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    public void shutdown() {
    }

    public void initCli(IrisApplication app) {
    }

    public static JFrame getRootFrame() {
        return ws.getRootFrame();
    }

    public SAMPController getSAMPConnection() {
        return app.getSAMPController();
    }

    private class VisualizerMenus extends ArrayList<IMenuItem> {

        public VisualizerMenus() {
            add(new AbstractDesktopItem("SED Viewer", "Explore SEDs",
                    "/iris_button_small.png", "/iris_button_tiny.png") {

                @Override
                public void onClick() {
                    if (sedManager.getSeds().isEmpty()) {
                        idm = new IrisDisplayManager(sedManager, ws, visualizer);
                        idm.setDesktopMode(true);
                        idm.setConnection(app.getSAMPController());

                        disposeCurrentFrame();
                    }

                    ExtSed sed = idm.getDisplaying();
                    if (sed != null && sed.getNumberOfSegments() <= 1) {
                        disposeCurrentFrame();
                    }

                    if (currentFrame == null) {
                        currentFrame = idm.getInternalFrame();
                        currentFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

                        ws.addFrame(currentFrame);
                    }

                    GUIUtils.moveToFront(currentFrame);

                    try {
                        currentFrame.setSelected(true);
                    } catch (java.beans.PropertyVetoException e) {
                    }

                    if (sedManager.getSeds().isEmpty()) {
                        NarrowOptionPane.showMessageDialog(ws.getRootFrame(),
                                "No SEDs to display. Please load a file.", "SED Visualizer",
                                NarrowOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            add(new AbstractDesktopItem("Fitting Tool", "Fit mathematical, phisical models, and templates",
                    "/ruler_small.png", "/ruler_tiny.png") {

                @Override
                public void onClick() {

                    // this prevents fitting of error arrays to be performed. Not a very good
                    // solution but will make do for now.

                    AbstractPlotWidget plotWidget = (AbstractPlotWidget) idm.getPlotWidget();
                    if (plotWidget != null) {
                        String selectedY = plotWidget.selected_y;
                        if (selectedY.contains("err") || selectedY.contains("Err") || selectedY.contains("ERR")) {
                            return;
                        }
                    }

                    if (sedManager.getSelected() != null) {

                        ExtSed sed = sedManager.getSelected();

                        try {
                            Spectrum sp = factory.readAllSegments(null, sed);
                            if (sp == null) {
                                NarrowOptionPane.showMessageDialog(ws.getRootFrame(), "No SEDs to fit. Please load a file.", "Fitting Engine", NarrowOptionPane.INFORMATION_MESSAGE);
                                return;
                            }
                            sp.setName(sed.getId());

                            // Get model manager from Sed attachment
                            // and activate it.

                            SpectrumContainer container = (SpectrumContainer) sed.getAttachment(IrisDisplayManager.FIT_MODEL);
                            SherpaModelManager modelManager = (SherpaModelManager) container.getModelManager();

                            modelManager.execute(null);

                            // Display the model manager frame.

                            JInternalFrame frame = modelManager.getInternalFrame();
                            frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                            ws.addFrame(frame);
                            GUIUtils.moveToFront(frame);
//                            frame.setVisible(true);
                            try {
                                frame.setSelected(true);
                            } catch (java.beans.PropertyVetoException e) {
                            }

                            // Get the FittedSpectrum instance from the model manager.

                            FittedSpectrum fsp = modelManager.getSEDFittedSpectrum();
                            fsp.enableNotifications(true);

                            // This new fitted spectrum, as well as the now active model
                            // manager, must be associated with the sed as an attachment
                            // Before attaching, we must make sure that any existing
                            // attachment with any old model gets removed first. This
                            // assumes that a Sed instance supposedly can be associated
                            // with only one model at a time. If this restriction changes
                            // in the future, we must re-visit the logic here.

                            if (sed.getAttachment(IrisDisplayManager.FIT_MODEL) != null) {
                                sed.removeAttachment(IrisDisplayManager.FIT_MODEL);
                            }

                            // Now, attach existing model and new
                            // FittedSpectrum instance to the Sed.

                            SpectrumContainer spectrumContainer = new SpectrumContainer(fsp, modelManager);
                            sed.addAttachment(IrisDisplayManager.FIT_MODEL, spectrumContainer);

                            // And display it.

                            display(sed);

                            idm.removeVisualEditor();

                        } catch (Exception ex) {
                            LogEvent.getInstance().fire(this, new LogEntry("Error: " + ex.getMessage(), sed));
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        NarrowOptionPane.showMessageDialog(ws.getRootFrame(), "No SEDs to fit. Please load a file.", "Fitting Engine", NarrowOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            add(new AbstractMenuItem("Custom Models Manager", "Install Custom Components that can be used for fitting SEDs", true,
                    "/ruler_small.png", "/ruler_tiny.png") {

                public void onClick() {

                    if (customManagerView == null) {
                        try {
                            customManagerView = new CustomModelsManagerView(customManager, ws.getFileChooser());
                        } catch (IOException ex) {
                            Logger.getLogger(IrisVisualizer.class.getName()).log(Level.SEVERE, null, ex);
                            int ans = NarrowOptionPane.showConfirmDialog(ws.getRootFrame(), "Error initializing Custom Fit Component Manager: " + ex.getMessage()
                                    + "\nDo you want to reset custom models?", "Iris Visualizer", NarrowOptionPane.ERROR_MESSAGE);
                            if (ans == NarrowOptionPane.OK_OPTION) {
                                File rootDir = new File(app.getConfigurationDir() + File.separator + "analysis" + File.separator + "custom_models");
                                try {
                                    if (rootDir.isDirectory()) {
                                        deleteDirectory(rootDir);
                                    } else {
                                        rootDir.delete();
                                    }
                                    rootDir.mkdir();
                                    customManager = new CustomModelsManager(rootDir);

                                } catch (IOException ex1) {
                                    Logger.getLogger(IrisVisualizer.class.getName()).log(Level.SEVERE, null, ex1);
                                }

                            }
                        }
                        ws.addFrame(customManagerView);
                    }

                    GUIUtils.moveToFront(customManagerView);
//                    customManagerView.show();
//                    try {
//                        customManagerView.setIcon(false);
//                    } catch (PropertyVetoException ex) {
//                        Logger.getLogger(IrisVisualizer.class.getName()).log(Level.SEVERE, null, ex);
//                    }

                }
            });
        }
    }

    // This class responds to the Dispose button in the model manager
    // and discards the model associated with the Sed, re-displaying
    // the Sed as a non-fitted Sed.

    public class OnDisposeCommand implements Command {

        private ExtSed sed;

        private OnDisposeCommand(ExtSed sed) {
            this.sed = sed;
        }

        public void execute(Object o) {
            sed.removeAttachment(IrisDisplayManager.FIT_MODEL);
            idm.removeVisualEditor();
            display(sed);
        }
    }

    // This class responds to selection actions in the component tree widget.
    // It supports both the traditional Sherpa models, as well as the new
    // templates/tables/functions custom models.

    public class ComponentTreeSelectionListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {

            JTree tree = FunctionFactorySherpaHelper.GetJTree();

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            Object node_object = node.getUserObject();

            if (node.isLeaf()) {

                try {
                    // analytic Sherpa functions.
                    Function rf = (Function) node_object;
                    try {
                        if (rf instanceof Polynomial) {
                            rf = new Polynomial(1);
                        } else {
                            rf = (Function) rf.clone();
                        }
                    } catch (CloneNotSupportedException ex) {
                        rf = null;
                    }

                    FunctionFactorySherpaHelper.SetFunction(rf);

                } catch (ClassCastException e1) {

                    // templates, tables and user files
                    try {
                        DefaultCustomModel model = (DefaultCustomModel) node_object;

                        String functionName = model.getFunctionName();

                        SherpaFunction function = new SherpaFunction();

                        URL url = model.getUrl();
                        String path = url.getPath();
                        function.addPath(path);

                        String name = model.getName();

                        if (path.contains("/tables/")) {
                            name = "tablemodel";
                        }
                        if (path.contains("/functions/")) {
                            name = "usermodel";
                        }
                        if (path.contains("/templates/")) {
                            name = "template";
                        }

                        function.setUserID(name);
                        function.setName(name);
                        function.setFunctionName(functionName);

                        // Converting a DefaultCustomModel to a SherpaFunction might
                        // require more than this. We'll see as we go further down
                        // the road...

                        // First, we need to break up the comma-separated string
                        // parameters into something that can actually be used to
                        // build instances of Function.

                        String[] parNames = model.getParnames().split("\\,");
                        String[] parVals = model.getParvals().split("\\,");
                        String[] parMins = model.getParmins().split("\\,");
                        String[] parMaxs = model.getParmaxs().split("\\,");
                        String[] parFrozen = model.getParfrozen().split("\\,");

                        int npars = parNames.length;

                        if (npars != parVals.length
                                || npars != parMins.length
                                || npars != parMaxs.length
                                || npars != parFrozen.length) {
                            ExceptionHandler.handleException(new Exception(
                                    "Parameter lists wih different lengths."));
                            tree.clearSelection();
                            FunctionFactorySherpaHelper.dispose();
                            return;
                        }

                        // now we loop over the parameter lists, building
                        // each parameter in turn and adding it to the Function.

                        for (int i = 0; i < npars; i++) {
                            SherpaFParameter functionParameter = null;
                            try {

                                double value = Double.valueOf(parVals[i]);
                                double min = Double.valueOf(parMins[i]);
                                double max = Double.valueOf(parMaxs[i]);

                                functionParameter = new SherpaFParameter(parNames[i],
                                        value, min, max, new NonSupportedUnits(""));

                                String frozen = parFrozen[i];
                                if (frozen != null) {
                                    boolean fixed = (frozen.equalsIgnoreCase("True")) ? true : false;
                                    functionParameter.setFixed(fixed);
                                }

                            } catch (NumberFormatException ex) {
                                ExceptionHandler.handleException(ex);
                                tree.clearSelection();
                                FunctionFactorySherpaHelper.dispose();
                                return;
                            } catch (ArrayIndexOutOfBoundsException ex) {
                                ExceptionHandler.handleException(ex);
                                tree.clearSelection();
                                FunctionFactorySherpaHelper.dispose();
                                return;
                            }

                            function.addParameter(functionParameter);
                        }

                        FunctionFactorySherpaHelper.SetFunction(function);

                    } catch (ClassCastException e2) {
                    }
                }
                tree.clearSelection();
                FunctionFactorySherpaHelper.dispose();
            }
        }
    }

    // This class will re-build the tree model and stick a
    // new tree selection handler to it. It should be used
    // whenever the tree must be refreshed to pick up new
    // models that the user might have opened.

    public class TreeRefresher implements Command {

        public void execute(Object o) {

            DefaultTreeModel models = null;
            try {
                models = customManager.getCustomModels();
            } catch (IOException e) {
            }
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) models.getRoot();
            FunctionFactorySherpaHelper.SetRoot(root);

            FunctionFactorySherpaHelper.AddTreeSelectionListener(new ComponentTreeSelectionListener());
        }
    }

    private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
}
