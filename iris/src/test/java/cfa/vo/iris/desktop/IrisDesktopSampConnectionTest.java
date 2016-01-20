package cfa.vo.iris.desktop;

import cfa.vo.interop.SAMPConnectionListener;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.test.unit.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.uispec4j.Window;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IrisDesktopSampConnectionTest extends AbstractUISpecTest {
    private StubAdapter stubAdapter;
    private Window window;
    private SampApplicationStub appStub;

    private final String SAMP = "SAMP";
    private final String SHERPA = "Sherpa";

    @Before
    public void setUp() throws Exception {
        appStub = new SampApplicationStub();
        StubWorkspace ws = new StubWorkspace(new SampMainWindow(appStub));
        appStub.setWorkspace(ws);
        stubAdapter = new StubAdapter(appStub);
        window = stubAdapter.getMainWindow();
        this.RETRY = 60;
    }

    @Test
    public void testSampConnection() throws Exception {
        // At first, SAMP is disconnected
        checkLabel(window, SAMP, false);
        // simulate connection
        appStub.connectSAMP();
        checkLabel(window, SAMP, true);
        // simulate disconnection
        appStub.disconnectSAMP();
        checkLabel(window, SAMP, false);
    }

    @Test
    public void testSherpaConnection() throws Exception {
        // at first, sherpa is disconnected
        checkLabel(window, SHERPA, false);
        // simulate connection
        appStub.connectSherpa();
        checkLabel(window, SHERPA, true);
        // simulate disconnection
        appStub.disconnectSherpa();
        checkLabel(window, SHERPA, false);
        // simulate connection so we can emulate a samp failure
        appStub.connectSherpa();
        checkLabel(window, SHERPA, true);
        // simulate a *samp* disconnection
        appStub.disconnectSAMP();
        checkLabel(window, SHERPA, false);
    }

    private class SampMainWindow implements IMainWindow {
        private IrisDesktop desktop;
        private JFrame mainFrame;

        private SampMainWindow(ApplicationStub app) throws Exception {
            desktop = new IrisDesktop(app);
            mainFrame = app.getWorkspace().getRootFrame();
        }

        @Override
        public JDesktopPane getDesktop() {
            return desktop.getDesktopPane();
        }

        @Override
        public JFrame getMainFrame() {
            return mainFrame;
        }
    }

    private class SampApplicationStub extends ApplicationStub {
        private List<SAMPConnectionListener> sherpaListeners = new ArrayList<>();
        private List<SAMPConnectionListener> sampListeners = new ArrayList<>();

        public void setWorkspace(StubWorkspace wSpace) {
            this.wSpace = wSpace;
        }

        @Override
        public Collection<? extends IrisComponent> getComponents() {
            return new ArrayList();
        }

        @Override
        public URL getDesktopIcon() {
            return ApplicationStub.class.getResource("/iris_button_tiny.png");
        }

        @Override
        public boolean isSampEnabled() {
            return true;
        }

        @Override
        public void addSherpaConnectionListener(SAMPConnectionListener listener) {
            sherpaListeners.add(listener);
        }

        @Override
        public void addConnectionListener(SAMPConnectionListener listener) {
            sampListeners.add(listener);
        }

        public void disconnectSherpa() {
            for (SAMPConnectionListener l : sherpaListeners) {
                l.run(false);
            }
        }

        public void connectSherpa() {
            for (SAMPConnectionListener l : sherpaListeners) {
                l.run(true);
            }
        }

        public void disconnectSAMP() {
            for (SAMPConnectionListener l : sampListeners) {
                l.run(false);
            }

            disconnectSherpa();
        }

        public void connectSAMP() {
            for (SAMPConnectionListener l : sampListeners) {
                l.run(true);
            }
        }
    }

}
