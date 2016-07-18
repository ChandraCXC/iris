package cfa.vo.iris.sed.stil;

import uk.ac.starlink.table.StarTable;

public interface IrisDataStarTable extends StarTable {
    public double[] getSpecValues();
    public double[] getFluxValues();
    public double[] getSpecErrValues();
    public double[] getSpecErrValuesLo();
    public double[] getSpecErrValuesHi();
    public double[] getFluxErrValues();
    public double[] getFluxErrValuesLo();
    public double[] getFluxErrValuesHi();
}
