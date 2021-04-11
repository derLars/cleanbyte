package com.derlars.moneyflow;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.derlars.moneyflow.Database.Abstracts.BaseCallback;
import com.derlars.moneyflow.Database.Callbacks.BaseValueCallback;
import com.derlars.moneyflow.Resource.ItemPrice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ItemPriceTest extends BaseUnitTest implements BaseCallback {

    @Before
    public void before() {
        setFlags("isUpdated");

        ItemPrice preset = new ItemPrice("OnlineTest",null);
        preset.setOnline();
        preset.delete();

        delay();
        resetFlags();
    }

    @Test
    public void offlineTest() {
        ItemPrice setItemPrice = new ItemPrice("OfflineTest",null);
        ItemPrice checkItemPrice = new ItemPrice("OfflineTest",this);
        delay();

        setItemPrice.setPrice(1.35);
        checkItemPrice.setPrice(2.95);

        checkFlags(false);

        double price = setItemPrice.getPrice();
        assertTrue(price < 1.36);
        assertTrue(price > 1.34);

        price = checkItemPrice.getPrice();
        assertTrue(price < 2.96);
        assertTrue(price > 2.94);
    }

    @Test
    public void onlineTest() {
        ItemPrice setItemPrice = new ItemPrice("OnlineTest",null);
        ItemPrice checkItemPrice = new ItemPrice("OnlineTest",this);
        delay();

        setItemPrice.setPrice(1.35);
        checkItemPrice.setPrice(2.95);
        checkItemPrice.addContributor("Test Contributor1",0.25);
        checkItemPrice.addContributor("Test Contributor2",0.75);

        setItemPrice.setOnline();
        checkFlags(true);

        double price = setItemPrice.getPrice();
        assertTrue(price < 1.36);
        assertTrue(price > 1.34);

        price = checkItemPrice.getPrice();
        assertTrue(price < 1.36);
        assertTrue(price > 1.34);

        List contributors = setItemPrice.getContributors();
        assertEquals(2,contributors.size());

        checkItemPrice.addContributor("Test Contributor1",0.333);
        checkItemPrice.addContributor("Test Contributor2",0.333);
        checkItemPrice.addContributor("Test Contributor3",0.333);
        checkFlags(true);


        assertEquals(3,contributors.size());

        checkItemPrice.setPrice(2.05);
        checkFlags(true);

        price = setItemPrice.getPrice();
        assertTrue(price < 2.06);
        assertTrue(price > 2.04);

        checkItemPrice.delete();
        delay();
    }

    @Override
    public void update(String key) {
        raiseFlag("isUpdated");
    }
}
