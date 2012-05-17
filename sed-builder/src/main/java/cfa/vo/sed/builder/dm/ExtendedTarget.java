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

package cfa.vo.sed.builder.dm;

import cfa.vo.sedlib.Segment;

/**
 *
 * @author olaurino
 */
public final class ExtendedTarget extends Target {

    public ExtendedTarget(Target target, String publisher) {
        setName(target.getName());
        setRa(target.getRa());
        setDec(target.getDec());
        setPublisher(publisher);
    }

    public ExtendedTarget() {
        
    }

    private String publisher = "UNKNOWN";
    public static final String PROP_PUBLISHER = "publisher";

    /**
     * Get the value of publisher
     *
     * @return the value of publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Set the value of publisher
     *
     * @param publisher new value of publisher
     */
    public void setPublisher(String publisher) {
        String oldPublisher = this.publisher;
        this.publisher = publisher;
        propertyChangeSupport.firePropertyChange(PROP_PUBLISHER, oldPublisher, publisher);
    }

    @Override
    public void addTo(Segment segment) {
        super.addTo(segment);
        segment.createCuration().createPublisher().setValue(publisher);
    }

}
