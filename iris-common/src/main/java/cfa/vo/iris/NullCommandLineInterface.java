/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

/**
 *
 * @author olaurino
 */
public class NullCommandLineInterface implements ICommandLineInterface {

    private String name;

    public NullCommandLineInterface(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void call(String[] args) {
        
    }

}
