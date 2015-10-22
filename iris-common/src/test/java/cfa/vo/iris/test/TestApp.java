package cfa.vo.iris.test;

import cfa.vo.iris.AbstractIrisApplication;
import cfa.vo.iris.IrisComponent;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by olaurino on 10/23/15.
 */
public class TestApp extends AbstractIrisApplication {
    private static List<IrisComponent> components = Collections.synchronizedList(new ArrayList());

    public static void main( String[] args ) {
        launch(TestApp.class, args);
    }

    public static boolean hasComponent(Class clazz) {
        boolean returnValue = false;
        for (IrisComponent comp : components) {
            if (clazz.isInstance(comp)) {
                returnValue = true;
                break;
            }
        }

        return returnValue;
    }

    public static void addComponent(IrisComponent component) {
        components.add(component);
    }

    @Override
    public String getName() {
        return "TestApp";
    }

    @Override
    public String getDescription() {
        return "TestApp";
    }

    @Override
    public URL getSAMPIcon() {
        return getClass().getResource("/tool_tiny.png");
    }

    @Override
    public List<IrisComponent> getComponents() throws Exception {
        return components;
    }

    @Override
    public JDialog getAboutBox() {
        return null;
    }

    @Override
    public URL getDesktopIcon() {
        return getClass().getResource("/tool.png");
    }

    @Override
    public void setProperties(List<String> properties) {
        Logger.getLogger("").setLevel(Level.ALL);

    }

    @Override
    public URL getHelpURL() {
        return null;
    }
}
