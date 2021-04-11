package com.derlars.moneyflow.Database.Subscriptables;

import com.derlars.moneyflow.Database.Abstracts.Subscriptable;
import com.derlars.moneyflow.Database.Callbacks.DatabaseCallback;
import com.google.firebase.database.DataSnapshot;

public abstract class SubscriptableDatabase<Callback extends DatabaseCallback> extends Subscriptable<Callback> {

    public SubscriptableDatabase(Callback callback) {
        super(callback);
    }

    public void notifyValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        for(Callback c : callbacks) {
            if(c != null) {
                c.databaseValueRetrieved(path,key,dataSnapshot);
            }
        }
    }

    public void notifyNoValueRetrieved(String path, String key) {
        for(Callback c : callbacks) {
            if(c != null) {
                c.databaseNoValueRetrieved(path,key);
            }
        }
    }

    public void notifyValueDeleted(String path, String key) {
        for(Callback c : callbacks) {
            if(c != null) {
                c.databaseValueDeleted(path,key);
            }
        }
    }

    public void notifyChildAdded(String path, String key, String childKey) {
        for(Callback c : callbacks) {
            if(c != null) {
                c.databaseChildAdded(path,key,childKey);
            }
        }
    }

    public void notifyChildDeleted(String path, String key, String childKey) {
        for(Callback c : callbacks) {
            if(c != null) {
                c.databaseChildDeleted(path, key, childKey);
            }
        }
    }

    public void notifyChildChanged(String path, String key, String childKey) {
        for(Callback c : callbacks) {
            if(c != null) {
                c.databaseChildChanged(path, key, childKey);
            }
        }
    }
}
