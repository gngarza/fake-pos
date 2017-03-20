package com.ultrafake.pos.dao;


import com.ultrafake.pos.model.Item;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;

@Component
public class Items {

    private final static List<Item> itemList;

    static {
        LinkedList<Item> items = new LinkedList<>();

        try(InputStream itemsStream =
                Items.class.getClassLoader().getResourceAsStream("items.csv")) {

            Scanner scanner = new Scanner(itemsStream);

            int lineNum = 0;

            while(scanner.hasNextLine()) {

                lineNum++;

                String fields[] = scanner.nextLine().split(",");

                if(fields.length < 2) {
                    continue;
                }

                String name = fields[0].trim();

                // Skip the first line in an intentionally brittle way so format changes
                // will cause this to get re-examined
                if(lineNum == 1 && "Item Name".equals(name) && "Price".equals(fields[1].trim())) {
                    continue;
                }

                BigDecimal price = new BigDecimal(fields[1].trim());

                items.add(new Item(name, price));
            }
        } catch(Exception e) {
            throw new Error(e);
        }

        items.sort(Comparator.comparing(Item::getName));

        itemList = Collections.unmodifiableList(items);
    }

    public List<Item> getAllItems() {
        return itemList;
    }

    public Item itemFor(String name) {
        for(Item item : itemList) {
            if(item.getName().equals(name)) {
                return item;
            }
        }

        throw new Error("Unknown item: " + name);
    }

}
