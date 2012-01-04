/**
 * Copyright (C) 2011 Smithsonian Astrophysical Observatory
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

package cfa.vo.sed.setup;

import cfa.vo.sed.quantities.SPVYUnit;
import cfa.vo.sed.quantities.XUnit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 *
 * @author olaurino
 */
public class SetupBean implements ISetup {

    private Integer positionInFile = 0;
    public static final String PROP_POSITIONINFILE = "positionInFile";

    /**
     * Get the value of positionInFile
     *
     * @return the value of positionInFile
     */
    public Integer getPositionInFile() {
        return positionInFile;
    }

    /**
     * Set the value of positionInFile
     *
     * @param positionInFile new value of positionInFile
     */
    public void setPositionInFile(Integer positionInFile) {
        Integer oldPositionInFile = this.positionInFile;
        this.positionInFile = positionInFile;
        propertyChangeSupport.firePropertyChange(PROP_POSITIONINFILE, oldPositionInFile, positionInFile);
    }


    private String errorType;
    public static final String PROP_ERRORTYPE = "errorType";

    /**
     * Get the value of errorType
     *
     * @return the value of errorType
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * Set the value of errorType
     *
     * @param errorType new value of errorType
     */
    public void setErrorType(String errorType) {
        String oldErrorType = this.errorType;
        this.errorType = errorType;
        propertyChangeSupport.firePropertyChange(PROP_ERRORTYPE, oldErrorType, errorType);
    }

    private String upperErrorParameter;
    public static final String PROP_UPPERERRORPARAMETER = "upperErrorParameter";

    /**
     * Get the value of upperErrorParameter
     *
     * @return the value of upperErrorParameter
     */
    public String getUpperErrorParameter() {
        return upperErrorParameter;
    }

    /**
     * Set the value of upperErrorParameter
     *
     * @param upperErrorParameter new value of upperErrorParameter
     */
    public void setUpperErrorParameter(String upperErrorParameter) {
        String oldUpperErrorParameter = this.upperErrorParameter;
        this.upperErrorParameter = upperErrorParameter;
        propertyChangeSupport.firePropertyChange(PROP_UPPERERRORPARAMETER, oldUpperErrorParameter, upperErrorParameter);
    }

    private String lowerErrorParameter;
    public static final String PROP_LOWERERRORPARAMETER = "lowerErrorParameter";

    /**
     * Get the value of lowerErrorParameter
     *
     * @return the value of lowerErrorParameter
     */
    public String getLowerErrorParameter() {
        return lowerErrorParameter;
    }

    /**
     * Set the value of lowerErrorParameter
     *
     * @param lowerErrorParameter new value of lowerErrorParameter
     */
    public void setLowerErrorParameter(String lowerErrorParameter) {
        String oldLowerErrorParameter = this.lowerErrorParameter;
        this.lowerErrorParameter = lowerErrorParameter;
        propertyChangeSupport.firePropertyChange(PROP_LOWERERRORPARAMETER, oldLowerErrorParameter, lowerErrorParameter);
    }

    private String symmetricErrorParameter;
    public static final String PROP_SYMMETRICERRORPARAMETER = "symmetricErrorParameter";

    /**
     * Get the value of symmetricErrorParameter
     *
     * @return the value of symmetricErrorParameter
     */
    public String getSymmetricErrorParameter() {
        return symmetricErrorParameter;
    }

    /**
     * Set the value of symmetricErrorParameter
     *
     * @param symmetricErrorParameter new value of symmetricErrorParameter
     */
    public void setSymmetricErrorParameter(String symmetricErrorParameter) {
        String oldSymmetricErrorParameter = this.symmetricErrorParameter;
        this.symmetricErrorParameter = symmetricErrorParameter;
        propertyChangeSupport.firePropertyChange(PROP_SYMMETRICERRORPARAMETER, oldSymmetricErrorParameter, symmetricErrorParameter);
    }

    private String xAxisQuantity;
    public static final String PROP_XAXISQUANTITY = "xAxisQuantity";

