package org.hanghae99.productservice.repository;

import jakarta.validation.constraints.NotBlank;
import org.hanghae99.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByTitle(@NotBlank String title);
}
