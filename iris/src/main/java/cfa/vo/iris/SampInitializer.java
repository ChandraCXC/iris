package cfa.vo.iris;

import cfa.vo.interop.ISAMPController;
import cfa.vo.interop.SAMPConnectionListener;
import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPControllerBuilder;
import cfa.vo.iris.utils.Default;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SampInitializer {
    private IrisApplication app;
    private HubSAMPController sampController;

    public SampInitializer(IrisApplication app) {
        this.app = app;
    }

    public void init() throws Exception {
        long timeout = Default.getInstance()
                .getSampTimeout()
                .convertTo(TimeUnit.MILLISECONDS)
                .getAmount();
        SAMPControllerBuilder builder = new SAMPControllerBuilder(app.getName())
                .withDescription(app.getDescription())
                .withResourceServer("sedImporter/")
                .withIcon(app.getSAMPIcon());
        sampController = new HubSAMPController(builder, timeout);
    }

    public void setAutoRunHub(boolean autoRunHub) {
        sampController.setAutoRunHub(autoRunHub);
    }

    public void stop() {
        sampController.stop();
    }

    public HubSAMPController getSampController() {
        return sampController;
    }

    public static final class HubSAMPController extends SAMPController {

        private final long CONNECTION_RETRY_MILLIS = 1000;
        private boolean autoRunHub = true;
        private Timer timer;
        private boolean started = false;
        private Hub hub;
        private List<SAMPConnectionListener> sherpaConnectionListeners =
                Collections.synchronizedList(new ArrayList<SAMPConnectionListener>());

        private HubSAMPController(SAMPControllerBuilder builder, long timeoutMillis) throws Exception {
            super(builder);
            this.start(timeoutMillis);
        }

        @Override
        public void stop() {
            this.autoRunHub = false;
            started = false;
            if (hub != null) {
                hub.shutdown();
                hub = null;
            }
        }

        @Override
        public boolean start(long timeoutMillis) {
            if(!started) {
                timer = new Timer(true);
                timer.schedule(new CheckConnectionTask(), 0, CONNECTION_RETRY_MILLIS);
                started = true;
            }
            return super.start(timeoutMillis);
        }

        public void setAutoRunHub(boolean autoRunHub) {
            this.autoRunHub = autoRunHub;
        }

        public void addSherpaConnectionListener(SAMPConnectionListener listener) {
            sherpaConnectionListeners.add(listener);
        }

        private class CheckConnectionTask extends TimerTask {

            private boolean sherpaState = false;
            private boolean state = false;

            private void runHubIfNeeded() {
                if(!isConnected()) {
                    try {
                        if(autoRunHub) {
                            hub = Hub.runHub(HubServiceMode.MESSAGE_GUI);
                        } else {
                            hub = null;
                            // this is likely to lead to memory leaks.
                            // Shutting down the hub before freeing the reference does not seems to work
                            // Even trying to use a WeakReference results in [
                        }
                    } catch (IOException ex) {

                    }

                }
            }

            private void checkStatusUpdate() {
                boolean stateChanged = state != isConnected();

                if(stateChanged && isConnected())
                    try {
                        getConnection().notifyAll(new Message("updated status for "+ getName()));
                    } catch (SampException ex) {
                        Logger.getLogger(SAMPController.class.getName()).log(Level.WARNING, "Couldn't notify changed state. Maybe we (or the hub) are being shut down");
                    }

                if(stateChanged) {
                    state = isConnected();
                    for(SAMPConnectionListener listener : getListeners()) {
                        listener.run(state);
                    }
                }
            }

            private void checkSherpaStatusUpdate() {
                sherpaState = SherpaClient.ping(HubSAMPController.this);

                for(SAMPConnectionListener listener : sherpaConnectionListeners) {
                    listener.run(sherpaState);
                }
            }

            @Override
            public void run() {
                runHubIfNeeded();
                checkStatusUpdate();
                checkSherpaStatusUpdate();
            }
        }
    }
}
