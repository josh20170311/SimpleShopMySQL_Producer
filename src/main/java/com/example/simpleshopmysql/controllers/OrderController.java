package com.example.simpleshopmysql.controllers;

import com.example.simpleshopmysql.models.LineItem;
import com.example.simpleshopmysql.models.MessageResponse;
import com.example.simpleshopmysql.models.SaleOrder;
import com.example.simpleshopmysql.services.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="api/v1")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value="/order",method = RequestMethod.GET)
    public ResponseEntity<List<SaleOrder>> getAllOrders() {
        try {
            List<SaleOrder> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order",method = RequestMethod.POST)
    public ResponseEntity<SaleOrder> createOrders(@RequestBody SaleOrder saleOrder) {
        rabbitTemplate.setReceiveTimeout(1000000L);
        rabbitTemplate.setReplyTimeout(1000000L);
        try {
//            SaleOrder generatedOrder = orderService.createOrders(saleOrder);
            SaleOrder generatedOrder = (SaleOrder) rabbitTemplate.convertSendAndReceive("put_order", saleOrder);
            if(!generatedOrder.getEmail().equals("null"))
                return ResponseEntity.ok(generatedOrder);
            else{
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.GET)
    public ResponseEntity<SaleOrder> getOrder(@PathVariable("orderid") Integer orderid) {
        try {
            SaleOrder order = orderService.getOrder(orderid);
            if(order!=null)
                return ResponseEntity.ok(order);
            else
                return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.POST)
    public ResponseEntity<LineItem> createOrderLineItem(@PathVariable("orderid") Integer orderid, @RequestBody LineItem lineItem) {
        try {
            LineItem savedLineItem = orderService.createOrderLineItem(orderid,lineItem);
            return ResponseEntity.ok(savedLineItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteOrder(@PathVariable("orderid") Integer orderid) {
        try {
            orderService.deleteOrder(orderid);
            return ResponseEntity.ok(new MessageResponse(200,"delete order success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @RequestMapping(value="/order/{orderid}/{lineitemid}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteOrderLineItem(@PathVariable("orderid") Integer orderid, @PathVariable("lineitemid") Integer lineitemid) {
        try {
            orderService.deleteOrderLineItem(orderid,lineitemid);
            return ResponseEntity.ok(new MessageResponse(200,"delete lineitem success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}/checkout", method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> checkout(@PathVariable("orderid") Integer orderid) {
        try {
            int state = orderService.checkout(orderid);
            if(state==1)
                return ResponseEntity.ok(new MessageResponse(200,"checkouted"));
            else if(state==0)
                return ResponseEntity.ok(new MessageResponse(404,"order id not found"));
            else {
                return ResponseEntity.ok(new MessageResponse(500,"order was checked out "));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse(500,"no inventory"));
        }
    }

}

