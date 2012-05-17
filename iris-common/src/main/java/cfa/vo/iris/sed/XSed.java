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

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.XSedEvent;
import cfa.vo.iris.events.XSegmentEvent;
import cfa.vo.iris.utils.AbstractIterator;
import cfa.vo.iris.utils.IList;
import cfa.vo.iris.utils.IPredicate;
import cfa.vo.iris.utils.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author olaurino
 */
public class XSed extends List<IXSegment> implements IXSed {

    Map<String, Object> attachments = new HashMap();

    public XSed(String id) {
        super(id);
    }

    public XSed(String id, IList<IXSegment> elements) {
        super(id, elements);
    }

//    @Override
//    public Sed getSed() throws SedException {
//        Sed sed = new Sed();
//        for(IXIXIXIXSegment segment : getElements()) {
//            try {
//                sed.addIXIXIXSegment(segment);
//            } catch (cfa.vo.sedlib.common.SedException ex) {
//                throw new SedException(ex);
//            }
//        }
//        return sed;
//    }

    @Override
    public Iterator iterator() {
        return new AbstractIterator(super.iterator()) {

            @Override
            protected void fireEvent() {
                XSedEvent.getInstance().fire(XSed.this, SedCommand.REMOVED);
            }
        };
    }

    @Override
    public boolean add(IXSegment element) throws SedException {
        System.out.println("Adding IXSegment "+element+" to Sed "+getId());
        boolean resp;
        try {
            resp = super.add(element);
        } catch (cfa.vo.iris.sed.SedException ex) {
            throw new SedException();
        }
        if(resp)
            XSedEvent.getInstance().fire(this, SedCommand.CHANGED);

        return resp;
    }

    @Override
    public boolean addAll(Collection<? extends IXSegment> elements) throws SedException {
        System.out.println("Adding IXSegment "+elements+" to Sed "+getId());
        boolean resp = super.addAll(elements);
        if(resp)
            XSedEvent.getInstance().fire(this, SedCommand.CHANGED);

        return resp;
    }

    @Override
    public void add(int position, IXSegment element) throws SedException {
        System.out.println("Adding IXSegment "+element+" to Sed "+getId()+ " in position "+position);
        super.add(position, element);
        XSedEvent.getInstance().fire(this, SedCommand.CHANGED);
        XSegmentEvent.getInstance().fire(element, SedCommand.ADDED);
    }

    @Override
    public boolean remove(IXSegment element) {
        boolean resp = super.remove(element);
        if(resp)
            XSedEvent.getInstance().fire(this, SedCommand.REMOVED);

        return resp;
    }

    @Override
    public IList<IXSegment> filterSegments(IPredicate<IXSegment> predicate, String id) {
        return List.filter(this, predicate, id);
    }

    @Override
    public IList<IPoint> filterPoints(IPredicate<IPoint> predicate, String id) {
        IList<IPoint> resp = new List(id);
        for(IXSegment segment : getElements()) {
            IList<IPoint> pointList = segment.filter(predicate, "temp");
            try {
                resp.addAll(pointList);
            } catch (SedException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return resp;
    }

    @Override
    public void setSpectralUnits() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFluxUnits() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addAttachment(String attachmentId, Object attachment) {
        attachments.put(attachmentId, attachment);
    }

    @Override
    public Object getAttachment(String attachmentId) {
        return attachments.get(attachmentId);
    }

    @Override
    public void removeAttachment(String attachmentId) {
        attachments.remove(attachmentId);
    }

    





}
