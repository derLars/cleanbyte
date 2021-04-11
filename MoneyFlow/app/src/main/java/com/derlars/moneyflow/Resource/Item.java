package com.derlars.moneyflow.Resource;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;
import com.derlars.moneyflow.Resource.Callbacks.BaseResourceCallback;

import java.util.List;

public class Item<Callback extends BaseCallback> extends BaseResource<Callback> {
    Value<String> title;

    List<Value<String>> classification;

    Value<Long> timestamp;

    ItemPrice itemPrice;

    public Item(Callback callback) {
        super("ItemClassifications", callback);
        itemPrice = new ItemPrice(this);

        title = new Value(this.path,"title",this);

        timestamp = new Value(this.path,"timestamp",this);
    }

    public long getTimestamp() {
        return timestamp.get();

    }
    @Override
    public void setOnline() {
        super.setOnline();
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
