package cfa.vo.iris.gui.widgets;

import cfa.vo.iris.fitting.FitConfigurationBean;
import cfa.vo.sherpa.models.Model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.logging.Logger;

public class ModelExpressionVerifier {
    private ScriptEngine scriptEngine;
    private Logger logger = Logger.getLogger(ModelExpressionVerifier.class.getName());

    public ModelExpressionVerifier() {
        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngine = manager.getEngineByName("JavaScript");
    }

    public boolean verify(FitConfigurationBean fit) {
        boolean retVal = false;
        String expression = null;

        if (fit != null) {
            expression = fit.getModel().getName();
        }

        if (expression == null || expression.isEmpty()) {
            return retVal;
        }

        StringBuilder builder = new StringBuilder();
        List<Model> models = fit.getModel().getParts();
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
