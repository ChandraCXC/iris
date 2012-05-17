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

package cfa.vo.iris;

/**
 * A simple interface for providing CLI access in an extensible, pluggable way
 * @author olaurino
 */
public interface ICommandLineInterface {
    /**
     * The name that has to be associated with the implementing component.
     * When the calling application parses the command line, it will interpret the
     * first argument as the component to which the command has to be relayed, using this string
     * as a key.
     *
     * @return The compact name that identifies this CLI
     */
    String getName();
    /**
     * Callback that gets called when a command line is parsed and associated to the implementing component.
     *
     * @param args The command line arguments.
     */
    void call(String[] args);
}
