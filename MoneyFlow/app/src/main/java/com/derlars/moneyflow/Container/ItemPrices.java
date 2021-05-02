package com.derlars.moneyflow.Container;

import com.derlars.moneyflow.Container.Abstracts.BaseContainer;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Resource.ItemPrice;

public class ItemPrices extends BaseContainer<ItemPrice> {
    private static ItemPrices INSTANCE;

    private ItemPrices(){}

    public static ItemPrices getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ItemPrices();
        }
        return INSTANCE;
    }

    public static ItemPrices getInstance(BaseCallback callback) {
        Purchases.getInstance();

        INSTANCE.subscribe(callback);

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
