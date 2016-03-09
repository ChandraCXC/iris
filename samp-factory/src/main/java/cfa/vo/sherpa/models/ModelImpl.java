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

package cfa.vo.sherpa.models;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.*;

public class ModelImpl implements Model {

    private List<Parameter> pars = new ArrayList();

    private Map<String, Parameter> parMap = new HashMap();

    private String name;

    private String description;

    private String id;

    public ModelImpl(String name, String id) {
        this.name = name;
        this.id = id;
        this.description = "";
    }

    public ModelImpl(Model model, String id) {
        this.name = model.getName();
        this.description = model.getDescription();
        this.id = id;
        if (model.getPars() != null) {
            for (Parameter par : model.getPars()) {
                par = new ParameterImpl(par);
                if (this.id != null) {
                    String parname = par.getName();
                    if (parname.contains(".")) {
                        parname = parname.split(".")[1];
                    }
                    par.setName(this.id + "." + parname);
                }
                addPar(par);
            }
        }
    }

    public ModelImpl(Element modelElement) {
        String name = modelElement.getAttribute("name");
        String description = modelElement.getAttribute("description");
        this.name = name;
        this.description = description;
        NodeList parameters = modelElement.getElementsByTagName("parameter");
        for (int j=0; j<parameters.getLength(); j++) {
            Element parElem = (Element) parameters.item(j);
            Parameter par = new ParameterImpl(parElem);
            addPar(par);
        }
    }

    @Override
    public final void addPar(Parameter par) {
        pars.add(par);
        parMap.put(par.getName(), par);
    }

    @Override
    public String getName() {
        return name + (id != null ? "." + id : "");
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<Parameter> getPars() {
        return pars;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public Parameter getParameter(String name) throws NoSuchElementException {
        if (!parMap.containsKey(name)) {
            throw new NoSuchElementException("No such parameter "+ name);
        }
        return parMap.get(name);
    }

    @Override
    public String toString() {
        return getName();
    }

}
