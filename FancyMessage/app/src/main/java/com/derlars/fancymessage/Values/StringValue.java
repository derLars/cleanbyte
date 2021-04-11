package com.derlars.password_server.Values;

import android.widget.TextView;

import com.google.firebase.database.Exclude;

public class StringValue extends Value {
    public StringValue(String BaseReference, String reference, Callback callback) {
        super(BaseReference, reference, callback);
    }

    @Exclude
    public void getValue(TextView textView) {
        if(!requestOngoing && (value == null || keepOffline)) {
            requestOngoing = true;
            database.retrieveValue(reference,textView);
        }
        if(value != null && textView != null) {
            textView.setText((String)value);
        }
    }
}
