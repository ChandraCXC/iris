/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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

package cfa.vo.iris.visualizer;

import cfa.vo.iris.*;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.fitting.FittingMainView;

import org.astrogrid.samp.client.MessageHandler;
import java.util.ArrayList;
import java.util.List;

public class FittingToolComponent implements IrisComponent {
    
    protected IrisApplication app;
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
        return "Used to fit SED data.";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("cfa/vo/iris/fitting");
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
        private FittingMainView view;
        
        public MenuItems() {
            super();
            add(new AbstractDesktopItem("Fitting Tool",
                    "Fitting Tool Prototype", "/tool.png", "/tool_tiny.png") {
                @Override
                public void onClick() {
                    if (view == null) {
                        try {
                            view = new FittingMainView(ws);
                            ws.getDesktop().add(view);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    GUIUtils.moveToFront(view);
                }
            });
        }
    }
}