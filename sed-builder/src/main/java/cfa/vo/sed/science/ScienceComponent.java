/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science;

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.sed.SedlibSedManager;
import java.util.ArrayList;
import java.util.List;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class ScienceComponent implements IrisComponent {
    SedlibSedManager manager;
    IWorkspace workspace;
    IrisApplication app;
    private ScienceFrame frame;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        this.manager = (SedlibSedManager) workspace.getSedManager();
        this.workspace = workspace;
        this.app = app;
    }

    @Override
    public String getName() {
        return "Science";
    }

    @Override
    public String getDescription() {
        return "Science Features for SED Analysis";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("science");
    }

    @Override
    public void initCli(IrisApplication app) {
        
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new ScienceMenus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {

    }
    
    private class ScienceMenus extends ArrayList<IMenuItem> {

        public ScienceMenus() {
            add(new AbstractDesktopItem("Shift, Interpolate, Integrate", "A collection of Analysis tools", "/tools.png", "/tools_tiny.png") {

                @Override
                public void onClick() {
                    if(frame==null) {
                        frame = new ScienceFrame(app, workspace);
                        SedEvent.getInstance().add(frame);
                        workspace.addFrame(frame);
                    }
                    if(manager.getSelected()!=null) {
                        GUIUtils.moveToFront(frame);
                    }
                }
            });
        }
    }
    
    
}
