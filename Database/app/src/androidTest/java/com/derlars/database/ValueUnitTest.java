package com.derlars.database;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.derlars.database.Database.BaseValueCallback;
import com.derlars.database.Database.HashMapValue;
import com.derlars.database.Database.NestedHashMapValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class HashMapUnitTest implements BaseValueCallback.Callback {
    private boolean valueUpdatedFlag = false;
    private boolean valueNotOnlineFlag = false;

    private final static int DELAY = 1000;

    @Before
    public void before() {
        HashMapValue<Integer> preset = new HashMapValue("UnitTest","keepOfflineTest",null);
        preset.clear();
        waitFor(DELAY);

        preset = new HashMapValue("UnitTest","onlineTest",null);
        preset.clear();
        waitFor(DELAY);

        preset = new HashMapValue("UnitTest","interferenceTest",null);
        preset.clear();
        waitFor(DELAY);
    }

    @Test
    public void keepOfflineTest() {
        HashMapValue<Integer> setHashMap = new HashMapValue("UnitTest","keepOfflineTest",null);
        HashMapValue<Integer> getHashMap = new HashMapValue("UnitTest","keepOfflineTest",this);

        checkFlags(false,true);

        setHashMap.put("key1",15);

        checkFlags(false,false);

        int value = setHashMap.get("key1");
        assertEquals(15,value);

        setHashMap.put("key1",19);

        setHashMap.put("key2",41);

        value = setHashMap.get("key1");
        assertEquals(19,value);

        int keysCount = setHashMap.getKeyList().size();
        assertEquals(2,keysCount);

        setHashMap.delete("key2");

        keysCount = setHashMap.getKeyList().size();
        assertEquals(1,keysCount);

        setHashMap.clear();

        keysCount = setHashMap.getKeyList().size();
        assertEquals(0,keysCount);

        setHashMap.clear();
        waitFor(DELAY);
    }

    @Test
    public void onlineTest() {
        HashMapValue<Integer> setHashMap = new HashMapValue("UnitTest","onlineTest",null);

        waitFor(DELAY);

        setHashMap.put("key1",45);
        setHashMap.setOnline();

        waitFor(DELAY);

        HashMapValue<Integer> getHashMap = new HashMapValue("UnitTest","onlineTest",this);

        checkFlags(true,false);

        setHashMap.put("key2",75);

        checkFlags(true,false);

        setHashMap.put("key3",77);

        checkFlags(true,false);

        setHashMap.put("key4",82);
        setHashMap.put("key5",79);

        checkFlags(true,false);

        //There should be two keys
        int keyListSize = getHashMap.getKeyList().size();
        assertEquals(5,keyListSize);

        getHashMap.delete("key1");

        checkFlags(true,false);

        List keyList = getHashMap.getKeyList();
        assertFalse(keyList.contains("key1"));
        assertTrue(keyList.contains("key2"));
        assertTrue(keyList.contains("key3"));
        assertTrue(keyList.contains("key4"));
        assertTrue(keyList.contains("key5"));

        setHashMap.delete("key2");
        setHashMap.delete("key3");
        setHashMap.delete("key4");
        setHashMap.delete("key5");

        checkFlags(true,true);

        setHashMap.clear();
        waitFor(DELAY);
    }

    @Test
    public void interferenceTest() {
        HashMapValue<Integer> setHashMap = new HashMapValue("UnitTest","interferenceTest",null);
        setHashMap.setOnline();

        HashMapValue<Integer> getHashMap = new HashMapValue("UnitTest","interferenceTest",this);

        checkFlags(false,true);

        setHashMap.put("key2",10);
        setHashMap.put("key3",11);
        setHashMap.put("key4",12);
        setHashMap.put("key5",13);
        setHashMap.put("key6",14);
        setHashMap.put("key7",15);
        setHashMap.put("key8",16);
        setHashMap.put("key9",17);
        setHashMap.put("key10",18);
        setHashMap.put("key11",19);
        setHashMap.put("key12",20);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        List keyList = getHashMap.getKeyList();
        assertEquals(11,keyList.size());

        setHashMap.put("key21",21);
        getHashMap.put("key22",22);
        setHashMap.put("key23",23);
        getHashMap.put("key24",24);
        setHashMap.put("key25",25);
        getHashMap.put("key26",26);
        setHashMap.put("key27",27);
        getHashMap.put("key28",28);
        setHashMap.put("key29",29);
        getHashMap.put("key30",30);
        setHashMap.put("key31",31);
        getHashMap.put("key32",32);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        keyList = getHashMap.getKeyList();
        assertEquals(23,keyList.size());

        setHashMap.delete("key21");
        getHashMap.put("key40",40);
        setHashMap.delete("key22");
        getHashMap.put("key41",41);
        setHashMap.delete("key23");
        getHashMap.put("key42",42);
        setHashMap.delete("key25");
        getHashMap.put("key43",43);
        setHashMap.delete("key26");
        getHashMap.put("key44",44);
        setHashMap.delete("key27");
        getHashMap.put("key45",45);
        setHashMap.delete("key29");
        getHashMap.put("key46",46);
        setHashMap.delete("key30");
        getHashMap.put("key47",47);
        setHashMap.delete("key31");
        getHashMap.put("key48",48);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        keyList = getHashMap.getKeyList();
        assertEquals(23,keyList.size());

        setHashMap.delete("key40");
        getHashMap.put("key40",60);
        setHashMap.delete("key41");
        getHashMap.put("key41",61);
        setHashMap.delete("key42");
        getHashMap.put("key42",62);
        setHashMap.delete("key43");
        getHashMap.put("key43",63);
        setHashMap.delete("key44");
        getHashMap.put("key44",64);
        setHashMap.delete("key45");
        getHashMap.put("key45",65);
        setHashMap.delete("key46");
        getHashMap.put("key46",66);
        setHashMap.delete("key47");
        getHashMap.put("key47",67);
        setHashMap.delete("key48");
        getHashMap.put("key48",68);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        keyList = getHashMap.getKeyList();
        assertEquals(23,keyList.size());

        setHashMap.clear();
        waitFor(DELAY);
    }

    private void waitFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkFlags(boolean eValueUpdatedFlag,boolean eValueNotOnlineFlag) {
        waitFor(DELAY);

        assertEquals(eValueUpdatedFlag,valueUpdatedFlag);
        assertEquals(eValueNotOnlineFlag,valueNotOnlineFlag);

        resetFlags();
    }

    private void resetFlags() {
        valueUpdatedFlag = false;
        valueNotOnlineFlag = false;
    }

    @Override
    public void valueUpdated(String key) {
        valueUpdatedFlag = true;
    }

    @Override
    public void valueNotOnline(String key) {
        valueNotOnlineFlag = true;
    }
}
