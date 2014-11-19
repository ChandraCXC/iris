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
class Wavenumber extends AbstractQuantity {

    public Wavenumber() {
	super("Wavenumber", "Wavenumber", "em.wavenumber");
        add(XUnit.WNMICRON);
                //add(XUnit.ARBITRARYXWN);
    }
    
}
