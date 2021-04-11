package com.derlars.moneyflow;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BaseTest {
    private Map<Integer, Boolean> flagStatus = new HashMap();
    private String[] flags;

    protected final static int DELAY = 1000;

    protected void setFlags(String[] flags) {
        this.flags = flags;
        for(int i=0; i < flags.length; i++) {
            flagStatus.put(i,false);
        }
    }

    protected void checkFlags(Boolean[] expectedStatus) {
        waitFor(DELAY);

        for(int i=0; i<expectedStatus.length; i++) {
            assertEquals(flags[i],expectedStatus[i],flagStatus.get(i));
        }

        resetFlags();
    }

    protected void raiseFlag(String flag) {
        for(int i=0; i<flags.length;i++) {
            if(flags[i].compareTo(flag) == 0) {
                flagStatus.put(i,true);
                break;
            }
        }
    }

    protected void resetFlags() {
        for(Integer i : flagStatus.keySet()) {
            flagStatus.put(i,false);
        }
    }

    protected void waitFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
