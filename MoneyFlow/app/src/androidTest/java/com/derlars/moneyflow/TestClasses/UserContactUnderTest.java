package com.derlars.moneyflow.TestClasses;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Resource.UserContact;

public class UserContactUnderTest extends UserContact {
    public UserContactUnderTest(String key, BaseCallback callback) {
        super(key, callback);
    }

    public Long getPremium() {
        return premium.get();
    }

    @Override
    public void delete() {
        super.delete();
        premium.delete();
    }

    @Override
    public void notOnline(String key) {

    }
}
