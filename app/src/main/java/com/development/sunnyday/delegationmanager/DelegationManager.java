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
        addDelegate(holder, type, false);
    }

    /**
     * Add delegate. If it has methods with not Void return type and must use it
     * than firstReturner must be true.
     * @param holder - reference on object that implement delegated type
     * @param type - delegated type
     * @param firstReturner - if true return value will get from it.
     */
    public void addDelegate(Object holder, Class type, boolean firstReturner){
        ArrayList<Object> holders = mMap.get(type);
        if (holders == null){
            holders = new ArrayList<>();
        }
        //If must get return value from it, set it first in array
        if (firstReturner){
            holders.add(0, holder);
        }else{
            holders.add(holder);
        }
        mMap.put(type, holders);
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

    /**
     * Get delegated interface
     */
    @SuppressWarnings("unchecked")
    public <T> T getDelegateInterface(final Class<T> tClass){
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ArrayList<Object> handlers = mMap.get(tClass);
                if (handlers == null) return null;

                //if have Void return value, do work with all delegates in array
                //Else get return value from first item
                for (Object handler : handlers) {
                    if (method.getReturnType().equals(Void.TYPE)) {
                        method.invoke(handler, args);
                    } else {
                        return method.invoke(handler, args);
                    }
                }
                return null;
            }
        });
    }
}
