package ask.op.pluginplatform;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class ReflectUtils {

    private static Class<?>[] retrieveParameterClz(Object... parameters) {
        Class<?>[] paramClzs = new Class[parameters.length];
        for (int i = 0; i < paramClzs.length; i++) {
            paramClzs[i] = parameters[i].getClass();
        }
        return paramClzs;
    }

    static Object newInstance(Class<?> clz, Object... parameters) throws Exception {
        Constructor<?> c = clz.getDeclaredConstructor(retrieveParameterClz(parameters));
        c.setAccessible(true);
        return c.newInstance(parameters);
    }

    static Object newInstance(Class<?> clz, Object[] parameters, Class<?>[] parameterClzs) throws Exception {
        Constructor<?> c = clz.getDeclaredConstructor(parameterClzs);
        c.setAccessible(true);
        return c.newInstance(parameters);
    }

    static Object invokeMethod(Object obj, String methodName, Object... parameters) throws Exception {
        Class<?> clz = obj.getClass();
        Method m = clz.getDeclaredMethod(methodName, retrieveParameterClz(parameters));
        m.setAccessible(true);
        return m.invoke(obj, parameters);
    }

    static Object invokeMethod(Object obj, String methodName, Object[] parameters, Class<?>[] parameterClzs) throws Exception {
        Class<?> clz = obj.getClass();
        Method m;
        try {
            m = clz.getMethod(methodName, parameterClzs);
            return m.invoke(obj, parameters);
        } catch (Exception e) {
            clz = obj.getClass();
            m = clz.getDeclaredMethod(methodName, parameterClzs);
            m.setAccessible(true);
            return m.invoke(obj, parameters);
        }
    }

    static Object invokeField(Object obj, String fieldName) throws Exception {
        Class<?> clz = obj.getClass();
        Field f;
        try {
            f = clz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            f = clz.getField(fieldName);
            return f.get(obj);
        }
    }
}
