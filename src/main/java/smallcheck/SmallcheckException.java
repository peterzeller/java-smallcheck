package smallcheck;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 */
public class SmallcheckException extends AssertionError {
    private final Object[] args;

    public SmallcheckException(Method method, Object[] args, Throwable cause) {
        super(message(method, args, cause), cause);
        this.args = args;
    }

    private static String message(Method method, Object[] args, Throwable cause) {
        StringBuilder msg = new StringBuilder("Test failed when calling " + method.getName() + " with the following arguments:");
        for (int i = 0; i < args.length; i++) {
            String paramName = method.getParameters()[i].getName();
            Object arg = args[i];
            msg.append("\n  ");
            msg.append(paramName);
            msg.append(" = ");
            msg.append(printArg(arg));
        }
        if (cause.getMessage() != null) {
            msg.append("\n").append(cause.getMessage());
        }
        return msg.toString();
    }

    private static String printArg(Object o) {
        if (o instanceof Object[]) {
            return Arrays.deepToString(((Object[]) o));
        }
        return "" + o;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        // only show stacktrace of the cause
        return getCause().getStackTrace();
    }

    AssertionError skipInternals() {
        List<StackTraceElement> trace = new ArrayList<>();
        StackTraceElement[] orig = getCause().getStackTrace();
        for (StackTraceElement se : orig) {
            if (se.getClassName().equals(PropertyStatement.class.getCanonicalName())) {
                break;
            }
            trace.add(se);
        }

        StackTraceElement[] st = trace.toArray(new StackTraceElement[0]);
        setStackTrace(st);

        AssertionError err = new AssertionError(getMessage());
        err.setStackTrace(st);
        return err;
    }
}
