package com.derlars.moneyflow.Resource;

import android.provider.ContactsContract;
import android.util.Log;

import com.derlars.moneyflow.Authentication.Authentication;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseContact;
import com.derlars.moneyflow.Utils.DatabaseTime;

public class UserContact extends BaseContact implements DatabaseTime.Callback{
    protected Value<Long> premium;

    private DatabaseTime databaseTime;
    private long lastTimestamp = 0;

    private static long THREE_MONTH = 1000*60*60*24*90L;

    public UserContact(String phone, BaseCallback callback) {
        super("Contact", Authentication.getInstance().getUID(),phone,callback);

        databaseTime = DatabaseTime.getInstance(this);
    }

    @Override
    protected void initialize() {
        premium = new Value(this.path,"premium",true,true,false,this);

        userID = new Value("UID",this.phone,false,true,true,null);
        userID.setOnline();
        userID.set(key);

        name = new Value(this.path,"name",true,true,false,this);
        name.set(this.phone);

        imageID = new Value(this.path,"imageID",true,true,false,this);
        imageID.set("dummy.jpg");

        purchases = new HashMapValue(this.path,"purchases",true,true,false,this);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setImageID(String imageID) {
        this.imageID.set(imageID);
    }

    public boolean isPremium() {
        lastTimestamp = databaseTime.getLastTime();
        return lastTimestamp > 0 && isOnline() && premium.get() != null && premium.get() > lastTimestamp;
    }

    @Override
    public void setOnline() {
        super.setOnline();
        try {
            premium.setOnline();
            //purchases.setOnline();
        }catch(NullPointerException ex){

        }
    }

    public void delete() {
        name.delete();
        imageID.delete();
        purchases.clear();
    }

    @Override
    public void notOnline(String key) {
        if(databaseTime == null) {
            databaseTime = DatabaseTime.getInstance(this);
        }

        if(key.compareTo("premium") == 0) {
            Log.d("MUTEX","premium: " + THREE_MONTH);
            premium.set(databaseTime.getLastTime() + THREE_MONTH);

            this.setOnline();
        }
    }

    @Override
    public void timeUpdate(long time) {
        this.lastTimestamp = time;
    }

    @Override
    public void update(String key) {
        this.setOnline();
        online = true;

        notifyUpdate(this.key);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
