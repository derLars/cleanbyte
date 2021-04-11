package com.derlars.password_server.Values;

import com.derlars.password_server.Utils.Database;
import com.google.firebase.database.DataSnapshot;

public class Value<T> implements Database.Callback {
    public final static String TAG = "DEBUGPRINT";

    public interface Callback {
        void updateAvailable();
        void deleted();
        void notFound(String reference);
    }

    protected T value;
    protected T lastUpdatedValue;

    protected Callback callback;

    protected Database database;

    protected String reference;

    protected String baseReference;

    protected boolean requestOngoing = false;

    protected boolean keepOffline = false;

    public Value(String baseReference, String reference, Callback callback) {
        database = new Database(baseReference,this);

        this.baseReference = baseReference;
        this.reference = reference;
        this.callback = callback;
    }

    public void setValue(T value) {
        if(this.value == null || this.value != value) {
            if(!keepOffline) {
                database.setValue(reference, value);
            }
            this.value = value;
            this.lastUpdatedValue = value;
        }
    }

    public T getValue() {
        if(!requestOngoing && (value == null || keepOffline)) {
            requestOngoing = true;
            database.retrieveValue(reference);
        }

        return value;
    }

    public void valueRetrieved(String reference, String path, DataSnapshot value) {
        requestOngoing = false;
        if(value != null && value.getValue() != null) {
            keepOffline = false;

            this.value = (T)value.getValue();

            if(this.lastUpdatedValue == null || !this.lastUpdatedValue.equals((T)value.getValue())) {
                this.lastUpdatedValue = this.value;

                if(callback != null) {
                    callback.updateAvailable();
                }
            }
        }else if(value.getValue() == null && this.value != null) {
            this.value = null;
            this.lastUpdatedValue = null;
            if(callback != null) {
                callback.notFound(reference);
            }
        }else{
            if(callback != null) {
                callback.notFound(reference);
            }
        }
    }

    public void valueDeleted() {
        this.value = null;
        this.lastUpdatedValue = null;

        if(callback != null) {
            callback.deleted();
        }
    }

    public void noValueRetrieved() {
        requestOngoing = false;

        if(callback != null) {
            callback.notFound(reference);
        }
    }

    public boolean isOnline() {
        return !keepOffline;
    }

    public void setKeepOffline(boolean keepOffline) {
        this.keepOffline = keepOffline;
    }

    public String getReference() {
        return reference;
    }

    public String getBaseReference() {
        return baseReference;
    }

    public void remove() {
        database.deleteValue(reference);
    }

    public boolean isRequestOngoing() {
        return requestOngoing;
    }

    public String toString() {
        return reference + " = " + value.toString() + "; ";
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
