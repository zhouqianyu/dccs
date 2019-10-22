package com.zju.dcss;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class DelegateHandler implements InvocationHandler {
    private Object[] delegates = null;

    static Object bind(Class[] interfaces, Object... handler) {
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new DelegateHandler(
                handler
        ));
    }

    private DelegateHandler(Object... delegates) {
        this.delegates = delegates;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        System.out.println(method.getName());
        //拿到反射接口的名称 proxy.getClass().getInterfaces()[0]与所有代理进行比较
        for (int i = 0; i < delegates.length; ++i) {
            Method[] methods = delegates[i].getClass().getDeclaredMethods();
            for (int j = 0; j < methods.length; ++j) {
                Method method1 = methods[j];
                if (method1.isAnnotationPresent(Delegate.class)) {
                    Delegate delegate = method1.getAnnotation(Delegate.class);
                    if (!delegate.interfaceName().equals(proxy.getClass().getInterfaces()[0].getName())) {
                    } else {
                        if (delegate.methodName().equals(method.getName()) && Arrays.equals(method.getParameterTypes(),
                                method1.getParameterTypes())) {
                            try {
                                return method1.invoke(delegates[i], args);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Object delegate = new WorkerDelegate();
        Method[] methods = delegate.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Delegate.class)) {
                System.out.println(method.getAnnotation(Delegate.class).interfaceName());
            }
        }
    }
}
