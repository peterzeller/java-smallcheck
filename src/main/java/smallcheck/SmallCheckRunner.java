package smallcheck;

import org.junit.Test;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import smallcheck.annotations.Property;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SmallCheckRunner extends BlockJUnit4ClassRunner {
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code clazz}
     *
     * @param clazz
     * @throws InitializationError if the test class is malformed.
     */
    public SmallCheckRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }


    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(Test.class, false, errors);
        validatePropertyMethods(errors);
    }

    private void validatePropertyMethods(List<Throwable> errors) {
        for (FrameworkMethod each : getTestClass().getAnnotatedMethods(Property.class))
            each.validatePublicVoid(false, errors);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> methods = new ArrayList<>();
        methods.addAll(getTestClass().getAnnotatedMethods(Test.class));
        methods.addAll(getTestClass().getAnnotatedMethods(Property.class));
        return methods;
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        Property property = method.getAnnotation(Property.class);
        if (property == null) {
            return super.methodInvoker(method, test);
        }
        return new PropertyStatement(property, method, getTestClass());
    }

    @Override
    protected TestClass createTestClass(Class<?> testClass) {
        return new JUnitQuickcheckTestClass(testClass);
    }


//    @Override
//    public Description getDescription() {
//        return Description.createSuiteDescription("SmallCheck Runner");
//    }
//
//    @Override
//    public void run(RunNotifier notifier) {
//        notifier.
//
//    }
}
