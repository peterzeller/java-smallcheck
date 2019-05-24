package smallcheck;

import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import smallcheck.annotations.Property;
import smallcheck.annotations.StaticFactories;
import smallcheck.annotations.StaticFactory;
import smallcheck.generators.GenFactory;
import smallcheck.generators.ParamGen;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 *
 */
public class PropertyStatement extends Statement {
    private Property property;
    private final FrameworkMethod method;
    private final GenFactory genFactory;
    private final Object testInstance;

    public PropertyStatement(Property property, FrameworkMethod method, TestClass testClass) {
        this.property = property;
        this.method = method;
        this.genFactory = new GenFactory(); // TODO configure
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof StaticFactories) {
                for (StaticFactory staticFactory : ((StaticFactories) annotation).value()) {
                    genFactory.addStaticFactory(staticFactory.value(), staticFactory.copyFunc());
                }
            } else if (annotation instanceof StaticFactory) {
                StaticFactory staticFactory = (StaticFactory) annotation;
                genFactory.addStaticFactory(staticFactory.value(), staticFactory.copyFunc());
            }
        }

        try {
            testInstance = testClass.getJavaClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void evaluate() throws Throwable {
        Method m = this.method.getMethod();
        Parameter[] parameters = m.getParameters();
        AtomicLong invocations = new AtomicLong(0);
        AtomicLong preConditionFailures = new AtomicLong(0);
        AtomicReference<Object[]> lastArgs = new AtomicReference<>();
        try {
            int maxDepth = property.maxDepth();
            int maxInvocations = property.maxInvocations();
            int timeout = property.timeout();
            if (timeout < 0) {
                execute(m, parameters, invocations, preConditionFailures, maxDepth, maxInvocations, lastArgs);
            } else {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future f = executor.submit(() -> execute(m, parameters, invocations, preConditionFailures, maxDepth, maxInvocations, lastArgs));
                executor.shutdown();
                try {
                    f.get(timeout, TimeUnit.SECONDS);
                } catch (ExecutionException e) {
                    throw e.getCause();
                } catch (TimeoutException e) {
                    throw new SmallcheckException(m, lastArgs.get(),
                            new Exception("Test timed out after " + timeout + " seconds.", e));
                } catch (Exception e) {
                    throw new SmallcheckException(m, lastArgs.get(), e);
                }
            }
        } catch (SmallcheckException e) {
            throw e.skipInternals();
        } catch (MaxInvocationsReached e) {
            // ignore, tests passed
        }
        long invocs = invocations.decrementAndGet();
        long successfulInvocations = invocs - preConditionFailures.get();
        if (successfulInvocations < property.minExamples()) {
            Assert.fail("Did not find enough examples, only " + successfulInvocations + "/" + invocs + " inputs met the precondition.");
        }
//        System.out.println("execute " + this.method + " in " + testClass);
    }

    private void execute(Method m, Parameter[] parameters, AtomicLong invocations, AtomicLong preConditionFailures, int maxDepth, int maxInvocations, AtomicReference<Object[]> lastArgs) {
        for (int depth = 0; depth <= maxDepth; depth++) {
            Stream<Object[]> argStream = ParamGen.generate(genFactory, parameters, depth);
            argStream.forEach(args -> {
                lastArgs.set(args);
                try {
                    if (invocations.incrementAndGet() > maxInvocations) {
                        throw new MaxInvocationsReached();
                    }
                    m.invoke(testInstance, args);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof AssumptionViolatedException) {
                        preConditionFailures.incrementAndGet();
                        // ignore this case
                        return;
                    }
                    throw new SmallcheckException(m, args, cause);
                }
            });
        }
    }

    private class MaxInvocationsReached extends RuntimeException {
    }
}
