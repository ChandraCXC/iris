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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author olaurino
 */
public class SedManager implements ISedManager<IXSed> {

    private Map<String, IXSed> sedMap = new HashMap();

    private IXSed selected;

    @Override
    public IList<IXSed> getSeds() {
        return new List(sedMap.values());
    }

    @Override
    public boolean existsSed(String id) {
        return sedMap.containsKey(id);
    }

    @Override
    public boolean existsSed(IXSed sed) {
        return sedMap.containsValue(sed);
    }

    @Override
    public void addAttachment(IXSed sed, String attachmentId, Object attachment) {
        sed.addAttachment(attachmentId, attachment);
    }

    @Override
    public void removeAttachment(IXSed sed, String attachmentId) {
        sed.removeAttachment(attachmentId);
    }

    @Override
    public void removeAttachment(String id, String attachmentId) {
        sedMap.get(id).removeAttachment(attachmentId);
    }

    @Override
    public void addAttachment(String id, String attachmentId, Object attachment) {
        sedMap.get(id).addAttachment(attachmentId, attachment);
    }

    @Override
    public Object getAttachment(IXSed sed, String attachmentId) {
        return sed.getAttachment(attachmentId);
    }

    @Override
    public Object getAttachment(String id, String attachmentId) {
        return sedMap.get(id).getAttachment(attachmentId);
    }

    @Override
    public IXSed getSelected() {
        return selected;
    }

    @Override
    public void add(IXSed sed) {
        sedMap.put(sed.getId(), sed);
    }

    @Override
    public void remove(String id) {
        sedMap.remove(id);
    }

    @Override
    public void select(IXSed sed) {
        this.selected = sed;
    }

//    @Override
//    public void SetDefaultSpectralUnits() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void SetDefaultFluxUnits() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public IXSed newSed(String id) {
        IXSed sed = new XSed(id);
        sedMap.put(id, sed);
        return sed;
    }

    @Override
    public void rename(IXSed sed, String newId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
