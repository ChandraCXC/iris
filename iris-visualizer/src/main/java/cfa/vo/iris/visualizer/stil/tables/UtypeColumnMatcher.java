package cfa.vo.iris.visualizer.stil.tables;

import org.apache.commons.lang.StringUtils;

import uk.ac.starlink.table.ColumnInfo;

public class UtypeColumnMatcher implements ColumnMatcher {

    @Override
    public boolean isCompatible(ColumnInfo c1, ColumnInfo c2) {
        if (StringUtils.isEmpty(c1.getUtype()) || StringUtils.isEmpty(c2.getUtype())) {
            return false;
        }
        if (StringUtils.equalsIgnoreCase(c1.getUtype(), c2.getUtype()))
        {
            return true;
        }
        return false;
    }

}
