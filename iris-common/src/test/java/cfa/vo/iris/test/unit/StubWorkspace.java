/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.unit;

import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.desktop.IrisMenuItem;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.SedlibSedManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author olaurino
 */
public class StubWorkspace implements IWorkspace {
    private JFrame mainWindow = new JFrame("Test App");
    private JDesktopPane desktop;
    private SedlibSedManager manager = new SedlibSedManager();
    private JFileChooser chooser = new JFileChooser();
    private JMenu fileMenu = new JMenu("File");
    private JMenu toolsMenu = new JMenu("Tools");

    public StubWorkspace() {
        mainWindow = new JFrame("Test App");
        desktop = new JDesktopPane();
        JMenuBar menuBar = new JMenuBar();
        mainWindow.setContentPane(desktop);
        mainWindow.setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
    }

    public void addComponent(IrisComponent component) {
        JMenu componentMenu = null;
        for (IMenuItem item : component.getMenus()) {
            IrisMenuItem i = new IrisMenuItem(item);
            if (i.getMenu().equals("File")) {
                fileMenu.add(i);
            } else {
                if (componentMenu == null) {
                    componentMenu = new JMenu(component.getName());
                    toolsMenu.add(componentMenu);
                }
                componentMenu.add(i);
            }
        }
    }

    @Override
    public void addFrame(JInternalFrame frame) {
        desktop.add(frame);
        GUIUtils.moveToFront(frame);
    }

    @Override
    public void removeFrame(JInternalFrame frame) {
        desktop.remove(frame);
    }

    @Override
    public JFileChooser getFileChooser() {
        return chooser;
    }

    @Override
    public ISedManager getSedManager() {
        return manager;
    }

    @Override
    public JFrame getRootFrame() {
        return mainWindow;
    }

    @Override
    public JDesktopPane getDesktop() {
        return desktop;
    }
}
