package cfa.vo.iris.fitting;

import cfa.vo.iris.test.IrisAppResource;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import cfa.vo.iris.test.unit.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.uispec4j.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FittingFunctionalIT extends AbstractUISpecTest {
    @Rule
    public IrisAppResource appResource = new IrisAppResource(false, true);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private String examplesUrlString;
    private String templateLibUrlString;
    private String templateUrlString;
    private String functionUrlString;

    private Window window;
    private Window modelsManager;
    private Desktop desktop;

    @Before
    public void setUp() throws Exception {
        window = appResource.getAdapter().getMainWindow();
        desktop = window.getDesktop();

        // Set up tests/examples directory.
        examplesUrlString = tempFolder.getRoot().getParentFile().getAbsolutePath()+"/examples";
        templateLibUrlString = examplesUrlString+"/sed_templates.dat";
        functionUrlString = examplesUrlString+"/mypowlaw.py";
        templateUrlString = examplesUrlString+"/sed_temp_data.dat";

        Path templatePath = Paths.get(templateLibUrlString);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(templatePath), charset);
        content = content.replaceAll("<YOUR DIRECTORY PATH HERE>", examplesUrlString);
        Files.write(templatePath, content.getBytes(charset));
    }

    @Test
    public void testFittingThread() throws Exception {
        installModels();
        loadSed();
        setupModelExpression();
    }

    private void installModels() throws Exception {
        // Menu is built dynamically, so it might not be ready when the test runs
        TestUtils.invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                window.getMenuBar().getMenu("Tools").getSubMenu("Fitting Tool").getSubMenu("Custom Models Manager").click();
            }
        });
        modelsManager = desktop.getWindow("Custom Fit Models Manager");

        modelsManager.getRadioButton("Template Library").click();
        TextBox nextField = modelsManager.getTextBox("jTextField1");
        nextField.setText(templateLibUrlString);
        nextField = modelsManager.getTextBox("jTextField2");
        nextField.setText("index,refer");
        nextField = modelsManager.getTextBox("jTextField3");
        nextField.setText("0.0,5000");
        nextField = modelsManager.getTextBox("jTextField4");
        nextField.setText("-0.5,5000");
        nextField = modelsManager.getTextBox("jTextField5");
        nextField.setText("0.0,5000");
        nextField = modelsManager.getTextBox("jTextField6");
        nextField.setText("False,False");
        nextField = modelsManager.getTextBox("jTextField7");
        nextField.setText("test_template");

        Button installButton = modelsManager.getButton("install");
        installButton.click();

        modelsManager.getRadioButton("Python Function").click();
        nextField = modelsManager.getTextBox("jTextField1");
        nextField.setText(functionUrlString);
        nextField = modelsManager.getTextBox("jTextField2");
        nextField.setText("ref,ampl,index");
        nextField = modelsManager.getTextBox("jTextField3");
        nextField.setText("5000,1.0,-0.5");
        nextField = modelsManager.getTextBox("jTextField4");
        nextField.setText("5000,1e-38,-1.0");
        nextField = modelsManager.getTextBox("jTextField5");
        nextField.setText("5000,3e38,1.0");
        nextField = modelsManager.getTextBox("jTextField6");
        nextField.setText("True,False,False");
        nextField = modelsManager.getTextBox("jTextField8");
        nextField.setText("mypowlaw");
        nextField = modelsManager.getTextBox("jTextField7");
        nextField.setText("test_function");

        installButton.click();

        modelsManager.getRadioButton("Table").click();
        nextField = modelsManager.getTextBox("jTextField1");
        nextField.setText(templateUrlString);
        nextField = modelsManager.getTextBox("jTextField2");
        nextField.setText("ampl");
        nextField = modelsManager.getTextBox("jTextField3");
        nextField.setText("1.0");
        nextField = modelsManager.getTextBox("jTextField4");
        nextField.setText("1e-38");
        nextField = modelsManager.getTextBox("jTextField5");
        nextField.setText("3e38");
        nextField = modelsManager.getTextBox("jTextField6");
        nextField.setText("False");
        nextField = modelsManager.getTextBox("jTextField7");
        nextField.setText("test_table");

        installButton.click();

        Tree modelsTree = modelsManager.getTree("jTree1");
        modelsTree.contains("templates/test_template").check();
        modelsTree.contains("functions/test_function").check();
        modelsTree.contains("tables/test_table").check();

    }

    private void loadSed() throws Exception {
        window.getMenuBar().getMenu("File").getSubMenu("Load File").click();
        Window loader = desktop.getWindow("Load an input File");
        loader.getRadioButton("Location on Disk").click();
        loader.getInputTextBox("diskTextBox").setText(examplesUrlString+"/3c273.xml");
        loader.getComboBox().select("VOTable");
        loader.getButton("Load Spectrum/SED").click();

        Window builder = desktop.getWindow("SED Builder");
        builder.getListBox().click(0);
        builder.getTable().contentEquals(new String[][]{{"3C 273", "187.28, 2.0524", "NASA/IPAC Extragalactic Database (NED)", "474"}}).check();
    }

    private void setupModelExpression() throws Exception {
        window.getMenuBar().getMenu("Tools").getSubMenu("Fitting Tool").getSubMenu("Fitting Tool").click();
        Window fittingView = desktop.getWindow("Fitting Tool");
        Tree availableTree = fittingView.getTree("availableTree");
        Tree modelsTree = fittingView.getTree("modelsTree");
        TextBox modelExpression = fittingView.getTextBox("modelExpressionField");

        modelExpression.textEquals("No Model").check();

        availableTree.contains("User Model Components/tables/test_table").check();
        availableTree.contains("User Model Components/functions/test_function").check();
        availableTree.contains("User Model Components/templates/test_template").check();
        availableTree.contains("Preset Model Components/powerlaw").check();

        availableTree.doubleClick("User Model Components/tables/test_table");
        modelsTree.contains("test_table.m1").check();
        availableTree.doubleClick("User Model Components/functions/test_function");
        modelsTree.contains("mypowlaw.m2").check();
        availableTree.doubleClick("User Model Components/templates/test_template");
        modelsTree.contains("test_template.m3").check();
        availableTree.doubleClick("Preset Model Components/powerlaw");
        modelsTree.contains("powerlaw.m4").check();

        modelExpression.textEquals("m1 + m2 + m3 + m4").check();
    }
}
