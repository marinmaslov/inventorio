package com.inventorio;

import com.inventorio.product.Product;
import com.inventorio.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceIntegrationTests {

    @Autowired
    private ProductService productService;

    @BeforeEach
    void cleanup() {
        productService.list().stream()
                .filter(p -> "ABC123DEFG".equals(p.getCode()))
                .forEach(p -> productService.delete(p.getId()));
    }

    @Test
    void testCreateAndGetProduct() {
        Product product = new Product();
        product.setCode("ABC123DEFG");
        product.setName("Test Product");
        product.setPriceEur(new BigDecimal("10.00"));
        product.setAvailable(true);

        Product saved = productService.create(product);
        assertThat(saved.getId()).isNotNull();

        Optional<Product> fetched = productService.get(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getCode()).isEqualTo("ABC123DEFG");
    }

    @Test
    void testListProducts() {
        assertThat(productService.list()).isNotNull();
    }
}