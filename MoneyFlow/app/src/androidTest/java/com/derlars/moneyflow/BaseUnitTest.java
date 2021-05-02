package com.derlars.moneyflow;

import androidx.test.rule.ActivityTestRule;

import com.derlars.moneyflow.Authentication.Authentication;
import com.derlars.moneyflow.Database.Database;
import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.UserContact;

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

    protected void setUp(String... flags) {
        setFlags(flags);

        Database.SET_TEST_MODE();

        authenticate("+33 7 53 00 00 02","123456");

        Database db = new Database("","",false,true,true,null);
        db.deleteValue();

        delay();

        UserContact contact1 = new UserContact("+33 7 53 00 00 02",null);
        delay(DELAY/4);
        contact1.setName("onlineName2");
        delay(DELAY/4);
        contact1.free();

        authenticate("+33 7 53 00 00 03","123456");
        UserContact contact2 = new UserContact("+33 7 53 00 00 03",null);
        delay(DELAY/4);
        contact2.setName("onlineName3");
        delay(DELAY/4);
        contact2.free();

        authenticate("+33 7 53 00 00 04","123456");
        UserContact contact3 = new UserContact("+33 7 53 00 00 04",null);
        delay(DELAY/4);
        contact3.setName("onlineName4");
        delay(DELAY/4);
        contact3.free();

        authenticate("+33 7 53 00 00 01","123456");

        UserContact userContact = new UserContact("+33 7 53 00 00 01",null);
        delay(DELAY/4);
        userContact.setName("onlineName1");
        delay(DELAY/4);
        contact3.free();
    }

    protected void authenticate(String phone, String code) {
        auth = Authentication.getInstance(rule.getActivity(),this);
        auth.signOut();
        delay();

        auth.startAuthentication();
        delay(1900);
        auth.authenticate(phone);
        delay(1900);
        auth.confirm(code);
        delay(1900);

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
        delay(DELAY);

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

    protected void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void delay() {
        delay(DELAY);
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
