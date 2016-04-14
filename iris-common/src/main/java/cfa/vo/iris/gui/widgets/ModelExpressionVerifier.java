package cfa.vo.iris.gui.widgets;

import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.sherpa.models.Model;

import javax.script.*;
import java.util.List;
import java.util.logging.Logger;

public class ModelExpressionVerifier {
    private ScriptEngine scriptEngine;
    private Logger logger = Logger.getLogger(ModelExpressionVerifier.class.getName());

    public ModelExpressionVerifier() {
        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngine = manager.getEngineByName("JavaScript");
    }

    public boolean verify(FitConfiguration fit) {
        boolean retVal = false;
        String expression = null;

        if (fit != null) {
            expression = fit.getModel().getName();
        }

        if (expression == null || expression.isEmpty()) {
            return retVal;
        }

        Bindings b = new SimpleBindings();
        List<Model> models = fit.getModel().getParts();
        if (models != null) {
            for (Model m : models) {
                b.put(m.getName().split("\\.")[1], Double.NaN);
            }
        }

        try {
            scriptEngine.setBindings(b, ScriptContext.ENGINE_SCOPE);
            Object res = scriptEngine.eval(expression);
            if (res != null) {
                retVal = res.equals(Double.NaN);
            }
        } catch (ScriptException ex) {
            logger.severe("Cannot validate expression: " + expression);
        }
        return retVal;
    }
}
