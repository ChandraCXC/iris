/**
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
package cfa.vo.iris.visualizer.masks;

import java.util.BitSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public class NullMask implements Mask {
    
    @Override
    public BitSet getMaskedRows(IrisStarTable table) {
        BitSet bitSet = new BitSet();
        return bitSet;
    }

    @Override
    public long cardinality() {
        return 0;
    }

    @Override
    public void clearMasks(int[] rows) {
    }

    @Override
    public void applyMasks(int[] rows) {
        
    }
}
