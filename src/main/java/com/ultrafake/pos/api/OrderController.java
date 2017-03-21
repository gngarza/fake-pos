package com.ultrafake.pos.api;


import com.ultrafake.pos.dao.Items;
import com.ultrafake.pos.model.Item;
import com.ultrafake.pos.model.Order;
import com.ultrafake.pos.model.OrderLineItem;
import com.ultrafake.pos.model.TenderRecord;
import com.ultrafake.pos.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class OrderController {

    AtomicInteger orderSeq = new AtomicInteger(0);

    ConcurrentHashMap<String, Order> currentOrders = new ConcurrentHashMap<>();

    BigDecimal salesTaxRate = new BigDecimal("0.096");

    @Autowired
    Items items;

    @RequestMapping("/api/orders/createNew")
    public Order createNew(HttpSession session) {
        Order newOrder = new Order();

        currentOrders.put(session.getId(), newOrder);

        return newOrder;
    }

    @RequestMapping(value="/api/orders/addItem", method=RequestMethod.POST)
    public Order addItem(
            HttpSession session,
            @RequestBody AddItemRequest addItemRequest) {

        String itemName = addItemRequest.itemName;

        Order order = getCurrentOrder(session);

        Optional<OrderLineItem> existingLineItem =
                findLineItem(order, itemName);

        if(existingLineItem.isPresent()) {
            OrderLineItem lineItem = existingLineItem.get();

            lineItem.setQty(lineItem.getQty() + 1);

            lineItem.setExtendedPrice(
                    lineItem.getPrice().multiply(new BigDecimal(lineItem.getQty())));
        } else {
            OrderLineItem lineItem = new OrderLineItem();

            Item item = items.itemFor(itemName);

            lineItem.setItem(item);
            lineItem.setQty(1);
            lineItem.setPrice(item.getPrice());
            lineItem.setExtendedPrice(item.getPrice());

            order.getLineItems().add(lineItem);
        }

        updateTotals(order);

        return order;
    }

    @RequestMapping(value="/api/orders/voidItem", method=RequestMethod.POST)
    public Order voidItem(HttpSession session, @RequestBody VoidItemRequest request) {

        Order order = getCurrentOrder(session);

        Optional<OrderLineItem> maybeLineItem = findLineItem(order, request.itemName);

        if(maybeLineItem.isPresent()) {
            order.getLineItems().remove(maybeLineItem.get());
        }

        updateTotals(order);

        return order;
    }

    // Not currently used from the front-end, but in the spec
    @RequestMapping(value="/api/orders/changeItemQuantity", method=RequestMethod.POST)
    public Order changeItemQuantity(
            HttpSession session,
            @RequestBody ChangeItemQuantityRequest request) {

        Order order = getCurrentOrder(session);

        Optional<OrderLineItem> maybeLineItem = findLineItem(order, request.itemName);

        if(maybeLineItem.isPresent()) {
            OrderLineItem lineItem = maybeLineItem.get();

            if(request.newQty <= 0) {
                order.getLineItems().remove(lineItem);
            } else {
                lineItem.setQty(request.newQty);
            }
        }

        updateTotals(order);

        return order;
    }

    @RequestMapping(value="/api/orders/calcChangeDue", method=RequestMethod.GET)
    public CalcChangeDueResponse calcChangeDue(
            HttpSession session,
            @RequestParam(name="amountTendered") String amountTenderedInput) {

        Order order = getCurrentOrder(session);

        try {
            BigDecimal amountTendered = new BigDecimal(amountTenderedInput);

            if(amountTendered.compareTo(BigDecimal.ZERO) < 0) {
                return CalcChangeDueResponse.error( "Please enter a non-negative number");
            }

            if(amountTendered.compareTo(order.getGrandTotal()) < 0) {
                return CalcChangeDueResponse.error("Please enter an amount greater then what is owed");
            }

            CalcChangeDueResponse resp = new CalcChangeDueResponse();
            resp.valid = true;
            resp.displayChangeDue =
                    MathUtils.toMoneyString(amountTendered.subtract(order.getGrandTotal()));

            return resp;

        } catch(NumberFormatException e) {
            return CalcChangeDueResponse.error("Please enter a valid number");
        }
    }

    @RequestMapping(value="/api/orders/completeOrder", method=RequestMethod.POST)
    public CompleteOrderResponse completeOrder(
            HttpSession session,
            @RequestBody CompleteOrderRequest request) {

        CalcChangeDueResponse changeDueResp =
                calcChangeDue(session, request.amountTenderedInput);

        CompleteOrderResponse resp = new CompleteOrderResponse();

        Order order = getCurrentOrder(session);

        if(order.getTenderRecord() != null) {
            resp.valid = true;
            resp.order = order;
        } else if(!changeDueResp.valid) {
            resp.valid = false;
            resp.errorMessage = changeDueResp.errorMessage;
        } else {
            resp.valid = true;

            TenderRecord tenderRecord = new TenderRecord();
            tenderRecord.setAmountTendered(new BigDecimal(request.amountTenderedInput));
            tenderRecord.setChangeGiven(tenderRecord.getAmountTendered().subtract(order.getGrandTotal()));
            tenderRecord.setTimestamp(Instant.now());

            order.setTenderRecord(tenderRecord);
            order.setOrderNumber(orderSeq.getAndIncrement());

            resp.order = order;
        }

        return resp;
    }


    private Optional<OrderLineItem> findLineItem(Order order, String itemName) {
        return order.getLineItems().stream()
                .filter(it -> it.getItem().getName().equals(itemName)).findAny();
    }

    private Order getCurrentOrder(HttpSession session) {
        Order order = currentOrders.get(session.getId());

        if(order == null) {
            order = new Order();
            currentOrders.put(session.getId(), order);
        }
        return order;
    }

    protected void updateTotals(Order order) {

        order.setSubTotal(order.getLineItems().stream()
             .map(OrderLineItem::getExtendedPrice)
             .reduce(BigDecimal.ZERO, BigDecimal::add));

        order.setTotalTax(order.getSubTotal().multiply(salesTaxRate));

        order.setGrandTotal(order.getSubTotal().add(order.getTotalTax()));

    }
}
