/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.sed;

import cfa.vo.iris.utils.IList;
import cfa.vo.iris.utils.List;

/**
 *
 * @author olaurino
 */
public class SpectroPhotometricPoint implements IPoint<ISpectralSegment, IXSegment> {

    private IList<IXSegment> segments = new List();

    private ISpectralSegment value;

    public SpectroPhotometricPoint(IXSegment segment, ISpectralSegment value) throws SedException {
        segments.add(segment);
        setDataModelInstance(value);
    }

    @Override
    public ISpectralSegment getDataModelInstance() {
        return value;
    }

    @Override
    public final void setDataModelInstance(ISpectralSegment value) {
        this.value = value;
    }

    @Override
    public IList<IXSegment> getSegments() {
        return segments;
    }

    @Override
    public void addToSegment(IXSegment segment) throws SedException {
        segments.add(segment);
    }

    @Override
    public void removeFromSegment(IXSegment segment) {
        segments.remove(segment);
    }

//    @Override
//    public IMetadata getMetadata() {
//        return metadata;
//    }
//
//    @Override
//    public void setMetadata(IUType utype, Object value) throws SedException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public IUTypedValue getMetadata(IUType utype) throws SedException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void setMetadata(IUTypedValue value) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public Class getDataModelClass() {
        return value.getClass();
    }

}
