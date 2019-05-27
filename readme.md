# Java SmallCheck

Java SmallCheck is a Java library for property based testing.
It will generate all possible inputs (up to a certain size) for testing your functions.

This library is based on ideas from "Smallcheck and lazy smallcheck: automatic exhaustive testing for small values" (Colin Runciman, Matthew Naylor, and Matthew Naylor; Haskell 2008)


## Basic Example

The following function is supposed to compute the maximum out of 3 integers.
Unfortunately it contains a bug.
Can you find a counter example where it would fail?

    static int max3(int x, int y, int z) {
        if (x > y && x > z) {
            return x;
        } else if (y > x && y > z) {
            return y;
        } else {
            return z;
        }
    }

With SmallCheck it is easy to write a test that finds a minimal counter example:


    import org.junit.runner.RunWith;
    import smallcheck.SmallCheckRunner;
    import smallcheck.annotations.Property;

    import static org.junit.Assert.assertTrue;

    @RunWith(SmallCheckRunner.class)
    public class Max3Example {

        @Property
        public void testMax3(int x, int y, int z) {
            int result = Max.max3(x, y, z);
            assertTrue(result >= x);
            assertTrue(result >= y);
            assertTrue(result >= z);
        }

    }

When executed this test produces the following counter example:

    java.lang.AssertionError: Test failed when calling testMax3 with the following arguments:
      x = 1
      y = 1
      z = 0
        at org.junit.Assert.fail(Assert.java:86)
        at org.junit.Assert.assertTrue(Assert.java:41)
        at org.junit.Assert.assertTrue(Assert.java:52)
        at Max3Example.testMax3(Max3Example.java:26)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:566)



## Getting Started

