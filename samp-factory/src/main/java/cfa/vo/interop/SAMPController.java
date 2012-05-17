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

package cfa.vo.interop;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.Metadata;
import org.astrogrid.samp.client.DefaultClientProfile;
import org.astrogrid.samp.client.LogResultHandler;
import org.astrogrid.samp.client.MessageHandler;
import org.astrogrid.samp.client.ResultHandler;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.gui.GuiHubConnector;
import org.astrogrid.samp.httpd.HttpServer;
import org.astrogrid.samp.httpd.ResourceHandler;
import org.astrogrid.samp.httpd.ServerResource;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;

/**
 * This class implements a generic controller for SAMP connections. For convenience,
 * it extends the HubConnector class, thus providing direct access to its methods.
 *
 * In particular, given a client Name and Description it will register a new client
 * to the Hub.
 *
 * The class also starts a thread that periodically checks the connection status.
 *
 * Through this class one can add and remove both message handlers and connection listeners.
 *
 * A MessageHandler is invoked each time the Hub sends a new message for which
 * that MessageHandler has been registered.
 *
 * A SAMPConnectionListener, instead, is called each time the controller detects
 * a change in the connection status. The listeners can then perform operations
 * according to the new status. For example, they can update a connection status icon or
 * warn the user.
 *
 * Notice that client's do not need to specify the subscriptions. They are automatically
 * computed and communicated to the Hub when new MessageHandlers are added or removed.
 *
 * @author olaurino
 */
public class SAMPController extends GuiHubConnector {

    private static final int DEFAULT_TIMEOUT = 10;

    private boolean autoRunHub = true;

    private Hub hub;

    private HubServiceMode mode = HubServiceMode.MESSAGE_GUI;

    private String name;

    private ResourceHandler resourceHandler;

    HttpServer server;

    private List<SAMPConnectionListener> listeners = Collections.synchronizedList(new ArrayList());

    private Thread t;

    /**
     *
     * Construct a new SAMPController, opens a connection to the SAMP Hub, or waits
     * for a SAMP Hub to start.
     *
     * @param name The Name to use for the new registered client
     * @param description The description string for the new client
     * @param iconUrl The location of the icon to show as associated to the new client.
     */
    public SAMPController(String name, String description, String iconUrl) {
        super(DefaultClientProfile.getProfile());
        this.name = name;

        Metadata meta = new Metadata();

        meta.setName(name);
        meta.setDescriptionText(description);

        this.declareMetadata(meta);

        this.declareSubscriptions(computeSubscriptions());

        if(iconUrl!=null)
            meta.setIconUrl(iconUrl);

        this.addConnectionListener(new SubscriptionListener());

        this.setAutoconnect(1);

    }

    /**
     * A Connection Listener is a listener that gets called each time the status of
     * the connection is changed.
     *
     * @param listener
     */
    public final void addConnectionListener(SAMPConnectionListener listener) {
        listeners.add(listener);
        listener.run(isConnected());
    }

    /**
     * A MessageHandler is invoked each time a new message is sent to the Hub. The MessageHandler subscribes
     * only to certain messages with a specific mtype.
     *
     * @param handler
     * @throws SampException
     */
    @Override
    public void addMessageHandler(MessageHandler handler) {
        super.addMessageHandler(handler);
        if (isConnected()) {
            declareSubscriptions(computeSubscriptions());
        }
    }

    /**
     * Add a new ServerResource to this Controller. A ServerResource is employed to serve a resource through the internal
     * HTTP Server.
     *
     * @param name
     * @param resource
     * @return the URL at which the resource will be reachable.
     */
    public URL addResource(String name, ServerResource resource) {
        if (resourceHandler == null) {
            throw new RuntimeException("null ResourceHandler. Did you start the HTTP server?");
        }
        return resourceHandler.addResource(name, resource);
    }

    /**
     * Remove the server resource available at the specified URL
     * @param url
     */
    public void removeResource(URL url) {
        if (resourceHandler == null) {
            throw new RuntimeException("null ResourceHandler. Did you start the HTTP server?");
        }
        resourceHandler.removeResource(url);
    }

    /**
     * Get the current status of the AutoRunHub feature.d
     * @return
     */
    public boolean isAutoRunHub() {
        return autoRunHub;
    }

