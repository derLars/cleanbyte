package com.derlars.moneyflow.Resource.Abstracts;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;

public abstract class BaseContact<Callback extends BaseCallback> extends BaseResource<Callback> {
    protected Value<String> userID;

    protected Value<String> name;
    protected Value<String> imageID;

    protected HashMapValue<String> purchases;

    protected String phone;

    public BaseContact(String pathRoot, String phone, Callback callback) {
        super(pathRoot, callback);

        this.phone = phone;

        initialize();
    }

    public BaseContact(String pathRoot, String key, String phone, Callback callback) {
        super(pathRoot, key, callback);

        this.phone = phone;

        initialize();
    }

    protected abstract void initialize();

    public String getPhone() {
        return phone;
    }

    @Override
    public void setOnline() {
        super.setOnline();
        try {
            name.setOnline();
            imageID.setOnline();
            purchases.setOnline();
        }catch(NullPointerException ex){

        }

    }

    public String getName() {
        String n = this.name.get();

        return (n == null) ? "" : n;
    }

    @Override
    public int compareTo(BaseResource o) {
        BaseContact bc = (BaseContact)o;

        int compare = this.name.get().compareTo(bc.getName());
        if(compare == 0) {
            compare = this.key.compareTo(bc.getKey());
        }
        return compare;
    }
}
