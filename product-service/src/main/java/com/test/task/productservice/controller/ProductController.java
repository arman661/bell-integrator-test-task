package com.test.task.productservice.controller;

import com.test.task.productservice.Order;
import com.test.task.productservice.ProductRecord;
import com.test.task.productservice.ProductResponse;
import com.test.task.productservice.entity.Product;
import com.test.task.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Log4j2
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> save(@RequestBody ProductRecord productRecord) {
        try {
            Product product = productService.save(productRecord);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(value = "/remove")
    public ResponseEntity<Product> delete(@RequestParam("id") Long id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> update(@RequestBody ProductRecord productRecord) {
        try {
            Product product = productService.update(productRecord);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PostMapping(value = "/buy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> buy(@RequestBody Order order) {
        try {
            ProductResponse response = productService.buy(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return null;
        }
    }
}
