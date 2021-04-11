package com.derlars.moneyflow.Container.Abstracts;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.Abstracts.Subscriptable;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;
import com.derlars.moneyflow.Resource.Callbacks.BaseResourceCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseContainer<T extends BaseResource> extends Subscriptable<BaseCallback> implements BaseResourceCallback {
    protected Map<String, T> collection = new HashMap();

    protected List<T> all = new ArrayList<T>();

    protected List<T> selected = new ArrayList();
    protected List<T> displayed = new ArrayList();

    protected boolean includeOffline = false;

    protected String searchWord = "";

    public BaseContainer() {
        super(null);
    }

    public void add(String key, T resource) {
        if(!collection.containsKey(key)) {
            collection.put(key,resource);
            organizeLists(this.includeOffline,this.searchWord);
        }
    }

    public T get(String key) {
        if(collection.containsKey(key)) {
            return collection.get(key);
        }

        return null;
    }

    public void remove(String key) {
        if(!collection.containsKey(key)) {
            collection.remove(key);
        }
    }

    public List<T> getAll(boolean includeOffline, String searchWord) {
        if(this.includeOffline != includeOffline || this.searchWord.compareTo(searchWord) != 0 || all.size() == 0) {
            organizeLists(includeOffline,searchWord);
        }

        Collections.sort(all);

        return all;
    }

    public List<T> getAllSelected(boolean includeOffline, String searchWord) {
        if(this.includeOffline != includeOffline || this.searchWord.compareTo(searchWord) != 0 || selected.size() == 0) {
            organizeLists(includeOffline,searchWord);
        }

        Collections.sort(selected);

        return selected;
    }

    public List<T> getAllDisplayed(boolean includeOffline, String searchWord) {
        if(this.includeOffline != includeOffline || this.searchWord.compareTo(searchWord) != 0 || displayed.size() == 0) {
            organizeLists(includeOffline,searchWord);
        }

        Collections.sort(displayed);

        return displayed;
    }

    private Set<String> getKeySet() {
        Set<String> keySet = new HashSet();

        boolean success = true;
        do {
            try {
                keySet = new HashSet(this.collection.keySet());
                success = true;
            } catch (ConcurrentModificationException ex) {
                success = false;
            }
        }while(!success);

        return keySet;
    }

    public void clearAllSelected() {
        Set<String> keySet = getKeySet();

        for(String key : keySet) {
            collection.get(key).setSelected(false);
        }
        organizeLists(this.includeOffline,this.searchWord);
    }

    public void clearAllDisplayed() {
        Set<String> keySet = getKeySet();

        for(String key : keySet) {
            collection.get(key).setDisplayed(false);
        }
        organizeLists(this.includeOffline,this.searchWord);
    }

    protected void organizeLists(boolean includeOffline, String searchWord) {
        this.includeOffline = includeOffline;
        this.searchWord = searchWord.toLowerCase();

        this.all.clear();
        this.displayed.clear();
        this.selected.clear();

        Set<String> keySet = getKeySet();

        for(String key : keySet) {
            T t = collection.get(key);
            boolean keyMatch = t.getKey().toLowerCase().contains(this.searchWord);
            keyMatch = keyMatch  || t.getKey().toLowerCase().replace(" ","").contains(this.searchWord);

            if(keyMatch && t.isOnline()) {
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
                T t = collection.get(key);
                boolean keyMatch = t.getKey().toLowerCase().contains(this.searchWord);
                keyMatch = keyMatch  || t.getKey().toLowerCase().replace(" ","").contains(this.searchWord);

                if(keyMatch && !t.isOnline()) {
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

    @Override
    public void update(String key) {
        organizeLists(this.includeOffline, this.searchWord);

        notifyUpdate(key);
    }
}
