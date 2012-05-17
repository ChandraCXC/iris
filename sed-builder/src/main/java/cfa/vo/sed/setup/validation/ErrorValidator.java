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

package cfa.vo.sed.setup.validation;

import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.setup.ErrorType;

/**
 *
 * @author olaurino
 */
public class ErrorValidator extends AbstractValidator {

    public ErrorValidator() {
        super(null, false, null);
    }

    public ErrorValidator(IValidator validator, boolean register, SetupBean conf) {
        super(validator, register, conf);
    }

    @Override
    public String[] getRegisteredProperties() {
        return new String[] {
            SetupBean.PROP_ERRORTYPE,
            SetupBean.PROP_LOWERERRORCOLUMNNUMBER,
            SetupBean.PROP_CONSTANTERRORVALUE,
            SetupBean.PROP_LOWERERRORPARAMETER,
            SetupBean.PROP_SYMMETRICERRORCOLUMNNUMBER,
            SetupBean.PROP_UPPERERRORCOLUMNNUMBER,
            SetupBean.PROP_UPPERERRORPARAMETER,
            SetupBean.PROP_SYMMETRICERRORPARAMETER,
        };
    }

    @Override
    public void validateConfiguration() {

        SetupBean conf = getConf();

        ErrorType errtype = null;
        try {
            errtype = ErrorType.valueOf(conf.getErrorType());
        } catch (Exception ex) {
            assertBoolean("Please choose a Y Error type.", false);
            return;
        }



        switch(errtype) {
            case ConstantValue:
                assertBoolean("The ErrorType is ConstantValue but no value has been provided.",
                        conf.getConstantErrorValue()!=null);
                try {
                    Double.valueOf(conf.getConstantErrorValue());
                } catch (Exception ex) {
                    assertBoolean("Invalid ConstantErrorValue", false);
                }
                break;
            case SymmetricColumn:
                assertBoolean("The ErrorType is SymmetricColumn but no column has been selected.",
                        conf.getSymmetricErrorColumnNumber()!=null);
                if(conf.getSymmetricErrorColumnNumber()!=null)
                    assertBoolean("The selected column for YError contains strings!", conf.getSymmetricErrorColumnNumber() >= 0);
                break;
            case SymmetricParameter:
                assertBoolean("The selected parameter for YError contains strings!", !conf.getSymmetricErrorParameter().equals("INVALID"));
                assertBoolean("The ErrorType is SymmetricParameter but no parameter has been selected.",
                        conf.getSymmetricErrorParameter()!=null);
                break;
            case AsymmetricColumn:
                assertBoolean("The ErrorType is AsymmetricColumn but no Lower Error column has been selected.",
                        conf.getLowerErrorColumnNumber()!=null);
                assertBoolean("The ErrorType is AsymmetricColumn but no Upper Error column has been selected.",
                        conf.getUpperErrorColumnNumber()!=null);
                break;
            case AsymmetricParameter:
                assertBoolean("The ErrorType is AsymmetricParameter but no Lower Error parameter has been selected.",
                        conf.getLowerErrorParameter()!=null);
                assertBoolean("The ErrorType is AsymmetricParameter but no Upper Error parameter has been selected.",
                        conf.getUpperErrorParameter()!=null);
                break;
        }
    }

}