    public void removeConnectionListener(SAMPConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeMessageHandler(MessageHandler handler) {
        super.removeMessageHandler(handler);
        if (isConnected()) {
            declareSubscriptions(computeSubscriptions());
        }
    }

    /**
     * Convenience method that allows to send a message. In this case, no action is necessary after the message
     * has been sent. The implementation registers a simple logging handler that logs the result of the call, if any.
     * @param message
     * @throws SampException
     */
    public void sendMessage(SAMPMessage message) throws SampException {
        if(this.getConnection().getSubscribedClients(message.get().getMType()).isEmpty())
            throw new SampException("No clients can receive the SAMP Message");

        callAll(message.get(), new LogResultHandler(message.get()), DEFAULT_TIMEOUT);
    }

    /**
     * Convenience method that allows to send a message, register a handler and set a timeout
     *
     * @param message The message
     * @param handler The result handler
     * @param timeout The timeout in seconds
     * @throws SampException
     */
    public void sendMessage(SAMPMessage message, ResultHandler handler, int timeout) throws SampException {
        if(this.getConnection().getSubscribedClients(message.get().getMType()).isEmpty())
            throw new SampException("No clients can receive the SAMP Message");

        callAll(message.get(), handler, timeout);
    }

    /**
     * Set the AutoRunHub feature on or off. If the feature is on a new internal Hub
     * will be started each time a connection can't be established.
     *
     * @param autoRunHub
     */
    public void setAutoRunHub(boolean autoRunHub) {
        this.autoRunHub = autoRunHub;
    }

    private boolean started = false;

    /**
     * Start the controller. The controller starts a hub if it cannot find one running.
     * This method allows to indicate whether the hub has to be launched in GUI mode or without
     * any GUI.
     * @param withGui True if the started hub must have a GUI.
     */
    public void start(boolean withGui) {
        mode = withGui? HubServiceMode.MESSAGE_GUI : HubServiceMode.NO_GUI;
        if(!started) {
            try {
                Thread.sleep(2000);
                t = new Thread(new CheckConnection());
                t.start();
                started = true;
            } catch (InterruptedException ex) {

            }
        }
    }

    /**
     * Start this controller. If a new hub is started it will be in GUI mode.
     */
    public void start() {
        start(true);
    }

    /**
     * Start this controller and initialize the internal web server.
     * @param serverRoot String prefix for the served resources.
     * @param withGui Whether the started hub has to be in GUI mode or not.
     * @throws IOException
     */
    public void startWithResourceServer(String serverRoot, boolean withGui) throws IOException {
        start(withGui);
        if(server == null)
            server = new HttpServer();

        server.start();
        resourceHandler = new ResourceHandler(server, serverRoot);
        server.addHandler(resourceHandler);
    }

    /**
     * Stop the SAMP client. If this controller had started a Hub, it will be shut down.
     * If it had started the internal HttpServer it will be shut down.
     */
    public void stop() {

        this.setAutoRunHub(false);
        this.setAutoconnect(0);

        if(t!=null) {
            t.interrupt();
        }

        if (server != null) {
            server.stop();
            server = null;
        }

        if (hub != null) {
            hub.shutdown();
        } else {
            disconnect();
        }

        started = false;

    }

    private class CheckConnection extends Thread {

        private boolean state = false;

        @Override
        public void run() {

            while(true) {
                try {

                    if(!isConnected()) {
                        try {
                            if(autoRunHub) {
                                hub = Hub.runHub(mode);
                                
                            }
                            else
                                hub = null;
                        } catch (IOException ex) {

                        }

                    }

                    

                    boolean stateChanged = state != isConnected();

                    if(stateChanged && isConnected())
                        if(!this.isInterrupted()) {
                            try {
                                getConnection().notifyAll(new Message("updated status for "+name));
                            } catch (SampException ex) {
                                Logger.getLogger(SAMPController.class.getName()).log(Level.WARNING, "Couldn't notify changed state. Maybe we (or the hub) are being shut down");
                            }
                        } else {
                            throw new InterruptedException();
                        }

                    if(stateChanged) {
                        state = isConnected();
                        for(SAMPConnectionListener listener : listeners) {
                            listener.run(state);
                        }
                    }
                
                    Thread.sleep(1000);

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        }

    }

    private class SubscriptionListener implements SAMPConnectionListener {

        @Override
        public void run(boolean status) {
            if(status)
                declareSubscriptions(computeSubscriptions());
        }

    }

}