    /**
     * Get the value of xAxisQuantity
     *
     * @return the value of xAxisQuantity
     */
    public String getXAxisQuantity() {
        return xAxisQuantity;
    }

    /**
     * Set the value of xAxisQuantity
     *
     * @param xAxisQuantity new value of xAxisQuantity
     */
    public void setXAxisQuantity(String xAxisQuantity) {
        String oldXAxisQuantity = this.xAxisQuantity;
        this.xAxisQuantity = xAxisQuantity;
        propertyChangeSupport.firePropertyChange(PROP_XAXISQUANTITY, oldXAxisQuantity, xAxisQuantity);
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
        publisher = publisher==null ? "" : publisher;
        this.publisher = publisher.isEmpty()? "UNKNOWN" : publisher;
        propertyChangeSupport.firePropertyChange(PROP_PUBLISHER, oldPublisher, publisher);
    }

    private String constantErrorValue;
    public static final String PROP_CONSTANTERRORVALUE = "constantErrorValue";

    /**
     * Get the value of constantErrorValue
     *
     * @return the value of constantErrorValue
     */
    public String getConstantErrorValue() {
        return constantErrorValue;
    }

    /**
     * Set the value of constantErrorValue
     *
     * @param constantErrorValue new value of constantErrorValue
     */
    public void setConstantErrorValue(String constantErrorValue) {
        NumberFormat nf = NumberFormat.getInstance();
        try {
            nf.parse(constantErrorValue);
        } catch (Exception ex) {
            constantErrorValue = null;
        }
        String oldConstantErrorValue = this.constantErrorValue;
        this.constantErrorValue = constantErrorValue;
        propertyChangeSupport.firePropertyChange(PROP_CONSTANTERRORVALUE, oldConstantErrorValue, constantErrorValue);
    }

    private Integer upperErrorColumnNumber;
    public static final String PROP_UPPERERRORCOLUMNNUMBER = "upperErrorColumnNumber";

    /**
     * Get the value of upperErrorColumnNumber
     *
     * @return the value of upperErrorColumnNumber
     */
    public Integer getUpperErrorColumnNumber() {
        return upperErrorColumnNumber;
    }

    /**
     * Set the value of upperErrorColumnNumber
     *
     * @param upperErrorColumnNumber new value of upperErrorColumnNumber
     */
    public void setUpperErrorColumnNumber(Integer upperErrorColumnNumber) {
        Integer oldUpperErrorColumnNumber = this.upperErrorColumnNumber;
        this.upperErrorColumnNumber = upperErrorColumnNumber;
        propertyChangeSupport.firePropertyChange(PROP_UPPERERRORCOLUMNNUMBER, oldUpperErrorColumnNumber, upperErrorColumnNumber);
    }

    private Integer lowerErrorColumnNumber;
    public static final String PROP_LOWERERRORCOLUMNNUMBER = "lowerErrorColumnNumber";

    /**
     * Get the value of lowerErrorColumnNumber
     *
     * @return the value of lowerErrorColumnNumber
     */
    public Integer getLowerErrorColumnNumber() {
        return lowerErrorColumnNumber;
    }

    /**
     * Set the value of lowerErrorColumnNumber
     *
     * @param lowerErrorColumnNumber new value of lowerErrorColumnNumber
     */
    public void setLowerErrorColumnNumber(Integer lowerErrorColumnNumber) {
        Integer oldLowerErrorColumnNumber = this.lowerErrorColumnNumber;
        this.lowerErrorColumnNumber = lowerErrorColumnNumber;
        propertyChangeSupport.firePropertyChange(PROP_LOWERERRORCOLUMNNUMBER, oldLowerErrorColumnNumber, lowerErrorColumnNumber);
    }

    private Integer symmetricErrorColumnNumber;
    public static final String PROP_SYMMETRICERRORCOLUMNNUMBER = "symmetricErrorColumnNumber";

