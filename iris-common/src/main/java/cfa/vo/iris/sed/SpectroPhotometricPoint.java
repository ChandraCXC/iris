/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
