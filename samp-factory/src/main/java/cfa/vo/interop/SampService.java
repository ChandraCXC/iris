package cfa.vo.interop;

import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Metadata;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.*;
import org.astrogrid.samp.httpd.ServerResource;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;
import org.astrogrid.samp.xmlrpc.StandardClientProfile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SampService {
    private final String PING_MTYPE = "sherpa.ping";
    private final int DEFAULT_TIMEOUT = 10;
    private final int RETRY = 100;
    private final int RETRY_INTERVAL = 100;
    private Logger logger = Logger.getLogger(SampService.class.getName());

    Hub hub;
    private ClientProfile sampClientProfile = StandardClientProfile.getInstance();
    private HubConnector sampClient;
    private Metadata metadata;
    ResourceServer resourceServer;

    private boolean autoRunHub = false;
    private boolean sampUp = false;
    private boolean sherpaUp = false;
    private boolean startingHub = false;
    private boolean started = false;

    private List<SAMPConnectionListener> sampListeners = Collections.synchronizedList(new ArrayList<SAMPConnectionListener>());
    private List<SAMPConnectionListener> sherpaListeners = Collections.synchronizedList(new ArrayList<SAMPConnectionListener>());

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture sampMonitorHandle;
    private ScheduledFuture sherpaMonitorHandle;

    public SampService() {
        createDefaultMetadata();
    }

    public SampService(SAMPServiceBuilder builder) {
        metadata = new Metadata();
        metadata.setIconUrl(builder.getIcon().toString());
        metadata.setDescriptionText(builder.getDescription());
        metadata.setName(builder.getName());

        if (builder.isWithResourceServer()) {
            resourceServer = new ResourceServer(builder.getServerRoot());
        }
    }

    public SampService(boolean autoRunHub, Metadata metadata) {
        this.metadata = metadata;
        this.autoRunHub = autoRunHub;
    }

    public SampService(boolean autoRunHub) {
        createDefaultMetadata();
        this.autoRunHub = autoRunHub;
    }

    private void createDefaultMetadata() {
        metadata = new Metadata();
        metadata.setName("Anonymous");
        metadata.setDescriptionText(this.toString());
    }

    public void setAutoRunHub(boolean autoRunHub) {
        logger.log(Level.INFO, "Setting autoRunHub: " + autoRunHub);
        this.autoRunHub = autoRunHub;
    }

    public void start() {
        logger.log(Level.INFO, "Starting SAMPService");
        if(started) {
            throw new IllegalStateException("This SAMP Service was already started.");
        }
        startSamp();
        startSherpa();
        started = true;
    }

    public boolean isSampUp() {
        return sampUp;
    }

    public void shutdown() {
        if (!started) {
            throw new IllegalStateException("Service was not started, it does not make sense to shut it down");
        }
        shutdownSamp();
        shutdownSherpa();
        executor.shutdown();
        started = false;
    }

    public void addSampConnectionListener(SAMPConnectionListener listener) {
        sampListeners.add(listener);
        listener.run(sampUp);
    }

    private void monitorSampOnce() {
        logger.log(Level.INFO, "Monitor State: ");
        logger.log(Level.INFO, "sampClient.isConnected(): " + sampClient.isConnected());
        logger.log(Level.INFO, "sampClientProfile.isHubRunning(): " + sampClientProfile.isHubRunning());
        try {
            logger.log(Level.INFO, "sampClient.getConnection(): " + sampClient.getConnection());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "sampClient.getConnection(): exception", ex);
        }
        logger.log(Level.INFO, "startingHub: " + startingHub);
        logger.log(Level.INFO, "autoRunHub: " + autoRunHub);
        if (!sampClientProfile.isHubRunning() && !startingHub && autoRunHub) {
            logger.log(Level.INFO, "No Hub running, starting one ourselves");
            startingHub = true;
            if (hub != null) {
                logger.log(Level.WARNING, "A hub was found, shutting it down before proceeding");
                hub.shutdown();
            }
            logger.log(Level.INFO, "Starting Hub");
            try {
                hub = Hub.runHub(HubServiceMode.MESSAGE_GUI);
            } catch (IOException e) {
                logger.log(Level.WARNING, "A hub was found after all, trying to continue without starting our own");
            }
            startingHub = false;
        }
        boolean newState;
        newState = sampClient.isConnected() && sampClientProfile.isHubRunning();
        logger.log(Level.INFO, "SAMP client connected: " + newState);
        if (newState != sampUp) {
            logger.log(Level.INFO, "Client connection status changed: "+sampUp+ " -> "+newState);
            sampUp = newState;
            logger.log(Level.INFO, "Calling connection listeners callbacks");
            for(SAMPConnectionListener listener : sampListeners) {
                listener.run(sampUp);
            }
        }
    }

    private void monitorSherpaOnce() {
        boolean newState;
        try {
            newState = pingSherpa();
            logger.log(Level.INFO, "Sherpa client connected: " + newState);
            if (newState != sherpaUp) {
                logger.log(Level.INFO, "Sherpa Client connection status changed: "+sherpaUp+ " -> "+newState);
                sherpaUp = newState;
                for(SAMPConnectionListener listener : sherpaListeners) {
                    listener.run(sherpaUp);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "SampException while pinging Sherpa", e);
        }
    }

    private void startSherpa() {
        logger.log(Level.INFO, "Starting Sherpa monitor thread");
        Runnable sherpaMonitor = new Runnable() {
            @Override
            public void run() {
                monitorSherpaOnce();
            }
        };
        sherpaMonitorHandle = executor.scheduleAtFixedRate(sherpaMonitor, 0, 1, TimeUnit.SECONDS);
    }

    private void startSamp() {
        logger.log(Level.INFO, "Initializing Hub Connector");
        sampClient = new HubConnector(sampClientProfile);
        sampClient.declareMetadata(metadata);
        sampClient.declareSubscriptions(sampClient.computeSubscriptions());
        sampClient.setActive(true);
        Runnable sampMonitor = new Runnable() {
            @Override
            public void run() {
                monitorSampOnce();
            }
        };
        if (resourceServer != null) {
            resourceServer.start();
            logger.log(Level.INFO, "Starting SAMP resource server");
        }
        logger.log(Level.INFO, "Starting SAMP monitor thread");
        sampMonitorHandle = executor.scheduleAtFixedRate(sampMonitor, 0, 1, TimeUnit.SECONDS);
    }

    private void shutdownSamp() {
        sampMonitorHandle.cancel(true);
        sampClient.setActive(false);
        sampClient = null;
        if (hub != null) {
            hub.shutdown();
        }
    }

    private void shutdownSherpa() {
        sherpaMonitorHandle.cancel(true);
    }

    public void addMessageHandler(MessageHandler handler) {
        sampClient.addMessageHandler(handler);
        if (sampClientProfile.isHubRunning()) {
            sampClient.declareSubscriptions(sampClient.computeSubscriptions());
        }
    }

    public void sendMessage(SAMPMessage message) throws SampException {
        if(sampClient.getConnection().getSubscribedClients(message.get().getMType()).isEmpty())
            throw new SampException("No clients can receive the SAMP Message");

        sampClient.callAll(message.get(), new LogResultHandler(message.get()), DEFAULT_TIMEOUT);
    }

    public HubConnector getSampClient() {
        return sampClient;
    }

    public URL addResource(String filename, ServerResource serverResource) {
        return resourceServer.addResource(filename, serverResource);
    }

    public void addSherpaConnectionListener(SAMPConnectionListener listener) {
        sherpaListeners.add(listener);
        listener.run(sherpaUp);
    }

    public Response callSherpaAndRetry(SAMPMessage message) throws SEDException, SampException {
        String id = null;
        for (int i=0; i<RETRY; i++) {
            try {
                Response response = getSampClient().callAndWait(findSherpa(message.get().getMType()), message.get(), 10);
                if (isException(response)) {
                    throw getException(response);
                }
                return response;
            } catch (SampException ex) {
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e) {}
                continue;
            }
        }
        String action = "calling";
        String msg = "Tried " + action + " Sherpa for " + RETRY + " times every " + RETRY_INTERVAL + " milliseconds. Giving up";
        throw new SEDException(msg);
    }

    public boolean pingSherpa() {
        final int stepSeconds = 1;
        try {
            logger.log(Level.INFO, "pinging Sherpa with a " + stepSeconds + " seconds timeout");
            String id = findSherpa(PING_MTYPE);
            if (!id.isEmpty()) {
                getSampClient().callAndWait(id, new PingMessage().get(), stepSeconds);
                logger.log(Level.INFO, "Sherpa replied");
                return true;
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Cannot ping Sherpa");
        }
        return false;
    }

    private String findSherpa(String mtype) throws SampException {
        if (!isSampUp()) {
            logger.log(Level.WARNING, "Not connected to the hub, giving up looking for Sherpa");
            return "";
        }
        logger.log(Level.INFO, "looking for Sherpa");
        Map clients = getSampClient().getConnection().getSubscribedClients(mtype);
        if (!clients.isEmpty()) {
            String retval = (String) clients.keySet().iterator().next();
            logger.log(Level.INFO, "found Sherpa with id: "+retval);
            return retval;
        } else {
            logger.log(Level.WARNING, "Sherpa not found connected to the hub");
            return "";
        }
    }

    protected boolean isException(Response rspns) {
        return !rspns.isOK();
    }

    protected SEDException getException(Response rspns) {
        try {
            String message = (String) rspns.getResult().get("message");
            return new SEDException(message);
        } catch (Exception ex) {
            Logger.getLogger(SherpaClient.class.getName()).log(Level.SEVERE, null, ex);
            return new SEDException(ex);
        }
    }

    public class SEDException extends Exception {
        public SEDException(String msg) {
            super(msg);
        }

        public SEDException(Exception ex) {
            super(ex);
        }
    }
}
