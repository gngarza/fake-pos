package com.ultrafake.pos.dao;

import com.ultrafake.pos.model.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemsTest {

    @Autowired
    Items items;

    @Test
    public void getAllItems() throws Exception {
        List<Item> allItems = items.getAllItems();

        assertTrue("Should have more then 2 items", allItems.size() > 2);

        BigDecimal sum =
                allItems.stream()
                        .map(Item::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertTrue("Sum of all item prices should be greater then zero",
                sum.compareTo(BigDecimal.ZERO) > 0);

        Set<String> itemNames =
                allItems.stream().map(Item::getName).collect(Collectors.toSet());


        assertTrue("Should have unique names", allItems.size() == itemNames.size());
    }

    @Test
    public void itemFor() throws Exception {
        // The apostrophe ain't the one on the keyboard (it's ’ not ').
        Item item = items.itemFor("Meat Lover’s Pizza");

        assertNotNull(item);
        assertNotNull(item.getPrice());
        assertTrue(item.getPrice().compareTo(BigDecimal.ZERO) > 0);
    }

}