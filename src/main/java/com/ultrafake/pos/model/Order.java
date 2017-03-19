package com.ultrafake.pos.model;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class Order {

    public int orderNumber = -1;

    public Instant timestamp = Instant.now();

    public BigDecimal subTotal = new BigDecimal(0);

    public BigDecimal totalTax = new BigDecimal(0);

    // I'm assuming "grantTotal" was a typo in the PDF
    public BigDecimal grandTotal = new BigDecimal(0);

    public List<OrderLineItem> lineItems = new LinkedList<OrderLineItem>();




}
