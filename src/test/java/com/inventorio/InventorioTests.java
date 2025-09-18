package com.inventorio;

import com.inventorio.product.ProductService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Disabled("Temporarily disabled during build")
class InventorioTests {

    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() {
    }

    @Test
    void testMainMethod() {
        Inventorio.main(new String[] {});
    }

    @Test
    void testBeanPresence() {
        assertThat(productService).isNotNull();
    }

}
