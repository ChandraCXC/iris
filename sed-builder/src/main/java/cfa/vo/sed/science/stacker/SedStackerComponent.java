/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.stacker;

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import cfa.vo.iris.gui.GUIUtils;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author jbudynk
 */
public class SedStackerComponent implements IrisComponent{

    private IrisApplication app;
    private IWorkspace ws;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        this.app = app;
        this.ws = workspace;
    }

    @Override
    public String getName() {
        return "SEDStacker";
    }

    @Override
    public String getDescription() {
        return "SEDStacker";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("sedstacker");
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
            add(new AbstractDesktopItem("SEDStacker", "Statistically combine SEDs into a single SED", "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {
                    if(frame==null) {
                        frame = new SedStackerFrame();
                        ws.addFrame(frame);
                    }
                    GUIUtils.moveToFront(frame);
                }
            });
        }
    }
}
