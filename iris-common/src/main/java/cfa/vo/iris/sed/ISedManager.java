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

/**
 *
 * SED managers provide centralized management of SED instances. This interface is generic to support
 * the transition from Iris v1.0 to v2.0, where SEDLib changes are expected but not yet designed.
 *
 * The parameter must be constrained to the class of SEDs that is managed by the SED Manager implementation.
 *
 * Iris Components should use a SED manager as the only means of keeping track of opened SEDs.
 *
 * @author olaurino
 */
public interface ISedManager<SedClass> {
    /**
     * Return the list of SEDs managed by this SED Manager.
     *
     * @return
     */
    IList<SedClass> getSeds();
    /**
     * Check whether an SED is managed by this SED Manager.
     *
     * @param id The ID of the SED to be queried.
     * @return True if this manager has an SED with the provided ID.
     */
    boolean existsSed(String id);
    /**
     * Check whether an SED is managed by this SED Manager.
     *
     * @param sed The SED instance to be queried.
     * @return True is the provided SED is managed but this manager.
     */
    boolean existsSed(SedClass sed);
    /**
     * Components can attach any kind of objects to the SED, identifying them by a string.
     * To avoid name clashes it is strongly recommended that components use a namespace mechanism
     * to the ID of the attachments.
     * 
     * @param sed The SED to which attach the new object.
     * @param attachmentId The String ID of the attachment.
     * @param attachment The actual object to be attached.
     */
    void addAttachment(SedClass sed, String attachmentId, Object attachment);
    /**
     * Remove an attachment from a SED.
     * @param sed The SED to which the attachment has to be removed.
     * @param attachmentId The ID of the attachment that has to be removed.
     */
    void removeAttachment(SedClass sed, String attachmentId);
    /**
     * Add an attachment using the SED ID as a key
     * @param id The ID of the SED to which the attachment has to be added.
     * @param attachmentId The ID of the attachment.
     * @param attachment The actual object to be attached.
     */
    void addAttachment(String id, String attachmentId, Object attachment);
    /**
     * Retrieve an attachment from a given SED.
     * @param sed The SED object from which the attachment has to be retrieved.
     * @param attachmentId the ID of the attachment to be retrieved.
     * @return The attachment Object. The client must know how to cast this object to
     * an useful class.
     */
    Object getAttachment(SedClass sed, String attachmentId);
    /**
     * Retrieve an attachment from a given SED.
     * @param sed The ID of the SED from which the attachment has to be retrieved.
     * @param attachmentId the ID of the attachment to be retrieved.
     * @return The attachment Object. The client must know how to cast this object to
     * an useful class.
     */
    Object getAttachment(String id, String attachmentId);
    /**
     * Remove an attachment from an SED.
     * @param id The ID of the SED from which the attachment has to be removed.
     * @param attachmentId The ID of the attachment to be removed.
     */
    void removeAttachment(String id, String attachmentId);
    /**
     * All the time a single SED can be considered as "selected". The semantics of the selection
     * depend on the use case, and the components are free to override this semantics or to
     * ignore this selection accordingly to their use cases.
     *
     * @return The SED currently selected.
     */
    SedClass getSelected();
    /**
     * Add an SED to be managed by this manager.
     * @param sed The SED to add.
     */
    void add(SedClass sed);
    /**
     * Create a new, empty managed SED.
     * @param id The ID of the new SED.
     *
     * @return The newly created SED object.
     */
    SedClass newSed(String id);
    /**
     * Remove an SED from this manager.
     * @param id The ID of the SED that has to be dropped.
     */
    void remove(String id);
    /**
     * Set the selected SED.
     * @param sed The SED that has to be selected.
     */
    void select(SedClass sed);
    /**
     * Rename a managed sed;
     * @param sed The SED to be renamed
     * @param newId the new SED name
     */
    void rename(SedClass sed, String newId);
//    void SetDefaultSpectralUnits();
//    void SetDefaultFluxUnits();
}
