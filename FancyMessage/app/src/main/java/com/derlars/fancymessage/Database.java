package com.derlars.password_server.Utils;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Database {
    private final static String TAG = "DEBUGPRINT";

    private Callback callback;

    public interface Callback{
        void valueRetrieved(String reference, String path, DataSnapshot value);
        void valueDeleted();
        void noValueRetrieved();
    }

    private final static FirebaseDatabase db = FirebaseDatabase.getInstance();

    private final DatabaseReference ref;
    private final String reference;

    public Database(String reference) {
        this.reference = reference;
        ref = db.getReference(reference);
    }

    public Database(String reference, Callback callback) {
        this(reference);
        this.callback = callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void retrieveValue(final String path) {
        retrieveValue(path,null);
    }

    public void retrieveValue(final String path, final TextView textView) {

        ref.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    callback.valueRetrieved(reference, path, dataSnapshot);

                    if(textView != null) {
                        textView.setText((String)dataSnapshot.getValue());
                    }
                }catch(Exception ex) {
                    if(dataSnapshot.getKey() != null) {
                        Log.e(TAG,dataSnapshot.getKey());
                        Log.e(TAG," returning: " + ex.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.noValueRetrieved();
            }
        });
    }

    public <T> void setValue(String path, T value) {
        try {
            ref.child(path).setValue(value);
        }catch(Exception ex) {
            Log.e(TAG,"Database: " + ex.toString());
        }
    }

    public void deleteValue(final String path) {
        ref.child(path).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(callback != null) {
                    callback.valueDeleted();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Deletion: " + e.toString());
            }
        });
    }
}
