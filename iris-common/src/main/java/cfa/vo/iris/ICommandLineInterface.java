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
