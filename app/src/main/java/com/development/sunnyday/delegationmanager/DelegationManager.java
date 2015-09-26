package com.development.sunnyday.delegationmanager;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * Created by sashka on 18.09.15.
 * mail: sunnyday.development@gmail.com
 */
public class DelegationManager {

    private static WeakHashMap<Activity, DelegationManager> sManagersMap = new WeakHashMap<>();

    private HashMap<Class, ArrayList<WeakReference<Object>>> mMap = new HashMap<>();

    public static void delegateToActivity(Activity activity, Object holder){
        Delegate delegate = holder.getClass().getAnnotation(Delegate.class);
        if (delegate != null && delegate.value().length > 0){
            DelegationManager dm = sManagersMap.get(activity);
            if (dm == null){
                dm = new DelegationManager();
                sManagersMap.put(activity, dm);
            }
            for (Class item:delegate.value()){
                dm.addDelegate(holder, item);
            }
        }
    }

    public static DelegationManager get(Activity activity){
        return sManagersMap.get(activity);
    }

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
        ArrayList<WeakReference<Object>> holders = mMap.get(type);
        if (holders == null){
            holders = new ArrayList<>();
            mMap.put(type, holders);
        }

        //Create weak reference (no need control memory leak)
        WeakReference<Object> reference = new WeakReference<>(holder);
        //If must get return value from it, set it first in array
        if (firstReturner){
            holders.add(0, reference);
        }else{
            holders.add(reference);
        }
    }

    /**
     * Remove all delegated interface by type and holder reference.
     */
    public void removeDelegate(Object holder, Class type) {
        ArrayList<WeakReference<Object>> holders = mMap.get(type);
        if (holders != null){
            Iterator<WeakReference<Object>> iterator = holders.iterator();
            while (iterator.hasNext()){
                WeakReference<Object> reference = iterator.next();
                if (reference.get() == null || reference.get() == holder){
                    iterator.remove();
                }
            }
            if (holders.size() == 0){
                mMap.remove(type);
            }
        }
    }

    /**
     * Remove all delegated interfaces by holder reference.
     */
    public void removeDelegate(Object holder) {
        for (Class key : mMap.keySet()) {
            removeDelegate(holder, key);
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
                ArrayList<WeakReference<Object>> handlers = mMap.get(tClass);
                if (handlers == null) return null;

                //if have Void return value, do work with all delegates in array
                //Else get return value from first item
                Object result = null;
                boolean resultSetted = false;
                boolean isVoid = method.getReturnType().equals(Void.TYPE);

                Iterator<WeakReference<Object>> refIterator = handlers.iterator();
                while (refIterator.hasNext()){
                    WeakReference<Object> reference = refIterator.next();
                    Object delegate = reference.get();
                    if (delegate == null){
                        refIterator.remove();
                    }else{
                        if (isVoid || !resultSetted){
                            resultSetted = true;
                            result = method.invoke(delegate, args);
                        }
                    }
                }
                return result;
            }
        });
    }
}
