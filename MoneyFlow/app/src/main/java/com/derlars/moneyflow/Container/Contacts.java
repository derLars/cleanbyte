package com.derlars.moneyflow.Container;

import com.derlars.moneyflow.Container.Abstracts.BaseContainer;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.UserContact;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

public class Contacts extends BaseContainer<Contact> {
    private static Contacts INSTANCE;

    private UserContact userContact;

    private Contacts(){}

    public static Contacts getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Contacts();
        }
        return INSTANCE;
    }

    public static Contacts getInstance(BaseCallback callback) {
        Contacts.getInstance();

        INSTANCE.subscribe(callback);

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public void addUserContact(String phone) {
        userContact = new UserContact(phone,this);
    }

    public UserContact getUserContact() {
        return userContact;
    }

    public void add(String phone, String name) {
        if(!collection.containsKey(phone) && userContact != null && userContact.getKey().compareTo(phone) != 0) {
            Contact contact = new Contact(phone,this);
            contact.setName(name);

            this.collection.put(phone,contact);
            organizeLists(this.includeOffline,this.searchWord);
        }
    }

    @Override
    public void add(String phone, Contact contact) {
        if(!collection.containsKey(phone) && userContact != null && userContact.getKey().compareTo(phone) != 0) {
            collection.put(phone,contact);
            organizeLists(this.includeOffline,this.searchWord);
        }
    }

    @Override
    protected void organizeLists(boolean includeOffline, String searchWord) {
        this.includeOffline = includeOffline;
        this.searchWord = searchWord.toLowerCase();

        this.all.clear();
        this.selected.clear();
        this.displayed.clear();
        Set<String> keySet = new HashSet();
        boolean success;
        do {
            try {
                keySet = new HashSet(this.collection.keySet());
                success = true;
            } catch (ConcurrentModificationException ex) {
                success = false;
            }
        }while(!success);

        for(String key : keySet) {
            Contact t = collection.get(key);
            boolean keyMatch = t.getKey().toLowerCase().contains(this.searchWord);
            keyMatch = keyMatch  || t.getKey().toLowerCase().replace(" ","").contains(this.searchWord);

            boolean matchName = t.getName().toLowerCase().contains(this.searchWord);
            matchName = matchName  || t.getName().toLowerCase().replace(" ","").contains(this.searchWord);

            if((keyMatch || matchName) && t.isOnline()) {
                all.add(t);
                if(t.isSelected()) {
                    selected.add(t);
                }
                if(t.isDisplayed()) {
                    displayed.add(t);
                }
            }
        }

        if(this.includeOffline) {
            for (String key : keySet) {
                Contact t = collection.get(key);
                boolean keyMatch = t.getKey().toLowerCase().contains(this.searchWord);
                keyMatch = keyMatch  || t.getKey().toLowerCase().replace(" ","").contains(this.searchWord);

                boolean matchName = t.getName().toLowerCase().contains(this.searchWord);
                matchName = matchName  || t.getName().toLowerCase().replace(" ","").contains(this.searchWord);

                if((keyMatch || matchName) && !t.isOnline()) {
                    all.add(t);
                    if(t.isSelected()) {
                        selected.add(t);
                    }
                    if(t.isDisplayed()) {
                        displayed.add(t);
                    }
                }
            }
        }
    }
}
