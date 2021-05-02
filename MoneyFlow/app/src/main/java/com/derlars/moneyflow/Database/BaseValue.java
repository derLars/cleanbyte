package com.derlars.moneyflow.Database;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.derlars.moneyflow.Database.Callbacks.DatabaseCallback;
import com.derlars.moneyflow.Database.Enums.DatabaseStatus;
import com.derlars.moneyflow.Database.Subscriptables.SubscriptableBaseValue;
import com.google.firebase.database.DataSnapshot;

public abstract class BaseValue<Callback extends BaseValueCallback> extends SubscriptableBaseValue<Callback> implements DatabaseCallback {
    protected Database database;

    protected final String path;

    protected final String key;

    //protected boolean online = false;

    //protected boolean connected = false;

    //protected boolean settingOnline = false;

    protected boolean readable;
    protected boolean writable;
    protected boolean connectOnRequest;

    protected DatabaseStatus databaseStatus = DatabaseStatus.OFFLINE;

    public BaseValue(final String path, final String key, boolean readable, boolean writable, boolean connectOnRequest, Callback callback) {
        super(callback);

        this.path = path;

        this.key = key;

        this.readable = readable;

        this.writable = writable;

        this.connectOnRequest = connectOnRequest;
    }

    public void setOnline() {
        if(databaseStatus == DatabaseStatus.OFFLINE) {
            databaseStatus = readable ? DatabaseStatus.CONNECTING : DatabaseStatus.ONLINE;

            database = new Database(path,key, readable,writable,connectOnRequest, this);
        }
    }

    public boolean isOnline() {
        return this.databaseStatus.ordinal() > DatabaseStatus.CONNECTING.ordinal();
    }

    public boolean isConnecting() {
        return this.databaseStatus == DatabaseStatus.CONNECTING;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {
        this.databaseStatus = DatabaseStatus.UPDATED;

        //online = true;
        //connected = true;
    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {
        this.databaseStatus = DatabaseStatus.NOT_FOUND;

        //connected = true;
    }

    @Override
    public void databaseValueDeleted(String path, String key) {
        this.databaseStatus = DatabaseStatus.DELETED;

        //online = true;
        //connected = true;
    }

    @Override
    public void databaseChildAdded(String path, String key, String childKey) {
        //online = true;
        //connected = true;
    }

    @Override
    public void databaseChildDeleted(String path, String key, String childKey) {
        //online = true;
        //connected = true;
    }

    @Override
    public void databaseChildChanged(String path, String key, String childKey) {
        //online = true;
        //connected = true;
    }

    @Override
    public void update(String key) {

    }

    public String toString() {
        return "BaseValue{path:" + path
                + " key:" + key
                + " " + super.toString()
                + "}";
    }
}
