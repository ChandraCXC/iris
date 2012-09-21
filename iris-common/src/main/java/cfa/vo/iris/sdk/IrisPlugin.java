/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.sdk;

import cfa.vo.iris.IrisComponent;
import java.util.List;

/**
 *
 * @author olaurino
 */
public interface IrisPlugin {
    public String getName();
    public String getDescription();
    public String getVersion();
    public String getAuthor();
    public List<IrisComponent> getComponents();
    public String getAcknowledgments();
}
