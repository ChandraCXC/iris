package cfa.vo.iris.desktop;

import cfa.vo.interop.SAMPConnectionListener;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import cfa.vo.iris.test.unit.ApplicationStub;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Window;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class IrisDesktopSampConnectionTest extends AbstractUISpecTest {

    private AppStub stub;
    private Window window;

    private final String SAMP = "SAMP";
    private final String SHERPA = "Sherpa";
    private final String ICON = "iris_button_tiny.png";

    @Before
    public void setUp() throws Exception {
        stub = new AppStub();
        IrisDesktop desktopInstance = new IrisDesktop(stub);
        window = new Window(desktopInstance);
    }

    @Test
    public void testSampConnection() throws Exception {

        checkLabel(window, SAMP, false);

        stub.changeSampStatus();

        checkLabel(window, SAMP, true);

        stub.changeSampStatus();

        checkLabel(window, SAMP, false);
    }

    @Test
    public void testSherpaConnection() throws Exception {

        checkLabel(window, SHERPA, false);

        stub.changeSherpaStatus();

        checkLabel(window, SHERPA, true);

        stub.changeSherpaStatus();

        checkLabel(window, SHERPA, false);
    }


    private class AppStub extends ApplicationStub {
        private List<IrisComponent> components = new ArrayList<>();
        private List<SAMPConnectionListener> sampConnectionListeners = new ArrayList<>();
        private List<SAMPConnectionListener> sherpaConnectionListeners = new ArrayList<>();
        private boolean sampStatus = false;
        private boolean sherpaStatus = false;

        @Override
        public boolean isSampEnabled() {
            return true;
        }

        @Override
        public Collection<? extends IrisComponent> getComponents() {
            return components;
        }

        @Override
        public URL getDesktopIcon() {
            return IrisDesktop.class.getResource(ICON);
        }

        @Override
        public void setAutoRunHub(boolean autoHub) {

        }

        @Override
        public void addConnectionListener(SAMPConnectionListener sampConnectionListener) {
            sampConnectionListeners.add(sampConnectionListener);
        }

        @Override
        public void addSherpaConnectionListener(SAMPConnectionListener sampConnectionListener) {
            sherpaConnectionListeners.add(sampConnectionListener);
        }

        public void changeSampStatus() {
            sampStatus = !sampStatus;
            for (SAMPConnectionListener l : sampConnectionListeners) {
                l.run(sampStatus);
            }
        }

        public void changeSherpaStatus() {
            sherpaStatus = !sherpaStatus;
            for (SAMPConnectionListener l : sherpaConnectionListeners) {
                l.run(sherpaStatus);
            }
        }
    }
}