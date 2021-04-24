package com.derlars.moneyflow;

import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.Item;
import com.derlars.moneyflow.Resource.ItemPrice;
import com.derlars.moneyflow.Resource.Purchase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseGenerator {
    private Map<String,ItemPreparation> itemPreparations = new HashMap();
    private List<ItemPreparation> all = new ArrayList();

    public void addItem(ItemPreparation item) {
        itemPreparations.put(item.key,item);
        all.add(item);
    }

    public void removeItem(String key) {
        all.remove(itemPreparations.get(key));
        itemPreparations.remove(key);
    }

    public List<ItemPreparation> getItems() {
        return all;
    }

    public void publish() {
        Map<Contact, Purchase> purchases = new HashMap();
        List<Item> items = new ArrayList();
        List<ItemPrice> itemPrices = new ArrayList();

        for(String key : itemPreparations.keySet()) {
            ItemPrice itemPrice = new ItemPrice(null);
            itemPrices.add(itemPrice);

            itemPrice.setPrice(itemPreparations.get(key).price);

            for(Contact c : itemPreparations.get(key).contacts) {
                itemPrice.addContributor(c.getKey(),itemPreparations.get(key).ratios.get(c));

                if(!purchases.containsKey(c)) {
                    purchases.put(c,new Purchase(null));
                }
                Item item = new Item(null);
                items.add(item);

                item.setItemPriceKey(itemPrice.getKey());
                item.setTitle(itemPreparations.get(key).name);
                item.setClassification(itemPreparations.get(key).classification);

                purchases.get(c).addItemKey(item.getKey());
            }
        }

        for(ItemPrice itemPrice : itemPrices) {
            itemPrice.setOnline();
        }

        for(Item item : items) {
            item.setOnline();
        }

        for(Contact c : purchases.keySet()) {
            //c.

        }
    }
}
