package cfa.vo.iris.test.unit;

import org.jdesktop.application.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.uispec4j.Desktop;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

/**
 * Abstract test case for Iris GUI tests. Sets up an Iris workspace,
 * adds whatever components we would like to test, and sets up a 
 * UISpec4j window handler on the iris application to simulate and verify 
 * GUI operations.
 * 
 * Extensions of this class are responsible for setting up components.
 *
 */
public abstract class AbstractIrisGUITest extends UISpecTestCase {
    
    protected IrisApplicationStub app;
    
    protected Desktop desktop;
    protected Window window;
    
    @BeforeClass
    public static void before() {}
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("samp", "false");
        
        setAdapter(new MainClassAdapter(IrisApplicationStub.class, new String[0]));
        
        // Apparently in eclipse you have to execute this manually.
        // IrisApplicationStub.main(new String[0]);
        
        window = getMainWindow();
        desktop = window.getDesktop();
        
        app = (IrisApplicationStub) Application.getInstance();
    }
    
    @After
    public void teardown() {
    }
}
