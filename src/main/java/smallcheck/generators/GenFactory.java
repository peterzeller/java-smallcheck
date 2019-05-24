package smallcheck.generators;

import smallcheck.annotations.From;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 *
 */
public class GenFactory {

    private Map<Type, SeriesGen<?>> typeGenerators = initDefaultGenerators();
    private List<StaticFactory> staticFactories = new ArrayList<>();


    private static class StaticFactory {
        Class<?> clazz;
        Function<Object,Object> copyMethod;

        public StaticFactory(Class<?> clazz, Function<Object, Object> copyMethod) {
            this.clazz = clazz;
            this.copyMethod = copyMethod;
        }
    }


    private Map<Type, SeriesGen<?>> initDefaultGenerators() {
        HashMap<Type, SeriesGen<?>> res = new HashMap<>();
        res.put(int.class, new IntegerGen());
        res.put(Integer.class, new IntegerGen());
        res.put(long.class, new LongGen());
        res.put(Long.class, new LongGen());
        res.put(boolean.class, new BoolGen());
        res.put(Boolean.class, new BoolGen());
        res.put(byte.class, new ByteGen());
        res.put(Byte.class, new ByteGen());
        res.put(char.class, new CharGen());
        res.put(Character.class, new CharGen());
        res.put(short.class, new ShortGen());
        res.put(Short.class, new ShortGen());
        res.put(boolean[].class, ArrayGenG.booleanArray(new BoolGen()));
        res.put(byte[].class, ArrayGenG.byteArray(new ByteGen()));
        res.put(int[].class, ArrayGenG.intArray(new IntegerGen()));
        res.put(long[].class, ArrayGenG.longArray(new LongGen()));
        res.put(char[].class, ArrayGenG.charArray(new CharGen()));
        res.put(short[].class, ArrayGenG.shortArray(new ShortGen()));
        return res;
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
            Class<?> clazz = (Class<?>) type;



            // try to find static factory methods
            List<Method> staticFactoryMethods = new ArrayList<>();
            Function<Object, Object> copyFunc = null;
            for (StaticFactory staticFactory : staticFactories) {
                for (Method method : staticFactory.clazz.getMethods()) {
                    if (clazz.isAssignableFrom(method.getReturnType())) {
                        staticFactoryMethods.add(method);
                        copyFunc = staticFactory.copyMethod;
                    }
                }
            }
            if (!staticFactoryMethods.isEmpty()) {
                SeriesGen<?> gen = new StaticFactoryMethodsGenerator(this, staticFactoryMethods, copyFunc);
                typeGenerators.put(clazz, gen);
                return gen;
            }

            if (Enum.class.isAssignableFrom(clazz)) {
                // we have an enum:
                return new EnumGen(clazz);
            }
        }





        String msg = "Could not find generator for type " + type;
        msg += " (" + type.getClass() + ")";
        if (annotatedType.getAnnotations().length > 0) {
            msg += " with annotations " + Arrays.toString(annotatedType.getAnnotations());
        }
        throw new RuntimeException(msg);

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
}
