package com.derlars.moneyflow.Database.Subscriptables;

import com.derlars.moneyflow.Database.Abstracts.Subscriptable;
import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;

public class SubscriptableBaseValue <Callback extends BaseValueCallback> extends Subscriptable<Callback> {
    public SubscriptableBaseValue(Callback callback) {
        super(callback);
    }

    public void notifyNotOnline(String key) {
        for(Callback callback : callbacks) {
            if(callback != null) {
                callback.notOnline(key);
            }
        }
    }
}
