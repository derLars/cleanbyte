package com.derlars.moneyflow.Resource;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;
import com.derlars.moneyflow.Resource.Callbacks.BaseResourceCallback;

import java.util.ArrayList;
import java.util.List;

public class Item<Callback extends BaseCallback> extends BaseResource<Callback> {
    Value<String> title;

    HashMapValue<String> classification;

    Value<Long> timestamp;

    Value<String> itemPriceKey;

    public Item(Callback callback) {
        super("Item", callback);

        initialize();
    }

    public Item(String key, Callback callback) {
        super("Item", key, callback);

        initialize();
    }

    protected void initialize() {
        title = new Value(this.path,"title",true,true,false,this);

        itemPriceKey = new Value(this.path,"itemPriceKey",true,true,false,this);

        timestamp = new Value(this.path,"timestamp",true,true,false,this);

        classification = new HashMapValue(this.path,"classification",true,true,false,this);

        title.setOnline();
        itemPriceKey.setOnline();
        timestamp.setOnline();
        classification.setOnline();
    }

    public void setTimestamp(long timestamp) {
        this.timestamp.set(timestamp);

    }

    public long getTimestamp() {
        return timestamp.get();

    }

    public void setItemPriceKey(String itemPriceKey) {
        this.itemPriceKey.set(itemPriceKey);
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getTitle() {
        return this.title.get();
    }

    public void setClassification(List<String> classification) {
        this.classification.clear();

        for(int i=0; i<classification.size(); i++) {
            this.classification.put("class"+i,classification.get(i));
        }
    }

    public void setClassification(int i, String classification) {
        this.classification.put("class"+i,classification);
    }

    public List<String> getClassification() {
        List<String> list = new ArrayList();

        for(String k : classification.getKeyList()) {
            list.add(classification.get(k));
        }

        return list;
    }

    @Override
    public void setOnline() {
        super.setOnline();
        try {
            title.setOnline();

           classification.setOnline();

            timestamp.setOnline();

            itemPriceKey.setOnline();

        }catch(NullPointerException ex){

        }
    }

    public void delete() {

    }

    @Override
    public void notOnline(String key) {

    }

    @Override
    public void update(String key) {
        notifyUpdate(this.path);
    }

    @Override
    public int compareTo(BaseResource o) {
        Item i = (Item)o;

        return (this.timestamp.get() - i.getTimestamp()) > 0 ? 1 : ((this.timestamp.get() - i.getTimestamp()) < 0 ? -1 : 0);
    }
}
