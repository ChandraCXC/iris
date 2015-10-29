/*
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top source directory.
 */

/**
 * Created by IntelliJ IDEA.
 * User: busko
 * Date: Jun 9, 2003
 * Time: 11:55:32 AM
 */

package cfa.vo.iris.units.spv;

import java.util.Enumeration;

public class UnknownXUnits extends XUnits {

    public UnknownXUnits (String units_string) {
        this.original_spelling = new String(units_string);
        this.units_string = new String(units_string);
    }

    public String getLabel() {
        return units_string;
    }

    public Enumeration getSupportedUnits() {
        return new Enumeration() {
            public boolean hasMoreElements() {
                return false;
            }
            public Object nextElement() {
                return null;
            }
        };
    }

    public double convertToStandardUnits(double value, double avalue) {
        return value;
    }

    public double convertFromStandardUnits(double value, double avalue) {
        return value;
    }

    protected void getConverterObject() {
    }

    /**
     *  This units type is invalid by definition.
     *
     *  @return   always <code>false</code>
     */
    public boolean isValid() {
        return false;
    }
}