    /**
     * Get the value of symmetricErrorColumnNumber
     *
     * @return the value of symmetricErrorColumnNumber
     */
    public Integer getSymmetricErrorColumnNumber() {
        return symmetricErrorColumnNumber;
    }

    /**
     * Set the value of symmetricErrorColumnNumber
     *
     * @param symmetricErrorColumnNumber new value of symmetricErrorColumnNumber
     */
    public void setSymmetricErrorColumnNumber(Integer symmetricErrorColumnNumber) {
        Integer oldSymmetricErrorColumnNumber = this.symmetricErrorColumnNumber;
        this.symmetricErrorColumnNumber = symmetricErrorColumnNumber;
        propertyChangeSupport.firePropertyChange(PROP_SYMMETRICERRORCOLUMNNUMBER, oldSymmetricErrorColumnNumber, symmetricErrorColumnNumber);
    }

    private String targetDec;
    public static final String PROP_TARGETDEC = "targetDec";

    /**
     * Get the value of targetDec
     *
     * @return the value of targetDec
     */
    public String getTargetDec() {
        return targetDec;
    }

    /**
     * Set the value of targetDec
     *
     * @param targetDec new value of targetDec
     */
    public void setTargetDec(String targetDec) {
        String oldTargetDec = this.targetDec;
        this.targetDec = targetDec;
        propertyChangeSupport.firePropertyChange(PROP_TARGETDEC, oldTargetDec, targetDec);
    }

    private String targetRa;
    public static final String PROP_TARGETRA = "targetRa";

    /**
     * Get the value of targetRa
     *
     * @return the value of targetRa
     */
    public String getTargetRa() {
        return targetRa;
    }

    /**
     * Set the value of targetRa
     *
     * @param targetRa new value of targetRa
     */
    public void setTargetRa(String targetRa) {
        String oldTargetRa = this.targetRa;
        this.targetRa = targetRa;
        propertyChangeSupport.firePropertyChange(PROP_TARGETRA, oldTargetRa, targetRa);
    }

    private String targetName;
    public static final String PROP_TARGETNAME = "targetName";

    /**
     * Get the value of targetName
     *
     * @return the value of targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Set the value of targetName
     *
     * @param targetName new value of targetName
     */
    public void setTargetName(String targetName) {
        String oldTargetName = this.targetName;
        this.targetName = targetName;
        propertyChangeSupport.firePropertyChange(PROP_TARGETNAME, oldTargetName, targetName);
    }

    private String yAxisUnit;
    public static final String PROP_YAXISUNIT = "yAxisUnit";

    /**
     * Get the value of yAxisUnit
     *
     * @return the value of yAxisUnit
     */
    public String getYAxisUnit() {
        return yAxisUnit;
    }

    /**
     * Set the value of yAxisUnit
     *
     * @param yAxisUnit new value of yAxisUnit
     */
    public void setYAxisUnit(String yAxisUnit) {
        String oldYAxisUnit = this.yAxisUnit;
        SPVYUnit[] values = SPVYUnit.values();
        for(SPVYUnit value : values) {
            if(value.getString().equals(yAxisUnit) || value.name().equals(yAxisUnit))
            this.yAxisUnit = value.name();
        }

        propertyChangeSupport.firePropertyChange(PROP_YAXISUNIT, oldYAxisUnit, yAxisUnit);
    }

    private String yAxisQuantity;
    public static final String PROP_YAXISQUANTITY = "yAxisQuantity";

    /**
     * Get the value of yAxisQuantity
     *
     * @return the value of yAxisQuantity
     */
    public String getYAxisQuantity() {
        return yAxisQuantity;
    }

    /**
     * Set the value of yAxisQuantity
     *
     * @param yAxisQuantity new value of yAxisQuantity
     */
    public void setYAxisQuantity(String yAxisQuantity) {
        String oldYAxisQuantity = this.yAxisQuantity;
        this.yAxisQuantity = yAxisQuantity;
        propertyChangeSupport.firePropertyChange(PROP_YAXISQUANTITY, oldYAxisQuantity, yAxisQuantity);
    }

