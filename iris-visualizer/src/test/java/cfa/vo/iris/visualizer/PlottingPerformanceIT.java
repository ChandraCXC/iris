package cfa.vo.iris.visualizer;

import static org.junit.Assert.assertSame;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.sedlib.io.SedFormat;
import cfa.vo.testdata.TestData;

public class PlottingPerformanceIT extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String windowName;

    @Before
    public void setUp() throws Exception {
        windowName = comp.getName();
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }
    
    @Test(timeout=20000)
    public void testReadPerformance() throws Exception {
        URL benchmarkURL = TestData.class.getResource("test300k_VO.fits");
        final ExtSed sed = ExtSed.read(benchmarkURL.openStream(), SedFormat.FITS);
        SedlibSedManager manager = (SedlibSedManager) app.getWorkspace().getSedManager();
        manager.add(sed);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                window.getMenuBar()
                        .getMenu("Tools")
                        .getSubMenu(windowName)
                        .getSubMenu(windowName)
                        .click();
            }
        });

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertSame(sed, comp.getDefaultPlotterView().getSed());
            }
        });
    }
}
