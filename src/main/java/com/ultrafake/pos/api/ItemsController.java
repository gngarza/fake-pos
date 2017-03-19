package com.ultrafake.pos.api;

import com.ultrafake.pos.dao.Items;
import com.ultrafake.pos.model.Item;
import com.ultrafake.pos.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
public class ItemsController {

    @Autowired
    Items items;

    @RequestMapping("/api/getAllItems")
    public List<Item> getAllItems() {
        return items.getAllItems();
    }

}
