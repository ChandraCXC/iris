/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.test;

import cfa.vo.iris.ICommandLineInterface;

/**
 *
 * @author olaurino
 */
public class TestBuilder2 extends TestBuilder {

    @Override
    public ICommandLineInterface getCli() {
        return new ICommandLineInterface() {

            @Override
            public String getName() {
                return "tbuilder2";
            }

            @Override
            public void call(String[] args) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        };
    }
}
