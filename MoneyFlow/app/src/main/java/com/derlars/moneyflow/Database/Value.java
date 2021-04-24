package com.derlars.moneyflow.Database;

import android.util.Log;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.google.firebase.database.DataSnapshot;

public class Value<V extends Comparable> extends BaseValue {
    private V databaseValue;
    private V value;

    public Value(String path, String key, boolean readable, boolean writable, boolean connectOnRequest, BaseValueCallback callback) {
        super(path, key, readable, writable,connectOnRequest, callback);
    }

    public void set(V v) {
        if(databaseValue == null || writable) {
            //Log.d("UNITTEST","Setting value for " + this.getClass().getName() + " -> " + this.path + "/" + this.key + ": " + v);
            value = v;
        }

        if(writable && online && connected && (databaseValue == null || value.compareTo(databaseValue) != 0)){
            database.setValue(v);
        }
    }

    public V get() {
        database.getValue();

        //Log.d("UNITTEST","Getting value for " + this.getClass().getName() + " -> " + this.path + "/" + this.key + ": " + value);
        return value;
    }

    public void delete() {
        value = null;

        if(writable && online) {
            database.deleteValue();
        }
    }

    private void updateValue() {
        if(readable && online) {
            V tmp = (V)database.getValue();

            //Log.d("UNITTEST","Updating value for " + this.getClass().getName() + " -> " + this.path + "/" + this.key + ": " + value);

            if(tmp != null && (databaseValue == null || tmp.compareTo(databaseValue) != 0)) {
                value = tmp;
                notifyUpdate(key);
            }else if(tmp == null) {
                notifyNotOnline(key);
            }
        }

        databaseValue = (V)database.getValue();
    }

    @Override
    public void setOnline() {
        if(!online) {
            super.setOnline();

            if(writable && isConnected()) {
                database.setValue(value);
            }
        }
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        super.databaseValueRetrieved(path, key, dataSnapshot);

        settingOnline = false;
        updateValue();
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

        updateValue();
    }

    @Override
    public void update(String key) {
        setOnline();
        notifyUpdate(this.key);
    }
}