We recommend using [Gradle](https://gradle.org/) as the build tool.
If you are using a different build tool, follow the instructions at [https://jitpack.io/#peterzeller/java-smallcheck](https://jitpack.io/#peterzeller/java-smallcheck).


For Gradle follow these steps:



1. Add Jitpack to your repositories in `build.gradle`:

        repositories {
            jcenter()
            maven { url 'https://jitpack.io' }
        }

2. Add the SmallCheck dependency in `build.gradle`:

        dependencies {
            // Smallcheck Testing library
            testCompile group: 'com.github.peterzeller', name: 'java-smallcheck', version: '-SNAPSHOT'
        }

    Change `-SNAPSHOT` to a fixed tag to get a stable build.

3. (optional) Configure Gradle to keep parameter names. This will give you better error messages.

        compileTestJava.options.compilerArgs.add '-parameters'


## Writing Property Tests

To write property based tests using SmallCheck, two ingredients are required:

1. The class containing the tests must be annotated with

        @RunWith(SmallCheckRunner.class)

2. Each property is written in a public method annotated with `@Property`, similar to the `@Test` annotation for normal Junit tests.


SmallCheck will invoke each property method with all possible parameters up to a certain depth.

    @Property
    public void generate(int x, char c) {
        System.out.println(x + ", " + c);
    }

This simple example will be invoked with the following inputs:

    // depth 1
    0, a
    1, a
    -1, a
    0, b
    1, b
    -1, b
    // depth 2
    0, a
    1, a
    2, a
    -1, a
    -2, a
    0, b
    1, b
    2, b
    -1, b
    -2, b
    0, c
    1, c
    2, c
    -1, c
    -2, c
    // depth 3
    0, a
    1, a
    2, a
    3, a
    -1, a
    -2, a
    -3, a
    0, b
    1, b
    2, b
    3, b
    -1, b
    -2, b
    -3, b
    0, c
    1, c
    2, c
    3, c
    -1, c
    -2, c
    -3, c
    0, d
    1, d
    2, d
    3, d
    -1, d
    -2, d
    -3, d
    // depth 4
    ...

By default values up to depth 5 will be tested.

Many standard Java types (like `int` and `char` above) are supported with built-in generators.
For other types, custom generators can be written.



### Property Settings

The `@Property` annotation provides the following parameters for configuring SmallCheck:

- **int maxDepth** (default 5)

    The maximum depth for generated inputs.

- **int maxInvocations** (default 100000)

    The maximum number of different inputs used in testing.

- **long minExamples** (default 20)

    The minimum number of valid examples that must be produced.
    The test will fail if not enough generated arguments pass the preconditions giving with JUnits `Asssume` methods.

- **int timeout** (default -1)

    A maximum execution time in seconds.
    The test fails if the overall execution takes longer than the given duration.


The following example shows a property with custom configuration:

    @Property(maxDepth = 6, maxInvocations = 1000, minExamples = 100, timeout = 30)
    public void configuredExample(int x, char c) {
        ...
    }


## Custom Generators

A generator for a type `T` is defined by creating a subclass of `SeriesGen<T>`:

        public abstract class SeriesGen<T> {

            public abstract Stream<T> generate(int depth);

            public T copy(T obj) {
                return obj;
            }
        }

The `generate` method creates a stream of elements up to a certain depth.

For example the builtin `IntegerGen` generates values the numbers `0, 1, 2, 3, -1, -2, -3` for `depth = 3`.

The `copy` method must be overridden for mutable datatypes.
It should create a copy of a `T` object.
This is used when generating collections like a `List<T>`.
The List generator must generate many lists starting with the same element and thus needs to call `copy`.

The following example shows a custom number generator that only generates even numbers.


    public static class CustomNumberGen extends SeriesGen<Integer> {
        @Override
        public Stream<Integer> generate(int depth) {
            return IntStream.range(0, 1 + depth).map(i -> 2 * i).boxed();
        }
    }

    public static class CustomCharGen extends SeriesGen<Character> {
        @Override
        public Stream<Character> generate(int depth) {
            return IntStream.range(0, 1+depth).mapToObj((int i) -> {
                if (i % 2 == 0) {
                    return (char) ('a' + i / 2);
                } else {
                    return (char) ('A' + i / 2);
                }
            });
        }
    }


### Registering Custom Generators

To use the custom generators they can be registered for a test method using the `@RegisterGenerator` annotation as shown in the example below.  


    @Property
    @RegisterGenerator(CustomNumberGen.class)
    public void genList(List<Integer> list) {
        System.out.println(list);
    }

    @Property
    @RegisterGenerator(CustomNumberGen.class)
    @RegisterGenerator(CustomCharGen.class)
    public void genMap(Map<Integer, Character> m) {
        System.out.println(m);
    }


### Configuring Custom Generators with @From

It is also possible to use the `@From` annotation on a specific parameter to use the generator only for that case:

    @Property
    public void genList2(List<@From(CustomNumberGen.class) Integer> list) {
        System.out.println(list);
    }

### Custom Generators from Static Factories

Another option for generating custom values is to define a static factory.
A static factory is a class that provides static methods that generate instances of a certain type.

SmallCheck will try all combinations of these methods to create values of the type.

In the example below we use this feature to create arithmetic expressions and find the smallest expression that evaluates to a value greater than or equal to 8.
The expression found is `((2 * 2) * 2)`.


    @Property(maxInvocations = 5000000)
    @StaticFactory(ExprFactory.class)
    public void testExpr(Expr e) {
        assertTrue(e.evaluate() < 8);
    }


    public static class ExprFactory {
        public static Number number(int i) {
            return new Number(i);
        }

        public static Plus plus(Expr a, Expr b) {
            return new Plus(a, b);
        }

        public static Mult mult(Expr a, Expr b) {
            return new Mult(a, b);
        }
    }



## The StateGen Generator

So far we have only seen SmallCheck generate inputs in the form of parameters.
The stateful generator `StateGen` provides an alternative way to generate inputs for your tests.

To use this, simply add a parameter of type `StateGen` to your test method and use the `StateGen.gen` methods to generate values.

    @Property
    public void testMax3S(StateGen sg) {
        int x = sg.gen(Integer.class);
        int y = sg.gen(Integer.class);
        int z = sg.gen(Integer.class);
        int result = max3(x, y, z);
        assertTrue(result >= x);
        assertTrue(result >= y);
        assertTrue(result >= z);
    }

SmallCheck will invoke the test method multiple times and choose different values each execution.

First all values for `z` (the last choice) are tested.
When all values for `z` are ok, SmallCheck will backtrack and try the next value for `y`.

Since the last generated values are explored first, the counter examples given with `StateGen` differ from the ones found with normal parameters.




