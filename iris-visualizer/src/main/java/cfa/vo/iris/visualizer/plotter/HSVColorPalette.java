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
public class HSVColorPalette implements ColorPalette {
    
    private Random random = new Random();
    private Color color = new Color(Color.black.getRGB());
    private double hue = 0;
    private double saturation = 1.0;
    private double brightness = 0;
    private long index;
    private final double INVERSE_GOLDEN_RATION = 1/1.61803398875;
    
    // saturation and brightness thresholds
    private final double SATURATION_THRESHOLD = 0.3;
    private final double BRIGHTNESS_THRESHOLD = 0.25;
    
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
     * 
     * @return color
     */
    @Override
    public final Color getNextColor() {
        
        // first, set the current color in the palette. 
        // This is the return value
        color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
        
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
        
        return color;
    }
    
    public final static String toHexString(Color color) throws NullPointerException {
        String hexColor = Integer.toHexString(color.getRGB() & 0xffffff);
        if (hexColor.length() < 6) {
            hexColor = "000000".substring(0, 6 - hexColor.length()) + hexColor;
        }
        return hexColor;
    }

}
