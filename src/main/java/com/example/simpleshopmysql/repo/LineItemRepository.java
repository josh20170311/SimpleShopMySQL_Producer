package com.example.simpleshopmysql.repo;

import com.example.simpleshopmysql.models.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Integer> {
}
