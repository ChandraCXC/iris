/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import cfa.vo.iris.sed.ExtSed;
import java.util.List;

/**
 *
 * @author jbudynk
 */
public interface IStack extends List<ExtSed> {
    public ExtSed getStackedSed();
    
    public String getId();
    
    public void setId(String id);
}
