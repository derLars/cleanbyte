package com.derlars.moneyflow.Container;

import com.derlars.moneyflow.Container.Abstracts.BaseContainer;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.Purchase;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Purchases extends BaseContainer<Purchase> {
    private static Purchases INSTANCE;

    private Purchases(){}

    public static Purchases getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Purchases();
        }
        return INSTANCE;
    }

    public static Purchases getInstance(BaseCallback callback) {
        Purchases.getInstance();

        INSTANCE.subscribe(callback);

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public void add(String key) {
        if(!collection.containsKey(key)) {
            this.collection.put(key, new Purchase(key,this));
            this.collection.get(key).setOnline();

            organizeLists(this.includeOffline, this.searchWord);
        }
    }

    @Override
    public Purchase get(String key) {
        Purchase p = null;

        if(this.collection.containsKey(key)) {
            p = this.collection.get(key);
            List<String> keyList = p.getItemKeyList();

            Items items = Items.getInstance();
            if(items != null) {
                for(String s : keyList) {
                    items.get(s);
                }

            }
        }
        return p;
    }

    @Override
    public List<Purchase> getAll(boolean includeOffline, String searchWord) {
        Contacts contacts = Contacts.getInstance();
        if(contacts != null) {
            contacts.getUserContact().getPurchases();
        }

        return super.getAll(includeOffline,searchWord);
    }

    @Override
    public List<Purchase> getAllSelected(boolean includeOffline, String searchWord) {
        Contacts contacts = Contacts.getInstance();
        if(contacts != null) {
            contacts.getUserContact().getPurchases();
        }

        return super.getAllSelected(includeOffline, searchWord);
    }

    @Override
    public List<Purchase> getAllDisplayed(boolean includeOffline, String searchWord) {
        Contacts contacts = Contacts.getInstance();
        if(contacts != null) {
            contacts.getUserContact().getPurchases();
        }

        return super.getAllDisplayed(includeOffline, searchWord);
    }

    @Override
    protected void organizeLists(boolean includeOffline, String searchWord) {
        this.includeOffline = includeOffline;
        this.searchWord = searchWord.toLowerCase();

        this.all.clear();
        this.selected.clear();
        this.displayed.clear();

        Set<String> keySet = getKeySet();

        for(String key : keySet) {
            Purchase t = collection.get(key);
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
                Purchase t = collection.get(key);
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
