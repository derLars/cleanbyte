package com.derlars.moneyflow;

import androidx.test.rule.ActivityTestRule;

import com.derlars.moneyflow.Authentication.Authentication;

import org.junit.Rule;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseUnitTest implements Authentication.Callback  {
    private Map<Integer, Boolean> flagStatus = new HashMap();
    private String[] flags;

    @Rule
    public ActivityTestRule<MainActivity> rule  = new  ActivityTestRule<>(MainActivity.class);

    Authentication auth;
    boolean authenticated;

    protected final static int DELAY = 1000;

    protected void authenticate(String phone, String code) {
        auth = Authentication.getInstance(rule.getActivity(),this);
        auth.signOut();
        delay();

        auth.startAuthentication();
        waitFor(2000);
        auth.authenticate(phone);
        waitFor(2000);
        auth.confirm(code);
        waitFor(2000);

        assertTrue(authenticated);
    }

    protected void setFlags(String... flags) {
        this.flags = new String[flags.length];
        for(int i=0; i < flags.length; i++) {
            this.flags[i] = flags[i];
            flagStatus.put(i,false);
        }
    }

    protected void checkFlags(Boolean... expectedStatus) {
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
        delay();

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

    protected void delay() {
        waitFor(DELAY);
    }

    @Override
    public void onPhoneNumberRequested() {
        auth.authenticate("+33 7 53 00 00 01");
        authenticated = false;
    }

    @Override
    public void onAuthenticationStarted() {
        authenticated = false;
    }

    @Override
    public void onCodeRequested() {
        auth.confirm("123456");
        authenticated = false;
    }

    @Override
    public void onCodeConfirmationStarted() {
        authenticated = false;
    }

    @Override
    public void onAuthenticationCompleted() {
        authenticated = true;
    }
}
