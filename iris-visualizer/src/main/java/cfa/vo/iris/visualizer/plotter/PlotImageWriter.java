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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.visualizer.stil.StilPlotter;


public class PlotImageWriter {
    
    private static final Logger logger = Logger.getLogger(PlotImageWriter.class.getName());
    
    private static final ImageTypeComboMenu fileTypes = new ImageTypeComboMenu();
    private final PlotterView view;
    private final StilPlotter display;
    
    JFileChooser fileChooser;
    
    public PlotImageWriter(StilPlotter display, PlotterView view) {
        if (display == null) {
            throw new IllegalArgumentException("Cannot print an empty display!");
        }
        
        this.view = view;
        this.display = display;
        this.fileChooser = new JFileChooser();
        
        fileChooser.setAccessory(fileTypes);
    }
    
    public void openSavePlotDialog() {
        int ret = showSaveDialog();
        if (ret == JFileChooser.APPROVE_OPTION) {
            saveImage();
        }
    }
    
    private void saveImage() {
        
        String fileType = fileTypes.getSelectedItem();
        File outFile = fileChooser.getSelectedFile();
        
        // Append the file type to the file name
        if (!StringUtils.endsWith(outFile.getName(), "." + fileType)) {
            outFile = new File(outFile.getAbsolutePath() + "." + fileType);
        }
        
        BufferedImage image = new BufferedImage(display.getWidth(), display.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        display.print(g);
        g.dispose();
        
        logger.info(String.format("Saving plotter to file %s as %s.", outFile, fileType));
        
        try {
            write(image, fileType, outFile);
        } catch (IOException | IllegalArgumentException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    int showSaveDialog() {
        return fileChooser.showSaveDialog(view);
    }
    
    void write(BufferedImage image, String fileType, File outFile) throws IOException {
        boolean success = ImageIO.write(image, fileType, outFile);
        if (!success) {
            String msg = String.format("invalid file (%s) or file type (%s)", outFile, fileType);
            throw new IllegalArgumentException(msg);
        }
        
    }
}
