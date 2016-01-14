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
import cfa.vo.iris.visualizer.plotter.PlotterView;

import org.astrogrid.samp.client.MessageHandler;
import java.util.ArrayList;
import java.util.List;

public class VisualizerComponent implements IrisComponent {
    
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
        return "Iris Visualizer";
    }

    @Override
    public String getDescription() {
        return "Used to visualize SED data in a plotter or in a scrolling metadata browser.";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("visualizer");
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
        private PlotterView view;

        public MenuItems() {
            super();
            add(new AbstractDesktopItem("Iris Visualizer", "Visualize SED data", 
                    "/iris_button_small.png", "/iris_button_tiny.png") {
                @Override
                public void onClick() {
                    if (view == null) {
                        try {
                            view = new PlotterView("Iris Visualizer", app, ws);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        ws.addFrame(view);
                    }
                    GUIUtils.moveToFront(view);
                }
            });
        }
    }
}