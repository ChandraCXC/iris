package cfa.vo.iris.sed.stil;

import uk.ac.starlink.table.StarTable;

public interface IrisDataStarTable extends StarTable {
    
    // Live values
    public double[] getSpecValues();
    public double[] getFluxValues();
    public double[] getSpecErrValues();
    public double[] getSpecErrValuesLo();
    public double[] getSpecErrValuesHi();
    public double[] getFluxErrValues();
    public double[] getFluxErrValuesLo();
    public double[] getFluxErrValuesHi();
    
    // Retain points to original values for accuracy of units conversion
    public double[] getOriginalFluxValues();
    public double[] getOriginalSpecValues();
    public double[] getOriginalFluxErrValues();
    public double[] getOriginalFluxErrValuesHi();
    public double[] getOriginalFluxErrValuesLo();
}
