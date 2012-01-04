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

package cfa.vo.sed.setup.validation;

import cfa.vo.sed.quantities.SPVYQuantity;
import cfa.vo.sed.quantities.SPVYUnit;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.quantities.XQuantity;
import cfa.vo.sed.quantities.XUnit;

/**
 *
 * @author olaurino
 */
public class AxesValidator extends AbstractValidator {

    public AxesValidator() {
        super(null, false, null);
    }

    public AxesValidator(IValidator validator, boolean register, SetupBean conf) {
        super(validator, register, conf);
    }

    public final String[] getRegisteredProperties() {
        return new String[] {
            SetupBean.PROP_XAXISCOLUMNNUMBER,
            SetupBean.PROP_XAXISQUANTITY,
            SetupBean.PROP_XAXISUNIT,
            SetupBean.PROP_YAXISCOLUMNNUMBER,
            SetupBean.PROP_YAXISQUANTITY,
            SetupBean.PROP_YAXISUNIT
        };
    }

    public void validateConfiguration() {

        SetupBean conf = getConf();

        boolean xnonnull = conf.getXAxisColumnNumber() != null;
        boolean ynonnull = conf.getYAxisColumnNumber() != null;

        assertBoolean("X Axis column number is null", xnonnull);
        assertBoolean("Y Axis column number is null", ynonnull);

        if(xnonnull && ynonnull) {
            assertBoolean("The selected column for X Axis contains strings!", conf.getXAxisColumnNumber() >= 0);
            assertBoolean("The selected column for Y Axis contains strings!", conf.getYAxisColumnNumber() >= 0);
        }

        try {
            XQuantity q = XQuantity.valueOf(conf.getXAxisQuantity());
            try {
                XUnit u = XUnit.valueOf(conf.getXAxisUnit());
                if(!q.getPossibleUnits().contains(u)) {
                    assertBoolean(conf.getXAxisUnit()+" is not a valid unit for "+q, false);
                }
            } catch(Exception ex) {
                assertBoolean("Non-existent or invalid X Axis Unit", false);
            }
        } catch (Exception ex) {
            assertBoolean("Please enter a valid quantity for X values (current quantity: "+conf.getXAxisQuantity()+")", false);
        }

        try {
            SPVYQuantity q = SPVYQuantity.valueOf(conf.getYAxisQuantity());
            try {
                SPVYUnit u = SPVYUnit.valueOf(conf.getYAxisUnit());
                if(!q.getPossibleUnits().contains(u)) {
                    assertBoolean(conf.getYAxisUnit()+" is not a valid unit for "+q, false);
                }
            } catch(Exception ex) {
                assertBoolean("Non-existent or invalid Y Axis Unit", false);
            }
        } catch (Exception ex) {
            assertBoolean("Please enter a valid quantity for Y values (current quantity: "+conf.getYAxisQuantity()+")", false);
        }

    }


 
}


