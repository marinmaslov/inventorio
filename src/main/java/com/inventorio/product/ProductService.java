package com.inventorio.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LogManager.getLogger(ProductService.class);
    private final ProductRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(Product product) {
        BigDecimal rate = getUsdRate();
        product.setPriceUsd(product.getPriceEur().multiply(rate));
        logger.info("Creating product: {}", product.toString());
        return repo.save(product);
    }

    public Optional<Product> get(Long id) {
        logger.info("Fetching product with id: {}", id);
        return repo.findById(id);
    }

    public List<Product> list() {
        logger.info("Fetching list of all products");
        return repo.findAll();
    }

    private BigDecimal getUsdRate() {
        try {
            String url = "https://api.hnb.hr/tecajn-eur/v3?valuta=USD";
            ResponseEntity<List<Map<String, String>>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null,
                            new ParameterizedTypeReference<List<Map<String, String>>>() {});
            String rate = response.getBody().get(0).get("srednji_tecaj");
            rate = rate.replace(",", ".");
            return new BigDecimal(rate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch USD rate: " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void updateUsdPrices() {
        try {
            BigDecimal fxRate = getUsdRate();
            List<Product> products = repo.findAll();
            for (Product p : products) {
                p.setPriceUsd(p.getPriceEur().multiply(fxRate));
            }
            repo.saveAll(products);
        } catch (Exception e) {
            // Log error, do not rethrow to avoid breaking scheduler
            System.err.println("Failed to update USD prices: " + e.getMessage());
        }
    }
}
