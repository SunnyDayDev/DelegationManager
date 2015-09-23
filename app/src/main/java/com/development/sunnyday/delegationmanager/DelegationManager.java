package com.development.sunnyday.delegationmanager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sashka on 18.09.15.
 * mail: sunnyday.development@gmail.com
 */
public class DelegationManager {

    private HashMap<Class, ArrayList<Object>> mMap = new HashMap<>();

    public void addDelegate(Object holder, Class type){
        ArrayList<Object> holders = mMap.get(type);
        if (holders != null){
            holders.add(holder);
        }else{
            holders = new ArrayList<>();
            holders.add(holder);
            mMap.put(type, holders);
        }
    }

    /**
     * Remove all delegated interface by type and holder reference.
     */
    public void removeDelegate(Object holder, Class type) {
        ArrayList<Object> holders = mMap.get(type);
        if (holders != null){
            holders.remove(holder);
            if (holders.size() == 0){
                mMap.remove(type);
            }
        }
    }

    /**
     * Remove all delegated interfaces by holder reference.
     */
    public void removeDelegate(Object holder) {
        Iterator<Map.Entry<Class, ArrayList<Object>>> iterator
                = mMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Class, ArrayList<Object>> entry = iterator.next();
            ArrayList<Object> holders = entry.getValue();
            if (holders != null){
                holders.remove(holder);
                if (holders.size() == 0){
                    iterator.remove();
                }
            }else{
                iterator.remove();
            }
        }
    }

    private Object getDelegateFor(final Class mClass){
        return Proxy.newProxyInstance(mClass.getClassLoader(), new Class[]{mClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ArrayList<Object> handlers = mMap.get(mClass);
                if (handlers == null) return null;

                for (Object handler : handlers) {
                    method.invoke(handler, args);
                }
                return null;
            }
        });
    }

    public <T> T getDelegateInterface(Class<T> tClass){
        return ((T) getDelegateFor(tClass));
    }
}
