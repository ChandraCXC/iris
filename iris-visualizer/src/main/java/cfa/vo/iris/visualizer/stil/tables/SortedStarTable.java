/*
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

import java.io.IOException;
import java.util.Arrays;

import uk.ac.starlink.table.RowPermutedStarTable;
import uk.ac.starlink.table.StarTable;

/**
 * SortedStarTable implementation borrowed from:
 * http://www.star.bris.ac.uk/~mbt/stil/sun252/sortExample.html
 * Supports sorting by either ascending or descending values
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SortedStarTable extends RowPermutedStarTable {

    public SortedStarTable(StarTable baseTable, int sortCol, Boolean ascending) throws IOException {
        super(baseTable);
        
        // BaseTable must provide Random access
        if (!baseTable.isRandom()) {
            throw new IllegalArgumentException("baseTable must provide random access");
        }
        
        // Check that the column we are being asked to sort on has
        // a defined sort order.
        Class<?> clazz = baseTable.getColumnInfo( sortCol ).getContentClass();
        if ( ! Comparable.class.isAssignableFrom( clazz ) ) {
            throw new IllegalArgumentException( clazz + " not Comparable" );
        }
        
        // Fill an array with objects which contain both the index of each
        // row, and the object in the selected column in that row.
        int nrow = (int) getRowCount();
        RowKey[] keys = new RowKey[ nrow ];
        for ( int irow = 0; irow < nrow; irow++ ) {
            Object value = baseTable.getCell( irow, sortCol );
            keys[ irow ] = new RowKey( (Comparable) value, irow, ascending );
        }

        // Sort the array on the values of the objects in the column;
        // the row indices will get sorted into the right order too.
        Arrays.sort( keys );

        // Read out the values of the row indices into a permutation array.
        long[] rowMap = new long[ nrow ];
        for ( int irow = 0; irow < nrow; irow++ ) {
            rowMap[ irow ] = keys[ irow ].index;
        }

        // Finally set the row permutation map of this table to the one
        // we have just worked out.
        setRowMap( rowMap );
    }
    
    // Defines a class (just a structure really) which can hold 
    // a row index and a value (from our selected column).
    class RowKey implements Comparable { 
        Comparable value;
        int index;
        Boolean ascending;
        
        RowKey( Comparable value, int index, Boolean ascending ) {
            this.value = value;
            this.index = index;
            this.ascending = ascending;
        }
        
        public int compareTo( Object o ) {
            RowKey other = (RowKey) o;
            int val = this.value.compareTo( other.value );
            
            // If ascending switch the comparison
            return !ascending ? -1 * val : val;
        }
    }
}
