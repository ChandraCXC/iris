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
import cfa.vo.iris.sed.SedlibSedManager.ExtSed;
import cfa.vo.sedlib.Segment;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;

import org.astrogrid.samp.client.MessageHandler;
import spv.SpvInitialization;
import spv.controller.ManagedSpectrum2;
import spv.controller.SherpaModelManager;
import spv.fit.FittedSpectrum;
import spv.fit.FittingEngine;
import spv.fit.FittingEngineFactory;
import spv.fit.NoSuchEngineException;
import spv.spectrum.Spectrum;
import spv.spectrum.factory.SED.SEDFactoryModule;
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
    private SherpaModelManager modelManager;
    private SedlibSedManager manager;
    private ExtSed displayedSed;
    private SEDFactoryModule factory = new SEDFactoryModule();
    private FittingEngine sherpa;
    private String sherpaDir = System.getProperty("IRIS_DIR") + "/lib/sherpa";

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

        idm = new IrisDisplayManager();
        idm.setDesktopMode(true);
        idm.setConnection(app.getSAMPController());

        SedEvent.getInstance().add(new SedListener() {

            @Override
            public void process(final ExtSed source, SedCommand payload) {
                display(source);
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, final SegmentPayload payload) {
                ExtSed sed = payload.getSed();

                ManagedSpectrum2 msp = (ManagedSpectrum2) sed.getAttachment(IrisDisplayManager.FIT_MODEL);
                if (msp != null) {

                    sed.removeAttachment(IrisDisplayManager.FIT_MODEL);

                    if (modelManager != null && modelManager.isActive()) {
                        modelManager.resetFitManagerReference();
                        modelManager.dispose();
                        modelManager.setVisible(false);
                        modelManager.setActive(false);
                        modelManager = null;
                    }
                }

                display(payload.getSed());
            }
        });
    }

    private void display(ExtSed sed) {

        try {
            displayedSed = sed;

            Spectrum sp = factory.readAllSegments(null, sed);

            sp.setName(sed.getId());

            ManagedSpectrum2 managedSpectrum = (ManagedSpectrum2) sed.getAttachment(IrisDisplayManager.FIT_MODEL);

            if (managedSpectrum == null) {

                // There is no attachment to the Sed, so build one.

                modelManager = new SherpaModelManager(sp, idm.getSAMPConnector(), ws.getDesktop());
                modelManager.setActive(false);

            } else {

                // Retrieve model manager from the attachment.

                modelManager = (SherpaModelManager) managedSpectrum.getModelManager();
            }

            managedSpectrum = new ManagedSpectrum2(sp, modelManager);
            displayedSed.addAttachment(IrisDisplayManager.FIT_MODEL, managedSpectrum);

            idm.display(displayedSed, "");

            JInternalFrame frame = idm.getInternalFrame();
            if (frame != currentFrame) {
                currentFrame.dispose();
                currentFrame = frame;
                currentFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                frame.setTitle("Fit Engine");
                ws.addFrame(frame);
            }

        } catch (Exception ex) {
            LogEvent.getInstance().fire(this, new LogEntry("Error: " + ex.getMessage(), sed));
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
        VisualizerMenus visualizerMenus = new VisualizerMenus();
        return visualizerMenus;
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {
        sherpa.shutdown();
    }

    private class VisualizerMenus extends ArrayList<IMenuItem> {

        public VisualizerMenus() {
            add(new AbstractDesktopItem("SED Visualizer", "Explore SEDs",
                    "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {

                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            if (currentFrame == null) {
                                currentFrame = idm.getInternalFrame();
                                currentFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                                ws.addFrame(currentFrame);
                            }

                            currentFrame.setVisible(true);

                        }
                    });
                }
            });

            add(new AbstractDesktopItem("Fitting", "Fitting",
                    "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {

                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            if (manager.getSelected() != null) {

                                ExtSed sed = manager.getSelected();

                                try {
                                    Spectrum sp = factory.readAllSegments(null, sed);
                                    sp.setName(sed.getId());

                                    if (modelManager == null) {
                                        modelManager = new SherpaModelManager(sp, idm.getSAMPConnector(), ws.getDesktop());
                                    }

                                    // Activate model manager.

                                    modelManager.execute(null);

                                    // Display its frame.

                                    JInternalFrame frame = modelManager.getInternalFrame();
                                    frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                                    ws.addFrame(frame);
                                    frame.setVisible(true);
                                    try {
                                        frame.setSelected(true);
                                    } catch (java.beans.PropertyVetoException e) {
                                    }

                                    // Get the fitted spectrum from the model manager.

                                    FittedSpectrum fsp = modelManager.getSEDFittedSpectrum();
                                    fsp.enableNotifications(true);

                                    // Before attaching the new model to the Sed being displayed,
                                    // we must make sure that any existing attachment with an
                                    // old model gets removed first. This assumes that a Sed
                                    // instance supposedly can be associated with only one
                                    // model at a time, although this can change in the future.

                                    if (displayedSed.getAttachment(IrisDisplayManager.FIT_MODEL) != null) {
                                        displayedSed.removeAttachment(IrisDisplayManager.FIT_MODEL);
                                    }

                                    // Now, attach existing model and new
                                    // FittedSpectrum instance to the Sed.

                                    ManagedSpectrum2 msp = new ManagedSpectrum2(fsp, modelManager);
                                    displayedSed.addAttachment(IrisDisplayManager.FIT_MODEL, msp);

                                    // And display it.

                                    idm.display(displayedSed, "");

                                } catch (Exception ex) {
                                    LogEvent.getInstance().fire(this, new LogEntry("Error: " + ex.getMessage(), sed));
                                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                NarrowOptionPane.showMessageDialog(ws.getRootFrame(), "Please create/select an SED first", "Fitting Engine", NarrowOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });
                }
            });
        }
    }
}
