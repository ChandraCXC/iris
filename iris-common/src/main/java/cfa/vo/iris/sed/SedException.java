/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.sed;

/**
 *
 * @author olaurino
 */
public class SedException extends Exception {

    /**
     * Creates a new instance of <code>SedException</code> without detail message.
     */
    public SedException() {
    }

    public SedException(Exception ex) {
        super(ex);
    }


    /**
     * Constructs an instance of <code>SedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SedException(String msg) {
        super(msg);
    }
}
