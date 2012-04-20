/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sed.setup.validation.Validable;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import java.util.List;

/**
 *
 * @author olaurino
 */
public interface SedBlock extends Validable {
    List<Segment> addTo(ExtSed sed) throws SedException;
}
