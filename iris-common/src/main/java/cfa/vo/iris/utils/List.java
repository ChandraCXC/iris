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

import cfa.vo.iris.sed.SedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author olaurino
 */
public class List<T> implements IList<T>{

    private ArrayList<T> list = new ArrayList();

    private String id;

    public List() {
        this.id = "Anonymous";
    }

    public List(String id) {
        this.id = id;
    }

    public List(String id, IList<T> elements) {
        this.id = id;
        for(T element : elements) {
            list.add(element);
        }
    }

    public List(Collection<T> elements) {
        this();
        for(T element : elements) {
            list.add(element);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public boolean add(T element) throws SedException {
        return list.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) throws SedException {
        return list.addAll(elements);
    }

    @Override
    public void add(int position, T element) throws SedException {
        list.add(position, element);
    }

    @Override
    public boolean remove(T element) {
        return list.remove(element);
    }

    protected final ArrayList<T> getElements() {
        return list;
    }

    public static <T> IList<T> filter(IList<T> target, IPredicate<T> predicate, String id) {
    IList<T> result = new List<T>(id);
        for (T element: target) {
            if (predicate.apply(element)) {
                    try {
                        result.add(element);
                    } catch (SedException ex) {
                        // This exception can only be thrown due to a runtime exception,
                        // since the original collection should be consistent.
                        throw new IllegalStateException(ex);
                    }
            }
        }
        return result;
    }

    @Override
    public boolean addAll(IList<? extends T> elements) throws SedException {
        try {
            for(T element : elements)
                this.add(element);
        } catch(SedException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new IllegalStateException(ex);
        }

        return true;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public T get(int i) {
        return list.get(i);
    }

    private class IrisIterator implements Iterator {

        private Iterator it;

        public IrisIterator(Iterator it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Object next() {
            return it.next();
        }

        @Override
        public void remove() {
            it.remove();

        }

    }


}
