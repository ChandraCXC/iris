/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.utils;

/**
 *
 * Generic interfaces for filter predicates. The type parameter represents the class of objects
 * this predicate can be applied to.
 *
 * @author olaurino
 */
public interface IPredicate<T> {
    /**
     * Callback that applies this predicate to an object.
     *
     * @param object The object to which the predicate has to be applied.
     * @return True if the object satisfies the predicate.
     */
    boolean apply(T object);
}
