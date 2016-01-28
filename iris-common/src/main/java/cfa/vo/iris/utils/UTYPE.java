package cfa.vo.iris.utils;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class UTYPE {
    String prefix;
    String main;

    public final static String FLUX_STAT_ERROR = "Spectrum.Data.FluxAxis.Accuracy.StatError";

    public UTYPE(String utypeString) {
        if (utypeString == null || utypeString.isEmpty()) {
            throw new IllegalArgumentException("UTYPE needs a non null, non empty string");
        }

        utypeString = utypeString.replaceAll("(?i)spectrum.", "");

        if (utypeString.contains(":")) {
            String[] tokens = utypeString.split(":");
            prefix = tokens[0].toLowerCase();
            main = tokens[1].toLowerCase();
        } else {
            main = utypeString.toLowerCase();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UTYPE utype = (UTYPE) o;

        return new EqualsBuilder()
                .append(main, utype.main)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(main)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "UTYPE{" +
                "prefix='" + prefix + '\'' +
                ", main='" + main + '\'' +
                '}';
    }

    public String getCanonicalString() {
        String p = "";
        if(prefix != null) {
            p = prefix+":";
        }
        return p+main;
    }

    public String getMain() {
        return main;
    }
}
