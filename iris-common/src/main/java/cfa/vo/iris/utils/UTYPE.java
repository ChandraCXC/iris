package cfa.vo.iris.utils;

import org.apache.commons.lang.StringUtils;

import cfa.vo.sedlib.common.Utypes;

public class UTYPE {
    public final static String FLUX_STAT_ERROR = "Spectrum.Data.FluxAxis.Accuracy.StatError";

    
    public static String trimPrefix(String utype) {
        
        // Remove prefixes if present
        if (StringUtils.contains(utype, ":")) {
            utype = utype.split(":")[1];
        }
        
        return utype;
    }
    
    public static boolean compareUtypes(String utype1, String utype2) {
        
        // If either of them are empty they are not equal
        if (StringUtils.isEmpty(utype1) || StringUtils.isEmpty(utype2)) {
            return false;
        }
        
        // if the strings are equal then they are equals
        if (StringUtils.equalsIgnoreCase(utype1, utype2)) {
            return true;
        }
        
        utype1 = trimPrefix(utype1);
        utype2 = trimPrefix(utype2);
        
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