    private Integer yAxisColumnNumber;
    public static final String PROP_YAXISCOLUMNNUMBER = "yAxisColumnNumber";

    /**
     * Get the value of yAxisColumnNumber
     *
     * @return the value of yAxisColumnNumber
     */
    public Integer getYAxisColumnNumber() {
        return yAxisColumnNumber;
    }

    /**
     * Set the value of yAxisColumnNumber
     *
     * @param yAxisColumnNumber new value of yAxisColumnNumber
     */
    public void setYAxisColumnNumber(Integer yAxisColumnNumber) {
        Integer oldYAxisColumnNumber = this.yAxisColumnNumber;
        this.yAxisColumnNumber = yAxisColumnNumber;
        propertyChangeSupport.firePropertyChange(PROP_YAXISCOLUMNNUMBER, oldYAxisColumnNumber, yAxisColumnNumber);
    }

    private String xAxisUnit;
    public static final String PROP_XAXISUNIT = "xAxisUnit";

    /**
     * Get the value of xAxisUnitString
     *
     * @return the value of xAxisUnitString
     */
    public String getXAxisUnit() {
        return xAxisUnit;
    }

    /**
     * Set the value of xAxisUnitString
     *
     * @param xAxisUnitString new value of xAxisUnitString
     */
    public void setXAxisUnit(String xAxisUnit) {
        String oldXAxisUnit = this.xAxisUnit;
        XUnit[] values = XUnit.values();
        for(XUnit value : values) {
            if(value.getString().equals(xAxisUnit) || value.name().equals(xAxisUnit))
                this.xAxisUnit = value.name();
        }
        
        propertyChangeSupport.firePropertyChange(PROP_XAXISUNIT, oldXAxisUnit, xAxisUnit);
    }

    private Integer xAxisColumnNumber;
    public static final String PROP_XAXISCOLUMNNUMBER = "xAxisColumnNumber";

    /**
     * Get the value of xAxisColumnNumber
     *
     * @return the value of xAxisColumnNumber
     */
    public Integer getXAxisColumnNumber() {
        return xAxisColumnNumber;
    }

    /**
     * Set the value of xAxisColumnNumber
     *
     * @param xAxisColumnNumber new value of xAxisColumnNumber
     */
    public void setXAxisColumnNumber(Integer xAxisColumnNumber) {
        Integer oldXAxisColumnNumber = this.xAxisColumnNumber;
        this.xAxisColumnNumber = xAxisColumnNumber;
        propertyChangeSupport.firePropertyChange(PROP_XAXISCOLUMNNUMBER, oldXAxisColumnNumber, xAxisColumnNumber);
    }

    private Integer tableIndex;
    public static final String PROP_TABLEINDEX = "tableIndex";

    /**
     * Get the value of tableIndex
     *
     * @return the value of tableIndex
     */
    public Integer getTableIndex() {
        return tableIndex;
    }

    /**
     * Set the value of tableIndex
     *
     * @param tableIndex new value of tableIndex
     */
    public void setTableIndex(Integer tableIndex) {
        Integer oldTableIndex = this.tableIndex;
        this.tableIndex = tableIndex;
        propertyChangeSupport.firePropertyChange(PROP_TABLEINDEX, oldTableIndex, tableIndex);
    }

    private String pluginURL;
    public static final String PROP_PLUGINURL = "pluginURL";

    /**
     * Get the value of pluginURL
     *
     * @return the value of pluginURL
     */
    public String getPluginURL() {
        return pluginURL;
    }

    /**
     * Set the value of pluginURL
     *
     * @param pluginURL new value of pluginURL
     */
    public void setPluginURL(String pluginURL) {
        String oldPluginURL = this.pluginURL;
        this.pluginURL = pluginURL;
        propertyChangeSupport.firePropertyChange(PROP_PLUGINURL, oldPluginURL, pluginURL);
    }

    private String formatName;
    public static final String PROP_FORMATNAME = "formatName";

