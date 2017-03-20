package com.ultrafake.pos.api;


import com.ultrafake.pos.model.Order;

public class CompleteOrderResponse {

    public boolean valid;
    public String errorMessage;
    public Order order;
}
