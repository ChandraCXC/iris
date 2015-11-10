package cfa.vo.iris.test.unit;

import cfa.vo.iris.IrisComponent;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractComponentGUITest extends AbstractGUITest {

    @Override
    protected List<IrisComponent> getComponents() {
        return Arrays.asList(new IrisComponent[]{getComponent()});
    }

    protected abstract IrisComponent getComponent();
}
