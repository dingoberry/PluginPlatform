package ask.op.sdk.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {

    static Object newInstance(Class<?> clz, Class<?>[] parameterTypes, Object[] initargs) throws Exception {
        Constructor<?> c = clz.getDeclaredConstructor(parameterTypes);
        c.setAccessible(true);
        return c.newInstance(initargs);
    }

    static Object newInstance(Class<?> clz) throws Exception {
        Constructor<?> c = clz.getDeclaredConstructor();
        c.setAccessible(true);
        return c.newInstance();
    }

    public static Method getMethod(Class<?> targetClass, String name, Class<?>... parameterTypes) {
        do {
            try {
                Method m = targetClass.getDeclaredMethod(name, parameterTypes);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                // ignore;
            }
            targetClass = targetClass.getSuperclass();
        } while (null != targetClass);
        return null;
    }

    public static Field getField(Class<?> targetClass, String name) {
        do {
            try {
                Field f = targetClass.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                // ignore;
            }
            targetClass = targetClass.getSuperclass();
        } while (null != targetClass);
        return null;
    }

    public static <T, S> void copyFields(Class<?> baseClass, T src, S des) {
        if (null == src || null == des) {
            return;
        }

        while (null != baseClass) {
            if (baseClass == Object.class) {
                break;
            }

            Field[] fields = baseClass.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                try {
                    f.set(des, f.get(src));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            baseClass = baseClass.getSuperclass();
        }
    }
}
