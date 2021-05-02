package com.derlars.moneyflow.Resource;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;

import java.util.List;

public class ItemPrice<Callback extends BaseCallback> extends BaseResource<Callback> {
    Value<Double> price;
    HashMapValue<Double> contributors;

    public ItemPrice(Callback callback) {
        super("ItemPrice", callback);

        price = new Value(this.path,"price",true,true,false,this);
        contributors = new HashMapValue(this.path,"contributors",true,true,false,this);
    }

    public ItemPrice(String key, Callback callback) {
        super("ItemPrices",key,callback);

        price = new Value(this.path,"price",true,true,false,this);
        contributors = new HashMapValue(this.path,"contributors",true,true,false,this);
    }

    public void setPrice(Double price) {
        this.price.set(price);
    }

    public Double getPrice() {
        Double t = this.price.get();
        if(t == null) {
            t = 0.0;
        }

        return t;
    }

    public void addContributor(String contributor,Double contribution) {
        contributors.put(contributor,contribution);
    }

    public void removeContributor(String contributor) {
        contributors.delete(contributor);
    }

    public List<String> getContributors() {
        return contributors.getKeyList();
    }

    public void delete() {
        price.delete();
        contributors.clear();
    }

    public void setOnline() {
        super.setOnline();

        try {
            price.setOnline();
            contributors.setOnline();
        }catch(NullPointerException ex){

        }
    }

    public String toString() {
        return "ItemPrice {"
                + " price: " + price.toString() + ";"
                + " contributors: " + contributors.toString() + ";"
                + super.toString()
                + "};";
    }

    @Override
    public void notOnline(String key) {

    }

    @Override
    public void update(String key) {
        setOnline();
        notifyUpdate(this.key);
    }

    @Override
    public int compareTo(BaseResource o) {
        ItemPrice i = (ItemPrice)o;

        return (this.price.get() - i.getPrice()) > 0 ? 1 : ((this.price.get() - i.getPrice()) < 0 ? -1 : 0);
    }
}
