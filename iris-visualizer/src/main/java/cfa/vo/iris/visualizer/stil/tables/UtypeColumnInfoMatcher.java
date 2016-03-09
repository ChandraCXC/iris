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

import cfa.vo.sedlib.common.Utypes;
import uk.ac.starlink.table.ColumnInfo;

public class UtypeColumnInfoMatcher implements ColumnInfoMatcher {

    @Override
    public boolean isCompatible(ColumnInfo c1, ColumnInfo c2) {
        
        boolean utypesMatch = compareUtypes(c1.getUtype(), c2.getUtype());
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
    
    private boolean compareUtypes(String utype1, String utype2) {
        
        // If either of them are empty they are not equal
        if (StringUtils.isEmpty(utype1) || StringUtils.isEmpty(utype2)) {
            return false;
        }
        
        // if the strings are equal then they are equals
        if (StringUtils.equalsIgnoreCase(utype1, utype2)) {
            return true;
        }
        
        // Use Utype comparisons
        int u1 = Utypes.getUtypeFromString(utype1);
        int u2 = Utypes.getUtypeFromString(utype2);
        
        if (Utypes.INVALID_UTYPE == u1 || Utypes.INVALID_UTYPE == u2) {
            return false;
        }
        
        if (u1 == u2) {
            return true;
        }
        
        return false;
    }

}
