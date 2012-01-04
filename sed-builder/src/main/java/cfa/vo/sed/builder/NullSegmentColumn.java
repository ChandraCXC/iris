/**
 * Copyright (C) Smithsonian Astrophysical Observatory
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

package cfa.vo.sed.builder;

import javax.swing.table.TableCellRenderer;

/**
 *
 * @author omarlaurino
 */
public class NullSegmentColumn implements ISegmentColumn {

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getUnitString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getUCD() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getUtype() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TableCellRenderer getCellRenderer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int[] getShape() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getElementSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Class getContentClass() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer getNumber() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
