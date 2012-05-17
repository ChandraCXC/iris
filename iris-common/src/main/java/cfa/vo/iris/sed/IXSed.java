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
