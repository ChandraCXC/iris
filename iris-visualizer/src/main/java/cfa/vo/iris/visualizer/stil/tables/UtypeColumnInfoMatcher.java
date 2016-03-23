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

package cfa.vo.iris.visualizer.stil.tables;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.utils.UTYPE;
import uk.ac.starlink.table.ColumnInfo;

public class UtypeColumnInfoMatcher implements ColumnInfoMatcher {

    @Override
    public boolean isCompatible(ColumnInfo c1, ColumnInfo c2) {
        
        boolean utypesMatch = UTYPE.compareUtypes(c1.getUtype(), c2.getUtype());
        if (utypesMatch) {
            return true;
        }
        
        // Fall back to name equality if the utypes are not equal or not available.
        if (!StringUtils.isEmpty(c1.getName()) && !StringUtils.isEmpty(c2.getName())) {
            if(StringUtils.equalsIgnoreCase(c1.getName(), c2.getName()))
            {
                return true;
            }
        }
        
        // If both utype and names are not equal
        return false;
    }
}
