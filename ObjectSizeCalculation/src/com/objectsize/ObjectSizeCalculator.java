package com.objectsize;

import java.util.Arrays;

import com.vladium.utils.IObjectProfileNode;
import com.vladium.utils.ObjectProfiler;

public class ObjectSizeCalculator {

    public static void main(String[] args) {
        printSize(new Integer(Integer.MIN_VALUE));
        printSize(new Integer(Integer.MAX_VALUE));
        printSize(new int[] {Integer.MIN_VALUE});
        printSize(new int[] {Integer.MAX_VALUE});
        printSize(new int[] {1});
        printSize(new int[] {1, 2});
        printSize(new int[] {1, 2, 3});

        printSize(new Double(Double.MIN_VALUE));
        printSize(new Double(Double.MAX_VALUE));
        printSize(new double[] {1});
        printSize(new double[] {1, 2});
        printSize(new double[] {1, 2, 3});
        printSize("STRINGABCDEF");
        printSize(new Data(1, 2));
    }

    public static void printSize(Object obj) {
        IObjectProfileNode profile = ObjectProfiler.profile(obj);
        int objSize = profile.size();
        if (obj.getClass().isArray()) {
            System.out.println(String.format("Size of array %s of class %s is: %d bytes", arrayToString(obj),
                getClassNameOfArrayElement(obj), objSize));
        } else {
            System.out.println(String.format("Size of object %s of class %s is: %d bytes", obj.toString(), obj.getClass().getName(),
                objSize));
        }
    }

    public static String getClassNameOfArrayElement(Object object) {
        String result = "";
        if (object.getClass().isArray()) {
            Class<?> componentType = object.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                if (boolean.class.isAssignableFrom(componentType)) {
                    result = Boolean.class.getName();
                } else if (byte.class.isAssignableFrom(componentType)) {
                    result = Byte.class.getName();
                } else if (char.class.isAssignableFrom(componentType)) {
                    result = Character.class.getName();
                } else if (double.class.isAssignableFrom(componentType)) {
                    result = Double.class.getName();
                } else if (float.class.isAssignableFrom(componentType)) {
                    result = Float.class.getName();
                } else if (int.class.isAssignableFrom(componentType)) {
                    result = Integer.class.getName();
                } else if (long.class.isAssignableFrom(componentType)) {
                    result = Long.class.getName();
                } else if (short.class.isAssignableFrom(componentType)) {
                    result = Short.class.getName();
                }
            }
            else {
                Object[] array = (Object[])object;
                result = array.length > 0 ? array[0].getClass().getName() : "";
            }
        }
        return result;
    }

    public static String arrayToString(Object object) {
        String result = "";
        if (object.getClass().isArray()) {
            Class<?> componentType = object.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                if (boolean.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((boolean[])object);
                } else if (byte.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((byte[])object);
                } else if (char.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((char[])object);
                } else if (double.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((double[])object);
                } else if (float.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((float[])object);
                } else if (int.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((int[])object);
                } else if (long.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((long[])object);
                } else if (short.class.isAssignableFrom(componentType)) {
                    result = Arrays.toString((short[])object);
                }
            }
            else {
                result = Arrays.toString((Object[])object);
            }
        }
        return result;
    }

    private static class Data {

        int i = 0;
        int j = 0;

        Data(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return "Data [i=" + i + ", j=" + j + "]";
        }
    }
}
