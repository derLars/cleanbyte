package com.derlars.moneyflow.Resource;

import android.provider.ContactsContract;
import android.util.Log;

import com.derlars.moneyflow.Authentication.Authentication;
import com.derlars.moneyflow.Container.Purchases;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.HashMapValue;
import com.derlars.moneyflow.Database.Value;
import com.derlars.moneyflow.Resource.Abstracts.BaseContact;
import com.derlars.moneyflow.Utils.DatabaseTime;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class UserContact extends BaseContact implements DatabaseTime.Callback{
    protected Value<Long> premium;

    private DatabaseTime databaseTime;
    private long lastTimestamp = 0;

    private static long THREE_MONTH = 1000*60*60*24*90L;

    public UserContact(String phone, BaseCallback callback) {
        super("Contact", Authentication.getInstance().getUID(),phone,callback);

        databaseTime = DatabaseTime.getInstance(this);
    }

    public void free() {
        if(this.premium != null) {
            this.premium.unsubscribe(this);
            this.premium = null;
        }
        if(this.userID != null) {
            this.userID.unsubscribe(this);
            this.userID = null;
        }

        if(this.name != null) {
            this.name.unsubscribe(this);
            this.name = null;
        }

        if(this.imageID != null) {
            this.imageID.unsubscribe(this);
            this.imageID = null;
        }

        if(this.purchases != null) {
            this.purchases.unsubscribe(this);
            this.purchases = null;
        }

        if(this.databaseTime != null) {
            this.databaseTime.unsubscribe(this);
            this.databaseTime = null;
        }
    }

    @Override
    protected void initialize() {
        premium = new Value(this.path,"premium",true,true,false,this);

        userID = new Value("UID",this.phone,false,true,true,null);

        name = new Value(this.path,"name",true,true,false,this);

        imageID = new Value(this.path,"imageID",true,true,false,this);

        purchases = new HashMapValue(this.path,"purchases",true,true,false,this);

        premium.setOnline();
        userID.setOnline();
        name.setOnline();
        imageID.setOnline();
        purchases.setOnline();

        userID.set(key);

        imageID.set("dummy.jpg");
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

    public List<String> getPurchases() {
        return purchases.getKeyList();
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
        if(this.name != null && key.compareTo(this.name.getKey()) == 0) {
            name.set(this.phone);
        }

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

    protected void concurrentUpdate(Runnable fun) {
        boolean success;
        do {
            try {
                fun.run();
                success = true;
            } catch (ConcurrentModificationException ex) {
                success = false;
            }
        }while(!success);
    }

    @Override
    public void update(String key) {
        if(this.purchases != null && key.compareTo(this.purchases.getKey()) == 0) {
            concurrentUpdate(() -> {
                Purchases purchases = Purchases.getInstance();
                if(purchases != null) {
                    List<String> keyList = this.purchases.getKeyList();
                    for (String k : keyList) {
                        purchases.add(k);
                    }
                }
            });
        }

        this.setOnline();
        online = true;

        notifyUpdate(this.key);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
