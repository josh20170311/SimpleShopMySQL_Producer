package com.example.simpleshopmysql.repo;

import com.example.simpleshopmysql.models.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder,Integer> {
}
