package com.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	public List<Product> findByProductNameContaining(String productName);

}
