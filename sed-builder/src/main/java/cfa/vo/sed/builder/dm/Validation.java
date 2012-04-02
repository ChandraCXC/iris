/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class Validation {
    private List<String> warnings = new ArrayList();
    private List<String> errors = new ArrayList();

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addError(String error) {
        errors.add(error);
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();
        for(String s : errors) {
            sb.append("Error: ").append(s).append("\n");
        }
        for(String s : warnings) {
            sb.append("Warning: ").append(s).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getString();
    }
}
