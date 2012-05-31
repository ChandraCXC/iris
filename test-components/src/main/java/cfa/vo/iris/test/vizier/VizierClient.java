/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.test.vizier;

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class VizierClient implements IrisComponent {

    private IrisApplication app;
    private IWorkspace workspace;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        this.app = app;
        this.workspace = workspace;
    }

    @Override
    public String getName() {
        return "VizierClient";
    }

    @Override
    public String getDescription() {
        return "Vizier SED Client";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("vizier");
    }

    @Override
    public void initCli(IrisApplication app) {
        
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new MenuItems();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {
        
    }

    private class MenuItems extends ArrayList<IMenuItem>{
        private JInternalFrame frame;

        public MenuItems() {
            add(new AbstractDesktopItem("Vizier SED Client", "Retrieve Photometry Data from Vizier", "/vizier.jpeg", "/tool_tiny.png") {

                @Override
                public void onClick() {
                    if(frame==null) {
                        frame = new VizierFrame(workspace.getSedManager());
                        workspace.addFrame(frame);
                    }


                    frame.setVisible(true);
                }
            });
        }
    }

}
