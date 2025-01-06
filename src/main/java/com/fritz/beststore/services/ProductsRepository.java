package com.fritz.beststore.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fritz.beststore.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer>{

}
