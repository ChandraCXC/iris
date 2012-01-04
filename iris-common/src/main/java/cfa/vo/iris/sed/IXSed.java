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
public interface IXSed extends IList<IXSegment>{
    IList<IXSegment> filterSegments(IPredicate<IXSegment> predicate, String id);
    IList<IPoint> filterPoints(IPredicate<IPoint> predicate, String id);
    void setSpectralUnits();
    void setFluxUnits();
//    Sed getSed() throws SedException;
    void addAttachment(String attachmentId, Object attachment);
    Object getAttachment(String attachmentId);
    void removeAttachment(String attachmentId);
}
