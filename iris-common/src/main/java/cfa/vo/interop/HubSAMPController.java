package cfa.vo.interop;

import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.hub.Hub;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HubSAMPController extends SAMPController {

    private boolean autoRunHub = true;
    private Thread t;
    private boolean started = false;
    private Hub hub;

    private static SAMPControllerBuilder builder;
    private static long timeoutMillis;

    private HubSAMPController() throws Exception {
        super(builder);
        this.start();
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
        super.stop();
    }

    @Override
    public void start() {
        if(!started) {
            t = new Thread(new CheckConnection());
            t.start();
            started = true;
        }
        super.start(timeoutMillis);
    }

    public void setAutoRunHub(boolean autoRunHub) {
        this.autoRunHub = autoRunHub;
    }

    private class CheckConnection extends Thread {

        private boolean state = false;

        private void runHubIfNeeded() {
            // I autorunhub is false we don't want to start a new hub.
            // Neither we want to start a hub if the controller is already connected (to a different hub)
            // In any case we want to start a hub only if we did not start one already.
            if (autoRunHub && hub == null && !isConnected()) {
                try {
                    // this returns a running hub.
                    // an exception is thrown if a hub is already running.
                    hub = Hub.runHub(getHubServiceMode());
                } catch (IOException ex) {
                    // do nothing, keep monitoring
                }
            } else if (autoRunHub && hub != null && !isConnected()) {
                hub.shutdown();
                hub = null;
            }
            // if we are connected there is nothing to do anyway.
        }

        private void checkStatusUpdate() throws InterruptedException {
            boolean stateChanged = state != isConnected();

            if(stateChanged && isConnected())
                if(!this.isInterrupted()) {
                    try {
                        getConnection().notifyAll(new Message("updated status for "+ getName()));
                    } catch (SampException ex) {
                        Logger.getLogger(SAMPController.class.getName()).log(Level.WARNING, "Couldn't notify changed state. Maybe we (or the hub) are being shut down");
                    }
                } else {
                    throw new InterruptedException();
                }

            if(stateChanged) {
                state = isConnected();
                for(SAMPConnectionListener listener : getListeners()) {
                    listener.run(state);
                }
            }
        }

        @Override
        public void run() {

            while(true) {
                try {
                    runHubIfNeeded();
                    checkStatusUpdate();
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static final class SingletonHolder {
        private static final HubSAMPController INSTANCE = initHubController();

        private static HubSAMPController initHubController() {
            try {
                return new HubSAMPController();
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        private SingletonHolder() {
            /* prevent instantiation */
        }

        private static HubSAMPController getInstance() {
            return SingletonHolder.INSTANCE;
        }
    }

    public static HubSAMPController getInstance(SAMPControllerBuilder builder, long timeoutMillis) throws Exception {
        if (HubSAMPController.builder != null) {
            throw new IllegalStateException("Class was already instantiated with a different argument");
        }

        HubSAMPController.timeoutMillis = timeoutMillis;
        HubSAMPController.builder = builder;

        try {
            return SingletonHolder.getInstance();
        } catch (ExceptionInInitializerError ex) {
            Throwable cause = ex.getCause();
            throw new Exception(cause);
        }
    }
}
