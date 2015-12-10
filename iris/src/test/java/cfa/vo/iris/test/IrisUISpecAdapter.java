package cfa.vo.iris.test;

import cfa.vo.iris.AbstractIrisApplication;
import cfa.vo.iris.Iris;
import cfa.vo.iris.IrisApplication;
import org.jdesktop.application.Application;
import org.uispec4j.*;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.utils.MainClassTrigger;

/**
 * UISpecAdapter that intercepts the samp hub window
 */
public final class IrisUISpecAdapter implements UISpecAdapter {
    private Window mainWindow;
    private Trigger trigger;
    private Window samphub;

    public IrisUISpecAdapter(String... args) {
        if (trigger == null)
            trigger = new MainClassTrigger(Iris.class, args);
        UISpec4J.setWindowInterceptionTimeLimit(60000);
        mainWindow = WindowInterceptor.run(trigger);
        samphub = WindowInterceptor.run(new Trigger() {
            @Override
            public void run() throws Exception {

            }
        });
        getIrisApp().setTest(true);
    }

    public Window getMainWindow() {
        return mainWindow;
    }

    public Window getSamphub() {
        return samphub;
    }

    public AbstractIrisApplication getIrisApp() {
        return Application.getInstance(AbstractIrisApplication.class);
    }

}
