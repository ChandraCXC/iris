package cfa.vo.iris.sed.stil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import cfa.vo.sedlib.Accuracy;
import cfa.vo.sedlib.ISegment;
import cfa.vo.sedlib.Point;
import cfa.vo.sedlib.SedQuantity;

public class SegmentStarTable implements StarTable {
    
    private ISegment data;
    private ColumnInfo[] columnInfo;
    private final String id;
    
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
    
    public SegmentStarTable(ISegment data) {
        this.data = data;
        this.columnInfo = createColumnInfo();
        
        if (data.getDataID() != null && 
            data.getDataID().getTitle() != null &&
            !StringUtils.isEmpty(data.getDataID().getTitle().getId())) 
        {
            this.id = data.getDataID().getTitle().getId();
        } else {
            this.id = UUID.randomUUID().toString();
        }
    }
    
    private ColumnInfo[] createColumnInfo() {
        
        List<ColumnInfo> infos = new ArrayList<ColumnInfo>(ColumnName.values().length);
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
        return data.getLength();
    }

    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public ColumnInfo getColumnInfo(int icol) {
        return columnInfo[icol];
    }

    @Override
    public List getColumnAuxDataInfos() {
        List<Object> list = new ArrayList<Object>(columnInfo.length);
        
        for (int i=0; i<columnInfo.length; i++) {
            list.add(i, columnInfo[i].getAuxData());
        }
        
        return list;
    }

    @Override
    public boolean isRandom() {
        return true;
    }

    @Override
    public Object getCell(long irow, int icol) throws IOException {
        return this.getRow(irow)[icol];
    }

    @Override
    public Object[] getRow(long irow) throws IOException {
        Point p = data.getData().getPoint().get((int) irow);
        return pointToRow(p);
    }

    private Object[] pointToRow(Point point) {

        Object[] row = new Object[ColumnName.values().length];

        // X and Y values
        Double xCol = new Double(point.getSpectralAxis().getValue().getValue());
        Double yCol = new Double(point.getFluxAxis().getValue().getValue());

        row[0] = xCol;
        row[1] = yCol;
        
        // X axis error values
        Double[] xvalues = getAccuracy(point.getSpectralAxis().getAccuracy(), xCol);
        row[2] = xvalues[0];
        row[3] = xvalues[1];
        
        // Y axis error values
        Double[] yvalues = getAccuracy(point.getFluxAxis().getAccuracy(), yCol);
        row[4] = yvalues[0];
        row[5] = yvalues[1];

        return row;
    }
    
    private Double[] getAccuracy(Accuracy accuracy, Double x) {
        Double[] values = new Double[2];
        
        if (accuracy == null) {
            return values;
        }
        
        if (accuracy.isSetStatErrHigh()) {
            values[0] = new Double(accuracy.getStatErrHigh().getValue());
        }
        if (accuracy.isSetStatErrLow()) {
            values[1] = new Double(accuracy.getStatErrLow().getValue());
        }
        
        if (accuracy.isSetStatError()) {
            values[0] = new Double(accuracy.getStatError().getValue());
        }
        
        return values;
    }

    @Override
    public RowSequence getRowSequence() throws IOException {
        return new SegmentIterator();
    }
    
    // Hopefully we won't need these?
    @Override
    public List getParameters() {
        return new ArrayList();
    }

    @Override
    public DescribedValue getParameterByName(String parname) {
        throw new RuntimeException("Called getParameterByName");
    }

    @Override
    public void setParameter(DescribedValue dval) {
        throw new RuntimeException("Called setParameter");
    }

    @Override
    public URL getURL() {
        throw new RuntimeException("Called getURL");
    }

    @Override
    public void setURL(URL url) {
        throw new RuntimeException("Called setURL");
    }

    @Override
    public void setName(String name) {
        throw new RuntimeException("Called setName");
    }

    
    /**
     * RowIterator
     *
     */
    public class SegmentIterator implements RowSequence {
        
        private Object[] next;

        private Iterator<Point> it;

        public SegmentIterator() {
            it = data.getData().getPoint().iterator();
        }

        @Override
        public boolean next() throws IOException {
            if (it.hasNext()) {
                next = pointToRow(it.next());
            }

            return it.hasNext();
        }

        @Override
        public Object getCell(int icol) throws IOException {
            return next[icol];
        }

        @Override
        public Object[] getRow() throws IOException {
            return next;
        }

        @Override
        public void close() throws IOException {
        }
    }
}
