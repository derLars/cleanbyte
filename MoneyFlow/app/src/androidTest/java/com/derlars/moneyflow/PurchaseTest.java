package com.derlars.moneyflow;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.derlars.moneyflow.Container.Contacts;
import com.derlars.moneyflow.Container.Purchases;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.Database;
import com.derlars.moneyflow.Resource.Abstracts.BaseContact;
import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.Purchase;
import com.derlars.moneyflow.Resource.UserContact;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PurchaseTest extends BaseUnitTest implements BaseCallback {

    @Before
    public void before() {

        /*
        setFlags("updated");
        Database.SET_TEST_MODE();
        authenticate("+33 7 53 00 00 01","123456");
        delay();
         */

        setUp("updated");
    }

    @Test
    public void purchaseTest() {
        Contacts contacts = Contacts.getInstance(this);
        contacts.addUserContact("+33 7 53 00 00 01");

        contacts.add("+33 7 53 00 00 02","Herbert");
        contacts.add("+33 7 53 00 00 03","Klaus");
        contacts.add("+33 7 53 00 00 20","Dieter");

        delay();

        List<String> classification1 = new ArrayList();
        classification1.add("Lebensmittel");
        classification1.add("Milchprodukt");

        Map<BaseContact,Double> contactList1 = new HashMap();

        contactList1.put(contacts.getUserContact(), 0.50);
        contactList1.put(contacts.get("+33 7 53 00 00 02"),0.25);
        contactList1.put(contacts.get("+33 7 53 00 00 20"),0.25);

        ItemPreparation item1 = new ItemPreparation("Milch",0.85, classification1, contactList1);


        List<String> classification2 = new ArrayList();
        classification2.add("Haushalt");
        classification2.add("Badezimmer");

        Map<BaseContact,Double> contactList2 = new HashMap();

        contactList2.put(contacts.getUserContact(), 1.00);

        ItemPreparation item2 = new ItemPreparation("Klopapier",1.99, classification2, contactList2);


        List<String> classification3 = new ArrayList();
        classification3.add("Haushalt");
        classification3.add("Badezimmer");

        Map<BaseContact,Double> contactList3 = new HashMap();

        contactList3.put(contacts.getUserContact(), 0.50);
        contactList3.put(contacts.get("+33 7 53 00 00 02"),0.50);

        ItemPreparation item3 = new ItemPreparation("Seife",0.49, classification3, contactList3);

        PurchaseAssistant assistant = new PurchaseAssistant();
        assistant.addItem(item1);
        assistant.addItem(item2);
        assistant.addItem(item3);
        assistant.publish("Carrefour shopping",1619340346739L);

        delay();

        Purchases purchases = Purchases.getInstance();

        List<Purchase> purchaseList = purchases.getAll(false,"");
        contacts.getUserContact();

        delay(5000);

        Log.d("UNITTEST",""+purchaseList);
        assertEquals(1,purchaseList.size());

        for(Purchase p : purchaseList) {
            Log.d("UNITTEST",""+p.getItemKeyList());
            assertEquals(3,p.getItemKeyList().size());
        }
    }

    @Override
    public void update(String key) {

    }
}
