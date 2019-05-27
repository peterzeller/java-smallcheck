package smallcheck.generators;

import io.leangen.geantyref.GenericTypeReflector;
import smallcheck.annotations.From;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class GenFactory {

    private final Map<Type, Class<? extends SeriesGen<?>>> typeGeneratorClasses = new HashMap<>();
    private final Map<Type, SeriesGen<?>> typeGenerators = new HashMap<>();
    private final List<StaticFactory> staticFactories = new ArrayList<>();


    private static class StaticFactory {
        Class<?> clazz;
        Function<Object, Object> copyMethod;

        public StaticFactory(Class<?> clazz, Function<Object, Object> copyMethod) {
            this.clazz = clazz;
            this.copyMethod = copyMethod;
        }
    }

    public GenFactory() {
        initDefaultGenerators();
        typeGeneratorClasses.put(String.class, StringGen.class);
    }

    private void initDefaultGenerators() {
        typeGenerators.put(int.class, new IntegerGen());
        typeGenerators.put(Integer.class, new IntegerGen());
        typeGenerators.put(long.class, new LongGen());
        typeGenerators.put(Long.class, new LongGen());
        typeGenerators.put(boolean.class, new BoolGen());
        typeGenerators.put(Boolean.class, new BoolGen());
        typeGenerators.put(byte.class, new ByteGen());
        typeGenerators.put(Byte.class, new ByteGen());
        typeGenerators.put(char.class, new CharGen());
        typeGenerators.put(Character.class, new CharGen());
        typeGenerators.put(short.class, new ShortGen());
        typeGenerators.put(Short.class, new ShortGen());
        typeGenerators.put(boolean[].class, ArrayGenG.booleanArray(new BoolGen()));
        typeGenerators.put(byte[].class, ArrayGenG.byteArray(new ByteGen()));
        typeGenerators.put(int[].class, ArrayGenG.intArray(new IntegerGen()));
        typeGenerators.put(long[].class, ArrayGenG.longArray(new LongGen()));
        typeGenerators.put(char[].class, ArrayGenG.charArray(new CharGen()));
        typeGenerators.put(short[].class, ArrayGenG.shortArray(new ShortGen()));
    }


    public SeriesGen<?> genForType(AnnotatedType annotatedType) {
        Type type = annotatedType.getType();
        From fromAnnotation = annotatedType.getAnnotation(From.class);
        if (fromAnnotation != null) {
            Class<? extends SeriesGen<?>> genClazz = fromAnnotation.value();
            try {
                return genClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else if (typeGenerators.containsKey(type)) {
            return typeGenerators.get(type);
        } else if (typeGeneratorClasses.containsKey(type)) {
            Class<? extends SeriesGen<?>> c = typeGeneratorClasses.get(type);

            List<Constructor<?>> validConstructors = Arrays.stream(c.getConstructors())
                    .filter(constr -> Arrays.stream(constr.getParameterTypes()).allMatch(
                            SeriesGen.class::isAssignableFrom
                    ))
                    .sorted(Comparator.comparing((Constructor constr) -> constr.getParameterTypes().length).reversed())
                    .collect(Collectors.toList());
            if (validConstructors.size() == 0) {
                throw new RuntimeException("Cannot instantiate " + c  +" for type " + type + ".\n" +
                        "Constructor which only takes other SeriesGens required.");
            }
            Constructor<?> constr = validConstructors.get(0);
            Object[] args = Arrays.stream(constr.getGenericParameterTypes())
                    .map(this::genForType)
                    .toArray();
            try {
                SeriesGen<?> res = (SeriesGen<?>) constr.newInstance(args);
                typeGenerators.put(type, res);
                return res;
            } catch (Exception e) {
                throw new RuntimeException("Could not create Generator for type " + type, e);
            }
        } else if (type.equals(String.class)) {
            return new StringGen();
        } else if (annotatedType instanceof AnnotatedArrayType) {
            AnnotatedArrayType arType = (AnnotatedArrayType) annotatedType;
            AnnotatedType componentType = arType.getAnnotatedGenericComponentType();
            Class clazz = (Class) componentType.getType();
            if (clazz.isPrimitive()) {
                throw new RuntimeException("Primitive array types not yet supported");
            }

            return new ArrayGen<>(clazz, genForType(componentType));
        } else if (annotatedType instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType pt = (AnnotatedParameterizedType) annotatedType;
            if (pt.getType() instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) pt.getType();
                if (t.getRawType().equals(List.class)
                        || t.getRawType().equals(Collection.class)
                        || t.getRawType().equals(Iterable.class)) {
                    AnnotatedType componentType = pt.getAnnotatedActualTypeArguments()[0];
                    return new ListGen<>(genForType(componentType));
                } else if (t.getRawType().equals(Set.class)) {
                    AnnotatedType componentType = pt.getAnnotatedActualTypeArguments()[0];
                    return new SetGen<>(genForType(componentType));
                } else if (t.getRawType().equals(Map.class)) {
                    AnnotatedType keyType = pt.getAnnotatedActualTypeArguments()[0];
                    AnnotatedType valueType = pt.getAnnotatedActualTypeArguments()[1];
                    return new MapGen<>(genForType(keyType), genForType(valueType));
                }
            }
        }

        if (type instanceof Class<?>) {
            SeriesGen<?> clazz = genFor((Class<?>) type);
            if (clazz != null) return clazz;
        }


        String msg = "Could not find generator for type " + type;
        msg += " (" + type.getClass() + ")";
        if (annotatedType.getAnnotations().length > 0) {
            msg += " with annotations " + Arrays.toString(annotatedType.getAnnotations());
        }
        throw new RuntimeException(msg);

    }

    public SeriesGen<?> genForType(Type type) {
        return genForType(GenericTypeReflector.annotate(type));
    }

    private SeriesGen<?> genFor(Class<?> type) {
        if (StateGen.class.isAssignableFrom(type)) {
            return new SeriesGen<Object>() {
                @Override
                public Stream<Object> generate(int depth) {
                    return Stream.of(new StateGen(GenFactory.this, depth));
                }
            };
        }

        // try to find static factory methods
        List<Method> staticFactoryMethods = new ArrayList<>();
        Function<Object, Object> copyFunc = null;
        for (StaticFactory staticFactory : staticFactories) {
            for (Method method : staticFactory.clazz.getMethods()) {
                if (type.isAssignableFrom(method.getReturnType())) {
                    staticFactoryMethods.add(method);
                    copyFunc = staticFactory.copyMethod;
                }
            }
        }
        if (!staticFactoryMethods.isEmpty()) {
            SeriesGen<?> gen = new StaticFactoryMethodsGenerator(this, staticFactoryMethods, copyFunc);
            typeGenerators.put(type, gen);
            return gen;
        }

        if (Enum.class.isAssignableFrom(type)) {
            // we have an enum:
            return new EnumGen(type);
        }
        return null;
    }

    public void addStaticFactory(Class<?> clazz, Function<Object, Object> copyFunc) {
        staticFactories.add(new StaticFactory(clazz, copyFunc));
    }

    public void addStaticFactory(Class<?> clazz, Class<? extends Function<Object, Object>> copyFunc) {
        try {
            addStaticFactory(clazz, copyFunc.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void registerGenerator(Class<? extends SeriesGen<?>> generator) {
        Type superType = GenericTypeReflector.getExactSuperType(generator, SeriesGen.class);
        if (superType instanceof ParameterizedType) {
            Type gt = ((ParameterizedType) superType).getActualTypeArguments()[0];
            typeGeneratorClasses.put(gt, generator);
            typeGenerators.remove(gt);
        } else {
            throw new RuntimeException("Generator " + generator + " must extend SeriesGen<T> with a concrete type parameter, but it only extends " + superType);
        }
    }

}
