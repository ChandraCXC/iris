/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.setup;

import cfa.vo.sed.filters.IFilter;

/**
 *
 * @author olaurino
 */
public interface Builder<OutputClass> {
    OutputClass build(IFilter filter, int row) throws Exception;
}
