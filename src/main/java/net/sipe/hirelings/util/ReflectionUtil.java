package net.sipe.hirelings.util;

public class ReflectionUtil {

    public static boolean isAssignableTo(Class<?> classToCheck, Class<?>[] classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAssignableFrom(classToCheck)) {
                return true;
            }
        }
        return false;
    }
}
