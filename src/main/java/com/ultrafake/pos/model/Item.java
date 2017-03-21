package com.ultrafake.pos.model;


import java.math.BigDecimal;

public class Item {

    private String name;

    private BigDecimal price;

    public Item() {}

    public Item(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDisplayPrice() {
        return price == null ? "null" : price.toString();
    }
}
