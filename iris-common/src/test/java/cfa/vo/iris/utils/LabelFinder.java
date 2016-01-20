package cfa.vo.iris.utils;

import org.uispec4j.Window;
import org.uispec4j.finder.ComponentMatcher;

import javax.swing.*;
import java.awt.*;

public class LabelFinder implements ComponentMatcher {
    private String labelString;

    public LabelFinder(String string) {
        this.labelString = string;
    }

    @Override
    public boolean matches(Component component) {
        if(component instanceof JLabel) {
            JLabel label = (JLabel) component;
            if(labelString.equals(label.getText())) {
                return true;
            }
        }
        return false;
    }

    public JLabel find(Window window) {
        return (JLabel) window.findSwingComponent(this);
    }
}
