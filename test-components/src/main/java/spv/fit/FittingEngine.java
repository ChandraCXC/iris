/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spv.fit;

import java.io.InputStream;

/**
 *
 * @author olaurino
 */
public interface FittingEngine extends Runnable {

    void print(InputStream in);

    void shutdown();

    void start();

}
