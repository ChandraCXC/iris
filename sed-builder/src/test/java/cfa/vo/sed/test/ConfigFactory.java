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

package cfa.vo.sed.test;

import cfa.vo.sed.filters.NativeFileFormat;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.setup.SetupManager;
import cfa.vo.sed.setup.ErrorType;
import cfa.vo.sed.setup.ISetup;
import cfa.vo.sed.setup.validation.AxesValidator;
import cfa.vo.sed.setup.validation.ErrorValidator;
import cfa.vo.sed.setup.validation.IValidator;
import cfa.vo.sed.quantities.IUnit;
import cfa.vo.sed.quantities.SPVYQuantity;
import cfa.vo.sed.quantities.SPVYUnit;
import cfa.vo.sed.quantities.XQuantity;
import cfa.vo.sed.quantities.XUnit;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author olaurino
 */
public class ConfigFactory {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        URL outconfig = ConfigFactory.class.getResource("/test_data/spvquantities.ini");

        List<ISetup> confList = getAllQuantitiesConfigurations();

        SetupManager.write(confList, outconfig);
    }

    public static List<ISetup> getAllQuantitiesConfigurations() throws Exception {
        List<ISetup> confList = new ArrayList();

        for(XQuantity xq : XQuantity.values()) {
            for(IUnit xu : xq.getPossibleUnits()) {
                for(SPVYQuantity yq : SPVYQuantity.values()) {
                    for(IUnit yu : yq.getPossibleUnits()) {
                        SetupBean conf = new SetupBean();

                        IValidator val = new AxesValidator(new ErrorValidator(), true, (SetupBean) conf);
                        val.isConfigurationValid();

                        conf.setErrorType(ErrorType.ConstantValue.name());
                        conf.setConstantErrorValue("invalid");
                        conf.setFileLocation(URLTestConverter.getURL("test:///test_data/3c273.dat").toString());
                        conf.setFormatName("ASCII");
                        conf.setPublisher("");
                        conf.setTargetName("3c273");
                        conf.setTargetRa("187.27791798");
                        conf.setTargetDec("2.05238729");
                        conf.setXAxisColumnNumber(5);
                        conf.setYAxisColumnNumber(6);
                        conf.setXAxisQuantity(xq.name());
                        conf.setXAxisUnit(XUnit.getFromUnitString(xu.getString()).name());
                        conf.setYAxisQuantity(yq.name());
                        conf.setYAxisUnit(SPVYUnit.getFromUnitString(yu.getString()).name());

                        val.isConfigurationValid();

                        confList.add(conf);
                    }
                }
            }
        }
        return confList;
    }

    public static List<ISetup> getAllFormatsConfigurations() throws Exception {
        List<ISetup> confList = new ArrayList();

        Map<String, String> fileMap = new HashMap();
        fileMap.put("dat", NativeFileFormat.ASCIITABLE.getName());
        fileMap.put("csv", NativeFileFormat.CSV.getName());
        fileMap.put("fits", NativeFileFormat.FITS.getName());
        fileMap.put("tst", NativeFileFormat.TST.getName());
        fileMap.put("xml", NativeFileFormat.VOTABLE.getName());

        for(Entry<String, String> entry : fileMap.entrySet()) {

            SetupBean conf = new SetupBean();

            IValidator val = new AxesValidator(new ErrorValidator(), true, (SetupBean) conf);

            conf.setErrorType(ErrorType.ConstantValue.name());
            conf.setConstantErrorValue("2.0");
            conf.setFileLocation(URLTestConverter.getURL("test:///test_data/3c273."+entry.getKey()).toString());
            conf.setFormatName(entry.getValue());
            conf.setPublisher("");
            conf.setTargetName("3c273");
            conf.setTargetRa("187.27791798");
            conf.setTargetDec("2.05238729");
            conf.setXAxisColumnNumber(5);
            conf.setYAxisColumnNumber(6);
            conf.setXAxisQuantity("FREQUENCY");
            conf.setXAxisUnit("HERTZ");
            conf.setYAxisQuantity("FLUX");
            conf.setYAxisUnit("FLUX0");
            confList.add(conf);

            val.isConfigurationValid();

        }
        return confList;
    }

    public static List<ISetup> getAllErrorTypesConfigurations() throws Exception {
        List<ISetup> confList = new ArrayList();

        Map<String, ErrorType> fileMap = new HashMap();
        fileMap.put("symmetricParameter", ErrorType.SymmetricParameter);
        fileMap.put("asymmetricParameter", ErrorType.AsymmetricParameter);
        fileMap.put("symmetricColumn", ErrorType.SymmetricColumn);
        fileMap.put("asymmetricColumn", ErrorType.AsymmetricColumn);

        for(Entry<String, ErrorType> entry : fileMap.entrySet()) {

            SetupBean conf = new SetupBean();

            IValidator val = new AxesValidator(new ErrorValidator(), true, (SetupBean) conf);
            
            switch(entry.getValue()) {
                case SymmetricColumn:
                    conf.setSymmetricErrorColumnNumber(7);
                    break;
                case AsymmetricColumn:
                    conf.setLowerErrorColumnNumber(7);
                    conf.setUpperErrorColumnNumber(7);
                    break;
                case SymmetricParameter:
                    conf.setSymmetricErrorParameter("DataLength");
                    break;
                case AsymmetricParameter:
                    conf.setLowerErrorParameter("DataLength");
                    conf.setUpperErrorParameter("DataLength");
                    break;
            }

            conf.setErrorType(entry.getValue().name());

            conf.setFileLocation(URLTestConverter.getURL("test:///test_data/3c273.xml").toString());
            conf.setFormatName("VOTABLE");
            conf.setPublisher("");
            conf.setTargetName("3c273");
            conf.setTargetRa("187.27791798");
            conf.setTargetDec("2.05238729");
            conf.setXAxisColumnNumber(5);
            conf.setYAxisColumnNumber(6);
            conf.setXAxisQuantity("FREQUENCY");
            conf.setXAxisUnit("HERTZ");
            conf.setYAxisQuantity("FLUX");
            conf.setYAxisUnit("FLUX0");
            confList.add(conf);

            val.isConfigurationValid();

        }
        return confList;
    }

}
