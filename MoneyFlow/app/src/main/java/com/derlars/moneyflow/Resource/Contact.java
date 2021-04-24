package com.derlars.moneyflow.Resource;

import android.util.Log;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;

import com.derlars.moneyflow.Resource.Abstracts.BaseContact;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;
import com.derlars.moneyflow.Utils.DatabaseTime;

public class Contact extends BaseContact {

    public Contact(String phone, BaseCallback callback) {
        super("Contact", phone, callback);
    }

    protected void initialize() {
        userID = new Value("UID",this.phone,true,false,false,this);
        userID.setOnline();

        name = new Value(this.path,"name",true,false,true,null);
        name.set(this.phone);

        imageID = new Value(this.path,"imageID",true,false,true,null);
        imageID.set("dummy.jpg");

        purchases = new HashMapValue(this.path,"purchases",false,false,true,null);
    }

    private void initialize(String key) {
        this.key = key;
        this.path = this.pathRoot + "/" + this.key;

        name = new Value(this.path,"name",true,false,false,this);
        name.set(this.phone);

        imageID = new Value(this.path,"imageID",true,false,true,this);
        imageID.set("dummy.jpg");

        purchases = new HashMapValue(this.path,"purchases",false,true,true,this);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        String n = (String)this.name.get();
        if (n == null) {
            n = "";
        }

        return n;
    }

    public void setImageID(String imageID) {
        this.imageID.set(imageID);
    }

    public String getImageID() {
        String id = (String)this.imageID.get();
        if (id == null) {
            id = "";
        }
        return id;
    }

    @Override
    public void setOnline() {
        super.setOnline();
        try {
            name.setOnline();
            imageID.setOnline();

        }catch(NullPointerException ex){

        }
    }

    @Override
    public void update(String key) {
        if(key.compareTo(this.phone) == 0) {
            initialize((String)userID.get());
        }else{
            setOnline();
            notifyUpdate(this.key);
        }
    }

    @Override
    public void notOnline(String key) {
        Log.d("UNITTEST","Not online");
    }

    @Override
    public int compareTo(BaseResource o) {
        Contact c = (Contact)o;

        if(this.name.get().compareTo(c.getName()) == 0) {
            return this.getKey().compareTo(o.getKey());
        }
        return this.name.get().compareTo(c.getName());
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
