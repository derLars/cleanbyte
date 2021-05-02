package com.derlars.moneyflow;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.derlars.moneyflow.Container.Contacts;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;

import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.UserContact;
import com.derlars.moneyflow.TestClasses.UserContactUnderTest;
import com.derlars.moneyflow.Utils.DatabaseTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ContactTest extends BaseUnitTest implements BaseCallback {
    private long THREE_MONTH = 1000*60*60*24*90L;

    @Before
    public void before() {
        setUp("updated");
    }

    @Test
    public void userContactTest() {
        DatabaseTime databaseTime = DatabaseTime.getInstance();
        long currentTimestamp = databaseTime.getLastTime();

        UserContactUnderTest userContact = new UserContactUnderTest("+33 7 53 00 00 01", this);

        delay();

        assertTrue(userContact.isOnline());
        long premium = userContact.getPremium();
        boolean isPremium = userContact.isPremium();

        assertTrue("Premium: " + premium + " | lower border: " + (long)(currentTimestamp + THREE_MONTH -1500),premium >= (currentTimestamp + THREE_MONTH -1500));
        assertTrue("Premium: " + premium + " | upper border: " + (long)(currentTimestamp + THREE_MONTH +1500),premium < currentTimestamp + THREE_MONTH +1500);

        assertTrue(isPremium);
    }

    @Test
    public void contactTest() {
        Contacts contacts = Contacts.getInstance();
        contacts.addUserContact("+33 7 53 00 00 01");

        UserContact userContact = contacts.getUserContact();

        Contact contact2 = new Contact("+33 7 53 00 00 02",null);
        Contact checkContact2 = new Contact("+33 7 53 00 00 02",this);

        contact2.setOnline();
        checkContact2.setOnline();

        checkFlags(true);

        //It is not possible to set the name for a contact
        contact2.setName("Shouldn't be set");

        String name = checkContact2.getName();

        assertEquals("onlineName2",name);

        checkContact2 = new Contact("+33 7 53 00 00 01",this);
        checkContact2.setOnline();

        checkFlags(true);
        name = checkContact2.getName();

        assertEquals("onlineName1",name);

        userContact.setName("changedName");
        userContact.setImageID("changedImage.jpg");

        checkFlags(true);

        name = checkContact2.getName();

        assertEquals("changedName",name);

        String imageID = checkContact2.getImageID();

        assertEquals("dummy.jpg",imageID);

        checkFlags(true);

        imageID = checkContact2.getImageID();

        assertEquals("changedImage.jpg",imageID);
        //It is not possible to delete a contact
        //contact.delete();
    }

    @Test
    public void contactsTest() {
        Contacts contacts = Contacts.getInstance(this);
        contacts.addUserContact("+33 7 53 00 00 01");

        checkFlags(true);

        List<Contact> all = contacts.getAll(false,"");
        List<Contact> displayed = contacts.getAllDisplayed(false,"");

        contacts.add("+33 7 53 00 00 02","known_group_02");
        contacts.add("+33 7 53 00 00 03","known_03");

        contacts.add("+33 7 53 00 00 15","fake15");
        contacts.add("+33 7 53 00 00 16","fake_group_16");
        contacts.add("+33 7 53 00 00 17","fake_name_17");

        contacts.get("+33 7 53 00 00 02").setDisplayed(true);
        contacts.get("+33 7 53 00 00 15").setDisplayed(true);
        contacts.get("+33 7 53 00 00 16").setDisplayed(true);
        contacts.get("+33 7 53 00 00 17").setDisplayed(true);

        delay();

        contacts.get("+33 7 53 00 00 17").setDisplayed(false);

        checkFlags(true);

        //get All
        assertEquals(2,all.size());
        assertEquals(1,displayed.size());

        contacts.getAll(true,"");

        assertEquals(5,all.size());
        assertEquals(3,displayed.size());

        contacts.getAll(true,"name");

        assertEquals(3,all.size());
        assertEquals(1,displayed.size());

        contacts.add("+33 7 53 00 00 04","known_group_04");

        checkFlags(true);

        assertEquals(4,all.size());
        assertEquals(1,displayed.size());

        contacts.getAll(false,"");

        assertEquals(3,all.size());
        assertEquals(1,displayed.size());

        contacts.getAll(true,"");

        assertEquals(6,all.size());
        assertEquals(3,displayed.size());

        contacts.add("+33 7 53 00 00 18","fake_name_18");

        assertEquals(7,all.size());
        assertEquals(3,displayed.size());

        checkFlags(false);
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