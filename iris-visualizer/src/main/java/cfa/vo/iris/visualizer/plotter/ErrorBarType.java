/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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

// http://www.star.bris.ac.uk/~mbt/stilts/sun256/layer-xyerror.html
public enum ErrorBarType {
    lines("lines"),
    capped_lines("capped_lines"),
    caps("caps"),
    arrows("arrows"),
    ellipse("ellipse"),
    crosshair_ellipse("crosshair_ellipse"),
    rectangle("rectangle"),
    crosshair_rectangle("crosshair_rectangle"),
    filled_ellipse("filled_ellipse"),
    filled_rectangle("filled_rectangle");
    
    private final String name;       

    private ErrorBarType(String s) {
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
