/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.plotter;

import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

import uk.ac.starlink.ttools.plot2.task.PlotDisplay;


public class PlotImageWriter {
    
    private static final Logger logger = Logger.getLogger(PlotImageWriter.class.getName());
    
    private static final ImageTypeComboMenu fileTypes = new ImageTypeComboMenu();
    private final PlotterView view;
    private final PlotDisplay display;
    private final JFileChooser fileChooser;
    
    /**
     * Creates new form PlotImageWriter
     */
    public PlotImageWriter(PlotDisplay display, PlotterView view) {

        this.view = view;
        this.display = display;
        this.fileChooser = new JFileChooser();
        
        fileChooser.setAccessory(fileTypes);
    }
    
    public void openSavePlotDialogue() {
        int ret = fileChooser.showSaveDialog(view);
        
        if (ret == JFileChooser.APPROVE_OPTION) {
            saveImage(fileChooser.getSelectedFile(), fileTypes.getSelectedItem());
        }
    }
    
    private void saveImage(File f, String fileType) {
        
    }
}
