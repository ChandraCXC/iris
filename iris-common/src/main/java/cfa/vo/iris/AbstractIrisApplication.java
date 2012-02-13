/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris;

import cfa.vo.interop.SAMPConnectionListener;
import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SimpleSAMPMessage;
import cfa.vo.iris.desktop.IrisDesktop;
import cfa.vo.iris.desktop.IrisWorkspace;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.sedlib.Sed;
import java.awt.EventQueue;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.MessageHandler;
import org.astrogrid.samp.client.SampException;
import org.jdesktop.application.Application;

/**
 *
 * @author olaurino
 */
public abstract class AbstractIrisApplication extends Application implements IrisApplication {

    private static SedSAMPController sampController;
    private static boolean isTest = false;
    private Map<String, IrisComponent> components = new HashMap();
    private IrisWorkspace ws;
    private IrisDesktop desktop;
    public static final File CONFIGURATION_DIR = new File(System.getProperty("user.home") + "/.vao/iris/importer/");
    public final boolean MAC_OS_X = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
    static boolean SAMP_ENABLED = System.getProperty("samp", "true").toLowerCase().equals("false") ? false : true;
    public static final boolean SAMP_FALLBACK = false;

    public void addConnectionListener(SAMPConnectionListener listener) {
        sampController.addConnectionListener(listener);
    }

    public void addMessageHandler(MessageHandler handler) {
        sampController.addMessageHandler(handler);
    }

    public void exitApp() {
        sampShutdown();
        System.exit(0);
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract URL getSAMPIcon();

    public static AbstractIrisApplication getInstance() {
        return Application.getInstance(AbstractIrisApplication.class);
    }

    public void sampSetup() {
        if (SAMP_ENABLED) {
            sampController = new SedSAMPController(getName(), getDescription(), getSAMPIcon().toString());
            try {
                sampController.startWithResourceServer("sedImporter/", !isTest);
            } catch (Exception ex) {
                System.err.println("SAMP Error. Disabling SAMP support.");
                System.err.println("Error message: " + ex.getMessage());
                SAMP_ENABLED = false;
            }
        }
    }

    public static void sampShutdown() {
        if (sampController != null) {
            Logger.getLogger(SedSAMPController.class.getName()).log(Level.INFO, "Shutting down SAMP");
            sampController.stop();
        }
    }

    public static void setAutoRunHub(boolean autoRunHub) {
        sampController.setAutoRunHub(autoRunHub);
    }

    public static void setTest(boolean t) {
        isTest = t;
    }
    protected String[] componentArgs;
    protected String componentName;
    protected boolean isBatch = false;

    protected AbstractIrisApplication() {
    }

    public abstract List<IrisComponent> getComponents() throws Exception;

    public abstract JDialog getAboutBox();

    @Override
    public File getConfigurationDir() {
        return CONFIGURATION_DIR;
    }

    @Override
    protected void initialize(String[] args) {
        if (args.length > 1) {
            isBatch = true;
            componentName = args[0];
            componentArgs = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                componentArgs[i - 1] = args[i];
            }
        }
        try {
            for (IrisComponent component : getComponents()) {
                components.put(component.getCli().getName(), component);
            }
        } catch (Exception ex) {
            System.out.println("Error reading component file");
        }

    }

    @Override
    public boolean isSampEnabled() {
        return SAMP_ENABLED;
    }

    @Override
    public void sendSedMessage(Sed sed, String sedId) throws SampException {
        sampController.sendSedMessage(sed, sedId);
    }

    @Override
    public void sendSampMessage(Message msg) throws SampException {
        sampController.sendMessage(new SimpleSAMPMessage(msg));
    }

    @Override
    protected void startup() {
        if (!CONFIGURATION_DIR.exists()) {
            CONFIGURATION_DIR.mkdirs();
        }
        if (isBatch) {
            components.get(componentName).getCli().call(componentArgs);
        } else {

            if (MAC_OS_X) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }
            System.out.println("Launching GUI...");
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Logger.getLogger("").setLevel(Level.SEVERE);
                    sampSetup();
                    ws = new IrisWorkspace();
                    for (IrisComponent component : components.values()) {

                        component.init(AbstractIrisApplication.this, ws);
                    }
                    try {
                        desktop = new IrisDesktop(AbstractIrisApplication.this);
                    } catch (Exception ex) {
                        System.out.println("Error initializing components");
                        Logger.getLogger(AbstractIrisApplication.class.getName()).log(Level.SEVERE, null, ex);
                        exitApp();
                    }
                    ws.setDesktop(desktop);
                    desktop.setVisible(true);
                }
            });
        }
    }

    @Override
    public SAMPController getSAMPController() {
        return sampController;
    }
}
