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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.*;

public class DefaultModel implements Model {

    private List<Parameter> pars = new ArrayList<>();

    private Map<String, Parameter> parMap = new HashMap<>();

    private String name;

    private String description;

    private String id;

    public DefaultModel() {
        // comply to javabeans spec, mostly for deserialization
    }

    public DefaultModel(Model model, String id) {
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
                    par.setName(buildParamName(parname));
                }
                addPar(par);
            }
        }
    }

    public DefaultModel(Element modelElement) {
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
        String[] tokens = StringUtils.split(name, ".");
        name = tokens.length > 1 ? tokens[0] : name;
        String id = tokens.length > 1? tokens[1] : null;
        this.name = name;
        setId(id);
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        if (id!=null) {
            this.id = id;
        }
    }

    public Parameter findParameter(String name) throws NoSuchElementException {
        name = buildParamName(name);
        if (!parMap.containsKey(name)) {
            throw new NoSuchElementException("No such parameter "+ name);
        }
        return parMap.get(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(11, 15);
        if(StringUtils.isNotBlank(name))
                builder.append(name);
        if(StringUtils.isNotBlank(description))
                builder.append(description);
        if(!pars.isEmpty())
                builder.append(pars);

        return builder.toHashCode();
    }

    private String buildParamName(String paramName) {
        return getId() + "." + paramName;
    }

    public static String findId(Model m) {
        return findId(m.getName());
    }

    public static String findId(UserModel m) {
        return findId(m.getName());
    }

    public static String toString(CompositeModel model) {
        StringBuilder builder = new StringBuilder();

        builder.append("Model Expression: ").append(model.getName()).append("\n")
                .append("Components:\n");


        if (model.getParts() != null) {
            for (Model m : model.getParts()) {
                builder.append(toString(m));
            }
        }

        return builder.toString();
    }

    private static String toString(Model model) {
        StringBuilder builder = new StringBuilder();

        builder.append("\t").append(model.getName()).append("\n");
        for (Parameter p : model.getPars()) {
            appendParamToString(p, builder);
        }

        return builder.toString();
    }

    private static void appendParamToString(Parameter p, StringBuilder builder) {
        String name = p.getName();
        Double value = p.getVal();
        String frozen = p.getFrozen() == 1 ? " Frozen" : "";

        builder
                .append(String.format("\t\t%26s = %12.5E%s",name, value, frozen))
                .append("\n");
        ;
    }

    private static String findId(String name) {
        String [] tokens = StringUtils.split(name, ".");
        if (tokens.length > 1) {
            return tokens[1];
        }
        return "";
    }

}
