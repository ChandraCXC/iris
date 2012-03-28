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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;

import org.astrogrid.samp.client.MessageHandler;

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import java.awt.Point;

import spv.SpvInitialization;
import spv.controller.ManagedSpectrum2;
import spv.controller.ModelManager2;
import spv.controller.SherpaModelManager;
import spv.fit.FittedSpectrum;
import spv.fit.FittingEngine;
import spv.fit.FittingEngineFactory;
import spv.fit.NoSuchEngineException;
import spv.glue.SpectrumVisualEditor;
import spv.spectrum.Spectrum;
import spv.spectrum.factory.SED.SEDFactoryModule;
import spv.util.Command;
import spv.util.Include;
import spv.util.properties.SpvProperties;

/**
 *
 * @author olaurino
 */
public class IrisVisualizer implements IrisComponent {

    private IrisDisplayManager idm;
    private IWorkspace ws;
    private IrisApplication app;
    private JInternalFrame currentFrame;
    private SedlibSedManager manager;
    private SEDFactoryModule factory = new SEDFactoryModule();
    private FittingEngine sherpa;
    private String sherpaDir = System.getProperty("IRIS_DIR") + "/lib/sherpa";
    private Point lastLocation;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {

        manager = (SedlibSedManager) workspace.getSedManager();

        SpvInitialization spvinit = new SpvInitialization(new String[]{}, null);

        SpvProperties.SetProperty(Include.APP_NAME, "Iris");
        SpvProperties.SetProperty(Include.PYTHON_PATH, sherpaDir);
        spvinit.initialize(null, false);

        FittingEngineFactory f = new FittingEngineFactory();
        try {
            sherpa = f.get("sherpa");
//            sherpa = f.get("test");
            sherpa.start();
            LogEvent.getInstance().fire(sherpa, new LogEntry("Sherpa started", this));
        } catch (NoSuchEngineException ex) {
            Logger.getLogger(IrisVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.app = app;
        this.ws = workspace;

        idm = new IrisDisplayManager(manager);
        idm.setDesktopMode(true);
        idm.setConnection(app.getSAMPController());

        SedEvent.getInstance().add(new SedListener() {

            @Override
            public void process(final ExtSed source, SedCommand payload) {

                if (payload == SedCommand.SELECTED) {

                    if (source.getNumberOfSegments() > 0) {
                        display(source);
                    }

                } else if (payload == SedCommand.REMOVED) {

                    remove(source);

                }
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, final SegmentPayload payload) {
                ExtSed sed = payload.getSed();

                // If the sed structure was modified, invalidate
                // any model associated with it.

                invalidateModel(sed);

                display(payload.getSed());
            }
        });
    }

    private void remove(ExtSed source) {
        invalidateModel(source);
        idm.remove(source.getId());
    }

    private void display(ExtSed sed) {

        manageAssociatedManagerWindows(sed);

        try {

            ManagedSpectrum2 managedSpectrum = (ManagedSpectrum2) sed.getAttachment(IrisDisplayManager.FIT_MODEL);

            // There is no Sed attachment, so build a model manager and attach it.

            if (managedSpectrum == null) {

                Spectrum sp = factory.readAllSegments(null, sed);
                sp.setName(sed.getId());

                SherpaModelManager modelManager = new SherpaModelManager(sp, idm.getSAMPConnector(), ws.getDesktop());
                modelManager.setActive(false);

                managedSpectrum = new ManagedSpectrum2(sp, modelManager);
                sed.addAttachment(IrisDisplayManager.FIT_MODEL, managedSpectrum);

                // This is needed to capture the 'Quit' button action
                // that comes from the model manager GUI.
                modelManager.setCallbackOnDispose(new OnDisposeCommand(sed));
            }

            // Now display the Sed.

            idm.display(sed, sed.getId());

            // and add its frame to the workspace.

            JInternalFrame frame = idm.getInternalFrame();
            if (frame != currentFrame) {
                lastLocation = currentFrame.getLocation();
                currentFrame.dispose();
                currentFrame = frame;
                currentFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                currentFrame.setLocation(lastLocation);
                frame.setTitle("Iris Visualizer");
                ws.addFrame(frame);
            }

        } catch (Exception ex) {
            LogEvent.getInstance().fire(this, new LogEntry("Error: " + ex.getMessage(), sed));
        }
    }

    private void manageAssociatedManagerWindows(ExtSed sed) {
        if (sed != manager.getSelected()) {

            // displayedSed is exiting: make its model manager and metadata windows invisible.
            if (manager.getSelected() != null) {
                ManagedSpectrum2 managedSpectrum = (ManagedSpectrum2) manager.getSelected().getAttachment(IrisDisplayManager.FIT_MODEL);
                if (managedSpectrum != null) {
                    ModelManager2 modelManager = managedSpectrum.getModelManager();
                    modelManager.setVisible(false);
                    SpectrumVisualEditor editor = idm.getVisualEditor();
                    if (editor != null) {
                        editor.getFrame().setVisible(false);
                    }
                }
            }

            // new Sed is entering display: make its model manager window visible if active.
            if (sed != null) {
                ManagedSpectrum2 managedSpectrum = (ManagedSpectrum2) sed.getAttachment(IrisDisplayManager.FIT_MODEL);
                if (managedSpectrum != null) {
                    ModelManager2 modelManager = managedSpectrum.getModelManager();
                    modelManager.setVisible(modelManager.isActive());
                }
            }
        }
    }

    private void invalidateModel(ExtSed sed) {
        ManagedSpectrum2 msp = (ManagedSpectrum2) sed.getAttachment(IrisDisplayManager.FIT_MODEL);
        if (msp != null) {

            ModelManager2 mm = msp.getModelManager();
            if (mm != null && mm.isActive()) {
                mm.dispose();
            }

            sed.removeAttachment(IrisDisplayManager.FIT_MODEL);
        }
    }

    @Override
    public String getName() {
        return "Analysis";
    }

    @Override
    public String getDescription() {
        return "Iris Visualization and Analysis";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("plot");
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new VisualizerMenus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {
        sherpa.shutdown();
    }

    @Override
    public void initCli(IrisApplication app) {
    }

    private class VisualizerMenus extends ArrayList<IMenuItem> {

        public VisualizerMenus() {
            add(new AbstractDesktopItem("View SED", "Explore SEDs",
                    "/iris_button_small.png", "/iris_button_tiny.png") {

                @Override
                public void onClick() {

                    if (manager.getSeds().isEmpty()) {
                        idm = new IrisDisplayManager(manager);
                        idm.setDesktopMode(true);
                        idm.setConnection(app.getSAMPController());
                        if (currentFrame != null) {
                            lastLocation = currentFrame.getLocation();
                            currentFrame.dispose();
                        }
                    }

                    if (currentFrame == null) {
                        currentFrame = idm.getInternalFrame();
                        currentFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                        ws.addFrame(currentFrame);
                    }

                    currentFrame.show();

                    if (manager.getSeds().isEmpty()) {
                        NarrowOptionPane.showMessageDialog(ws.getRootFrame(), "No SEDs to display. Please load a file.", "SED Visualizer", NarrowOptionPane.INFORMATION_MESSAGE);
                    }

                }
            });

            add(new AbstractDesktopItem("Fit SED", "Fit mathematical, phisical models, and templates",
                    "/ruler_small.png", "/ruler_tiny.png") {

                @Override
                public void onClick() {



                    if (manager.getSelected() != null) {

                        ExtSed sed = manager.getSelected();

                        try {
                            Spectrum sp = factory.readAllSegments(null, sed);
                            sp.setName(sed.getId());

                            // Get model manager from Sed attachment
                            // and activate it.

                            ManagedSpectrum2 managedSpectrum = (ManagedSpectrum2) sed.getAttachment(IrisDisplayManager.FIT_MODEL);
                            SherpaModelManager modelManager = (SherpaModelManager) managedSpectrum.getModelManager();

                            modelManager.execute(null);

                            // Display the model manager frame.

                            JInternalFrame frame = modelManager.getInternalFrame();
                            frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                            ws.addFrame(frame);
                            frame.setVisible(true);
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
                            // attachment with an old model gets removed first. This
                            // assumes that a Sed instance supposedly can be associated
                            // with only one model at a time, although this can change
                            // in the future.

                            if (sed.getAttachment(IrisDisplayManager.FIT_MODEL) != null) {
                                sed.removeAttachment(IrisDisplayManager.FIT_MODEL);
                            }

                            // Now, attach existing model and new
                            // FittedSpectrum instance to the Sed.

                            ManagedSpectrum2 msp = new ManagedSpectrum2(fsp, modelManager);
                            sed.addAttachment(IrisDisplayManager.FIT_MODEL, msp);

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
        }
    }

    // This class responds to the Dispose button in the model manager
    // and discards the model associated with the Sed, re-displaying
    // it as a non-fitted Sed.
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
}
