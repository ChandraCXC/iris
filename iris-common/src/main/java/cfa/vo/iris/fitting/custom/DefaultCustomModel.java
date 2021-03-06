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

package cfa.vo.iris.fitting.custom;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.models.UserModel;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class DefaultCustomModel implements CustomModel {
    public static final String PROP_URL = "url";
    public static final String PROP_NAME = "name";
    public static final String PROP_PARNAMES = "parnames";
    public static final String PROP_PARVALS = "parvals";
    public static final String PROP_PARMINS = "parmins";
    public static final String PROP_PARMAXS = "parmaxs";
    public static final String PROP_PARFROZEN = "parfrozen";
    public static final String PROP_FUNCTIONNAME = "functionName";

    private CustomModelType type;
    private URL url;
    private String name;
    private String parnames;
    private String parvals;
    private String parmins;
    private String parmaxs;
    private String parfrozen;
    private String functionName;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public DefaultCustomModel() {
        
    }

    public DefaultCustomModel(String name, String path, CustomModelType type) throws MalformedURLException {
        this.name = name;
        this.url = new URL("file:"+path);
        this.type = type;
    }

    public CustomModelType getType() {
        return type;
    }

    public void setType(CustomModelType type) {
        this.type = type;
    }

    /**
     * Get the value of url
     *
     * @return the value of url
     */
    @Override
    public URL getUrl() {
        return url;
    }

    /**
     * Set the value of url
     *
     * @param url new value of url
     */
    @Override
    public void setUrl(URL url) {
        URL oldUrl = this.url;
        this.url = url;
        propertyChangeSupport.firePropertyChange(PROP_URL, oldUrl, url);
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    @Override
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * Get the value of parnames
     *
     * @return the value of parnames
     */
    @Override
    public String getParnames() {
        return parnames;
    }

    /**
     * Set the value of parnames
     *
     * @param parnames new value of parnames
     */
    @Override
    public void setParnames(String parnames) {
        String oldParnames = this.parnames;
        this.parnames = removeSpaces(parnames);
        propertyChangeSupport.firePropertyChange(PROP_PARNAMES, oldParnames, parnames);
    }

    /**
     * Get the value of parvals
     *
     * @return the value of parvals
     */
    @Override
    public String getParvals() {
        return parvals;
    }

    /**
     * Set the value of parvals
     *
     * @param parvals new value of parvals
     */
    @Override
    public void setParvals(String parvals) {
        String oldParvals = this.parvals;
        this.parvals = removeSpaces(parvals);
        propertyChangeSupport.firePropertyChange(PROP_PARVALS, oldParvals, parvals);
    }

    /**
     * Get the value of parmins
     *
     * @return the value of parmins
     */
    @Override
    public String getParmins() {
        return parmins;
    }

    /**
     * Set the value of parmins
     *
     * @param parmins new value of parmins
     */
    @Override
    public void setParmins(String parmins) {
        String oldParmins = this.parmins;
        this.parmins = removeSpaces(parmins);
        propertyChangeSupport.firePropertyChange(PROP_PARMINS, oldParmins, parmins);
    }

    /**
     * Get the value of parmaxs
     *
     * @return the value of parmaxs
     */
    @Override
    public String getParmaxs() {
        return parmaxs;
    }

    /**
     * Set the value of parmaxs
     *
     * @param parmaxs new value of parmaxs
     */
    @Override
    public void setParmaxs(String parmaxs) {
        String oldParmaxs = this.parmaxs;
        this.parmaxs = removeSpaces(parmaxs);
        propertyChangeSupport.firePropertyChange(PROP_PARMAXS, oldParmaxs, parmaxs);
    }

    /**
     * Get the value of parfrozen
     *
     * @return the value of parfrozen
     */
    @Override
    public String getParfrozen() {
        return parfrozen;
    }

    /**
     * Set the value of parfrozen
     *
     * @param parfrozen new value of parfrozen
     */
    @Override
    public void setParfrozen(String parfrozen) {
        String oldParfrozen = this.parfrozen;
        this.parfrozen = removeSpaces(parfrozen);
        propertyChangeSupport.firePropertyChange(PROP_PARFROZEN, oldParfrozen, parfrozen);
    }

    /**
     * Get the value of functionName
     *
     * @return the value of functionName
     */
    @Override
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Set the value of functionName
     *
     * @param functionName new value of functionName
     */
    @Override
    public void setFunctionName(String functionName) {
        String oldFunctionName = this.functionName;
        this.functionName = functionName;
        propertyChangeSupport.firePropertyChange(PROP_FUNCTIONNAME, oldFunctionName, functionName);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public UserModel makeUserModel(String id) {
        UserModel m = SAMPFactory.get(UserModel.class);;
        m.setName(composeType(type,id));
        m.setFile(this.getUrl().getPath());
        m.setFunction(this.getFunctionName());
        return m;
    }

    public Model makeModel(String id) {
        Model m = SAMPFactory.get(Model.class);
        m.setName(composeType(type, id));
        String[] parNames = makeArray(this.getParnames(), String.class);
        Double[] parVals = makeArray(this.getParvals(), Double.class);
        Double[] parMins = makeArray(this.getParmins(), Double.class);
        Double[] parMaxs = makeArray(this.getParmaxs(), Double.class);
        Boolean[] parFrozens = makeArray(this.getParfrozen(), Boolean.class);
        for (int i=0; i<parNames.length; i++) {
            try {
                Parameter p = SAMPFactory.get(Parameter.class);
                p.setName(compose(id,parNames[i]));
                p.setVal(parVals[i]);
                p.setMin(parMins[i]);
                p.setMax(parMaxs[i]);
                p.setFrozen(parFrozens[i] ? 1 : 0);
                m.addPar(p);
            } catch(IndexOutOfBoundsException ex) {
                throw new IllegalStateException("Array Shapes Mismatch", ex);
            }
        }
        return m;
    }

    @Override
    public String toString() {
        return name;
    }

    @SuppressWarnings("unchecked")
    static <T>T[] makeArray(String expression, Class<T> tClass) {
        Pattern p = Pattern.compile(",");
        String[] tokens = expression.split(p.pattern());
        int length = tokens.length;
        T[] retVal = (T[]) Array.newInstance(tClass, length);
        try {
            Constructor<T> ctor = tClass.getConstructor(String.class);
            for (int i=0; i<length; i++) {
                String valS = tokens[i];
                try {
                    T val = ctor.newInstance(valS);
                    retVal[i] = val;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    throw new IllegalArgumentException(String.format("Error instantiating value %s as %s", valS, tClass), ex);
                }
            }
        } catch(NoSuchMethodException ex) {
            throw new IllegalArgumentException("Only classes with String constructor accepted", ex);
        }
        return retVal;
    }

    private String compose(String... comps) {
        return StringUtils.join(comps, ".");
    }

    private String composeType(CustomModelType type, String id) {
        return compose(type.name().toLowerCase(),id);
    }

    private String removeSpaces(String input) {
        return StringUtils.deleteWhitespace(input);
    }

}
