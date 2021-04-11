package com.derlars.moneyflow.Database.Abstracts;

import java.util.ArrayList;
import java.util.List;

public abstract class Subscriptable<Callback extends BaseCallback> {

    protected List<Callback> callbacks = new ArrayList();

    public Subscriptable(Callback callback) {
        if(callback != null) {
            subscribe(callback);
        }
    }
    public void subscribe(Callback callback) {
        if(callback != null && !callbacks.contains(callback)) {
            callbacks.add(0,callback);
        }
        cleanCallbacks();
    }

    public void unsubscribe(Callback callback) {
        if(callback != null && callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
        cleanCallbacks();
    }

    public void notifyUpdate(String key) {
        for(Callback callback : callbacks) {
            if(callback != null) {
                callback.update(key);
            }
        }
    }

    private void cleanCallbacks() {
        for(int i=callbacks.size()-1; i>=0; i--) {
            if(callbacks.get(i) == null) {
                callbacks.remove(i);
            }
        }
    }
}
