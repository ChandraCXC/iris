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

// http://www.star.bris.ac.uk/~mbt/stilts/sun256/layer-mark.html
public enum ShapeType {
    filled_circle("filled_circle"),
    open_circle("open_circle"),
    cross("cross"),
    x("x"),
    open_square("open_square"),
    open_diamond("open_diamond"),
    open_triangle_up("open_triangle_up"),
    open_triangle_down("open_triangle_down"),
    filled_square("filled_square"),
    filled_diamond("filled_diamond"),
    filled_triangle_up("filled_triangle_up"),
    filled_triangle_down("filled_triangle_down");
    
    private final String name;       

    private ShapeType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    @Override
    public String toString() {
       return this.name;
    }
}
