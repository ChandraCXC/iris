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

package cfa.vo.iris.logging;

import java.util.Date;

/**
 *
 * @author olaurino
 */
public class LogEntry {
    private String msg;
    private Object author;

    public LogEntry(String msg, Object author) {
        this.msg = msg;
        this.author = author;
    }

    public String getMessage() {
        return msg;
    }

    public Object getAuthor() {
        return author;
    }

    public String getFormatted() {
        String ts = new Date().toString();
        return ts+" - "+author.getClass().getSimpleName()+" - "+msg;
    }
}
