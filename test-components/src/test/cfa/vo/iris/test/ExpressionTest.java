package cfa.vo.iris.test;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.Assert.*;

public class ExpressionTest {
    @Test
    public void testValidScript() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        String[] models = {"p1", "p2"};
        String expression = "p1+p2";
        StringBuilder expressionBuilder = new StringBuilder();
        for (String comp : models) {
            expressionBuilder.append(String.format("var %s; ", comp));
        }
        expressionBuilder.append(expression);
        String expected = "var p1; var p2; p1+p2";
        String observed = expressionBuilder.toString();
        assertEquals(expected, observed);
        Object res = engine.eval(observed);
        assertEquals(Double.NaN, res);
    }
}
