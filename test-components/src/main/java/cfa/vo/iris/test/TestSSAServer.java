/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.test;

import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.AbstractIrisApplication;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.common.ValidationError;
import cfa.vo.sedlib.common.ValidationErrorEnum;
import cfa.vo.sedlib.io.SedFormat;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.AbstractMessageHandler;
import org.astrogrid.samp.client.HubConnection;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class TestSSAServer implements IrisComponent {

    private SedlibSedManager sedManager;
    private JFrame rootFrame;

    private static SedSAMPController c;

    public static SedSAMPController getController() {
        return c;
    }

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        sedManager = (SedlibSedManager) workspace.getSedManager();
        rootFrame = workspace.getRootFrame();
        c = new SedSAMPController(getName(), getDescription(), ((AbstractIrisApplication)TestBuilder.getApplication()).getSAMPIcon().toString());
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Component initialized");
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Test SSA server";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("tssa");
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new ArrayList();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        List l = new ArrayList();
//        l.add(new SSAHandler());
        return l;
    }

    @Override
    public void shutdown() {
        
    }

    @Override
    public void initCli(IrisApplication app) {
        
    }

    private class SSAHandler extends AbstractMessageHandler {

        public SSAHandler() {
            super(new String[]{"spectrum.load.ssa-generic",
                                });
        }

        @Override
        public Map processCall(HubConnection hc, String string, Message msg) throws MalformedURLException {
            String formatName = (String) ((Map)msg.getParam("meta")).get("Access.Format");
            formatName = formatName.equals("application/fits") ? "FITS" : "VOT";
            URL url = new URL((String) msg.getParam("url"));
            String name = (String) (msg.getParam("name") != null ? msg.getParam("name") : "SAMP");
            ExtSed sed = sedManager.getSelected() != null ? sedManager.getSelected() : sedManager.newSed(name);
            try {
                    LogEvent.getInstance().fire(null, new LogEntry("Received file: "+url+" from SAMP", this));
                    Sed s = Sed.read(url.openStream(), SedFormat.valueOf(formatName));
                    List<ValidationError> validErrors = new ArrayList();
                    s.validate(validErrors);
                    for (ValidationError error : validErrors) {
                        if (error.getError().equals(ValidationErrorEnum.MISSING_DATA_FLUXAXIS_VALUE) ||
                            error.getError().equals(ValidationErrorEnum.MISSING_DATA_SPECTRALAXIS_VALUE) ||
                            error.getError().equals(ValidationErrorEnum.MISSING_CHAR_FLUXAXIS_UCD) ||
                            error.getError().equals(ValidationErrorEnum.MISSING_CHAR_FLUXAXIS_UNIT)) {
                                LogEvent.getInstance().fire(null, new LogEntry("File is not a valid SSA spectrum, but might still be imported.", this));
                        }

                    }
                    for (int i = 0; i < s.getNumberOfSegments(); i++) {
                        sed.addSegment(s.getSegment(i));
                    }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                NarrowOptionPane.showMessageDialog(rootFrame,
                            ex.getMessage(),
                            "Import Error",
                            NarrowOptionPane.ERROR_MESSAGE);
            }

            return null;
        }

        

    }

}
