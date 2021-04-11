package com.derlars.moneyflow;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

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

        Contact preset = new Contact("+33 7 53 55 55 55", null);
        preset.setOnline();
        preset.delete();

        UserContactUnderTest preset2 = new UserContactUnderTest("+33 7 53 33 33 33",null);
        preset2.setOnline();
        delay();
        preset2.delete();

        resetFlags();
    }

    @Test
    public void offlineTest() {
        Contact setContact = new Contact("+33 7 53 77 77 77", null);
        Contact checkContact = new Contact("+33 7 53 77 77 77", this);

        checkFlags(false);

        String name = setContact.getName();
        assertEquals("+33 7 53 77 77 77",name);

        String imageID = setContact.getImageID();
        assertEquals("dummy",imageID);

        setContact.setName("name1");

        checkFlags(false);

        checkContact.setName("name2");
        setContact.setName("name3");

        checkFlags(false);

        name = setContact.getName();
        assertEquals("name3",name);

        name = checkContact.getName();
        assertEquals("name2",name);
    }

    @Test
    public void onlineTest() {
        Contact setContact = new Contact("+33 7 53 55 55 55", null);
        setContact.setImageID("testImageID");
        setContact.setOnline();

        setContact.setName("name1");

        delay();

        Contact checkContact = new Contact("+33 7 53 55 55 55", this);

        checkFlags(true);

        String name = checkContact.getName();

        assertEquals("+33 7 53 55 55 55",name);

        String imageID = checkContact.getImageID();
        assertEquals("testImageID",imageID);

        checkContact.delete();
    }

    @Test
    public void UserContactTest() {
        DatabaseTime databaseTime = DatabaseTime.getInstance();
        waitFor(1000);
        long currentTimestamp = databaseTime.getLastTime();

        UserContact userContact = new UserContact("+33 7 53 33 33 33",this);
        UserContactUnderTest userContactUnderTest = new UserContactUnderTest("+33 7 53 33 33 33",null);

        userContact.setOnline();

        delay();

        long premium = userContactUnderTest.getPremium();

        assertTrue(premium >= currentTimestamp + THREE_MONTH);
        assertTrue(premium < currentTimestamp + THREE_MONTH +3000);

        assertTrue(userContact.isPremium());

        userContact.delete();

        delay();

        userContact = new UserContact("+33 7 53 33 33 33",this);
        userContact.setOnline();

        delay();

        long newPremium = userContactUnderTest.getPremium();

        assertEquals(premium,newPremium);

        userContactUnderTest.delete();
        delay();
    }

    @Override
    public void update(String path) {
        raiseFlag("updated");
    }
}