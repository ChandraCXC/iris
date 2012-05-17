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

package cfa.vo.sed.builder;

import javax.swing.table.TableCellRenderer;
import uk.ac.starlink.table.ColumnInfo;

/**
 *
 * @author omarlaurino
 */
public class StarTableSegmentColumn implements ISegmentColumn {

    private ColumnInfo colInfo;

    private Integer number;

    public StarTableSegmentColumn(ColumnInfo colInfo, Integer number) {
        this.colInfo = colInfo;
        this.number = number;
    }

    public TableCellRenderer getCellRenderer() {
        return colInfo.getCellRenderer();
    }

    public Class getContentClass() {
        return colInfo.getContentClass();
    }

    public String getDescription() {
        return colInfo.getDescription();
    }

    public int getElementSize() {
        return colInfo.getElementSize();
    }

    public String getName() {
        return colInfo.getName();
    }

    public int[] getShape() {
        return colInfo.getShape();
    }

    public String getUCD() {
        return colInfo.getUCD();
    }

    public String getUnitString() {
        return colInfo.getUnitString();
    }

    public String getUtype() {
        return colInfo.getUtype();
    }

    @Override
    public String toString() {
        return getName();
    }

    public Integer getNumber() {
        return number;
    }

}
