package org.hanghae99.productservice.repository;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotBlank;
import org.hanghae99.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByTitle(@NotBlank String title);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :product_id")
    Optional<Product> findProductById(Long id);
}
