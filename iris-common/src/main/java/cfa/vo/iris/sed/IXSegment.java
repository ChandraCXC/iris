/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.sed;

import cfa.vo.iris.utils.IList;
import cfa.vo.iris.utils.IPredicate;

/**
 *
 * @author olaurino
 */
public interface IXSegment<T extends IPoint> extends IList<T> {
    IList<IXSed> getSeds();
    void add(IXSed sed);
    IList<T> filter(IPredicate<T> predicate, String id);
}
