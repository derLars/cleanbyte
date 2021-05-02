package com.derlars.moneyflow.Database;

import android.util.Log;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.derlars.moneyflow.Database.Enums.DatabaseStatus;
import com.google.firebase.database.DataSnapshot;

public class Value<V extends Comparable> extends BaseValue {
    private V databaseValue;
    private V value;

    public Value(String path, String key, boolean readable, boolean writable, boolean connectOnRequest, BaseValueCallback callback) {
        super(path, key, readable, writable,connectOnRequest, callback);
    }

    public void set(V v) {
        if(writable || !isOnline()) {
            value = v;
        }

        if(writable && isOnline() && (databaseValue == null || value.compareTo(databaseValue) != 0) ) {
            database.setValue(v);
        }
    }

    public V get() {
        database.getValue();

        return value;
    }

    public void delete() {
        if(writable) {
            value = null;
            databaseValue = null;

            if(isOnline()) {
                database.deleteValue();
            }
        }
    }

    private void updateValue() {
        if(readable) {
            V dbValue = (V)database.getValue();

            if(dbValue != null && (databaseValue == null || dbValue.compareTo(databaseValue) != 0)) {
                value = dbValue;
                notifyUpdate(key);
            }else if(dbValue == null) {
                notifyNotOnline(key);
            }
        }

        databaseValue = (V)database.getValue();
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        super.databaseValueRetrieved(path, key, dataSnapshot);

        updateValue();
    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {
        if(writable && isConnecting() && value != null){
            database.setValue(value);
        }else{
            super.databaseNoValueRetrieved(path, key);
        }

        notifyNotOnline(key);
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        super.databaseValueDeleted(path, key);

        updateValue();
    }

}
