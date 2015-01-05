/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.stacker;

/**
 *
 * @author jbudynk
 */
public interface SegmentPayload {
    public double[] getX();
    public void setX(double[] x);
    public double[] getY();
    public void setY(double[] y);
    public double[] getYerr();
    public void setYerr(double[] yerr);
    public Double getZ();
    public void setZ(Double redshift);
    public Double getNormConstant();
    public void setNormConstant(Double normConstant);
    public double[] getCounts();
    public void setCounts(double[] counts);
}
