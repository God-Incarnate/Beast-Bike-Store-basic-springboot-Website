package com.ProductBasedProject.ProductDetailsProject.Repositories;

import com.ProductBasedProject.ProductDetailsProject.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Product,Integer> {
//    @Query("select p1.id,p1.name,p1.brand,p1.category,p1.price,p1.imageFileName,p1.createdAt from Product p1")
//    List<Product> fetchdata();
}
