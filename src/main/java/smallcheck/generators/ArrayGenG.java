package smallcheck.generators;

import java.util.stream.Stream;

/**
 *
 */
public class ArrayGenG<T, Ar> extends SeriesGen<Ar> {


    private final Array<T, Ar> array;
    private final SeriesGen<T> elementGen;

    public ArrayGenG(SeriesGen<T> elementGen, Array<T, Ar> array) {
        this.array = array;
        this.elementGen = elementGen;
    }

    public static <T> ArrayGenG<T, T[]> generic(Class<T> clazz, SeriesGen<T> elementGen) {
        return new ArrayGenG<>(elementGen, new Array<T, T[]>() {
            @Override
            public T[] create(int size) {
                @SuppressWarnings("unchecked")
                T[] ar = (T[]) java.lang.reflect.Array.newInstance(clazz, size);
                return ar;
            }

            @Override
            public T read(T[] ts, int index) {
                return ts[index];
            }

            @Override
            public void write(T[] ts, int index, T elem) {
                ts[index] = elem;
            }

            @Override
            public int length(T[] ts) {
                return ts.length;
            }

            @Override
            public void arraycopy(T[] src, int srcPos, T[] dest, int destPos) {
                System.arraycopy(src, srcPos, dest, destPos, src.length);
            }
        });
    }

    public static ArrayGenG<Boolean, boolean[]> booleanArray(SeriesGen<Boolean> elementGen) {
        return new ArrayGenG<>(elementGen, new Array<Boolean, boolean[]>() {
            @Override
            public boolean[] create(int size) {
                return new boolean[size];
            }

            @Override
            public Boolean read(boolean[] ts, int index) {
                return ts[index];
            }

            @Override
            public void write(boolean[] ts, int index, Boolean elem) {
                ts[index] = elem;
            }

            @Override
            public int length(boolean[] ts) {
                return ts.length;
            }

            @Override
            public void arraycopy(boolean[] src, int srcPos, boolean[] dest, int destPos) {
                System.arraycopy(src, srcPos, dest, destPos, src.length);
            }
        });
    }

    public static ArrayGenG<Byte, byte[]> byteArray(SeriesGen<Byte> elementGen) {
        return new ArrayGenG<>(elementGen, new Array<Byte, byte[]>() {
            @Override
            public byte[] create(int size) {
                return new byte[size];
            }

            @Override
            public Byte read(byte[] ts, int index) {
                return ts[index];
            }

            @Override
            public void write(byte[] ts, int index, Byte elem) {
                ts[index] = elem;
            }

            @Override
            public int length(byte[] ts) {
                return ts.length;
            }

            @Override
            public void arraycopy(byte[] src, int srcPos, byte[] dest, int destPos) {
                System.arraycopy(src, srcPos, dest, destPos, src.length);
            }
        });
    }

    public static ArrayGenG<Integer, int[]> intArray(SeriesGen<Integer> elementGen) {
        return new ArrayGenG<>(elementGen, new Array<Integer, int[]>() {
            @Override
            public int[] create(int size) {
                return new int[size];
            }

            @Override
            public Integer read(int[] ts, int index) {
                return ts[index];
            }

            @Override
            public void write(int[] ts, int index, Integer elem) {
                ts[index] = elem;
            }

            @Override
            public int length(int[] ts) {
                return ts.length;
            }

            @Override
            public void arraycopy(int[] src, int srcPos, int[] dest, int destPos) {
                System.arraycopy(src, srcPos, dest, destPos, src.length);
            }
        });
    }

    public static ArrayGenG<Long, long[]> longArray(SeriesGen<Long> elementGen) {
        return new ArrayGenG<>(elementGen, new Array<Long, long[]>() {
            @Override
            public long[] create(int size) {
                return new long[size];
            }

            @Override
            public Long read(long[] ts, int index) {
                return ts[index];
            }

            @Override
            public void write(long[] ts, int index, Long elem) {
                ts[index] = elem;
            }

            @Override
            public int length(long[] ts) {
                return ts.length;
            }

            @Override
            public void arraycopy(long[] src, int srcPos, long[] dest, int destPos) {
                System.arraycopy(src, srcPos, dest, destPos, src.length);
            }
        });
    }

    public static ArrayGenG<Character, char[]> charArray(SeriesGen<Character> elementGen) {
        return new ArrayGenG<>(elementGen, new Array<Character, char[]>() {
            @Override
            public char[] create(int size) {
                return new char[size];
            }

            @Override
            public Character read(char[] ts, int index) {
                return ts[index];
            }

            @Override
            public void write(char[] ts, int index, Character elem) {
                ts[index] = elem;
            }

            @Override
            public int length(char[] ts) {
                return ts.length;
            }

            @Override
            public void arraycopy(char[] src, int srcPos, char[] dest, int destPos) {
                System.arraycopy(src, srcPos, dest, destPos, src.length);
            }
        });
    }

    public static ArrayGenG<Short, short[]> shortArray(SeriesGen<Short> elementGen) {
        return new ArrayGenG<>(elementGen, new Array<Short, short[]>() {
            @Override
            public short[] create(int size) {
                return new short[size];
            }

            @Override
            public Short read(short[] ts, int index) {
                return ts[index];
            }

            @Override
            public void write(short[] ts, int index, Short elem) {
                ts[index] = elem;
            }

            @Override
            public int length(short[] ts) {
                return ts.length;
            }

            @Override
            public void arraycopy(short[] src, int srcPos, short[] dest, int destPos) {
                System.arraycopy(src, srcPos, dest, destPos, src.length);
            }
        });
    }

    @Override
    public Stream<Ar> generate(int depth) {
        if (depth == 0) {
            Ar ar = array.create(0);
            return Stream.<Ar>of(ar);
        }
        return generate(depth - 1).flatMap(ar -> {
            return Stream.concat(
                    Stream.<Ar>of(ar),
                    elementGen.generate(depth - 1).map(e -> {
                        Ar res = array.create(array.length(ar) + 1);
                        array.arraycopy(ar, 0, res, 1);
                        array.write(res, 0, e);
                        return res;
                    })
            );
        });
    }

    @Override
    public Ar copy(Ar obj) {
        Ar res = array.create(array.length(obj));
        for (int i = 0; i < array.length(obj); i++) {
            array.write(res, i, elementGen.copy(array.read(obj, i)));
        }
        return res;
    }

    interface Array<T, Ar> {
        Ar create(int size);

        T read(Ar ar, int index);

        void write(Ar ar, int index, T elem);

        int length(Ar ar);

        void arraycopy(Ar src, int srcPos, Ar dest, int destPos);
    }
}
