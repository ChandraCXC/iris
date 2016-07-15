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
import cfa.vo.iris.fitting.FitController;
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.CustomModelsManagerView;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.fitting.FittingMainView;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;

import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.client.MessageHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FittingToolComponent implements IrisComponent {
    
    protected IrisApplication app;
    private IWorkspace ws;
    private List<IMenuItem> menuItems = new MenuItems();
    CustomModelsManager customManager;
    private CustomModelsManagerView customManagerView;
    private File customRootDir;
    private SherpaClient sherpaClient;
    VisualizerComponentPreferences preferences;
    private final String CUSTOM_PATH = File.separator + "analysis" + File.separator + "custom_models";
    private static final Logger LOGGER = Logger.getLogger(FittingToolComponent.class.getName());

    @Override
    public void init(IrisApplication irisApplication, IWorkspace iWorkspace) {
        this.app = irisApplication;
        this.ws = iWorkspace;
        preferences = IrisVisualizer.getInstance().createPreferences(ws);
        sherpaClient = new SherpaClient(irisApplication.getSampService());
        customRootDir = new File(app.getConfigurationDir() + CUSTOM_PATH);
        try {
            customManager = new CustomModelsManager(customRootDir);
        } catch (IOException e) {
            manageException(e);
            try {
                customManager = new CustomModelsManager(customRootDir);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot initialize CustomModelManager", ex);
            }
        }
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
        return new ArrayList<>();
    }

    @Override
    public void shutdown() {
    }

    private void manageException(Exception ex) {
        LOGGER.log(Level.SEVERE, null, ex);
        int ans = NarrowOptionPane.showConfirmDialog(ws.getRootFrame(), "Error initializing Custom Fit Component Manager: " + ex.getMessage()
                + "\nDo you want to reset custom models?", "Iris Fitting Tool", NarrowOptionPane.ERROR_MESSAGE);
        if (ans == NarrowOptionPane.OK_OPTION) {
            if (customRootDir.isDirectory()) {
                deleteDirectory(customRootDir);
            } else {
                customRootDir.delete();
            }
            customRootDir.mkdir();
        }
    }

    private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
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
                            ExtSed sed = (ExtSed) ws.getSedManager().getSelected();
                            if (sed == null) {
                                NarrowOptionPane.showMessageDialog(null, "No SEDs open. Please start building SEDs using the SED builder", "Error", NarrowOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            SedModel model = preferences.getDataStore().getSedModel(sed);
                            FitController controller = new FitController(model, customManager, sherpaClient);
                            view = new FittingMainView(preferences.getDataStore(), ws.getFileChooser(), controller);
                            ws.addFrame(view);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    GUIUtils.moveToFront(view);
                }
            });

            add(new AbstractMenuItem("Custom Models Manager", "Install Custom Components that can be used for fitting SEDs", true,
                    "/ruler_small.png", "/ruler_tiny.png") {
                public void onClick() {
                    if (customManagerView == null) {
                        try {
                            customManagerView = new CustomModelsManagerView(customManager, ws.getFileChooser());
                        } catch (IOException ex) {
                            FittingToolComponent.this.manageException(ex);
                            try {
                                customManagerView = new CustomModelsManagerView(customManager, ws.getFileChooser());
                            } catch (IOException e) {
                                LOGGER.log(Level.SEVERE, "Cannot initialize CustomModelsManagerView", e);
                            }
                        }
                        ws.addFrame(customManagerView);
                    }
                    GUIUtils.moveToFront(customManagerView);
                }
            });
        }
    }
}