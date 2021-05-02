package com.derlars.moneyflow.Resource.Abstracts;

import com.derlars.moneyflow.Container.Contacts;
import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.Abstracts.Subscriptable;
import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseResource<Callback extends BaseCallback> extends Subscriptable<Callback> implements BaseValueCallback, Comparable<BaseResource> {
    protected String pathRoot;
    protected String key;
    protected String path;

    protected boolean online;

    protected boolean selected = false;
    protected boolean displayed = false;

    protected boolean readable;
    protected boolean writable;

    public BaseResource(String pathRoot, Callback callback) {
        super(callback);

        String timestamp = ""+System.currentTimeMillis();
        String creator = Contacts.getInstance().getUserContact().getPhone();

        this.pathRoot = pathRoot;

        this.key = creator + "_" + timestamp + "_" + ThreadLocalRandom.current().nextInt();

        this.path = this.pathRoot + "/" + this.key;

        this.online = false;
    }

    public BaseResource(String pathRoot, String key, Callback callback) {
        super(callback);

        this.pathRoot = pathRoot;

        this.key = key;

        this.path = this.pathRoot + "/" + this.key;

        this.online = false;
    }

    public String getKey() {
        return this.key;
    }

    public void setOnline() {
        this.online = true;
    }

    public boolean isOnline() {
        return online;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        notifyUpdate(this.key);
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;

        notifyUpdate(this.key);
    }

    public boolean isDisplayed() {
        return this.displayed;
    }

    public String toString() {
        return "BaseResource {"
                + " pathRoot: " + pathRoot + ";"
                + " key: " + key + ";"
                + " path: " + path + ";"
                //+ " title: " + title.toString() + ";"
                + super.toString()
                + "};";
    }
}
