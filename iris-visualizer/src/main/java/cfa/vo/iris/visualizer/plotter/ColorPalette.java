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
import java.util.List;

/**
 * Interface for a plotter color palette. A color pallete should be able to
 * create or return a list of colors.
 * 
 */
public interface ColorPalette {
    /**
     * Get the next available color in the palette
     * @return a color
     */
    public Color getNextColor();
    
    /**
     * Returns a list of N colors 
     * @param n
     * @return a list of colors
     */
    public List<Color> createPalette(int n);
}
