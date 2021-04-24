package com.derlars.moneyflow.Resource;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseResource;
import com.derlars.moneyflow.Resource.Callbacks.BaseResourceCallback;

import java.util.List;

public class Item<Callback extends BaseCallback> extends BaseResource<Callback> {
    Value<String> title;

    Value<String> class1;
    Value<String> class2;
    Value<String> class3;
    Value<String> class4;
    Value<String> class5;

    Value<Long> timestamp;

    Value<String> itemPriceKey;

    public Item(Callback callback) {
        super("ItemClassifications", callback);

        title = new Value(this.path,"title",true,true,false,this);

        itemPriceKey = new Value(this.path,"itemPriceKey",true,true,false,this);

        timestamp = new Value(this.path,"timestamp",true,true,false,this);

        class1 = new Value(this.path,"class1",true,true,false,this);
        class2 = new Value(this.path,"class2",true,true,false,this);
        class3 = new Value(this.path,"class3",true,true,false,this);
        class4 = new Value(this.path,"class4",true,true,false,this);
        class5 = new Value(this.path,"class5",true,true,false,this);
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

    public void setClassification(List<String> classification) {
        if(classification.size() >= 1) {
            class1.set(classification.get(0));
            if (classification.size() >= 2) {
                class2.set(classification.get(1));
                if (classification.size() >= 3) {
                    class3.set(classification.get(2));
                    if (classification.size() >= 4) {
                        class4.set(classification.get(3));
                        if (classification.size() >= 5) {
                            class5.set(classification.get(4));
                        }else{
                            class5.set("");
                        }
                    }else{
                        class5.set("");
                        class4.set("");
                    }
                }else{
                    class5.set("");
                    class4.set("");
                    class3.set("");
                }
            }else{
                class5.set("");
                class4.set("");
                class3.set("");
                class2.set("");
            }
        }else{
            class5.set("");
            class4.set("");
            class3.set("");
            class2.set("");
            class1.set("");
        }
    }

    @Override
    public void setOnline() {
        super.setOnline();
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
