/*
 * Copyright 2015 Chandra X-Ray Observatory.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import uk.ac.starlink.table.AbstractStarTable;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;

/**
 * A StarTable with layer preferences attached.  
 *
 * @author jbudynk
 */
public class StarSegment extends AbstractStarTable {

    private StarTable data;
    private ColumnInfo[] columnInfo;
    private String prefix;
    
    public StarTablePreferences.LayerPreferences layerPreferences;
    
    public enum ColumnName {
        // Order is important. DO NOT CHANGE.
        X_COL("X axis values", Double.class),
        Y_COL("Y axis values", Double.class),
        X_ERR_HI("X axis high error values", Double.class),
        X_ERR_LO("X axis low error values", Double.class),
        Y_ERR_HI("Y axis high error values", Double.class),
        Y_ERR_LO("Y axis low error values", Double.class);
        
        public String description;
        public Class contentClass;
        
        private ColumnName(String description, Class contentClass) {
            this.description = description;
            this.contentClass = contentClass;
        }
    }
    
    public StarSegment(StarTable data) {
        
        this.data = data;
        this.prefix = StringUtils.isEmpty(data.getName()) ?
                UUID.randomUUID().toString() : data.getName();
        
        this.layerPreferences = 
                new StarTablePreferences.LayerPreferences(prefix)
                .setInSource(this.data)
                .setType("xyerror")
                .setShape("filled_circle");
        
        this.columnInfo = createColumnInfo();
    }
    
    private ColumnInfo[] createColumnInfo() {
        
        List<ColumnInfo> infos = new ArrayList<>(ColumnName.values().length);
        ColumnName[] names = ColumnName.values();
        
        for (int i=0; i<names.length; i++) {
            ColumnName cn = names[i];
            ColumnInfo col = new ColumnInfo(
                    cn.name(),
                    cn.contentClass,
                    cn.description);
            infos.add(col);
        }
        
        return infos.toArray(new ColumnInfo[0]);
    }

    @Override
    public int getColumnCount() {
        return columnInfo.length;
    }

    @Override
    public long getRowCount() {
        return data.getRowCount();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public ColumnInfo getColumnInfo(int icol) {
        return columnInfo[icol];
    }

    @Override
    public List getColumnAuxDataInfos() {
        List<Object> list = new ArrayList<>(columnInfo.length);
        
        for (int i=0; i<columnInfo.length; i++) {
            list.add(i, columnInfo[i].getAuxData());
        }
        
        return list;
    }

    @Override
    public Object[] getRow(long irow) throws IOException {
        return data.getRow(irow);
    }

    @Override
    public RowSequence getRowSequence() {
        throw new RuntimeException("Called getRowSequence");
    }
    
    public Map<String, Object> getPreferences() {
        return this.layerPreferences.preferences;
    }
    
}