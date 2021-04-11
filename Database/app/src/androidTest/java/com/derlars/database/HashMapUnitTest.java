package com.derlars.database;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.derlars.database.Database.BaseValue;
import com.derlars.database.Database.BaseValueCallback;
import com.derlars.database.Database.NestedHashMapValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NestedHashMapUnitTest implements BaseValueCallback.Callback {
    private boolean valueUpdatedFlag = false;
    private boolean valueNotOnlineFlag = false;

    private final static int DELAY = 1000;

    @Before
    public void before() {
        NestedHashMapValue<Integer> preset = new NestedHashMapValue("UnitTest","keepOfflineTest",null);
        preset.clear();
        waitFor(DELAY);

        preset = new NestedHashMapValue("UnitTest","onlineTest",null);
        preset.clear();
        waitFor(DELAY);

        preset = new NestedHashMapValue("UnitTest","interferenceTest",null);
        preset.clear();
        waitFor(DELAY);
    }

    @Test
    public void keepOfflineTest() {
        NestedHashMapValue<Integer> setHashMap = new NestedHashMapValue("UnitTest","keepOfflineTest",null);
        NestedHashMapValue<Integer> getHashMap = new NestedHashMapValue("UnitTest","keepOfflineTest",this);

        checkFlags(false,true);

        setHashMap.put("key1","key2",15);

        checkFlags(false,false);

        int value = setHashMap.get("key1","key2");
        assertEquals(15,value);

        setHashMap.put("key1","key2",19);
        setHashMap.put("key1","key3",32);
        setHashMap.put("key1","key4",35);

        setHashMap.put("key2","key6",41);
        setHashMap.put("key2","key7",43);

        value = setHashMap.get("key1","key2");
        assertEquals(19,value);

        int keysCount = setHashMap.getKeyList().size();
        assertEquals(2,keysCount);

        keysCount = setHashMap.getSubKeyList("key1").size();
        assertEquals(3,keysCount);

        setHashMap.delete("key2");

        keysCount = setHashMap.getKeyList().size();
        assertEquals(1,keysCount);

        setHashMap.delete("key1","key4");

        keysCount = setHashMap.getSubKeyList("key1").size();
        assertEquals(2,keysCount);

        setHashMap.clear();

        keysCount = setHashMap.getKeyList().size();
        assertEquals(0,keysCount);

        setHashMap.clear();
        waitFor(DELAY);
    }

    @Test
    public void onlineTest() {
        NestedHashMapValue<Integer> setHashMap = new NestedHashMapValue("UnitTest","onlineTest",null);

        waitFor(DELAY);

        setHashMap.put("key1","key2",45);
        setHashMap.setOnline();

        waitFor(DELAY);

        NestedHashMapValue<Integer> getHashMap = new NestedHashMapValue("UnitTest","onlineTest",this);

        checkFlags(true,false);

        setHashMap.put("key1","key2",75);

        checkFlags(true,false);

        setHashMap.put("key1","key3",77);

        checkFlags(true,false);

        setHashMap.put("key2","key4",82);
        setHashMap.put("key1","key5",79);

        checkFlags(true,false);

        //There should be two keys
        int keyListSize = getHashMap.getKeyList().size();
        assertEquals(2,keyListSize);

        //key1 should have 3 entries
        keyListSize = getHashMap.getSubKeyList("key1").size();
        assertEquals(3,keyListSize);

        //key2 should have 1 entry
        keyListSize = getHashMap.getSubKeyList("key2").size();
        assertEquals(1,keyListSize);

        getHashMap.delete("key1","key3");

        checkFlags(true,false);

        List keyList = getHashMap.getSubKeyList("key1");
        assertTrue(keyList.contains("key2"));
        assertTrue(keyList.contains("key5"));
        assertFalse(keyList.contains("key3"));

        keyList = getHashMap.getSubKeyList("key2");
        assertTrue(keyList.contains("key4"));
        assertEquals(1,keyList.size());

        setHashMap.delete("key1");

        checkFlags(true,false);

        keyList = getHashMap.getSubKeyList("key1");

        assertEquals(0,keyList.size());

        setHashMap.delete("key2");

        checkFlags(true,true);

        setHashMap.clear();
        waitFor(DELAY);
    }

    @Test
    public void interferenceTest() {
        NestedHashMapValue<Integer> setHashMap = new NestedHashMapValue("UnitTest","interferenceTest",null);
        setHashMap.setOnline();

        NestedHashMapValue<Integer> getHashMap = new NestedHashMapValue("UnitTest","interferenceTest",this);

        checkFlags(false,true);

        setHashMap.put("key1","key2",10);
        setHashMap.put("key1","key3",11);
        setHashMap.put("key1","key4",12);
        setHashMap.put("key2","key5",13);
        setHashMap.put("key1","key6",14);
        setHashMap.put("key1","key7",15);
        setHashMap.put("key1","key8",16);
        setHashMap.put("key2","key9",17);
        setHashMap.put("key1","key10",18);
        setHashMap.put("key1","key11",19);
        setHashMap.put("key1","key12",20);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        List keyList = getHashMap.getKeyList();
        assertEquals(2,keyList.size());
        keyList = getHashMap.getSubKeyList("key1");
        assertEquals(9,keyList.size());
        keyList = getHashMap.getSubKeyList("key2");
        assertEquals(2,keyList.size());

        setHashMap.put("key3","key21",21);
        getHashMap.put("key3","key22",22);
        setHashMap.put("key3","key23",23);
        getHashMap.put("key4","key24",24);
        setHashMap.put("key3","key25",25);
        getHashMap.put("key3","key26",26);
        setHashMap.put("key3","key27",27);
        getHashMap.put("key4","key28",28);
        setHashMap.put("key3","key29",29);
        getHashMap.put("key3","key30",30);
        setHashMap.put("key3","key31",31);
        getHashMap.put("key4","key32",32);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        keyList = getHashMap.getKeyList();
        assertEquals(4,keyList.size());
        keyList = getHashMap.getSubKeyList("key3");
        assertEquals(9,keyList.size());
        keyList = getHashMap.getSubKeyList("key4");
        assertEquals(3,keyList.size());

        setHashMap.delete("key3","key21");
        getHashMap.put("key3","key40",40);
        setHashMap.delete("key3","key22");
        getHashMap.put("key3","key41",41);
        setHashMap.delete("key3","key23");
        getHashMap.put("key3","key42",42);
        setHashMap.delete("key3","key25");
        getHashMap.put("key3","key43",43);
        setHashMap.delete("key3","key26");
        getHashMap.put("key3","key44",44);
        setHashMap.delete("key3","key27");
        getHashMap.put("key3","key45",45);
        setHashMap.delete("key3","key29");
        getHashMap.put("key3","key46",46);
        setHashMap.delete("key3","key30");
        getHashMap.put("key3","key47",47);
        setHashMap.delete("key3","key31");
        getHashMap.put("key3","key48",48);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        keyList = getHashMap.getKeyList();
        assertEquals(4,keyList.size());
        keyList = getHashMap.getSubKeyList("key3");
        assertEquals(9,keyList.size());

        setHashMap.delete("key4");
        getHashMap.put("key4","key50",50);

        checkFlags(true,false);

        keyList = getHashMap.getKeyList();
        assertEquals(4,keyList.size());
        keyList = getHashMap.getSubKeyList("key4");
        assertEquals(1,keyList.size());

        //This step causes interferences:
        setHashMap.delete("key3","key40");
        setHashMap.delete("key3","key41");
        getHashMap.put("key3","key40",60);
        setHashMap.delete("key3","key42");
        getHashMap.put("key3","key41",61);
        setHashMap.delete("key3","key43");
        getHashMap.put("key3","key42",62);
        setHashMap.delete("key3","key44");
        getHashMap.put("key3","key43",63);
        setHashMap.delete("key3","key45");
        getHashMap.put("key3","key44",64);
        setHashMap.delete("key3","key46");
        getHashMap.put("key3","key45",65);
        setHashMap.delete("key3","key47");
        getHashMap.put("key3","key46",66);
        setHashMap.delete("key3","key48");
        getHashMap.put("key3","key47",67);

        getHashMap.put("key3","key48",68);

        //This break is important because of the internal integrity check of the hashmap.
        waitFor(425);

        checkFlags(true,false);

        keyList = getHashMap.getKeyList();
        assertEquals(4,keyList.size());
        keyList = getHashMap.getSubKeyList("key3");
        assertEquals(9,keyList.size());

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