    /**
     * Get the value of formatName
     *
     * @return the value of formatName
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * Set the value of formatName
     *
     * @param formatName new value of formatName
     */
    public void setFormatName(String formatName) {
        String oldFormatName = this.formatName;
        this.formatName = formatName.toUpperCase().replaceAll(" ", "");
        propertyChangeSupport.firePropertyChange(PROP_FORMATNAME, oldFormatName, formatName);
    }

    private String fileLocation;
    public static final String PROP_FILELOCATION = "fileLocation";

    /**
     * Get the value of fileLocation
     *
     * @return the value of fileLocation
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Set the value of fileLocation
     *
     * @param fileLocation new value of fileLocation
     */
    public void setFileLocation(String fileLocation) {
        String oldFileLocation = this.fileLocation;
        this.fileLocation = fileLocation;
        propertyChangeSupport.firePropertyChange(PROP_FILELOCATION, oldFileLocation, fileLocation);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param property
     * @param listener
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public static ISetup copy(ISetup conf) {
        SetupBean object = new SetupBean();
        for(Method m : conf.getClass().getMethods()) {
            if(m.getName().startsWith("get")) {
                try {
                    String setName = m.getName().replaceFirst("get", "set");
                    Method set = SetupBean.class.getMethod(setName, m.getReturnType());
                    Object res = m.invoke(conf);
                    set.invoke(object, res);
                } catch (Exception ex) {
                    
                }
            }
        }
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SetupBean other = (SetupBean) obj;
        return this.hashCode()==other.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.errorType != null ? this.errorType.hashCode() : 0);
        hash = 47 * hash + (this.upperErrorParameter != null ? this.upperErrorParameter.hashCode() : 0);
        hash = 47 * hash + (this.lowerErrorParameter != null ? this.lowerErrorParameter.hashCode() : 0);
        hash = 47 * hash + (this.symmetricErrorParameter != null ? this.symmetricErrorParameter.hashCode() : 0);
        hash = 47 * hash + (this.xAxisQuantity != null ? this.xAxisQuantity.hashCode() : 0);
        hash = 47 * hash + (this.publisher != null ? this.publisher.hashCode() : 0);
        hash = 47 * hash + (this.constantErrorValue != null ? this.constantErrorValue.hashCode() : 0);
        hash = 47 * hash + (this.upperErrorColumnNumber != null ? this.upperErrorColumnNumber.hashCode() : 0);
        hash = 47 * hash + (this.lowerErrorColumnNumber != null ? this.lowerErrorColumnNumber.hashCode() : 0);
        hash = 47 * hash + (this.symmetricErrorColumnNumber != null ? this.symmetricErrorColumnNumber.hashCode() : 0);
        hash = 47 * hash + (this.targetDec != null ? this.targetDec.hashCode() : 0);
        hash = 47 * hash + (this.targetRa != null ? this.targetRa.hashCode() : 0);
        hash = 47 * hash + (this.targetName != null ? this.targetName.hashCode() : 0);
        hash = 47 * hash + (this.yAxisUnit != null ? this.yAxisUnit.hashCode() : 0);
        hash = 47 * hash + (this.yAxisQuantity != null ? this.yAxisQuantity.hashCode() : 0);
        hash = 47 * hash + (this.yAxisColumnNumber != null ? this.yAxisColumnNumber.hashCode() : 0);
        hash = 47 * hash + (this.xAxisUnit != null ? this.xAxisUnit.hashCode() : 0);
        hash = 47 * hash + (this.xAxisColumnNumber != null ? this.xAxisColumnNumber.hashCode() : 0);
        hash = 47 * hash + (this.tableIndex != null ? this.tableIndex.hashCode() : 0);
        hash = 47 * hash + (this.pluginURL != null ? this.pluginURL.hashCode() : 0);
        hash = 47 * hash + (this.formatName != null ? this.formatName.hashCode() : 0);
        hash = 47 * hash + (this.fileLocation != null ? this.fileLocation.hashCode() : 0);
        return hash;
    }



}
