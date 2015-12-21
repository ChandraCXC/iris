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

package cfa.vo.sherpa;

import cfa.vo.interop.SAMPFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author olaurino
 */
public abstract class AbstractModel implements Model {

    private List<Parameter> pars = new ArrayList();

    private Map<String, Parameter> parMap = new HashMap();

    private String name;

    private String id;

    public AbstractModel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public void addPar(Parameter par) {
        pars.add(par);
        parMap.put(par.getName(), par);
    }

    @Override
    public String getName() {
        return name + "." + id;
    }

    @Override
    public List<Parameter> getPars() {
        return pars;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    protected void addParams(List<String> paramNames) {
        for(String string : paramNames) {
            Parameter p = (Parameter) SAMPFactory.get(Parameter.class);
            String pname = id+"."+string;
            p.setName(pname);
            addPar(p);
        }
    }

    public Parameter getParameter(String name) {
        return parMap.get(name);
    }

    @Override
    public String toString() {
        return getName();
    }

}
