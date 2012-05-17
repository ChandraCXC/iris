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
package cfa.vo.sed.filters;

import cfa.vo.sed.builder.ISegmentColumn;
import cfa.vo.sed.builder.ISegmentMetadata;
import cfa.vo.sed.builder.ISegmentParameter;
import cfa.vo.sed.builder.SegmentMetadata;
import cfa.vo.sed.builder.StarTableSegmentColumn;
import cfa.vo.sed.builder.StarTableSegmentParameter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.EmptyStarTable;
import uk.ac.starlink.table.MultiTableBuilder;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableFormatException;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.table.Tables;
import uk.ac.starlink.util.DataSource;

/**
 *
 * @author olaurino
 */
public abstract class AbstractMultiStarTableFilter extends AbstractSingleStarTableFilter {

    private List<StarTable> tableList;
    private List<ISegmentMetadata> metaList;

    public AbstractMultiStarTableFilter(URL url) {
        super(url);
    }

    public AbstractMultiStarTableFilter() {
        super();
    }

    private void populateList() throws IOException {
        tableList = new ArrayList();

        TableSequence seq = makeTables(getDataSource(getUrl()));

        for (StarTable table; (table = seq.nextTable()) != null;) {
            tableList.add(Tables.randomTable(table));
        }
    }

    @Override
    public StarTable getStarTable() {
        if (tableList == null) {
            try {
                populateList();
            } catch (IOException ex) {
                Logger.getLogger(AbstractMultiStarTableFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (tableList != null) {
            return tableList.get(0);
        }

        return new EmptyStarTable();
    }

    @Override
    public Number[] getData(int segment, int column) throws IOException, FilterException {
        if (tableList == null) {
            populateList();
        }

        StarTable table = tableList.get(segment);

        int len = Tables.checkedLongToInt(table.getRowCount());


        Class contentClass = table.getColumnInfo(column).getContentClass();

        if (contentClass.isArray()) {
            if (!contentClass.getComponentType().isPrimitive()) {
                return (Number[]) table.getCell(segment, column);
            } else {

                Object arr = table.getCell(segment, column);

                if (arr instanceof float[]) {
                    len = ((float[]) arr).length;
                }

                if (arr instanceof double[]) {
                    len = ((double[]) arr).length;
                }

                if (arr instanceof short[]) {
                    len = ((short[]) arr).length;
                }

                if (arr instanceof long[]) {
                    len = ((long[]) arr).length;
                }

                Number[] array = (Number[]) Array.newInstance(Number.class, len);

                for (int i = 0; i < len; i++) {
                    array[i] = Array.getDouble(arr, i);
                }
                return array;
            }
        } else {

            Number[] array = (Number[]) Array.newInstance(contentClass, len);

            for (int i = 0; i < len; i++) {
                array[i] = (Number) table.getCell(i, column);

            }

            return array;

        }



    }

    @Override
    public Object[] getColumnData(int segment, int column) throws IOException, FilterException {
        if (tableList == null) {
            populateList();
        }

        StarTable table = tableList.get(segment);

        int len = Tables.checkedLongToInt(table.getRowCount());

        Class contentClass = table.getColumnInfo(column).getContentClass();

        Object[] array = (Object[]) Array.newInstance(contentClass, len);

        for (int i = 0; i < len; i++) {
            array[i] = table.getCell(i, column);
        }

        return array;

    }

    @Override
    public List<ISegmentMetadata> getMetadata() throws FilterException, IOException {

        List<ISegmentMetadata> list = new ArrayList();

        list.addAll(getMultipleMetadata());

        return list;
    }

    protected List<ISegmentMetadata> getMultipleMetadata() throws FilterException, IOException {

        try {
            if (tableList == null) {
                populateList();
            }

            if (metaList == null) {

                metaList = new ArrayList();

                for (StarTable table : tableList) {

                    List<ISegmentParameter> paramList = new ArrayList();
                    List<ISegmentColumn> columnList = new ArrayList();

                    for (DescribedValue v : (List<DescribedValue>) table.getParameters()) {
                        paramList.add(new StarTableSegmentParameter(v.getInfo(), v.getValue()));
                    }

                    int colCount = table.getColumnCount();

                    for (int i = 0; i < colCount; i++) {

                        ColumnInfo c = table.getColumnInfo(i);
                        StarTableSegmentColumn column = new StarTableSegmentColumn(c, i);
                        columnList.add(column);

                    }

                    metaList.add(new SegmentMetadata(paramList, columnList));
                }

            }

            return metaList;


        } catch (TableFormatException ex) {
            FilterCache.remove(getUrl());
            throw new FilterException("Error reading StarTable format", ex);
        }


    }

    @Override
    public final StarTable makeStarTable(DataSource ds) {
        throw new UnsupportedOperationException("Unsupported");
    }

    protected abstract MultiTableBuilder getTableBuilder();

    protected TableSequence makeTables(DataSource ds) throws IOException {
        return getTableBuilder().makeStarTables(ds, StoragePolicy.ADAPTIVE);
    }

    public List<StarTable> getStarTables() throws Exception {
        if (tableList == null) {
            populateList();
        }

        return tableList;
    }
}
