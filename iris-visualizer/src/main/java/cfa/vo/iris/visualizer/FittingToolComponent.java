package cfa.vo.iris.visualizer;

import cfa.vo.iris.*;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.visualizer.fitting.FittingToolGUI;

import org.astrogrid.samp.client.MessageHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FittingToolComponent implements IrisComponent {
    
    private IrisApplication app;
    private IWorkspace ws;
    private List<IMenuItem> menuItems = new MenuItems();

    @Override
    public void init(IrisApplication irisApplication, IWorkspace iWorkspace) {
        this.app = irisApplication;
        this.ws = iWorkspace;
    }

    @Override
    public String getName() {
        return "Fitting Tool";
    }

    @Override
    public String getDescription() {
        return "Iris ";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("fitting");
    }

    @Override
    public void initCli(IrisApplication irisApplication) {
    }

    @Override
    public List<IMenuItem> getMenus() {
        return menuItems;
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList<MessageHandler>();
    }

    @Override
    public void shutdown() {
    }

    private class MenuItems extends ArrayList<IMenuItem> {
        private FittingToolGUI view;
        
        public MenuItems() {
            super();
            add(new AbstractDesktopItem("Fitting Tool",
                    "Fitting Tool Prototype", "/tool.png", "/tool_tiny.png") {
                @Override
                public void onClick() {
                    if (view == null) {
                        try {
                            view = new FittingToolGUI(ws);
                            ws.getDesktop().add(view);
                        } catch (Exception ex) {
                            Logger.getLogger(FittingToolComponent.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                    GUIUtils.moveToFront(view);
                }
            });
        }
    }
}