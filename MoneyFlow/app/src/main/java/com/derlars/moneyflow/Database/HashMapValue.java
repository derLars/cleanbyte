package com.derlars.moneyflow.Database;

import android.util.Log;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HashMapValue<V> extends BaseValue {
    private Map<String, V> collection = new HashMap();
    private boolean collectionModified = true;

    private Map<String, V> databaseValue;

    private List<String> keyList = new ArrayList();

    private final ScheduledExecutorService confirmator = Executors.newSingleThreadScheduledExecutor();

    private Queue<Confirmation<V>> toBeConfirmed = new LinkedList<Confirmation<V>>();
    private boolean confirmationEnabled = false;

    public HashMapValue(String path, String key, boolean readable, boolean writable, boolean connectOnRequest, BaseValueCallback callback) {
        super(path, key,readable, writable,connectOnRequest, callback);
    }

    public void put(String k, V v) {
        if(databaseValue == null || writable) {
            insert(k,v);
        }

        if(writable && online && connected) {
            database.setValue(k,v);
            ConfirmKeys(k,v);
        }
    }

    private void insert(String k, V v) {
        collection.put(k,v);
        collectionModified = true;
    }

    public void delete(String k) {
        remove(k);

        if(writable && online) {
            database.deleteValue(k);
        }
    }

    private void remove(String k) {
        if(collection.containsKey(k)) {
            collection.remove(k);

            collectionModified = true;
        }
    }

    public void clear() {
        collection.clear();
        collectionModified = true;

        if(writable && online) {
            database.deleteValue();
        }
    }

    public V get(String k) {
        if(collection.containsKey(k)) {
            return collection.get(k);
        }
        return null;
    }

    public List<String> getKeyList() {
        sort();

        return keyList;
    }

    protected void updateValue() {
        if(readable && online) {
            collectionModified = true;
            databaseValue = (Map<String,V>)database.getValue();
            if(databaseValue != null) {
                for(String k : databaseValue.keySet()) {
                    insert(k,databaseValue.get(k));
                }

                boolean success = true;
                do {
                    try {
                        Set<String> keySet = new HashSet(collection.keySet());

                        for(String k : keySet) {
                            if(!databaseValue.containsKey(k)) {
                                remove(k);
                            }
                        }

                        success = true;
                    }catch(ConcurrentModificationException ex) {
                        success = false;
                    }
                }while(!success);

                sort();

                notifyUpdate(key);
            }

        }
    }

    private void sort() {
        if(collectionModified) {
            collectionModified = false;
            keyList.clear();

            boolean success = true;
            do {
                try {
                    Set<String> s = new HashSet(collection.keySet());
                    for(String k : s) {
                        keyList.add(k);
                    }
                }catch(ConcurrentModificationException ex) {
                    success = false;
                }
            }while(!success);

            Collections.sort(keyList);
        }
    }

    private void ConfirmKeys(String k, V v) {
        Runnable ru = () -> {
            if(!toBeConfirmed.isEmpty()) {
                Confirmation c = toBeConfirmed.remove();
                if(!collection.containsKey(c.k)) {
                    database.setValue(c.k,c.v);
                }

                if(!toBeConfirmed.isEmpty()) {
                    Runnable _ru = toBeConfirmed.peek().ru;
                    long delay = toBeConfirmed.peek().time - System.currentTimeMillis();
                    if(delay < 10) {
                        delay = 10;
                    }
                    confirmator.schedule(_ru, delay, TimeUnit.MILLISECONDS);
                }else{
                    confirmationEnabled = false;
                }
            }
        };
        toBeConfirmed.add(new Confirmation<V>(System.currentTimeMillis()+ 950, k,v,ru));

        if(!confirmationEnabled) {
            confirmationEnabled = true;

            confirmator.schedule(ru, 950, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void setOnline() {
        if(writable && !online) {
            super.setOnline();
            database.setValue(collection);
        }
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        super.databaseValueRetrieved(path,key,dataSnapshot);

        updateValue();
    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {
        super.databaseNoValueRetrieved(path,key);

        notifyNotOnline(key);
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        super.databaseValueDeleted(path,key);

        updateValue();
    }

    @Override
    public void databaseChildAdded(String path, String key, String childKey) {
        super.databaseChildAdded(path,key,childKey);
        updateValue();
    }

    @Override
    public void databaseChildDeleted(String path, String key, String childKey) {
        super.databaseChildDeleted(path,key,childKey);
        updateValue();
    }

    @Override
    public void databaseChildChanged(String path, String key, String childKey) {
        super.databaseChildChanged(path,key,childKey);
        updateValue();
    }

    @Override
    public void update(String key) {
        Log.d("TEST","test");
    }

    @Override
    public String toString() {
        return "HashMapValue{"
                + "collection:" + collection
                + " collectionModified:" + collectionModified
                + " keyList:" + keyList
                + " " + super.toString()
                + "}";
    }
}
