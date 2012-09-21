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

import java.io.File;
import javax.swing.Icon;

/**
 *
 * 
 *
 * @author olaurino
 */
public abstract class AbstractDesktopItem extends AbstractMenuItem {

    public AbstractDesktopItem(String title, String description, Icon icon, Icon thumbnail) {
        super(title, description, true, icon, thumbnail);
    }

    public AbstractDesktopItem(String title, String description, String iconPath, String thumbnailPath) {
        super(title, description, true, iconPath, thumbnailPath);
    }

    public AbstractDesktopItem(String title, String description, IButton button) {
        super(title, description, true, button);
    }

    @Override
    public void consolidate(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
