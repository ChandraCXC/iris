/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.visualizer.plotter;

import javax.swing.JPanel;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;

public class StilPlotter extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private IrisApplication app;
    private IWorkspace ws;

    public StilPlotter(String title, IrisApplication app, IWorkspace ws) {
        this.ws = ws;
        this.app = app;
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(1, 0, 0, 0));
    }
}
