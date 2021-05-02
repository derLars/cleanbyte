package com.derlars.moneyflow.Container;

import com.derlars.moneyflow.Container.Abstracts.BaseContainer;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Resource.Item;
import com.derlars.moneyflow.Resource.Purchase;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Items extends BaseContainer<Item> {
    private static Items INSTANCE;

    private Items(){}

    public static Items getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Items();
        }
        return INSTANCE;
    }

    public static Items getInstance(BaseCallback callback) {
        Purchases.getInstance();

        INSTANCE.subscribe(callback);

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public void add(String key) {
        if(!collection.containsKey(key)) {
            this.collection.put(key, new Item(key,this));
            this.collection.get(key).setOnline();

            organizeLists(this.includeOffline, this.searchWord);
        }
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
            Item t = collection.get(key);

            boolean matchName = t.getTitle().toLowerCase().contains(this.searchWord);
            matchName = matchName  || t.getTitle().toLowerCase().replace(" ","").contains(this.searchWord);

            List<String> classifications = t.getClassification();
            boolean matchClassification = false;
            for(String classification : classifications) {
                matchClassification = classification.toLowerCase().contains(this.searchWord);
                if(matchClassification) {
                    break;
                }
            }

            if((matchName || matchClassification) && t.isOnline()) {
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
