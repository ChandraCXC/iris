package cfa.vo.interop;

import org.astrogrid.samp.Message;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.HubConnection;
import org.astrogrid.samp.client.MessageHandler;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.httpd.ServerResource;
import org.astrogrid.samp.hub.Hub;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HubSAMPController implements ISAMPController {
    private SAMPController controllerDelegate;

    private boolean autoRunHub = true;
    private Thread t;
    private boolean started = false;
    private Hub hub;

    public HubSAMPController(SAMPControllerBuilder controllerBuilder, long timeoutMillis) throws Exception {
        this.controllerDelegate = controllerBuilder.build();
        this.start();
        controllerDelegate.start(timeoutMillis);
    }

    @Override
    public void stop() {
        this.autoRunHub = false;
        if(t!=null) {
            t.interrupt();
        }
        started = false;
        if (hub != null) {
            hub.shutdown();
        }
        controllerDelegate.stop();
    }

    @Override
    public void start() {
        if(!started) {
//            try {
//                Thread.sleep(2000);
                t = new Thread(new CheckConnection());
                t.start();
                started = true;
//            } catch (InterruptedException ex) {
//
//            }
        }
    }


    public void setAutoRunHub(boolean autoRunHub) {
        this.autoRunHub = autoRunHub;
    }

    @Override
    public void sendMessage(SAMPMessage message) throws SampException {
        controllerDelegate.sendMessage(message);
    }

    @Override
    public void addConnectionListener(SAMPConnectionListener listener) {
        controllerDelegate.addConnectionListener(listener);
    }

    @Override
    public void addMessageHandler(MessageHandler handler) {
        controllerDelegate.addMessageHandler(handler);
    }

    @Override
    public Response callAndWait(String id, Map message, int timeout) throws SampException {
        return controllerDelegate.callAndWait(id, message, timeout);
    }

    @Override
    public Map getClientMap() {
        return controllerDelegate.getClientMap();
    }

    @Override
    public HubConnection getConnection() throws SampException {
        return controllerDelegate.getConnection();
    }

    @Override
    public URL addResource(String filename, ServerResource serverResource) {
        return controllerDelegate.addResource(filename, serverResource);
    }

    private class CheckConnection extends Thread {

        private boolean state = false;

        @Override
        public void run() {

            while(true) {
                try {

                    if(!controllerDelegate.isConnected()) {
                        try {
                            if(autoRunHub) {
                                hub = Hub.runHub(controllerDelegate.getHubServiceMode());

                            }
                            else
                                hub = null;
                        } catch (IOException ex) {

                        }

                    }

                    boolean stateChanged = state != controllerDelegate.isConnected();

                    if(stateChanged && controllerDelegate.isConnected())
                        if(!this.isInterrupted()) {
                            try {
                                controllerDelegate.getConnection().notifyAll(new Message("updated status for "+controllerDelegate.getName()));
                            } catch (SampException ex) {
                                Logger.getLogger(SAMPController.class.getName()).log(Level.WARNING, "Couldn't notify changed state. Maybe we (or the hub) are being shut down");
                            }
                        } else {
                            throw new InterruptedException();
                        }

                    if(stateChanged) {
                        state = controllerDelegate.isConnected();
                        for(SAMPConnectionListener listener : controllerDelegate.getListeners()) {
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
}
