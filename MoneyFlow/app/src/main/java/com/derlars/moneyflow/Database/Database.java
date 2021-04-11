package com.derlars.moneyflow.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.derlars.moneyflow.Database.Callbacks.DatabaseCallback;
import com.derlars.moneyflow.Database.Subscriptables.SubscriptableDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Database<Callback extends DatabaseCallback> extends SubscriptableDatabase<Callback> {
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    public final String path;
    public final String key;

    private boolean deletionOngoing = false;
    private boolean connected = false;

    private DataSnapshot dataSnapshot = null;

    private List<String> deletionList = new ArrayList();

    public Database(final String path, final String key, final Callback callback) {
        super(callback);

        this.path = path;
        this.key = key;

        ref = db.getReference(this.path);

        connect();
    }

    private void connect() {
        if(!connected) {
            ref.child(this.key).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (isSnapshotValid(snapshot)) {
                        notifyChildAdded(path, key, getSnapshotKey(snapshot));
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (isSnapshotValid(snapshot)) {
                        notifyChildChanged(path, key, getSnapshotKey(snapshot));
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    if (isSnapshotValid(snapshot)) {
                        notifyChildDeleted(path, key, getSnapshotKey(snapshot));
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (isSnapshotValid(snapshot)) {
                        notifyChildDeleted(path, key, getSnapshotKey(snapshot));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            ref.child(this.key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataSnapshot = snapshot;

                    if(isSnapshotValid(snapshot)) {
                        notifyValueRetrieved(path,key,dataSnapshot);
                    }else{
                        notifyNoValueRetrieved(path,key);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyNoValueRetrieved(path, key);
                }
            });
            connected = true;
        }
    }

    public Object getValue() {
        if (isSnapshotValid(dataSnapshot)) {
            return dataSnapshot.getValue();
        }
        return null;
    }

    public <T> void setValue(T value) {
        if(value != null) {
            ref.child(key).setValue(value);
        }
    }

    public <T> void setValue(String subKey, T value) {
        ref.child(key).child(subKey).setValue(value);
    }

    public <T> void setValue(String subKey, String subSubKey, T value) {
        ref.child(key).child(subKey).child(subSubKey).setValue(value);
    }

    public void deleteValue() {
        if (!deletionList.contains("")) {
            deletionList.add("");
        }
        if (deletionList.size() > 0) {
            delete(deletionList.get(0));
        }
    }

    public void deleteValue(String subKey) {
        String deleteKey = subKey;
        if (!deletionList.contains("") && !deletionList.contains(deleteKey)) {
            deletionList.add(deleteKey);
        }

        if (deletionList.size() > 0) {
            delete(deletionList.get(0));
        }
    }

    public void deleteValue(String subKey, String subSubKey) {
        String deleteKey = subKey + "/" + subSubKey;
        if (!deletionList.contains("") && !deletionList.contains(subKey) && !deletionList.contains(deleteKey)) {
            deletionList.add(deleteKey);
        }
        if (deletionList.size() > 0) {
            delete(deletionList.get(0));
        }
    }

    private void delete(final String deleteKey) {
            if (!deletionOngoing) {
                deletionOngoing = true;

                DatabaseReference deleteRef = ref.child(key);
                for (String s : deleteKey.split("/")) {
                    deleteRef = deleteRef.child(s);
                }

                deleteRef.removeValue().addOnSuccessListener(aVoid -> {
                    deletionOngoing = false;

                    notifyValueDeleted(path, key);

                    deletionList.remove(deleteKey);
                    if (deletionList.size() > 0) {
                        delete(deletionList.get(0));
                    }
                }).addOnFailureListener(e -> {
                    deletionOngoing = false;

                    deletionList.remove(deleteKey);
                    if (deletionList.size() > 0) {
                        delete(deletionList.get(0));
                    }
                });
            }
    }

    private boolean isSnapshotValid(DataSnapshot dataSnapshot) {
        return (dataSnapshot != null && dataSnapshot.getValue() != null);
    }

    private String getSnapshotKey(DataSnapshot dataSnapshot) {
        return isSnapshotValid(dataSnapshot) ? dataSnapshot.getKey() : null;
    }

    private Object getSnapshotValue(DataSnapshot dataSnapshot) {
        return isSnapshotValid(dataSnapshot) ? dataSnapshot.getValue() : null;
    }

    public String toString() {
        return "Database {path:" + path + " key:" + key + " deletionOngoing:" + deletionOngoing + " connected:" + connected + " dataSnapshot:" + dataSnapshot + " " + super.toString() + "}";
    }
}
