/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker.samp;

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
    public double getRedshift();
    public void setRedshift(double redshift);
    public double getNormConstant();
    public void setNormConstant(double normConstant);
    public double[] getCounts();
    public void setCounts(double[] counts);
}
