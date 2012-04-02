/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sed.quantities.IQuantity;
import cfa.vo.sed.quantities.IUnit;

/**
 *
 * @author olaurino
 */
public interface Axis<QuantityClass extends IQuantity>  extends SegmentComponent {
    QuantityClass getQuantity();
    void setQuantity(QuantityClass quantity);
    IUnit getUnit();
    void setUnit(IUnit unit);
    Double getValue();
    void setValue(Double value);
}
