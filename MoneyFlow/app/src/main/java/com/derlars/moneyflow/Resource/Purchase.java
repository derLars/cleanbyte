package com.derlars.moneyflow.Resource;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;

public class Purchase<Callback extends BaseCallback> extends BaseResource<Callback> {
    Value<String> name;

    HashMapValue<String> itemKeys;

    public Purchase(Callback callback) {
        super("Purchase", callback);

        initialize();
    }

    public Purchase(String key, Callback callback) {
        super("Purchase",key, callback);

        initialize();
    }

    protected void initialize() {
        name = new Value(this.path,"name",true,true,false,this);
        name.set(this.key);

        itemKeys = new HashMapValue(this.path,"itemKeys",true,true,false,this);
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

    public void addItemKey(String itemKey) {
        itemKeys.put(itemKey,"1");
    }

    public void setOnline() {
        super.setOnline();
        try {
            name.setOnline();
            itemKeys.setOnline();

        }catch(NullPointerException ex){

        }
    }

    public void delete() {
        name.delete();
        itemKeys.clear();
    }

    @Override
    public void notOnline(String key) {

    }

    @Override
    public void update(String key) {

    }

    @Override
    public int compareTo(BaseResource o) {
        return 0;
    }
}
