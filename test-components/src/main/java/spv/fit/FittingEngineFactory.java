/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spv.fit;

import java.util.HashMap;

/**
 *
 * @author olaurino
 */
public class FittingEngineFactory {
    private Engines engines = new Engines();


    public FittingEngine get(String type) throws NoSuchEngineException {
        if(engines.containsKey(type))
            return engines.get(type);

        throw new NoSuchEngineException();
    }

    private class Engines extends HashMap<String, FittingEngine> {
        public Engines() {
            super();
            put("sherpa", new StartSherpa());
            put("test", new MockupSherpa());
        }
    }
}
