package com.derlars.moneyflow.Database.Callbacks;


import com.derlars.moneyflow.Database.Abstracts.BaseCallback;

public interface BaseValueCallback extends BaseCallback {
    void notOnline(String key);
}
