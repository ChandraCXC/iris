package cfa.vo.iris.fitting;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.sherpa.ConfidenceResults;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.assertion.UISpecAssert;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ConfidencePanelTest {
    private ConfidencePanel panel;
    private Panel uiPanel;
    private ConfidenceResults confidenceResults;
    private String[] columnNames;
    private String[][] expected;

    @Before
    public void setUp() throws Exception {
        panel = new ConfidencePanel();
        uiPanel = new Panel(panel);

        confidenceResults = SAMPFactory.get(ConfidenceResults.class);
        confidenceResults.setParnames(Arrays.asList("parA", "parB"));
        confidenceResults.setSigma(2.0);
        confidenceResults.setPercent(95.0);
        confidenceResults.setParvals(new double[]{1.0, 2.0});
        confidenceResults.setParmins(new double[]{-10.0, -20.0});
        confidenceResults.setParmaxes(new double[]{10.0, 20.0});

        columnNames = new String[]{"Parameter", "Value", "Lower Bound", "Upper Bound"};
        expected = new String[][]{{"parA", "1.0", "-10.0", "10.0"},
                {"parB", "2.0", "-20.0", "20.0"}
        };

        FitController controller = Mockito.mock(FitController.class);
        FitConfiguration fit = new FitConfiguration();
        Mockito.when(controller.getFit()).thenReturn(fit);
        Mockito.when(controller.computeConfidence()).thenReturn(confidenceResults);
        panel.setController(controller);
    }

    @Test
    public void testGetConfidence() throws Exception {
        TextBox sigma = uiPanel.getInputTextBox();
        sigma.textEquals("1.6").check();

        sigma.setText("1.0");
        assertEquals(1.0, panel.getController().getFit().getConfidence().getConfig().getSigma(), Double.MIN_VALUE);
    }

    @Test
    public void testSetConfidenceResults() throws Exception {
        String[][] empty = {};
        uiPanel.getTable().contentEquals(columnNames, empty).check();

        panel.setConfidenceResults(confidenceResults);

        uiPanel.getTable().contentEquals(columnNames, expected).check();
    }

    @Test
    public void testDoConfidence() throws Exception {
        uiPanel.getButton().click();
        UISpecAssert.waitUntil(uiPanel.getTable().contentEquals(columnNames, expected), 1000);
    }
}