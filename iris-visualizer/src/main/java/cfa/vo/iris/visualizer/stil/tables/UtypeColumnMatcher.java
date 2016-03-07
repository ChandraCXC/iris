package cfa.vo.iris.visualizer.stil.tables;

import org.apache.commons.lang.StringUtils;

import uk.ac.starlink.table.ColumnInfo;

public class UtypeColumnMatcher implements ColumnMatcher {

    @Override
    public boolean isCompatible(ColumnInfo c1, ColumnInfo c2) {
        if (StringUtils.containsIgnoreCase(c1.getUtype(), c2.getUtype()) ||
            StringUtils.containsIgnoreCase(c2.getUtype(), c1.getUtype())) 
        {
            return true;
        }
        return false;
    }

}
