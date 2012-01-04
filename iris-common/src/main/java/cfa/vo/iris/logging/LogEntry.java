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
