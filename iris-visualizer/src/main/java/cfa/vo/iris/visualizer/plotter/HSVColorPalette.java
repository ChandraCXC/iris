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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * Generate N distinct colors
 */
public class HSVColorPalette extends ColorPalette {
    
    private static final double INVERSE_GOLDEN_RATION = 1/1.61803398875;
    
    // saturation and brightness thresholds
    private static final double SATURATION_THRESHOLD = 0.3;
    private static final double BRIGHTNESS_THRESHOLD = 0.25;
    
    private Random random = new Random();
    private Color color;
    private double hue;
    private double saturation;
    private double brightness;
    private long index;
    
    // Default constructor
    public HSVColorPalette() {
        this.hue = 0;
        this.saturation = 1.0;
        this.brightness = 0;
        this.color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
    }
    
    /**
     * Constructor for initial settings of the color palette. The wheel starts at the 
     * specified hue, saturation, and brightness.
     * @param hue
     * @param saturation
     * @param brightness
     */
    public HSVColorPalette(double hue, double saturation, double brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
    }
    
    /**
     * Generate n distinct colors
     * @param n
     * @return a list of distinct colors
     * 
     */
    @Override
    public final List<Color> createPalette(int n) {
        
        List<Color> colors = new ArrayList<>();
                
        for (int i=0; i < n; i++) {
            colors.add(getNextColor());
        }
        
        return colors;
    }
    
    /**
     * Returns a new distinct color using the golden rule ratio.
     * Algorithm adopted from
     * http://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/
     * 
     * @return color
     */
    @Override
    public final Color getNextColor() {
        
        // Return current color then increment
        Color ret = this.color;
        
        // calculate the next hue using the golden ratio
        hue += INVERSE_GOLDEN_RATION;
        hue %= 1;
        
        // TODO: come up with an algorithm for distinct colors of 
        // different saturations and brightnesses.
        
        brightness = 1.0;
        
//        // reset the saturation and brightness
//        // if they get too low
//        if (brightness < BRIGHTNESS_THRESHOLD) {
//            brightness = 1.0;
//        }
//        if (saturation < SATURATION_THRESHOLD) {
//            saturation = 1.0;
//        }
//        
//        // Close hues repeat themselves every 6 colors.
//        // when this happens, change the brightness and/or saturation
//        // so that we get colors that are more different from the first set
//        if (index !=0 && index % 12 == 0) {
//            brightness -= 0.25;
//        }
//        
//        // change saturation every 4 brightness decreases
//        if (index != 0 && index % 6 == 0) {
//            saturation -= -0.25;
//        }
//        
//        // counter for hues.
//        index++;
        
        // Update the next color value
        color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
        
        return ret;
    }
}
