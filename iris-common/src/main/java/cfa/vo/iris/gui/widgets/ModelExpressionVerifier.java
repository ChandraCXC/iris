package cfa.vo.iris.gui.widgets;

import cfa.vo.sherpa.models.Model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.logging.Logger;

public class ModelExpressionVerifier {
    private ScriptEngine scriptEngine;
    private Logger logger = Logger.getLogger(ModelExpressionVerifier.class.getName());

    public ModelExpressionVerifier() {
        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngine = manager.getEngineByName("JavaScript");
    }

    public boolean verify(String expression, java.util.List<Model> models) {
        boolean retVal = false;

        if (expression == null || expression.isEmpty()) {
            return retVal;
        }

        StringBuilder builder = new StringBuilder();
        if (models != null) {
            for (Model m : models) {
                builder.append(String.format("var %s; ", m.getName().split("\\.")[1]));
            }
        }
        builder.append("1 + ");
        builder.append(expression);
        try {
            Object res = scriptEngine.eval(builder.toString());
            if (res != null) {
                retVal = res.equals(Double.NaN);
            }
        } catch (ScriptException ex) {
            logger.severe("Cannot validate expression: " + expression);
        }
        return retVal;
    }
}
