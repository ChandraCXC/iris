/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris;

import cfa.vo.interop.*;
import cfa.vo.iris.desktop.IrisDesktop;
import cfa.vo.iris.desktop.IrisWorkspace;
import cfa.vo.iris.sdk.PluginManager;
import cfa.vo.iris.sed.ExtSed;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

import cfa.vo.iris.utils.Default;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.MessageHandler;
import org.astrogrid.samp.client.SampException;
import org.jdesktop.application.Application;

/**
 * Base Iris application. Handles startup, shutdown, etc.
 *
 */
public abstract class AbstractIrisApplication extends Application implements IrisApplication {

    private static final Logger logger = Logger.getLogger(AbstractIrisApplication.class.getName());
    private static SAMPController sampController;
    private static boolean isTest = false;
    static boolean SAMP_ENABLED = !System.getProperty("samp", "true").toLowerCase().equals("false");
    public static final boolean SAMP_FALLBACK = false;
    public static final File CONFIGURATION_DIR = new File(System.getProperty("user.home") + "/.vao/iris/");
    public static final boolean MAC_OS_X = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
    
    protected String[] componentArgs;
    protected String componentName;
    protected boolean isBatch = false;

    protected IrisWorkspace ws;
    protected IrisDesktop desktop;
    private ComponentLoader componentLoader;
    private Map<String, IrisComponent> components = new TreeMap<>();

    public abstract String getName();
    public abstract String getDescription();
    public abstract URL getSAMPIcon();
    public abstract JDialog getAboutBox();
    public abstract URL getDesktopIcon();
    public abstract void setProperties(List<String> properties);
    protected abstract URL getComponentsFileLocation();

    // Override this method in subclasses if a custom component loader needs to be defined.
    public ComponentLoader getComponentLoader() {
        if (componentLoader != null) {
            return componentLoader;
        }
        URL componentsURL = getComponentsFileLocation();
        componentLoader = new ComponentLoader(componentsURL);
        return componentLoader;
    }

    public List<IrisComponent> getComponents() {
        return getComponentLoader().getComponents();
    }

    public static AbstractIrisApplication getInstance() {
        return Application.getInstance(AbstractIrisApplication.class);
    }

    public static void setTest(boolean t) {
        isTest = t;
    }

    public static void setAutoRunHub(boolean autoRunHub) {
        if (sampController != null) {
            sampController.setAutoRunHub(autoRunHub);
        }
    }

    public static void sampShutdown() {
        if (sampController != null) {
            Logger.getLogger(AbstractIrisApplication.class.getName()).log(Level.INFO, "Shutting down SAMP");
            sampController.stop();
        }
    }

    @Override
    public File getConfigurationDir() {
        return CONFIGURATION_DIR;
    }

    @Override
    protected void initialize(String[] args) {
        List<String> properties = new ArrayList<>();
        List<String> arguments = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                arg = arg.replaceFirst("--", "");
                properties.add(arg);
            } else {
                arguments.add(arg);
            }
        }
        if (arguments.size() >= 1) {
            isBatch = true;
            componentName = arguments.get(0);
            componentArgs = new String[arguments.size() - 1];
            for (int i = 1; i < arguments.size(); i++) {
                componentArgs[i - 1] = arguments.get(i);
            }
        }
        setProperties(properties);
    }

    @Override
    protected void startup() {
        if (!CONFIGURATION_DIR.exists()) {
            CONFIGURATION_DIR.mkdirs();
        }

        // Read and construct components
        initComponents();

        if (isBatch) {
            int status = 1; // assume we will fail
            if (!components.containsKey(componentName)) {
                System.out.println("Component " + componentName + " does not exist.");
            } else {
                status = components.get(componentName).getCli().call(componentArgs);
            }

            exitApp(status);
        }

        if (MAC_OS_X) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        System.out.println("Launching GUI...");
        
        // Setup samp call
        sampSetup();
        
        // Initialize the desktop/main view and set visible to true
        initDesktop(); 
        
        // Initialize iris workspace and add components
        initWorkspace();

        // Add plugin manager
        setupPluginManager();
    }
    
    protected void initComponents() {
        try {
            for (IrisComponent component : getComponents()) {
                component.initCli(this);
                components.put(component.getCli().getName(), component);
            }
        } catch (Exception ex) {
            // Do we want to application to continue if we can't load any components?
            System.err.println("Error reading component file"); 
            Logger.getLogger(AbstractIrisApplication.class.getName())
                .log(Level.SEVERE, "Error reading component file", ex);
        }
    }

    public void sampSetup() {
        if (SAMP_ENABLED) {
            try {
                sampController = new SAMPController.Builder(getName())
                        .withDescription(getDescription())
                        .withResourceServer("sedImporter/")
                        .withAutoHub()
                        .withGui(!isTest)
                        .buildAndStart(Default.getInstance().getSampTimeout().convertTo(TimeUnit.MILLISECONDS).getAmount());
            } catch (Exception ex) {
                System.err.println("SAMP Error. Disabling SAMP support.");
                System.err.println("Error message: " + ex.getMessage());
                logger.log(Level.SEVERE, null, ex);
                SAMP_ENABLED = false;
            }
        }
    }
    
    protected void initDesktop() {
        try {
            desktop = new IrisDesktop(this);
        } catch (Exception ex) {
            System.err.println("Error initializing components");
            logger.log(Level.SEVERE, null, ex);
            exitApp(1);
        }

        desktop.setVisible(true);
    }
    
    protected void initWorkspace() {
        ws = new IrisWorkspace();
        ws.setDesktop(desktop);
        desktop.setWorkspace(ws);
        for (final IrisComponent component : components.values()) {
            component.init(this, ws);
        }
    }
    
    protected void setupPluginManager() {
        PluginManager manager = new PluginManager();
        components.put(manager.getCli().getName(), manager);
        manager.init(this, ws);
        desktop.setPluginManager(manager);
        desktop.reset(new ArrayList<>(components.values()));
        manager.load();
    }

    public void exitApp(int status) {
        for (IrisComponent component : components.values()) {
            component.shutdown();
        }
        sampShutdown();
        System.exit(status);
    }

    @Override
    public boolean isSampEnabled() {
        return SAMP_ENABLED;
    }

    @Override
    public void sendSampMessage(Message msg) throws SampException {
        sampController.sendMessage(new SimpleSAMPMessage(msg));
    }

    @Override
    public SAMPController getSAMPController() {
        return sampController;
    }
    
    public void addConnectionListener(SAMPConnectionListener listener) {
        if (sampController != null) {
            sampController.addConnectionListener(listener);
        }
    }

    public void addMessageHandler(MessageHandler handler) {
        if (sampController != null) {
            sampController.addMessageHandler(handler);
        }
    }
}
