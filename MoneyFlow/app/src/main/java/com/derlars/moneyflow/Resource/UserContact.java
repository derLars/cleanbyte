package com.derlars.moneyflow.Resource;

import android.provider.ContactsContract;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Utils.DatabaseTime;

public class UserContact extends Contact implements DatabaseTime.Callback{
    protected Value<Long> premium;

    private DatabaseTime databaseTime;
    private long lastTimestamp = 0;

    private long THREE_MONTH = 1000*60*60*24*90L;
    public UserContact(String key, BaseCallback callback) {
        super(key, callback);

        databaseTime = DatabaseTime.getInstance(this);
    }

    @Override
    protected void initialize() {
        super.initialize();

        name.setReadOnly(false);
        imageID.setReadOnly(false);

        premium = new Value(this.path,"premium",this);
    }

    public boolean isPremium() {
        lastTimestamp = databaseTime.getLastTime();
        return lastTimestamp > 0 && isOnline() && premium.get() != null && premium.get() > lastTimestamp;
    }

    public void setOnline() {
        super.setOnline();
        try {
            premium.setOnline();
        }catch(NullPointerException ex){

        }
    }

    @Override
    public void notOnline(String key) {
        if(databaseTime == null) {
            databaseTime = DatabaseTime.getInstance(this);
        }

        if(key.compareTo("premium") == 0) {
            premium.set(databaseTime.getLastTime()+ THREE_MONTH);
            this.setOnline();
        }
    }

    @Override
    public void timeUpdate(long time) {
        this.lastTimestamp = time;
    }
}
