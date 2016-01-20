/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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

/*
 * Iris main application
 */
package cfa.vo.iris;

import cfa.vo.interop.*;
import cfa.vo.iris.cli.CommandLine;
import cfa.vo.iris.desktop.IrisDesktop;
import cfa.vo.iris.desktop.IrisWorkspace;
import cfa.vo.iris.sdk.PluginManager;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.MessageHandler;
import org.astrogrid.samp.client.SampException;
import org.jdesktop.application.Application;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 * The main class of the application.
 */
public class Iris extends Application implements IrisApplication {

    private final Logger logger = Logger.getLogger(Iris.class.getName());
    private CommandLine commandLine;
    private boolean isTest = false;
    private boolean isSampEnabled = true;
    private IrisWorkspace ws;
    private IrisDesktop desktop;
    private ComponentLoader componentLoader;
    private SampInitializer sampInitializer;

    public void setTest(boolean t) {
        isTest = t;
    }

    public void setAutoRunHub(boolean autoRunHub) {
        sampInitializer.setAutoRunHub(autoRunHub);
    }

    @Override
    public boolean isPlatformOSX() {
        return commandLine.isMacOsX();
    }

    public void sampShutdown() {
        if (sampInitializer != null) {
            logger.log(Level.INFO, "Shutting down SAMP");
            sampInitializer.stop();
        }
    }

    public URL getSAMPIcon() {
        return getClass().getResource("/iris_button_tiny.png");
    }

    @Override
    public Collection<? extends IrisComponent> getComponents() {
        return componentLoader.getComponents();
    }

    @Override
    public JDialog getAboutBox() {
        return new About(false);
    }

    public static void main(String[] args) {
        launch(Iris.class, args);
    }

    @Override
    public String getName() {
        return "Iris";
    }

    public String getDescription() {
        return "The VAO SED Analysis Tool";
    }

    @Override
    public URL getDesktopIcon() {
        return getClass().getResource("/Iris_logo.png");
    }

    @Override
    public URL getHelpURL() {
        try {
            String url = System.getenv("IRIS_DOC");
            return new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Iris.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public File getConfigurationDir() {
        return commandLine.getConfigurationDir();
    }

    @Override
    protected void initialize(String[] args) {
        commandLine = new CommandLine(args);
        new Configuration(commandLine).apply();
        componentLoader = new ComponentLoader(getClass().getResource("/components"),
                                              commandLine.getAdditionalComponents());
        sampSetup();
    }


    private void sampSetup() {
        sampInitializer = new SampInitializer(this);
        try {
            sampInitializer.init();
        } catch (Exception ex) {
            System.err.println("Cannot initialize SAMP. Fitting functionality will not be available.");
            System.err.println("Error message: " + ex.getMessage());
            logger.log(Level.SEVERE, null, ex);
            isSampEnabled = false;
        }
    }

    @Override
    protected void startup() {

        if (commandLine.isBatch()) {
            int status = commandLine.run(componentLoader.initComponentsCli(this));
            exitApp(status);
        }

        System.out.println("Launching GUI...");

        // Initialize the desktop/main view and set visible to true
        initDesktop();

        // Initialize iris workspace and add components
        initWorkspace();

        // Add plugin manager
        setupPluginManager();
    }

    protected void initDesktop() {
        try {
            desktop = new IrisDesktop(this);
        } catch (Exception ex) {
            System.err.println("Error initializing desktop");
            logger.log(Level.SEVERE, null, ex);
            exitApp(1);
        }

        desktop.setVisible(true);
    }

    protected void initWorkspace() {
        ws = new IrisWorkspace();
        ws.setDesktop(desktop);
        componentLoader.initComponents(this, ws);
    }

    protected void setupPluginManager() {
        PluginManager manager = (PluginManager) componentLoader.loadComponent(PluginManager.class, this, ws);
        desktop.setPluginManager(manager);
        desktop.reset();
        manager.load();
    }

    @Override
    public ComponentLoader getComponentLoader() {
        return componentLoader;
    }

    public void exitApp(int status) {
        try {
            componentLoader.shutdown();
            sampShutdown();
            if (!isTest) {
                System.exit(status);
            }
            desktop.dispose();
        } catch (Throwable t) {
            if (!isTest) {
                System.exit(status);
            }
        }
    }

    @Override
    public boolean isSampEnabled() {
        return isSampEnabled;
    }

    @Override
    public void sendSampMessage(Message msg) throws SampException {
        sampInitializer.getSampService().sendMessage(new SimpleSAMPMessage(msg));
    }

    @Override
    public SampService getSampService() {
        return sampInitializer.getSampService();
    }

    public void addConnectionListener(SAMPConnectionListener listener) {
        sampInitializer.getSampService().addSampConnectionListener(listener);
    }

    @Override
    public void addSherpaConnectionListener(SAMPConnectionListener listener) {
        sampInitializer.getSampService().addSherpaConnectionListener(listener);
    }

    public void addMessageHandler(MessageHandler handler) {
        sampInitializer.getSampService().addMessageHandler(handler);
    }
}
