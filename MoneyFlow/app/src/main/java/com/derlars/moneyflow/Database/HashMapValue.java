package com.derlars.moneyflow.Database;

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
        if(writable) {
            insert(k, v);

            if(isOnline()) {
                database.setValue(k,v);
                confirmKeys(k,v);
            }
        }
    }

    private void insert(String k, V v) {
        collection.put(k,v);
        collectionModified = true;
    }

    public void delete(String k) {
        if(writable) {
            remove(k);

            if(isOnline()) {
                database.deleteValue(k);
            }
        }
    }

    private void remove(String k) {
        if(collection.containsKey(k)) {
            collection.remove(k);

            collectionModified = true;
        }
    }

    public void clear() {
        if(writable) {
            collection.clear();
            collectionModified = true;

            if(isOnline()) {
                database.deleteValue();
            }
        }
    }

    public V get(String k) {
        if(readable && isOnline()) {
            database.getValue();
        }

        if(collection.containsKey(k)) {
            return collection.get(k);
        }
        return null;
    }

    public List<String> getKeyList() {
        if(readable && isOnline()) {
            database.getValue();
        }

        sort();

        return keyList;
    }

    private Set<String> getKeySet() {
        while(true) {
            try {
                Set<String> keySet = new HashSet(collection.keySet());

                return keySet;
            }catch(ConcurrentModificationException ex) {

            }
        }
    }

    private Map<String,V> getCollection() {
        Map<String, V> collectionMap;

        while(true) {
            collectionMap = new HashMap();

            try {
                Set<String> keySet = getKeySet();
                for(String k1 : keySet) {
                    collectionMap.put(k1,collection.get(k1));
                }

                return collectionMap;
            }catch(ConcurrentModificationException ex) {

            }
        }
    }

    protected void updateCollection() {
        if(readable && isOnline()) {
            collectionModified = true;

            databaseValue = (Map<String,V>)database.getValue();

            if(databaseValue != null) {
                for(String k : databaseValue.keySet()) {
                    insert(k,databaseValue.get(k));
                }
            }

            Set<String> keySet = getKeySet();

            for(String k : keySet) {
                if(!databaseValue.containsKey(k)) {
                    remove(k);
                }
            }

            sort();

            notifyUpdate(key);
        }
    }

    private void sort() {
        if(collectionModified) {
            collectionModified = false;
            keyList.clear();

            Set<String> keySet = getKeySet();

            for(String k : keySet) {
                keyList.add(k);
            }

            Collections.sort(keyList);
        }
    }

    private void confirmKeys(String k, V v) {
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

        toBeConfirmed.add(new Confirmation(System.currentTimeMillis()+ 950, k,v,ru));

        if(!confirmationEnabled) {
            confirmationEnabled = true;

            confirmator.schedule(ru, 950, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        super.databaseValueRetrieved(path,key,dataSnapshot);

        updateCollection();
    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {
        if(writable && isConnecting() && collection.size() > 0) {
            Map<String,V> collectionMap =getCollection();

            for(String k1 : collectionMap.keySet()) {
                database.setValue(k1,collectionMap.get(k1));
            }
        }else{
            super.databaseNoValueRetrieved(path,key);
        }


        super.databaseNoValueRetrieved(path,key);

        notifyNotOnline(key);
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        super.databaseValueDeleted(path,key);

        updateCollection();
    }

    @Override
    public void databaseChildAdded(String path, String key, String childKey) {
        updateCollection();
    }

    @Override
    public void databaseChildDeleted(String path, String key, String childKey) {
        updateCollection();
    }

    @Override
    public void databaseChildChanged(String path, String key, String childKey) {
        updateCollection();
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
