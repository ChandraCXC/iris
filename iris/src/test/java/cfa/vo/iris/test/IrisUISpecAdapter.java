package cfa.vo.iris.test;

import cfa.vo.iris.Iris;
import org.jdesktop.application.Application;
import org.uispec4j.*;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.utils.MainClassTrigger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UISpecAdapter that intercepts the samp hub window
 */
public final class IrisUISpecAdapter implements UISpecAdapter {
    private Window mainWindow;
    private Window samphub;

    private Logger logger = Logger.getLogger(IrisUISpecAdapter.class.getName());

    public IrisUISpecAdapter(boolean withSampHub) {
        Trigger trigger = new MainClassTrigger(Iris.class, "--debug");
        logger.log(Level.INFO, "setting timeout to 30000 millis");
        UISpec4J.setWindowInterceptionTimeLimit(30000);
        logger.log(Level.INFO, "intercepting main application window");
        mainWindow = WindowInterceptor.run(trigger);
        if (withSampHub) {
            logger.log(Level.INFO, "intercepting hub window");
            samphub = WindowInterceptor.run(new Trigger() {
                @Override
                public void run() throws Exception {

                }
            });
        } else {
            logger.log(Level.INFO, "skipping hub window interception");
        }
        getIrisApp().setTest(true);
    }

    public Window getMainWindow() {
        return mainWindow;
    }

    public Window getSamphub() {
        return samphub;
    }

    public Iris getIrisApp() {
        return Application.getInstance(Iris.class);
    }

}
