package com.derlars.moneyflow;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.derlars.moneyflow.Database.Value;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest extends BaseUnitTest implements BaseValueCallback {

    @Before
    public void before() {
        setFlags("valueUpdatedFlag","valueNotOnlineFlag");

        Value<Long> preset = new Value("Test","databaseTest",null);
        delay();

        preset.delete();
        delay();
    }

    @Test
    public void databaseConnected() {
        Value<Long> setValue = new Value("Test","databaseTest",null);
        Value<Long> checkValue = new Value("Test","databaseTest",this);

        delay();
        checkFlags(false,true);

        setValue.setOnline();

        setValue.set(17L);

        checkFlags(true,false);

        long v = checkValue.get();

        assertEquals(17, v);

        setValue.delete();
        delay();
    }

    @Override
    public void notOnline(String key) {
        raiseFlag("valueNotOnlineFlag");
    }

    @Override
    public void update(String key) {
        raiseFlag("valueUpdatedFlag");
    }
}
