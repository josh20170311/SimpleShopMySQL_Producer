package com.example.simpleshopmysql.repo;

import com.example.simpleshopmysql.models.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, Integer> {
}
