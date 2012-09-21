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
package cfa.vo.iris;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author olaurino
 */
public class Button implements IButton {

    private Icon icon;
    private Icon thumbnail;

    public Button(Icon icon, Icon thumbnail) {
        this.icon = icon;
        this.thumbnail = thumbnail;
    }

    public Button(String iconResourcePath, String thumbnailResourcePath) {

        if (getClass().getResource(iconResourcePath) == null) {
            try {
                icon = new ImageIcon(ImageIO.read(new URL("jar:file:"+iconResourcePath)));
            } catch (IOException ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
                icon = new ImageIcon(getClass().getResource("/tool.png"));
            }
                
//            }
        } else {
            icon = new ImageIcon(getClass().getResource(iconResourcePath));
        }
        if (getClass().getResource(thumbnailResourcePath) == null) {
            try {
                thumbnail = new ImageIcon(ImageIO.read(new URL("jar:file:"+thumbnailResourcePath)));
            } catch (Exception ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
                thumbnail = new ImageIcon(getClass().getResource("/tool_tiny.png"));
            }
        } else {
            thumbnail = new ImageIcon(getClass().getResource(thumbnailResourcePath));
        }
        
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public Icon getThumbnail() {
        return thumbnail;
    }
}
