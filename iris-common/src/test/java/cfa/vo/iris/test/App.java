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

import cfa.vo.interop.SAMPConnectionListener;
import cfa.vo.interop.SampService;
import cfa.vo.iris.ComponentLoader;
import cfa.vo.iris.IrisApplication;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import cfa.vo.iris.IrisComponent;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.MessageHandler;
import org.astrogrid.samp.client.SampException;

import javax.swing.*;

public class App implements IrisApplication {

    @Override
    public File getConfigurationDir() {
        return new File(System.getProperty("user.home") + "/.vao/iris/importer/");
    }

    @Override
    public boolean isSampEnabled() {
        return false;
    }

    @Override
    public void sendSampMessage(Message msg) throws SampException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SampService getSampService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public URL getHelpURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JDialog getAboutBox() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public URL getSAMPIcon() {
        return null;
    }

    @Override
    public Collection<? extends IrisComponent> getComponents() {
        return null;
    }

    @Override
    public void addMessageHandler(MessageHandler handler) {

    }

    @Override
    public ComponentLoader getComponentLoader() {
        return null;
    }

    @Override
    public void exitApp(int status) {

    }

    @Override
    public void addConnectionListener(SAMPConnectionListener sampConnectionListener) {

    }

    @Override
    public void addSherpaConnectionListener(SAMPConnectionListener sampConnectionListener) {

    }

    @Override
    public URL getDesktopIcon() {
        return null;
    }

    @Override
    public boolean isPlatformOSX() {
        return false;
    }

    @Override
    public void setAutoRunHub(boolean autoHub) {

    }
}
