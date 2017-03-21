package com.ultrafake.pos.api;


public class ChangeItemQuantityRequest {

    public String itemName;

    public int newQty;

    public ChangeItemQuantityRequest() {}

    public ChangeItemQuantityRequest(int newQty, String itemName) {
        this.newQty = newQty;
        this.itemName = itemName;
    }
}
