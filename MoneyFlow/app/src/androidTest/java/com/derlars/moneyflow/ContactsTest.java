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

        authenticate("+33 7 53 00 00 01", "123456");

        UserContactUnderTest preset = new UserContactUnderTest("+33 7 53 00 00 01",null);
        preset.setOnline();

        delay();

        preset.delete();

        resetFlags();
    }

    @Test
    public void listTest() {
        Contacts contacts = Contacts.getInstance(this);
        contacts.addUserContact("+33 7 53 00 00 01");

        delay();
        checkFlags(true);

        contacts.getUserContact().setName("Lars Larson");
        contacts.getUserContact().setImageID("LarsProfile.png");

        checkFlags(true);

        contacts.add("+33 7 53 00 00 02","NOM02");
        contacts.add("+33 7 53 00 00 03","NOM03");
        contacts.add("+33 7 53 00 00 04","name04");
        contacts.add("+33 7 53 00 00 05","name05");
        contacts.add("+33 7 53 00 00 11","NOM11");
        contacts.add("+33 7 53 00 00 12","NOM12");
        contacts.add("+33 7 53 00 00 13","name13");
        contacts.add("+33 7 53 00 00 14","name14");
        contacts.add("+33 7 53 00 00 15","name15");

        delay();

        List<Contact> all = contacts.getAll(false,"");

        assertEquals(4,all.size());

        contacts.getAll(true,"");

        assertEquals(9,all.size());

        contacts.getAll(false,"nom");

        assertEquals(0,all.size());

        contacts.getAll(true,"nom");

        assertEquals(2,all.size());

        contacts.getAll(true,"name");

        assertEquals(7,all.size());
    }

    @Test
    public void displayedSelectedTest() {
        Contacts contacts = Contacts.getInstance(this);
        contacts.addUserContact("+33 7 53 00 00 01");

        delay();
        checkFlags(true);

        contacts.getUserContact().setName("Lars Larson");
        contacts.getUserContact().setImageID("LarsProfile.png");

        checkFlags(true);

        contacts.add("+33 7 53 00 00 02","NOM02");//Dis name02
        contacts.add("+33 7 53 00 00 03","NOM03"); //name03
        contacts.add("+33 7 53 00 00 04","name04");//Dis name04
        contacts.add("+33 7 53 00 00 05","name05"); //name05
        contacts.add("+33 7 53 00 00 11","NOM11");//Dis NOM11
        contacts.add("+33 7 53 00 00 12","NOM12"); //NOM12
        contacts.add("+33 7 53 00 00 13","name13");//Dis name13
        contacts.add("+33 7 53 00 00 14","name14"); //name14
        contacts.add("+33 7 53 00 00 15","name15");//Dis name15

        delay();

        contacts.get("+33 7 53 00 00 02").setDisplayed(true);
        contacts.get("+33 7 53 00 00 04").setDisplayed(true);
        contacts.get("+33 7 53 00 00 11").setDisplayed(true);
        contacts.get("+33 7 53 00 00 13").setDisplayed(true);
        contacts.get("+33 7 53 00 00 15").setDisplayed(true);

        List<Contact> displayed = contacts.getAllDisplayed(true,"");
        assertEquals(5,displayed.size());

        contacts.getAllDisplayed(false,"");
        assertEquals(2,displayed.size());

        contacts.getAllDisplayed(true,"name");
        assertEquals(4,displayed.size());

        contacts.clearAllDisplayed();
        assertEquals(0,displayed.size());
    }

    @Override
    public void update(String key) {
        raiseFlag("updated");
    }

}
