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
import spv.SpvInitialization;
import spv.controller.display.IrisDisplayManager;
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
    private ISedManager<ExtSed> manager;
    private JInternalFrame currentVizFrame;
    

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        SpvInitialization spvinit = new SpvInitialization(new String[]{}, null);

        SpvProperties.SetProperty(Include.APP_NAME, "Iris");

        spvinit.initialize(null, false);

        this.ws = workspace;

        this.manager = ws.getSedManager();

        this.app = app;

        SedEvent.getInstance().add(new SedListener() {

            @Override
            public void process(final ExtSed source, SedCommand payload) {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        display(source);
                    }
                });

            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, final SegmentPayload payload) {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        display(payload.getSed());
                    }
                });
            }
        });


    }

    private void display(ExtSed sed) {
        try {
            idm.display(sed);
            JInternalFrame frame = idm.getInternalFrame();
            if (frame != currentVizFrame) {
                currentVizFrame.dispose();
                currentVizFrame = frame;
                currentVizFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                ws.addFrame(frame);
                ws.getDesktop().setLayer(frame, 1);
            }
        } catch (Exception ex) {
            LogEvent.getInstance().fire(sed, new LogEntry("Error: " + ex.getMessage(), this));
        }
    }

    @Override
    public String getName() {
        return "Visualizer";
    }

    @Override
    public String getDescription() {
        return "Iris Visualizer";
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

    private class VisualizerMenus extends ArrayList<IMenuItem> {

        public VisualizerMenus() {
            add(new AbstractDesktopItem("SED Visualizer", "Explore SEDs", "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {

                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            if (idm == null) {
                                idm = new IrisDisplayManager(app.getSAMPController());
                                idm.setDesktopMode(true);
                            }

                            if (currentVizFrame == null) {
                                currentVizFrame = idm.getInternalFrame();
                                currentVizFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
                                ws.addFrame(currentVizFrame);
                            }

                            currentVizFrame.setVisible(true);
                        }
                    });


                }
            });
        }
    }
}
