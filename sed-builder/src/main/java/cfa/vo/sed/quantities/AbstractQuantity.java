/**
 * Copyright (C) Smithsonian Astrophysical Observatory
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

package cfa.vo.sed.quantities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public abstract class AbstractQuantity implements IQuantity {

    private List<IUnit> unitList = new ArrayList();
    protected String name;
    protected String description;
    protected String ucd;

    protected AbstractQuantity(String name, String description, String ucd) {

        this.name = name;
        this.description = description;
        this.ucd = ucd;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public List<IUnit> getPossibleUnits() {
        return unitList;
    }

    public String getUCD() {
        return ucd;
    }

    public void add(IUnit unit) {
        unitList.add(unit);
    }

}
