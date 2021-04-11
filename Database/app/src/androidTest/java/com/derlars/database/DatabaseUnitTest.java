package com.derlars.database;

import com.google.firebase.database.DataSnapshot;

import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseUnitTest implements Database.Callback {

    private boolean valueRetrievedFlag = false;
    private boolean noValueRetrievedFlag = false;
    private boolean valueDeletedFlag = false;
    private boolean childAddedFlag = false;
    private boolean childDeletedFlag = false;


    @Test
    public void setValueTest() {
        Database database = new Database("UnitTest", "setValueUnitTest",this);

        //database.setValue(15);

        waitFor(5000);
    }

    private void resetFlags() {
        valueRetrievedFlag = false;
        noValueRetrievedFlag = false;
        valueDeletedFlag = false;
        childAddedFlag = false;
        childDeletedFlag = false;
    }

    private void waitFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void databaseValueRetrieved(String path, String key, DataSnapshot dataSnapshot) {

    }

    @Override
    public void databaseNoValueRetrieved(String path, String key) {

    }

    @Override
    public void databaseValueDeleted(String path, String key) {

    }

    @Override
    public void databaseChildAdded(String path, String key, String childKey) {

    }

    @Override
    public void databaseChildDeleted(String path, String key, String childKey) {

    }
}
