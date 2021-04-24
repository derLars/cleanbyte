package com.derlars.moneyflow;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.derlars.moneyflow.Authentication.Authentication;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.UserContact;
import com.derlars.moneyflow.TestClasses.UserContactUnderTest;
import com.derlars.moneyflow.Utils.DatabaseTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ContactTest extends BaseUnitTest implements BaseCallback {
    private long THREE_MONTH = 1000*60*60*24*90L;

    @Before
    public void before() {
        setFlags("updated");

        auth = Authentication.getInstance(rule.getActivity(),this);
        auth.signOut();
        waitFor(2000);
        /*
        for(int i=2; i<9; i++) {
            auth.startAuthentication();
            waitFor(1200);
            auth.authenticate("+33 7 53 00 00 0"+i);
            waitFor(1200);
            auth.confirm("123456");
            waitFor(1200);
            assertTrue(authenticated);
            UserContact userContact = new UserContact("+33 7 53 00 00 0"+i, null);
            userContact.setOnline();
            delay();
            auth.signOut();
            delay();
        }
         */

        auth.startAuthentication();
        waitFor(2700);
        auth.authenticate("+33 7 53 00 00 01");
        waitFor(2700);
        auth.confirm("123456");
        waitFor(2700);
        assertTrue(authenticated);

        UserContactUnderTest preset = new UserContactUnderTest("+33 7 53 00 00 01",null);
        preset.setOnline();

        delay();

        preset.delete();

        delay();

        resetFlags();
    }

    @Test
    public void userContactTest() {
        DatabaseTime databaseTime = DatabaseTime.getInstance();
        waitFor(1000);
        long currentTimestamp = databaseTime.getLastTime();

        UserContact userContact = new UserContact("+33 7 53 00 00 01", this);

        UserContactUnderTest userContactUnderTest = new UserContactUnderTest("+33 7 53 00 00 01",null);

        userContact.setOnline();

        delay();
        long premium = userContactUnderTest.getPremium();

        assertTrue("Premium: " + premium + " | lower border: " + (long)(currentTimestamp + THREE_MONTH),premium >= (currentTimestamp + THREE_MONTH));
        assertTrue("Premium: " + premium + " | upper border: " + (long)(currentTimestamp + THREE_MONTH +3000),premium < currentTimestamp + THREE_MONTH +3000);

        assertTrue(userContact.isPremium());
    }

    @Test
    public void contactTest() {
        UserContact userContact = new UserContact("+33 7 53 00 00 01", null);

        Contact contact = new Contact("+33 7 53 00 00 02",null);
        Contact checkContact = new Contact("+33 7 53 00 00 02",this);

        contact.setOnline();
        checkContact.setOnline();

        checkFlags(true);

        //It is not possible to set the name for a contact
        //contact.setName("Test");

        String name = checkContact.getName();

        assertEquals("name02",name);

        checkContact = new Contact("+33 7 53 00 00 01",this);
        checkContact.setOnline();

        checkFlags(true);
        name = checkContact.getName();

        assertEquals("+33 7 53 00 00 01",name);

        userContact.setName("test name");
        userContact.setImageID("testImage.jpg");

        checkFlags(true);

        name = checkContact.getName();

        assertEquals("test name",name);

        String imageID = checkContact.getImageID();

        assertEquals("dummy.jpg",imageID);

        checkFlags(true);

        imageID = checkContact.getImageID();

        assertEquals("testImage.jpg",imageID);
        //It is not possible to delete a contact
        //contact.delete();
    }

    @Test
    public void unknownContactTest() {
        Contact contact = new Contact("+33 7 53 00 00 09",this);

        contact.setOnline();
        delay();

        String name = contact.getName();
        assertEquals("+33 7 53 00 00 09",name);

        String imageID = contact.getImageID();
        assertEquals("dummy.jpg",imageID);

        checkFlags(false);

        name = contact.getName();
        assertEquals("+33 7 53 00 00 09",name);

        imageID = contact.getImageID();
        assertEquals("dummy.jpg",imageID);
    }

    @Override
    public void update(String path) {
        raiseFlag("updated");
    }

    @Override
    public void onPhoneNumberRequested() {
        //auth.authenticate("+33 7 53 00 00 01");
        authenticated = false;
    }

    @Override
    public void onAuthenticationStarted() {
        authenticated = false;
    }

    @Override
    public void onCodeRequested() {
        //auth.confirm("123456");
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