package com.derlars.moneyflow;

import android.util.Log;

import com.derlars.moneyflow.Container.Contacts;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.TestClasses.UserContactUnderTest;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContactsTest extends BaseUnitTest implements BaseCallback {
    @Before
    public void before() {
        setFlags("updated");

        UserContactUnderTest preset = new UserContactUnderTest("+00 75 10 00 00",null);
        preset.setOnline();
        preset.delete();

        delay();

        Contacts contacts = Contacts.getInstance(null);
        contacts.addUserContact("+00 75 00 00 01");
        contacts.addUserContact("+00 75 00 00 02");
        contacts.addUserContact("+00 75 00 00 03");
        contacts.addUserContact("+00 75 00 00 04");
        contacts.addUserContact("+00 75 00 00 05");
        contacts = null;
        Contacts.destroyInstance();

        delay();
    }

    @Test
    public void listTest() {
        Contacts contacts = Contacts.getInstance(this);
        contacts.addUserContact("+00 75 10 00 00");

        checkFlags(true);

        contacts.getUserContact().setName("Lars Larson");
        contacts.getUserContact().setImageID("LarsProfile.png");

        checkFlags(true);

        contacts.add("+00 75 00 00 01","name01");
        contacts.add("+00 75 00 00 02","NOM02");
        contacts.add("+00 75 00 00 03","NOM03");
        contacts.add("+00 75 00 00 04","name04");
        contacts.add("+00 75 00 00 05","name05");
        contacts.add("+00 75 00 00 11","NOM11");
        contacts.add("+00 75 00 00 12","NOM12");
        contacts.add("+00 75 00 00 13","name13");
        contacts.add("+00 75 00 00 14","name14");
        contacts.add("+00 75 00 00 15","name15");

        delay();

        List<Contact> all = contacts.getAll(false,"");
        for(Contact c : all) {
            Log.d("MUTEX",c.toString());
        }
        Log.d("MUTEX","--------");

        assertEquals(5,all.size());

        contacts.getAll(true,"");
        for(Contact c : all) {
            Log.d("MUTEX",c.toString());
        }
        Log.d("MUTEX","--------");

        assertEquals(10,all.size());

        contacts.getAll(false,"nom");
        for(Contact c : all) {
            Log.d("MUTEX",c.toString());
        }
        Log.d("MUTEX","--------");

        assertEquals(0,all.size());

        contacts.getAll(true,"nom");
        for(Contact c : all) {
            Log.d("MUTEX",c.toString());
        }
        Log.d("MUTEX","--------");

        assertEquals(2,all.size());

        contacts.getAll(true,"name");
        for(Contact c : all) {
            Log.d("MUTEX",c.toString());
        }
        Log.d("MUTEX","--------");

        assertEquals(3,all.size());
    }

    @Test
    public void displayedSelectedTest() {
        Contacts contacts = Contacts.getInstance(this);
        contacts.addUserContact("+00 75 10 00 00");

        checkFlags(true);

        contacts.getUserContact().setName("Lars Larson");
        contacts.getUserContact().setImageID("LarsProfile.png");

        checkFlags(true);

        contacts.add("+00 75 00 00 01","name01");
        contacts.add("+00 75 00 00 02","NOM02");
        contacts.add("+00 75 00 00 03","NOM03");
        contacts.add("+00 75 00 00 04","name04");
        contacts.add("+00 75 00 00 05","name05");
        contacts.add("+00 75 00 00 11","NOM11");
        contacts.add("+00 75 00 00 12","NOM12");
        contacts.add("+00 75 00 00 13","name13");
        contacts.add("+00 75 00 00 14","name14");
        contacts.add("+00 75 00 00 15","name15");

        delay();

        List<Contact> all = contacts.getAll(true,"");
        for(int i=0; i<all.size(); i += 2) {
            all.get(i).setDisplayed(true);
        }

        List<Contact> displayed = contacts.getAllDisplayed(true,"");
        assertEquals(5,displayed.size());

        contacts.getAllDisplayed(false,"");
        assertEquals(3,displayed.size());

        contacts.getAllDisplayed(true,"name");
        assertEquals(1,displayed.size());

        contacts.clearAllDisplayed();
        assertEquals(0,displayed.size());
    }

    @Override
    public void update(String key) {
        raiseFlag("updated");
    }
}
