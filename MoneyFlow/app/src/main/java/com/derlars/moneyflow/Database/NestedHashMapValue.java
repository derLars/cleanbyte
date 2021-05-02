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

public class NestedHashMapValue<V> extends BaseValue {

    private Map<String,HashMap<String,V>> collection = new HashMap();
    private boolean collectionModified = true;

    private Map<String,HashMap<String,V>> databaseValue;

    private List<String> keyList = new ArrayList();
    private Map<String,List<String>> subKeyLists = new HashMap();

    private final ScheduledExecutorService confirmator = Executors.newSingleThreadScheduledExecutor();

    private Queue<Confirmation<V>> toBeConfirmed = new LinkedList<Confirmation<V>>();
    private boolean confirmationEnabled = false;

    public NestedHashMapValue(String path, String key, boolean readable, boolean writable, boolean connectOnRequest, BaseValueCallback callback) {
        super(path, key, readable, writable,connectOnRequest, callback);
    }

    public void put(String k1, String k2, V v) {
        if(writable) {
            insert(k1, k2, v);

            if(isOnline()) {
                database.setValue(k1,k2,v);
                confirmKeys(k1, k2, v);
            }
        }
    }

    private void insert(String k1, String k2, V v) {
        if(!collection.containsKey(k1)) {
            collection.put(k1,new HashMap());
            if(!subKeyLists.containsKey(k1)) {
                subKeyLists.put(k1,new ArrayList());
            }
        }
        collection.get(k1).put(k2,v);
        collectionModified = true;
    }

    public void delete(String k1) {
        if(writable) {
            remove(k1);

            if(isOnline()) {
                database.deleteValue(k1);
            }
        }
    }

    public void delete(String k1, String k2) {
        if(writable) {
            remove(k1, k2);

            if(isOnline()) {
                database.deleteValue(k1,k2);
            }
        }
    }

    private void remove(String k1) {
        if(collection.containsKey(k1)) {
            collection.remove(k1);
            subKeyLists.get(k1).clear();

            collectionModified = true;
        }
    }

    private void remove(String k1, String k2) {
        if(collection.containsKey(k1)) {
            if(collection.get(k1).containsKey(k2)){
                collection.get(k1).remove(k2);

                collectionModified = true;
            }
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

    public V get(String k1, String k2) {
        if(collection.containsKey(k1)) {
            return collection.get(k1).get(k2);
        }
        return null;
    }

    public List<String> getKeyList() {
        sort();

        return keyList;
    }

    public List<String> getSubKeyList(String k1) {
        sort();

        return subKeyLists.get(k1);
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

    private Set<String> getKeySet(String k1) {
        while(true) {
            try {
                Set<String> keySet = new HashSet(collection.get(k1).keySet());

                return keySet;
            }catch(ConcurrentModificationException ex) {

            }
        }
    }

    private Map<String, Map<String,V>> getCollection() {
        Map<String, Map<String,V>> collectionMap;

        while(true) {
            collectionMap = new HashMap();

            try {
                Set<String> keySet = getKeySet();
                for(String k1 : keySet) {
                    collectionMap.put(k1,new HashMap());

                    Set<String> subKeySet = getKeySet(k1);
                    for(String k2 : subKeySet) {
                        collectionMap.get(k1).put(k2,collection.get(k1).get(k2));
                    }
                }

                return collectionMap;
            }catch(ConcurrentModificationException ex) {

            }
        }
    }

    private void updateCollection() {
        if(isOnline()) {
            collectionModified = true;
            databaseValue = (Map<String,HashMap<String,V>>)database.getValue();
            if(databaseValue != null) {
                for(String k1 : databaseValue.keySet()) {
                    for(String k2 : databaseValue.get(k1).keySet()) {
                        insert(k1,k2,databaseValue.get(k1).get(k2));
                    }
                }
                Set<String> keySet = getKeySet();
                for(String k1 : keySet) {
                    if(databaseValue.containsKey(k1)) {
                        Set<String> subKeySet = getKeySet(k1);

                        for (String k2 : subKeySet) {
                            if (!databaseValue.get(k1).containsKey(k2)) {
                                remove(k1, k2);
                            }
                        }
                    }else{
                        remove(k1);
                    }
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
            for(String k1 : keySet) {
                keyList.add(k1);
            }

            Collections.sort(keyList);

            keySet = getKeySet();
            for(String k1 : keySet) {
                if(!subKeyLists.containsKey(k1)) {
                    subKeyLists.put(k1,new ArrayList());
                }
                List l = subKeyLists.get(k1);
                l.clear();

                Set<String> subKeySet = getKeySet(k1);
                for(String k2 : subKeySet) {
                    l.add(k2);
                }
                Collections.sort(l);
            }
        }
    }

    private void confirmKeys(String k1, String k2, V v) {
        Runnable ru = () -> {
            if(!toBeConfirmed.isEmpty()) {
                Confirmation c = toBeConfirmed.remove();
                if(!collection.containsKey(c.k1) || !collection.get(k1).containsKey(c.k2)) {
                    database.setValue(c.k1,c.k2,c.v);
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
        toBeConfirmed.add(new Confirmation<V>(System.currentTimeMillis()+ 950, k1,k2,v,ru));

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
        if(writable && isConnecting() && collection.size() > 0){
            Map<String,Map<String, V>> collectionMap = getCollection();

            for(String k1 : collectionMap.keySet()) {
                Set<String> subKeySet = collectionMap.get(k1).keySet();
                for(String k2 : subKeySet) {
                    database.setValue(k1,k2,collectionMap.get(k1).get(k2));
                }
            }
        }else{
            super.databaseNoValueRetrieved(path,key);
        }

        notifyNotOnline(key);
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        super.databaseValueDeleted(path,key);

        updateCollection();
    }

    @Override
    public void databaseChildAdded(String path, String key, String childKey) {
        super.databaseChildAdded(path,key,childKey);

        updateCollection();
    }

    @Override
    public void databaseChildDeleted(String path, String key, String childKey) {
        super.databaseChildDeleted(path,key,childKey);

        updateCollection();
    }

    @Override
    public void databaseChildChanged(String path, String key, String childKey) {
        super.databaseChildChanged(path,key,childKey);

        updateCollection();
    }

    @Override
    public String toString() {
        return "NestedHashMapValue{"
                + "collection:" + collection
                + " collectionModified:" + collectionModified
                + " keyList:" + keyList
                + " subKeyLists:" + subKeyLists
                + " " + super.toString()
                + "}";
    }
}
