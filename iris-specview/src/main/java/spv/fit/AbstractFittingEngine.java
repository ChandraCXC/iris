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
public abstract class AbstractFittingEngine implements FittingEngine {

    public AbstractFittingEngine() {
    }

    @Override
    public abstract void print(InputStream in);

    @Override
    public abstract void run();

    @Override
    public abstract void shutdown();

    @Override
    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

}
