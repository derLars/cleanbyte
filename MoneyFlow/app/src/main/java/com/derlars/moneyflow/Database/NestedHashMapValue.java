package com.derlars.moneyflow.Database;

import android.util.Log;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    private List<String> keyList = new ArrayList();
    private Map<String,List<String>> subKeyLists = new HashMap();

    private final ScheduledExecutorService confirmator = Executors.newSingleThreadScheduledExecutor();

    private Queue<Confirmation<V>> toBeConfirmed = new LinkedList<Confirmation<V>>();
    private boolean confirmationEnabled = false;

    public NestedHashMapValue(String path, String key, BaseValueCallback callback) {
        super(path, key, callback);
    }

    public void put(String k1, String k2, V v) {
        insert(k1,k2,v);

        if(online) {
            database.setValue(k1,k2,v);
            ConfirmKeys(k1, k2, v);
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
        remove(k1);

        if(online) {
            database.deleteValue(k1);
        }
    }

    public void delete(String k1, String k2) {
        remove(k1, k2);

        if(online) {
            database.deleteValue(k1,k2);
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
        collection.clear();
        collectionModified = true;

        if(online) {
            database.deleteValue();
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

    private void update() {
        if(online) {
            collectionModified = true;
            Map<String,HashMap<String,V>> databaseCollection = (Map<String,HashMap<String,V>>)database.getValue();
            if(databaseCollection != null) {
                for(String k1 : databaseCollection.keySet()) {
                    for(String k2 : databaseCollection.get(k1).keySet()) {
                        insert(k1,k2,databaseCollection.get(k1).get(k2));
                    }
                }

                Set<String> keySet = new HashSet(collection.keySet());

                for(String k1 : keySet) {
                    if(databaseCollection.containsKey(k1)) {
                        Set<String> subKeySet = new HashSet(collection.get(k1).keySet());
                        for(String k2 : subKeySet) {
                            if(!databaseCollection.get(k1).containsKey(k2)) {
                                remove(k1,k2);
                            }
                        }
                    }else{
                        remove(k1);
                    }
                }

                sort();

                notifyUpdate(key);
            }
        }
    }

    private void sort() {
        if(collectionModified) {
            collectionModified = false;
            keyList.clear();

            Set<String> s = new HashSet<>(collection.keySet());
            for(String k1 : s) {
                keyList.add(k1);
            }

            Collections.sort(keyList);

            s = new HashSet<>(collection.keySet());
            for(String k1 : s) {
                if(!subKeyLists.containsKey(k1)) {
                    subKeyLists.put(k1,new ArrayList());
                }
                List l = subKeyLists.get(k1);
                l.clear();

                Set<String> sSub = new HashSet<>(collection.get(k1).keySet());
                for(String k2 : sSub) {
                    l.add(k2);
                }
                Collections.sort(l);
            }
        }
    }

    private void ConfirmKeys(String k1, String k2, V v) {
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



        System.out.println("start singleThreadScheduledExecutor at " + System.currentTimeMillis());

        //ses.shutdown();// shutDown auch bei singleshot notwendig
    }

    @Override
    public void setOnline() {
        if(!online) {
            super.setOnline();
            database.setValue(collection);
        }
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        online = true;
        update();
    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {
        notifyNotOnline(key);
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        online = true;
        update();
    }

    @Override
    public void databaseChildAdded(String path, String key, String childKey) {
        online = true;
        update();
    }

    @Override
    public void databaseChildDeleted(String path, String key, String childKey) {
        online = true;
        update();
    }

    @Override
    public void databaseChildChanged(String path, String key, String childKey) {
        online = true;
        update();
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

    @Override
    public void update(String key) {

    }
}
