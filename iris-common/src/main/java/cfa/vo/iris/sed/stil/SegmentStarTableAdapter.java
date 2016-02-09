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

package cfa.vo.iris.sed.stil;

import java.util.Map;
import java.util.WeakHashMap;

import cfa.vo.sedlib.Segment;
import uk.ac.starlink.table.StarTable;

public class SegmentStarTableAdapter implements StarTableAdapter<Segment> {
    
    private Map<Segment, StarTable> cache = new WeakHashMap<>();

    @Override
    public StarTable convertStarTable(Segment data) {
        if (cache.containsKey(data)) {
            return cache.get(data);
        }
        
        StarTable newTable = new SegmentStarTable(data);
        cache.put(data, newTable);
        return newTable;
    }

}
