package com.derlars.databaseapi.Datatypes;

import com.derlars.databaseapi.Value;

public class String extends Value<java.lang.String> {
    public String(java.lang.String baseReference, java.lang.String reference, Callback callback) {
        super(baseReference, reference, callback);
    }
}
