package com.ultrafake.pos.api;

import com.ultrafake.pos.model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpSession;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderControllerTest {

    @Autowired
    OrderController orderController;

    @Test
    public void createNew() throws Exception {

        HttpSession httpSession = new MockHttpSession();

        Order order = orderController.createNew(httpSession);

        assertEquals(0, order.getLineItems().size());
        assertEquals(-1, order.getOrderNumber());
        assertEquals(0, order.getGrandTotal().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void addItem() throws Exception {

        HttpSession httpSession = new MockHttpSession();

        Order o1 = orderController.createNew(httpSession);

        AddItemRequest addCokeReq = new AddItemRequest("Coke");

        Order o2 = orderController.addItem(httpSession, addCokeReq);

        assertEquals(o1, o2);

        assertEquals(1, o2.getLineItems().size());
        assertEquals(1, o2.getLineItems().get(0).getQty());

        BigDecimal firstTotal = o2.getGrandTotal();

        orderController.addItem(httpSession, addCokeReq);

        assertEquals(2, o2.getLineItems().get(0).getQty());
        assertTrue(firstTotal.multiply(new BigDecimal(2)).equals(o2.getGrandTotal()));
    }

    @Test
    public void voidItem() throws Exception {
        HttpSession httpSession = new MockHttpSession();

        Order order = orderController.createNew(httpSession);

        AddItemRequest addCokeReq = new AddItemRequest("Coke");
        AddItemRequest addDietCokeReq = new AddItemRequest("Diet Coke");

        orderController.addItem(httpSession, addCokeReq);
        orderController.addItem(httpSession, addDietCokeReq);

        assertEquals(2, order.getLineItems().size());

        VoidItemRequest voidCokeReq = new VoidItemRequest("Coke");

        orderController.voidItem(httpSession, voidCokeReq);

        assertEquals(1, order.getLineItems().size());
        assertEquals("Diet Coke", order.getLineItems().get(0).getItem().getName());
    }

    @Test
    public void changeItemQuantity() throws Exception {

        HttpSession httpSession = new MockHttpSession();

        Order order = orderController.createNew(httpSession);

        orderController.addItem(httpSession, new AddItemRequest("Coke"));
        orderController.addItem(httpSession, new AddItemRequest("Diet Coke"));

        assertEquals(2, order.getLineItems().size());

        orderController.changeItemQuantity(
                httpSession,
                new ChangeItemQuantityRequest(5, "Coke"));

        assertEquals(2, order.getLineItems().size());
        assertEquals(5, order.getLineItems().get(0).getQty());

        orderController.changeItemQuantity(
                httpSession,
                new ChangeItemQuantityRequest(0, "Coke"));

        assertEquals(1, order.getLineItems().size());
        assertEquals("Diet Coke", order.getLineItems().get(0).getItem().getName());
    }

}