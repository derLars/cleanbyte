package com.derlars.moneyflow.Database.Callbacks;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.google.firebase.database.DataSnapshot;

public interface DatabaseCallback extends BaseCallback {
    void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot);
    void databaseNoValueRetrieved(String path, String key);
    void databaseValueDeleted(String path, String key);
    void databaseChildAdded(String path, String key, String childKey);
    void databaseChildDeleted(String path, String key, String childKey);
    void databaseChildChanged(String path, String key, String childKey);
}
