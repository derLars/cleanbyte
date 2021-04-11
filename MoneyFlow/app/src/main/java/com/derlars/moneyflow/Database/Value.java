package com.derlars.moneyflow.Database;

import android.util.Log;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.google.firebase.database.DataSnapshot;

public class Value<V extends Comparable> extends BaseValue {
    private V databaseValue;
    private V value;

    protected boolean readOnly;

    public Value(String path, String key, BaseValueCallback callback) {
        this(path, key, callback,false);
    }

    public Value(String path, String key, BaseValueCallback callback, boolean readOnly) {
        super(path, key, callback);
        this.readOnly = readOnly;
    }

    public void set(V v) {
        if(!readOnly || !online) {
            value = v;
        }

        if(!readOnly && online && connected && (databaseValue == null || value.compareTo(databaseValue) != 0)) {
            database.setValue(v);
        }
    }

    public V get() {
        return value;
    }

    public void delete() {
        value = null;

        if(online) {
            database.deleteValue();
        }
    }

    private void update() {
        if(online) {
            value = (V)database.getValue();
            if(value != null && (databaseValue == null || value.compareTo(databaseValue) != 0)) {
                notifyUpdate(key);
            }else if(value == null) {
                notifyNotOnline(key);
            }
        }else{
            Log.d("MUTEX",this.key + " is not considered as online");
        }

        databaseValue = (V)database.getValue();
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public void setOnline() {
        if(!online) {
            super.setOnline();
            if(isConnected()) {
                database.setValue(value);
            }else{
                Log.d("MUTEX","not connected...");
            }
        }
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        super.databaseValueRetrieved(path, key, dataSnapshot);

        settingOnline = false;
        update();
    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {
        super.databaseNoValueRetrieved(path, key);

        if(settingOnline && value != null) {
            database.setValue(value);
        }

        settingOnline = false;
        notifyNotOnline(key);
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        super.databaseValueDeleted(path, key);

        update();
    }

    @Override
    public void update(String key) {

    }
}
