/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test;

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
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.SedlibSedManager.ExtSed;
import cfa.vo.sedlib.Segment;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import org.astrogrid.samp.client.MessageHandler;
import spv.controller.SherpaModelManager;
import spv.spectrum.Spectrum;
import spv.spectrum.factory.SED.SEDFactoryModule;

/**
 *
 * @author olaurino
 */
public class IrisFitManager implements IrisComponent {

    private JInternalFrame currentFitFrame;
    private SEDFactoryModule factory = new SEDFactoryModule();
    private SherpaModelManager smm;
    private IWorkspace ws;
    private ISedManager<ExtSed> manager;
    private IrisApplication app;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        this.ws = workspace;
        this.manager = ws.getSedManager();
        this.app = app;


        SedEvent.getInstance().add(new SedListener() {

            @Override
            public void process(ExtSed source, SedCommand payload) {
                displayFitWindow(source);
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, SegmentPayload payload) {
                displayFitWindow(payload.getSed());
            }
        });

    }

    private void displayFitWindow(ExtSed sed) {
        smm = getSherpaModel(sed);
        if (smm != null) {
            JInternalFrame frame = smm.getInternalFrame();
            if (frame != currentFitFrame) {
                currentFitFrame.dispose();
                currentFitFrame = frame;
                ws.getDesktop().setLayer(frame, 1);
                ws.addFrame(frame);
                frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                frame.setVisible(true);
            }
        }
    }

    private SherpaModelManager getSherpaModel(ExtSed sed) {
        try {
            Spectrum sp = factory.readAllSegments(null, sed);
            sp.setName(sed.getId());
            smm = new SherpaModelManager(sp, app.getSAMPController(), ws.getDesktop());
            return smm;
        } catch (Exception ex) {
            LogEvent.getInstance().fire(smm, new LogEntry("Error reading sed: " + sed, this));
            return null;
        }

    }

    @Override
    public String getName() {
        return "FitManager";
    }

    @Override
    public String getDescription() {
        return "Front End to the Sherpa Fitting Engine";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("fit");
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new Menus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class Menus extends ArrayList {

        public Menus() {
            super();
            add(new AbstractDesktopItem("Fit SEDs", "Front end to the Sherpa fitting engine", "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {

                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            ExtSed sed = manager.getSelected();

                            if (sed != null) {
                                smm = getSherpaModel(sed);
                                if (smm != null) {
                                    currentFitFrame = smm.getInternalFrame();
                                    currentFitFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                                    ws.addFrame(currentFitFrame);
                                }

                                if(currentFitFrame != null)
                                    currentFitFrame.setVisible(true);
                            } else {
                                NarrowOptionPane.showMessageDialog(ws.getRootFrame(), "Please create and select an SED first", "Fit Manager", NarrowOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });


                }
            });
        }
    }
}
