package cfa.vo.iris.test.unit;

import cfa.vo.iris.IrisComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.uispec4j.Desktop;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

import java.util.List;

/**
 * Abstract test case for Iris GUI tests. Sets up an Iris workspace,
 * adds whatever components we would like to test, and sets up a 
 * UISpec4j window handler on the iris application to simulate and verify 
 * GUI operations.
 * 
 * Extensions of this class are responsible for setting up components.
 *
 */
public abstract class AbstractGUITest extends UISpecTestCase {

    protected Desktop desktop;
    protected Window window;
    private ApplicationStub app;
    
    @BeforeClass
    public static void before() {}
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("samp", "false");

        StubAdapter adapter = new StubAdapter();
        setAdapter(adapter);
        app = adapter.getIrisApplication();
        
        // Apparently in eclipse you have to execute this manually.
        // IrisApplicationStub.main(new String[0]);
        
        window = getMainWindow();
        desktop = window.getDesktop();
        initComponents();
    }

    private void initComponents() {
        for (IrisComponent c : getComponents()) {
            c.init(app, app.getWorkspace());
            c.initCli(app);
            app.addComponent(c);
        }
    }

    protected abstract List<IrisComponent> getComponents();

    @After
    public void teardown() {
    }

}
