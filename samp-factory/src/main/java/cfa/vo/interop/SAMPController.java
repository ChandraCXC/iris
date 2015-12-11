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
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 */
public class SAMPController extends GuiHubConnector implements ISAMPController {

    private static final int DEFAULT_TIMEOUT = 10;

    private boolean withGui;

    private boolean withServer;

    private String serverRoot;

    protected HubServiceMode mode = HubServiceMode.MESSAGE_GUI;

    protected String name;

    private ResourceHandler resourceHandler;

    HttpServer server;

    protected List<SAMPConnectionListener> listeners = Collections.synchronizedList(new ArrayList<SAMPConnectionListener>());

    protected SAMPController(SAMPControllerBuilder builder) {
        this(builder.getName(),
                builder.getDescription(),
                builder.getIcon().toString());
        if (builder.isWithResourceServer()) {
            this.activateServer(builder.getServerRoot());
        }
        this.setGui(builder.isWithGui());
    }
    /**
     *
     * Construct a new SAMPController, opens a connection to the SAMP Hub, or waits
     * for a SAMP Hub to start.
     *
     * @param name The Name to use for the new registered client
     * @param description The description string for the new client
     * @param iconUrl The location of the icon to show as associated to the new client.
     */
    private SAMPController(String name, String description, String iconUrl) {
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

    public String getName() {
        return name;
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

    public SAMPConnectionListener[] getListeners() {
        return listeners.toArray(new SAMPConnectionListener[]{});
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

    public boolean start(long timeoutMillis) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mode = withGui? HubServiceMode.MESSAGE_GUI : HubServiceMode.NO_GUI;
                if (withServer) {
                    startResourceServer();
                }
                while (!isConnected()) {
                    Thread.sleep(1000); // This will be interrupted if a timeout occurs
                }
                return Boolean.TRUE;
            }
        };
        Future<Boolean> futureController = executor.submit(callable);
        try {
            return futureController.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            futureController.cancel(true);
            return false;
        } finally {
            executor.shutdown();
        }
    }

    public void start() throws Exception {
        if (!start(30000)) {
            String msg = "Timeout starting SAMPController";
            Logger.getLogger(SAMPController.class.getName()).log(Level.SEVERE, msg);
            throw new Exception(msg);
        }
    }

    public HubServiceMode getHubServiceMode() {
        return mode;
    }

    private void startResourceServer() throws IOException {
        if(server == null)
            server = new HttpServer();

        server.start();
        resourceHandler = new ResourceHandler(server, serverRoot);
        server.addHandler(resourceHandler);
    }

    protected void activateServer(String serverRoot) {
        this.withServer = true;
        this.serverRoot = serverRoot;
    }

    protected void setGui(boolean withGui) {
        this.withGui = withGui;
    }

    /**
     * Stop the SAMP client. If this controller had started a Hub, it will be shut down.
     * If it had started the internal HttpServer it will be shut down.
     */
    public void stop() {

        if (server != null) {
            server.stop();
            server = null;
        }

        setActive(false);

    }

    private class SubscriptionListener implements SAMPConnectionListener {

        @Override
        public void run(boolean status) {
            if(status)
                declareSubscriptions(computeSubscriptions());
        }

    }

}
