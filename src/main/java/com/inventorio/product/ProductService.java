package com.inventorio.product;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(Product product) {
        BigDecimal fxRate = getUsdRate();
        product.setPriceUsd(product.getPriceEur().multiply(fxRate));
        return repo.save(product);
    }

    public Optional<Product> get(Long id) {
        return repo.findById(id);
    }

    public List<Product> list() {
        return repo.findAll();
    }

    private BigDecimal getUsdRate() {
        String url = "https://api.hnb.hr/tecajn-eur/v3?valuta=USD";
        ResponseEntity<List<Map<String, String>>> response =
                restTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Map<String, String>>>() {});

        String rate = response.getBody().get(0).get("srednji_tecaj");
        rate = rate.replace(",", ".");
        return new BigDecimal(rate);
    }

    @Scheduled(cron = "0 0 2 * * *") // 2:00 in the morning
    public void updateUsdPrices() {
        BigDecimal fxRate = getUsdRate();
        List<Product> products = repo.findAll();
        for (Product p : products) {
            p.setPriceUsd(p.getPriceEur().multiply(fxRate));
        }
        repo.saveAll(products);
    }
}
