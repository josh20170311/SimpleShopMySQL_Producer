package com.example.simpleshopmysql.controllers;

import com.example.simpleshopmysql.models.Product;
import com.example.simpleshopmysql.models.SaleOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="api/v2")
public class SendMessageController {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@RequestMapping(value = "/put_product", method = RequestMethod.POST)
	public ResponseEntity putProduct(@RequestBody Product product) {
		rabbitTemplate.setReceiveTimeout(1000000L);
		rabbitTemplate.setReplyTimeout(1000000L);
		try{
			Product newProduct = (Product) rabbitTemplate.convertSendAndReceive("put_product", product);
			if(!newProduct.getName().equals("null"))
				return ResponseEntity.ok(newProduct);
			else{
				return ResponseEntity.badRequest().build();
			}
		}catch(Exception e){
			return ResponseEntity.badRequest().build();
		}
	}
	@RequestMapping(value = "/put_order", method = RequestMethod.POST)
	public ResponseEntity putOrder(@RequestBody SaleOrder order) {
		rabbitTemplate.setReceiveTimeout(1000000L);
		rabbitTemplate.setReplyTimeout(1000000L);
		try{
			SaleOrder newOrder = (SaleOrder) rabbitTemplate.convertSendAndReceive("put_order", order);
			if(!newOrder.getEmail().equals("null"))
				return ResponseEntity.ok(newOrder);
			else{
				return ResponseEntity.badRequest().build();
			}
		}catch(Exception e){
			return ResponseEntity.badRequest().build();
		}
	}
}