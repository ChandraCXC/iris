/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.sed;

import cfa.vo.iris.utils.IList;


/**
 *
 * Generic class for points. The parameters must be constrained to the data model
 * class the point is representing and to the segment class it can be added to.
 *
 * @author olaurino
 */
public interface IPoint<DataModelClass, SegmentClass> {
    /**
     * List of segments this point belongs to. The same point can belong to different
     * segments at the same time. It is up to the client to make sure that this potential redundancy
     * doesn't lead to inconsistencies, for example that the same point doesn't get fitted more than once.
     *
     * @return An IList of extended segments
     */
    IList<SegmentClass> getSegments();
    /**
     * Get an instance of the Data Model representing this point. the Data Model is the "value"
     * of the point according to some model.
     *
     * @return An instance of the parameter class, i.e. of the Data Model represented by this point.
     */
    DataModelClass getDataModelInstance();
    /**
     * Set the "value" of this point, by providing an instance of the Data Model class.
     *
     * @param instance The Data Model instance that stores the "value" of this point.
     */
    void setDataModelInstance(DataModelClass instance);
    /**
     * Get the Data Model class for this point. This method allows clients to cast the point
     * Data Model to the correct subclass, if known to the client, so to leverage subtype polymorphism in both directions.
     *
     * @return The actual class of this data point Data Model
     */
    Class getDataModelClass();
    /**
     * Add this point to a specific segment.
     *
     * @param segment The segment this point must be added to.
     * @throws SedException This method throws an exception if the point is somehow inconsistent with the segment
     * it is being tried to be added to.
     */
    void addToSegment(SegmentClass segment) throws SedException;
    /**
     * Remove this point from a segment.
     * @param segment The segment this point has to be removed from
     */
    void removeFromSegment(SegmentClass segment);
//    void setMetadata(IUType utype, Object value) throws SedException;
//    void setMetadata(IUTypedValue value);
//    IUTypedValue getMetadata(IUType utype) throws SedException;
}
