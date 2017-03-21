package com.ultrafake.pos.model;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static com.ultrafake.pos.util.MathUtils.toMoneyString;

public class Order {

    private int orderNumber = -1;

    private Instant timestamp = Instant.now();

    private BigDecimal subTotal = new BigDecimal(0);

    private BigDecimal totalTax = new BigDecimal(0);

    // I'm assuming "grantTotal" was a typo in the PDF
    private BigDecimal grandTotal = new BigDecimal(0);

    private List<OrderLineItem> lineItems = new LinkedList<OrderLineItem>();

    private TenderRecord tenderRecord;

    public String getDisplayOrderNumber() {
        return String.format("%03d", getOrderNumber());
    }

    public String getDisplaySubtotal() {
        return toMoneyString(getSubTotal());
    }

    public String getDisplayTotalTax() {
        return toMoneyString(getTotalTax());
    }

    public String getDisplayGrandTotal() {
        return toMoneyString(getGrandTotal());
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public TenderRecord getTenderRecord() {
        return tenderRecord;
    }

    public void setTenderRecord(TenderRecord tenderRecord) {
        this.tenderRecord = tenderRecord;
    }
}
