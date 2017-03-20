package com.ultrafake.pos.model;

import com.ultrafake.pos.util.MathUtils;

import java.math.BigDecimal;

public class OrderLineItem {

    private int qty = -1;

    private Item item;

    private BigDecimal price = new BigDecimal(0);

    private BigDecimal extendedPrice = new BigDecimal(0);

    public String getDisplayExtendedPrice() {
        return MathUtils.toMoneyString(extendedPrice);
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getExtendedPrice() {
        return extendedPrice;
    }

    public void setExtendedPrice(BigDecimal extendedPrice) {
        this.extendedPrice = extendedPrice;
    }
}
