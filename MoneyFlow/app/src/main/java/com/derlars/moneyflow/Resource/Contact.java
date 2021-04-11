package com.derlars.moneyflow.Resource;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;

import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;

public class Contact<Callback extends BaseCallback> extends BaseResource<Callback> {
    Value<String> name;
    Value<String> imageID;
    HashMapValue<String> purchases;

    private Contact(Callback callback) {
        super("Contact", callback);

        initialize();
    }

    public Contact(String key, Callback callback) {
        super("Contact", key, callback);

        initialize();
    }

    protected void initialize() {
        name = new Value(this.path,"name",this,true);
        name.set(this.key);

        imageID = new Value(this.path,"imageID",this,true);
        imageID.set("dummy");

        purchases = new HashMapValue(this.path,"purchases",this);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        String n = this.name.get();
        if (n == null) {
            n = "";
        }

        return n;
    }

    public void setImageID(String imageID) {
        this.imageID.set(imageID);
    }

    public String getImageID() {
        String id = this.imageID.get();
        if (id == null) {
            id = "";
        }
        return id;
    }

    public void setOnline() {
        super.setOnline();
        try {
            name.setOnline();
            imageID.setOnline();
            purchases.setOnline();
        }catch(NullPointerException ex){

        }
    }

    public void delete() {
            name.delete();
            imageID.delete();
            purchases.clear();
    }

    @Override
    public void update(String key) {
        setOnline();
        notifyUpdate(this.key);
    }

    @Override
    public void notOnline(String key) {

    }

    @Override
    public int compareTo(BaseResource o) {
        Contact c = (Contact)o;

        if(this.name.get().compareTo(c.getName()) == 0) {
            return this.getKey().compareTo(o.getKey());
        }
        return this.name.get().compareTo(c.getName());
    }
}
