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
import cfa.vo.iris.sed.SedlibSedManager.ExtSed;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import org.astrogrid.samp.client.MessageHandler;
import spv.IrisInitialization;
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
    private IrisInitialization irisinit;
    private IWorkspace ws;
    private IrisApplication app;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        SpvInitialization spvinit = new SpvInitialization(new String[]{}, null);

        SpvProperties.SetProperty(Include.APP_NAME, "Iris");

        spvinit.initialize(null, false);

        irisinit = new IrisInitialization();

        irisinit.initialize();

        this.app = app;

        this.ws = workspace;

        idm = new IrisDisplayManager(irisinit.getConnection());

        idm.setDesktopMode(true);

        SedEvent.getInstance().add(new SedListener() {

            @Override
            public void process(final ExtSed source, SedCommand payload) {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        idm.display(source);
                        ws.addFrame(idm.getInternalFrame());
                    }
                });
                
            }
        });

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

                            JInternalFrame frame = idm.getInternalFrame();

                            ws.addFrame(frame);

                            frame.setVisible(true);
                        }
                    });


                }
            });
        }
    }
}
