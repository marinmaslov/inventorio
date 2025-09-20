package com.inventorio;


import com.inventorio.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private Long createdProductId;

    @AfterEach
    void cleanup() {
        if (createdProductId != null) {
            productRepository.deleteById(createdProductId);
        }
    }

    String getAdminJwt() throws Exception {
        String jsonBody = "{\"username\":\"admin\",\"password\":\"adminpwd123\"}";
        return mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void testCreateAndDeleteProductAsAdmin() throws Exception {
        String adminJwt = getAdminJwt();

        String productJson = """
        {
            "code": "ZZZ999YYYY",
            "name": "DeleteMe",
            "priceEur": 5.00,
            "available": true
        }
        """;

        // Create product
        mockMvc.perform(
            post("/api/products")
                .header("Authorization", "Bearer " + adminJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isOk()
        );

        // Extract product ID from DB
        var product = productRepository.findByCode("ZZZ999YYYY").orElse(null);
        assertThat(product).isNotNull();
        createdProductId = product.getId();

        // Delete product
        mockMvc.perform(delete("/api/products/" + createdProductId)
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify deletion
        assertThat(productRepository.findById(createdProductId)).isEmpty();
        createdProductId = null;
    }
}