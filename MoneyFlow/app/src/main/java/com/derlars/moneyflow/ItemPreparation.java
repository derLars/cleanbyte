package com.derlars.moneyflow;

import com.derlars.moneyflow.Resource.Abstracts.BaseContact;
import com.derlars.moneyflow.Resource.Contact;
import com.derlars.moneyflow.Resource.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPreparation {
    public final String key;

    public String name;
    public double price;

    public List<String> classification;

    //public List<Contact> contacts;

    public Map<BaseContact,Double> ratios;

    public ItemPreparation(String name, double price, List<String> classification, Map<BaseContact,Double> ratios) {
        this.key = System.currentTimeMillis() + name;

        this.name = name;
        this.price = price;

        this.classification = classification;

        //this.contacts = contacts;

        this.ratios = ratios;
    }
}
