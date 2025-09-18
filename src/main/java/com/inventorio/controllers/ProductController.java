package com.inventorio.controllers;

import com.inventorio.product.Product;
import com.inventorio.product.ProductService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LogManager.getLogger(ProductController.class);
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody Product product) {
        try {
            service.create(product);
            logger.info("Created product with ID: {}", product.getId());
            return ResponseEntity.ok("Created product with ID: " + product.getId());
        } catch (Exception e) {
            logger.error("Failed to create product", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable Long id) {
        try {
            Product product = service.get(id).orElse(null);
            if (product == null) {
                logger.info("There's no product with id {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There's no product with id " + id);
            }
            logger.info("Fetched product with id {}: {}", id, product.toString());
            return ResponseEntity.ok("Fetched product: " + product.toString());
        } catch (Exception e) {
            logger.error("Failed to get product with id {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<String> list() {
        try {
            List<Product> products = service.list();
            logger.info("Fetching list of all products [items: {}]", products.size());
            return ResponseEntity.ok(products.toString());
        } catch (Exception e) {
            logger.error("Failed to get list of products", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            logger.info("Deleted product with id {}", id);
            return ResponseEntity.ok("Deleted product with id " + id);
        } catch (Exception e) {
            logger.error("Failed to delete product with id {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
