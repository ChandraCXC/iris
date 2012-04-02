/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spv.sherpa.custom;

import java.beans.PropertyChangeListener;
import java.net.URL;

/**
 *
 * @author olaurino
 */
public interface CustomModel {
    URL getUrl();
    void setUrl(URL url);
    String getName();
    void setName(String name);
    String getParnames();
    void setParnames(String parnames);
    String getParvals();
    void setParvals(String parvals);
    String getParmins();
    void setParmins(String parmins);
    String getParmaxs();
    void setParmaxs(String parmaxs);
    String getParfrozen();
    void setParfrozen(String parfozen);
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
