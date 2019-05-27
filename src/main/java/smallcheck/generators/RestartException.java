package smallcheck.generators;

/**
 *
 */
public class RestartException extends RuntimeException {
    private final int stackDepth;

    public RestartException(int stackDepth) {
        this.stackDepth = stackDepth;
    }

    public int getStackDepth() {
        return stackDepth;
    }


}
