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

package cfa.vo.sed.setup;

/**
 *
 * @author olaurino
 */
public interface ISetup extends Cloneable{

    public String getFileLocation();

    public Integer getPositionInFile();

    public String getFormatName();

    public Integer getTableIndex();

    public Integer getXAxisColumnNumber();

    public String getXAxisUnit();

    public String getXAxisQuantity();

    public Integer getYAxisColumnNumber();

    public String getYAxisQuantity();

    public String getYAxisUnit();

    public String getTargetName();

    // can be represented by strings (HH:MM:SS.SS notation)
    public String getTargetRa();

    public String getTargetDec();

    public String getPublisher();

    public String getErrorType();

    public Integer getSymmetricErrorColumnNumber();

    public Integer getLowerErrorColumnNumber();

    public Integer getUpperErrorColumnNumber();

    public String getSymmetricErrorParameter();

    public String getLowerErrorParameter();

    public String getUpperErrorParameter();

    public String getConstantErrorValue();

    @Override
    public boolean equals(Object other);

}
