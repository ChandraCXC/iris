package cfa.vo.iris.test;

import org.uispec4j.*;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.utils.MainClassTrigger;

/**
 * Created by olaurino on 10/23/15.
 */
public class IrisUISpecAdapter implements UISpecAdapter {
    private Window mainWindow;
    private Trigger trigger;
    private Window samphub;

    public IrisUISpecAdapter(Class mainClass, String... args) {
        if (trigger == null)
            trigger = new MainClassTrigger(mainClass, args);
        UISpec4J.setWindowInterceptionTimeLimit(60000);
    }

    public Window getMainWindow() {
        if (mainWindow == null) {
            mainWindow = WindowInterceptor.run(trigger);
            samphub = WindowInterceptor.run(new Trigger() {
                @Override
                public void run() throws Exception {

                }
            });
        }
        return mainWindow;
    }

    public Window getSamphub() {
        return samphub;
    }

}
