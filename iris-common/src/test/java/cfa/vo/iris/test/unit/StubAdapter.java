package cfa.vo.iris.test.unit;

import org.uispec4j.UISpecAdapter;
import org.uispec4j.Window;

public class StubAdapter implements UISpecAdapter {
    private ApplicationStub stub;
    private Window mainWindow;

    public StubAdapter() {
        stub = new ApplicationStub();
        mainWindow = new Window(stub.getWorkspace().getRootFrame());
    }

    @Override
    public Window getMainWindow() {
        return mainWindow;
    }

    public ApplicationStub getIrisApplication() {
        return stub;
    }
}
