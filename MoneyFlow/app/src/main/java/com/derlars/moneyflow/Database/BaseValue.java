package com.derlars.moneyflow.Database;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.derlars.moneyflow.Database.Callbacks.DatabaseCallback;
import com.derlars.moneyflow.Database.Subscriptables.SubscriptableBaseValue;
import com.google.firebase.database.DataSnapshot;

public abstract class BaseValue<Callback extends BaseValueCallback> extends SubscriptableBaseValue<Callback> implements DatabaseCallback {
    protected Database database;

    protected final String path;

    protected final String key;

    protected boolean online = false;

    protected boolean connected = false;

    protected boolean settingOnline = false;

    protected boolean readable;
    protected boolean writable;
    protected boolean connectOnRequest;

    public BaseValue(final String path, final String key, boolean readable, boolean writable, boolean connectOnRequest, Callback callback) {
        super(callback);

        this.path = path;

        this.key = key;

        this.readable = readable;

        this.writable = writable;

        this.connectOnRequest = connectOnRequest;

        database = new Database(path,key, readable,writable,connectOnRequest, this);
    }

    public void setOnline() {
        if(readable) {
            this.settingOnline = true;
        }else{
            this.connected = true;
        }
        this.online = true;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        online = true;
        connected = true;
    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {
        connected = true;
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        online = true;
        connected = true;
    }

    @Override
    public void databaseChildAdded(String path, String key, String childKey) {
        online = true;
        connected = true;
    }

    @Override
    public void databaseChildDeleted(String path, String key, String childKey) {
        online = true;
        connected = true;
    }

    @Override
    public void databaseChildChanged(String path, String key, String childKey) {
        online = true;
        connected = true;
    }

    public String toString() {
        return "BaseValue{path:" + path
                + " key:" + key
                + " online:" + online
                + " connected:" + connected
                + " " + super.toString()
                + "}";
    }
}
