package smallcheck.generators;

import smallcheck.annotations.From;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 */
public class GenFactory {

    private Map<Type, SeriesGen<?>> typeGenerators = initDefaultGenerators();
    private List<Class<?>> staticFactories = new ArrayList<>();


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

            return new ArrayGen(clazz, genForType(componentType));
        } else if (annotatedType instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType pt = (AnnotatedParameterizedType) annotatedType;
            if (pt.getType() instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) pt.getType();
                if (t.getRawType().equals(List.class)) {
                    AnnotatedType componentType = pt.getAnnotatedActualTypeArguments()[0];
                    return new ListGen(genForType(componentType));
                }
            }
        }

        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;

            // try to find static factory methods
            List<Method> staticFactoryMethods = new ArrayList<>();
            for (Class<?> staticFactory : staticFactories) {
                for (Method method : staticFactory.getMethods()) {
                    if (clazz.isAssignableFrom(method.getReturnType())) {
                        staticFactoryMethods.add(method);
                    }
                }
            }
            if (!staticFactoryMethods.isEmpty()) {
                SeriesGen<?> gen = new StaticFactoryMethodsGenerator(this, staticFactoryMethods);
                typeGenerators.put(clazz, gen);
                return gen;
            }
        }





        String msg = "Could not find generator for type " + type;
        msg += " (" + type.getClass() + ")";
        if (annotatedType.getAnnotations().length > 0) {
            msg += " with annotations " + Arrays.toString(annotatedType.getAnnotations());
        }
        throw new RuntimeException(msg);

    }

    public void addStaticFactory(Class<?> clazz) {
        staticFactories.add(clazz);
    }
}
