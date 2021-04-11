package com.derlars.moneyflow.Resource.Abstracts;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.Abstracts.Subscriptable;
import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;

public abstract class BaseResource<Callback extends BaseCallback> extends Subscriptable<Callback> implements BaseValueCallback, Comparable<BaseResource> {
    protected String pathRoot;
    protected String key;
    protected String path;

    protected boolean online;

    protected boolean selected = false;
    protected boolean displayed = false;

    public BaseResource(String pathRoot, Callback callback) {
        super(callback);

        String timestamp = ""+System.currentTimeMillis();
        String creator = "DUMMY";

        this.pathRoot = pathRoot;

        this.key = creator + "_" + timestamp;

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
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
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
