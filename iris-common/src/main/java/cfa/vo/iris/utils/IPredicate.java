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
