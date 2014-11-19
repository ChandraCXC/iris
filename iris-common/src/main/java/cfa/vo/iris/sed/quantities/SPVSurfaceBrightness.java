/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.sed.quantities;

/**
 *
 * @author jbudynk
 */
class SPVSurfaceBrightness extends AbstractQuantity {

    public SPVSurfaceBrightness() {
        super("SurfaceBrightness", "Surface Brightness", "phot.flux.sb");
	    add(SPVYUnit.RAYLEIGH0);
    }
    
}
