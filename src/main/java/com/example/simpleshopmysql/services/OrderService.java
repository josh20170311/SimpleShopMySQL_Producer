package com.example.simpleshopmysql.services;

import com.example.simpleshopmysql.models.LineItem;
import com.example.simpleshopmysql.models.Product;
import com.example.simpleshopmysql.models.SaleOrder;
import com.example.simpleshopmysql.repo.LineItemRepository;
import com.example.simpleshopmysql.repo.ProductRepository;
import com.example.simpleshopmysql.repo.SaleOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class OrderService {
    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<SaleOrder> getAllOrders() {
        List<SaleOrder> orders= saleOrderRepository.findAll();
        return orders;
    }

    public SaleOrder createOrders(SaleOrder saleOrder) {
        SaleOrder generatedOrder = saleOrderRepository.save(saleOrder);
        return generatedOrder;
    }

    public SaleOrder getOrder(Integer orderid) {
        Optional<SaleOrder> order = saleOrderRepository.findById(orderid);
        if(order.isPresent()) {
            return order.get();
        } else {
            return null;
        }
    }

    public LineItem createOrderLineItem(Integer orderid, LineItem lineItem) {
        if(lineItem==null)
            throw new RuntimeException("no lineitem data");
        Optional<SaleOrder> temporaryorder = saleOrderRepository.findById(orderid);
        if(temporaryorder.isEmpty())
            throw new RuntimeException("no order id");
        Optional<Product> temporaryproduct = productRepository.findById(lineItem.getSku());
        if(temporaryproduct.isEmpty())
            throw new RuntimeException("no order");
        Product product = temporaryproduct.get();
        if(lineItem.getQuantity()>product.getQuantity())
            throw new RuntimeException("no inventory");
        lineItem.setSaleOrder(temporaryorder.get());
        LineItem savedLineItem = lineItemRepository.save(lineItem);
        return savedLineItem;
    }

    public void deleteOrder(Integer orderid) {
        if(saleOrderRepository.existsById(orderid))
            saleOrderRepository.deleteById(orderid);
    }


    public void deleteOrderLineItem(Integer orderid, Integer lineitemid) {
        if(saleOrderRepository.existsById(orderid))
            if(lineItemRepository.existsById(lineitemid))
                lineItemRepository.deleteById(lineitemid);
    }

    public int checkout(Integer orderid) {
        Optional<SaleOrder> temporaryorder = saleOrderRepository.findById(orderid);
        if(temporaryorder.isPresent()) {
            SaleOrder saleOrder = temporaryorder.get();
            if(saleOrder.getState().equals("purchased"))
                return -1;
            List<LineItem> items = saleOrder.getLineItems();
            for(LineItem item : items) {
                Optional<Product> oproduct = productRepository.findById(item.getSku());
                if(oproduct.isPresent()) {
                    Product product = oproduct.get();
                    int quantity = product.getQuantity();
                    if(quantity>=item.getQuantity()) {
                        product.setQuantity(quantity-item.getQuantity());
                        productRepository.save(product);
                    } else {
                        throw new RuntimeException("inventory not enough");
                    }
                }
            }
            saleOrder.setCheckoutDate(LocalDateTime.now());
            saleOrder.setState("purchased");
            saleOrderRepository.save(saleOrder);
            return 1;
        } else {
            return 0;
        }
    }

}
