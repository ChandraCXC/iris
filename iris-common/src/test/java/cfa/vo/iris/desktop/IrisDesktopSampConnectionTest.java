package cfa.vo.iris.desktop;

import cfa.vo.interop.SAMPConnectionListener;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import cfa.vo.iris.test.unit.ApplicationStub;
import cfa.vo.iris.utils.LabelFinder;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Window;
import org.uispec4j.finder.ComponentMatcher;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class IrisDesktopSampConnectionTest extends AbstractUISpecTest {

    private AppStub stub;
    private IrisDesktop desktopInstance;
    private Window window;

    private final String SAMP = "SAMP";
    private final String SHERPA = "Sherpa";
    private final String CONNECTED = "connected";
    private final String DISCONNECTED = "disconnected";
    private final String STATUS = " status: ";
    private final String ICON = "iris_button_tiny.png";

    @Before
    public void setUp() throws Exception {
        stub = new AppStub();
        desktopInstance = new IrisDesktop(stub);
        window = new Window(desktopInstance);
    }

    @Test
    public void testSampConnection() throws Exception {

        checkLabel(SAMP, false);

        stub.changeSampStatus();

        checkLabel(SAMP, true);

        stub.changeSampStatus();

        checkLabel(SAMP, false);
    }

    @Test
    public void testSherpaConnection() throws Exception {

        checkLabel(SHERPA, false);

        stub.changeSherpaStatus();

        checkLabel(SHERPA, true);

        stub.changeSherpaStatus();

        checkLabel(SHERPA, false);
    }

    private void checkLabel(String name, boolean connected) {
        String right = connected ? CONNECTED : DISCONNECTED;
        String wrong = connected? DISCONNECTED : CONNECTED;
        JLabel label = new LabelFinder(name + STATUS + right).find(window);
        assertNotNull(label);
        label = new LabelFinder(name+ STATUS + wrong).find(window);
        assertNull(label);
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