/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sed.quantities.AxisMetadata;
import cfa.vo.sed.quantities.YQuantity;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.Utypes;

/**
 *
 * @author olaurino
 */
public class FluxAxis extends AbstractAxis<YQuantity> {

    private Double error;
    public static final String PROP_ERROR = "error";

    /**
     * Get the value of error
     *
     * @return the value of error
     */
    public Double getError() {
        return error;
    }

    /**
     * Set the value of error
     *
     * @param error new value of error
     */
    public void setError(Double error) {
        Double oldError = this.error;
        this.error = error;
        propertyChangeSupport.firePropertyChange(PROP_ERROR, oldError, error);
    }

    @Override
    public void addTo(Segment segment) throws SedException {

        AxisMetadata md = new AxisMetadata(getQuantity(), getUnit());

        segment.createChar().createFluxAxis().setUcd(md.getUCD());
        segment.setFluxAxisValues(new double[]{getValue()});
        segment.setFluxAxisUnits(md.getUnitString());
        if(error!=null)
            segment.createData().setDataValues(new double[]{error}, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);

    }

    @Override
    public Validation validate() {
        Validation v = new Validation();

        if(getValue()==null || getValue().isNaN())
            v.addError("Missing/Invalid Y Axis value");

        if(error!=null && error.isNaN())
            v.addError("Invalid Y Axis Error");

        if(getQuantity()==null)
            v.addError("Missing Y Axis Quantity");

        return v;
    }

}
