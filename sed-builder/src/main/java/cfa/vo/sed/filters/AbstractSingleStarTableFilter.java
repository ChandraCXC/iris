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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.TableFormatException;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.FileDataSource;
import uk.ac.starlink.util.URLDataSource;

/**
 *
 * @author omarlaurino
 */
public abstract class AbstractSingleStarTableFilter extends AbstractFilter {


    private StarTable table;

    private ISegmentMetadata meta;

    public AbstractSingleStarTableFilter(URL url) {
        setUrl(url);
    }

    public AbstractSingleStarTableFilter() {
        
    }

    @Override
    public Number[] getData(int segment, int column) throws IOException, FilterException {
        if(segment>1)
            throw new IndexOutOfBoundsException("This is a Single Table filter. Trying to access more than one segment.");

        return getData(column);
    }

    @Override
    public Object[] getColumnData(int segment, int column) throws IOException, FilterException {
        if(segment>1)
            throw new IndexOutOfBoundsException("This is a Single Table filter. Trying to access more than one segment.");

        return getColumnData(column);
    }

    public abstract StarTable makeStarTable(DataSource ds) throws TableFormatException, IOException;

    @Override
    public List<ISegmentMetadata> getMetadata() throws FilterException, IOException {
        
        List<ISegmentMetadata> list = new ArrayList();

        list.add(getSingleMetadata());

        return list;
    }

    protected ISegmentMetadata getSingleMetadata() throws FilterException, IOException {

        if(table==null)
            try {
                table = makeStarTable(getDataSource(getUrl()));
            } catch (TableFormatException ex) {
                FilterCache.remove(getUrl());
                throw new FilterException(ex);
            }

        if(meta==null) {
            List<ISegmentParameter> paramList = new ArrayList();

            for(DescribedValue v : (List<DescribedValue>) table.getParameters()) {
                paramList.add(new StarTableSegmentParameter(v.getInfo(), v.getValue()));
            }

            List<ISegmentColumn> columnList = new ArrayList();

            int colCount = table.getColumnCount();

            for (int i = 0; i < colCount; i++) {

                ColumnInfo c = table.getColumnInfo(i);
                StarTableSegmentColumn column = new StarTableSegmentColumn(c, i);
                columnList.add(column);

            }
            
            meta = new SegmentMetadata(paramList, columnList);
        }

        return meta;
    }

    public Number[] getData(int column) throws IOException {
        if(table==null)
            table = makeStarTable(getDataSource(getUrl()));

        List array = new ArrayList();

        RowSequence rowSequence = table.getRowSequence();

        for(int i = 0; rowSequence.next(); i++) {
            Object[] row = (Object[]) rowSequence.getRow();

            array.add(row[column]);
        }

        Number[] arr = new Number[array.size()];

        return (Number[]) array.toArray(arr);
    }

    public Object[] getColumnData(int column) throws IOException {
        if(table==null)
            table = makeStarTable(getDataSource(getUrl()));

        List array = new ArrayList();

        RowSequence rowSequence = table.getRowSequence();

        for(int i = 0; rowSequence.next(); i++) {
            Object[] row = (Object[]) rowSequence.getRow();

            array.add(row[column]);
        }

        return array.toArray(new Object[array.size()]);
    }

    protected final DataSource getDataSource(URL url) throws IOException {
        if(url.getProtocol().equals("file"))
            return new FileDataSource(url.getFile());

        return new URLDataSource(url);
    }

    @Override
    public String getAuthor(){
        return "Omar Laurino";
    }

    @Override
    public String getVersion() {
        return "2011";
    }

    public StarTable getStarTable() throws Exception {
        if(table==null)
            getMetadata();

        return table;
    }

}
